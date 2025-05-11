#include <iostream>
#include <vector>
#include <functional>
#include <mutex>
#include <thread>
#include <atomic>
#include <chrono>
#include <algorithm>
#include <cstdlib>
#include <memory>
#include <climits>

using namespace std;

template <typename T>
class Cuckoo {
private:
    int capacity = (INT_MAX - 1000) / 2; // initial number of buckets per table
    const int LIMIT = 25,  // maximum number of displacements before triggering a resize
              PROB_SIZE = 128,
              THRESHOLD = 50;

    struct Entry {
        T key;
        atomic<bool> isValid;
        Entry(const T& k) : key(k), isValid(true) {}
    };

    struct Bucket {
        vector<unique_ptr<Entry>> items;
        mutable mutex lock;
        Bucket() {
            items.reserve(128);
        }
    };

    vector<vector<unique_ptr<Bucket>>> table;


    // Primary hash function
    int hash1(const T& key) const {
        return hash_help(hash<T>{}(key));
    }

    // Secondary hash function
    int hash2(const T& key) const {
        return hash_help(hash<T>{}(key) >> 16);
    }

    int hash_help(size_t val) const {
        return static_cast<int>(val) % capacity;
    }

    // Relocate an item from an overloaded bucket
    bool relocate(int i, int hi) {
        int j = 1 - i;
        for (int round = 0; round < LIMIT; round++) {
            Bucket iBucket = table[i][hi].get();

            // Find the index of a valid entry
            int idx = -1;
            for (int k = 0; k < iBucket->items.size(); k++) {
                if (iBucket->items[k]->isValid.load()) {
                    idx = k;
                    break;
                }
            }

            // No valid entry to relocate
            if (idx == -1)
                return true; 
            
            T y = iBucket->items[idx]->key;
            int hj = (i == 0) ? hash1(y) : hash2(y);
            int h1 = hash1(y), h2 = hash2(y);

            {
                lock_guard<mutex> lock1(table[0][h1]->lock);
                lock_guard<mutex> lock2(table[1][h2]->lock);

                // check item is still valid
                if (!table[i][hi]->items[idx]->isValid.load()) {
                    continue;
                }

                table[i][hi]->items[idx]->isValid.store(false);


                unique_ptr<Bucket>& bucketJ = table[j][hj];
                int size = bucketJ->items.size();
                if (size < THRESHOLD) {
                    bucketJ->items.push_back(make_unique<Entry>(y));
                    return true;
                } else if (size < PROB_SIZE) {
                    bucketJ->items.push_back(make_unique<Entry>(y));
                    i = j;
                    hi = hj;
                    j = 1 - j;
                } else { // Relocation failed
                    table[i][hi]->items[idx]->isValid.store(true);
                    return false;
                }
            }
        }
        return false;
    }

public:
    Cuckoo() {
        table.resize(2);  // two rows for the two hash functions
        for (auto &row : table) {
            row.resize(capacity);
            for (int i = 0; i < capacity; i++) {
                row[i] = make_unique<Bucket>();
            }
        }
    }

    ~Cuckoo() {}

    bool add(const T& key) {
        if (contains(key)) {
            return false;
        }
        bool mustResize = false;
        int chosenTable = -1, chosenHash = -1;

        const int h1 = hash1(key), h2 = hash2(key);
        {
            lock_guard<mutex> lock1(table[0][h1]->lock);
            lock_guard<mutex> lock2(table[1][h2]->lock);
            for (int i = 0; i < 2; i++) {
                for (auto &entry : table[i][i == 0 ? h1 : h2]->items) {
                    if (entry->key == key) {
                        if (!entry->isValid.load()) {
                            entry->isValid.store(true);
                            return true;
                        } 
                        return false;
                    }
                }
            }

            const int size1 = table[0][h1]->items.size(), size2 = table[1][h2]->items.size();
            if (size1 < THRESHOLD) {
                table[0][h1]->items.push_back(make_unique<Entry>(key));
                return true;
            } else if (size2 < THRESHOLD) {
                table[1][h2]->items.push_back(make_unique<Entry>(key));
                return true;
            } else if (size1 < PROB_SIZE) {
                table[0][h1]->items.push_back(make_unique<Entry>(key));
                chosenTable = 0;
                chosenHash = h1;
            } else if (size2 < PROB_SIZE) {
                table[1][h2]->items.push_back(make_unique<Entry>(key));
                chosenTable = 1;
                chosenHash = h2;
            } else {
                mustResize = true;
            }
        }

        if (mustResize) {
            cout << "UHOH" << endl;
            exit(1);
            return add(key);
        } else if (!relocate(chosenTable, chosenHash)) {
            cout << "UHOH" << endl;
            exit(1);
        }
        return true;
    }

    bool remove(const T& key) {
        const int h1 = hash1(key), h2 = hash2(key);
        
        // lock_guard<mutex> lock1(table[0][h1]->lock);
        // lock_guard<mutex> lock2(table[1][h2]->lock);
        
        // Scan first bucket
        Bucket* b = table[0][h1].get();
        for (auto &entry : b->items) {
            if (entry->key == key && entry->isValid.load()) {
                bool expected = true;
                if (entry->isValid.compare_exchange_strong(expected, false)) {
                    return true;
                }

            }
        }

        Bucket* b2 = table[0][h2].get();
        for (auto &entry : b2->items) {
            if (entry->key == key && entry->isValid.load()) {
                bool expected = true;
                if (entry->isValid.compare_exchange_strong(expected, false)) {
                    return true;
                }

            }
        }
        
        return false;
    }

    bool contains(const T& key) const {
        // Scan first bucket
        const int h1 = hash1(key), h2 = hash2(key);

        const Bucket* b = table[0][h1].get();
        for (const auto& entry : b->items) {
            // load the value first because it should take less time 
            if (entry->key == key && entry->isValid.load()) {
                return true;
            }
        }

        // scan second bucket
        const Bucket* b2 = table[1][h2].get();
        for (const auto& entry : b2->items) {
            // load the value first because it should take less time 
            if (entry->key == key && entry->isValid.load()) {
                return true;
            }
        }

        return false;
    }

    // Populate the table
    void populate(int numKeys, function<T()> keyGen) {
        for (int i = 0; i < numKeys; i++) {
            add(keyGen());
        }
    }

    // Get the size of the table
    int size() const {
        int totalSize = 0;
        for (const auto& row : table) {
            for (const auto& bucket : row) {
                for (const auto& entry : bucket->items) {
                    if (entry->isValid.load()) {
                        totalSize++;
                    }
                }
            }
        }
        return totalSize;
    }
};


int main(int argc, char* argv[]) {

    // Parse command line arguments
    const int N = 1000;
    const int numKeys = 1000000;
    const int numThreads = 12;

    // Initialize the hash table
    Cuckoo<int> cuckoo;

    // Prepopulate half of the range
    cuckoo.populate(numKeys >> 1, []() {
        static int i = 0; 
        return i++;
    });

    cout << "Initial size: " << cuckoo.size() << endl;
    // Prepare random operations
    vector<int> randoms;
    vector<int> digit;
    randoms.reserve(N);
    digit.reserve(N);
    for (int i = 0; i < N; i++) {
        randoms.push_back(rand() % 100);   // random operation code
        digit.push_back(rand() % numKeys); // random key in [0, numKeys)
    }

    // Create worker threads
    vector<thread> threads;
    threads.reserve(numThreads);

    auto startTime = chrono::high_resolution_clock::now();

    for (int t = 0; t < numThreads; t++) {
        threads.emplace_back([&, t]() {
            int startIdx = t * (N / numThreads);
            int endIdx = (t == numThreads - 1) ? N : (t + 1) * (N / numThreads);
            for (int i = startIdx; i < endIdx; ++i) {
                int randNum = randoms[i];
                // 80%: contains, 10%: add, 10%: remove
                if (randNum <= 100) {
                    cuckoo.contains(digit[i]);
                } else if (randNum < 90) {
                    cuckoo.add(digit[i]);
                } else {
                    cuckoo.remove(digit[i]);
                }
            }
        });
    }

    // Join all threads
    for (auto &th : threads) {
        th.join();
    }

    auto endTime = chrono::high_resolution_clock::now();

    // Print elapsed time in microseconds
    cout << chrono::duration_cast<chrono::microseconds>(endTime - startTime).count() << endl;

    return 0;
}
