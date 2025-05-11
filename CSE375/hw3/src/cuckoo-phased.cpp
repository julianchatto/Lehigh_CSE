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

    struct Bucket {
        vector<T> items;
        mutable mutex lock;
    };

    int capacity = 10; // initial number of buckets per table
    const int LIMIT = 25,  // maximum number of displacements before triggering a resize
              PROB_SIZE = 128,
              THRESHOLD = 50;

    vector<vector<unique_ptr<Bucket>>> table;

    mutex resizeMutex;

    void acquire(const int hash1, const int hash2) const {
        // Acquire locks for both buckets
        table[0][hash1]->lock.lock();
        table[1][hash2]->lock.lock();
    }

    void release(const int hash1, const int hash2) const {
        // Release locks for both buckets
        table[0][hash1]->lock.unlock();
        table[1][hash2]->lock.unlock();
    }

    void acquireAll() const {
        for (int i = 0; i < capacity; i++) {
            table[0][i]->lock.lock();
            table[1][i]->lock.lock();
        }
    }

    void releaseAll() const {
        for (int i = 0; i < capacity; i++) {
            table[0][i]->lock.unlock();
            table[1][i]->lock.unlock();
        }
    }
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

    // Doubles the capacity 
    void resize() {
        int oldCapacity = capacity;
        // first lock everything 
        acquireAll();

        if (capacity != oldCapacity) { // if resize was called before we were able to acquire all
            releaseAll();
            return;
        }
        vector<vector<unique_ptr<Bucket>>> oldTable = move(table);
        capacity *= 2;
        // clear out the old table
        table.clear();
        table.resize(2);
        for (int i = 0; i < 2; i++) {
            table[i].resize(capacity);
            for (int j = 0; j < capacity; j++) {
                table[i][j] = make_unique<Bucket>();
            }
        }

        // add all the old items
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < oldCapacity; j++) {
                for (auto& item : oldTable[i][j]->items) {
                    add(item);
                }
            }
        }

        for (int i = 0; i < capacity; i++) {
            oldTable[0][i]->lock.unlock();
            oldTable[1][i]->lock.unlock();
        }
    }

    // Relocate an item from an overloaded bucket
    bool relocate(int i, int hi) {
        int hj = 0, j = 1 - i;
        for (int round = 0; round < LIMIT; round++) {
            vector<T> iSet = table[i][hi]->items;
            T y = iSet[0];

            if (i == 0)
                hj = hash1(y);
            else
                hj = hash2(y);

            int h1 = hash1(y), h2 = hash2(y);
            acquire(h1, h2);

            vector jSet = table[j][hj]->items;
            auto it = find(iSet.begin(), iSet.end(), y);
            // if item in iSet, remove it
            if (it != iSet.end()) {
                iSet.erase(it);
                if (jSet.size() < THRESHOLD) {
                    jSet.push_back(y);
                    release(h1, h2);
                    return true;
                } else if (jSet.size() < PROB_SIZE) {
                    jSet.push_back(y);
                    // relocating from the other bucket
                    i = 1 - i;
                    hi = hj;
                    j = 1 - j;
                } else {
                    iSet.push_back(y);
                    release(h1, h2);
                    return false;
                }
            } else if (iSet.size() >= THRESHOLD) {
                continue;
            } else {
                release(h1, h2);
                return true;
            }
            release(h1, h2);
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
        int h1 = hash1(key), h2 = hash2(key);
        if (contains(key)) {
            release(h1, h2);
            return false;
        }

        acquire(h1, h2);

        int i = -1, h = -1;
        bool mustResize = false;
        vector<T> set1 = table[0][h1]->items, set2 = table[1][h2]->items;
        if (set1.size() < THRESHOLD) {
            set1.push_back(key);
            release(h1, h2);
            return true;
        } else if (set2.size() < THRESHOLD) {
            set2.push_back(key);
            release(h1, h2);
            return true;
        } else if (set1.size() < PROB_SIZE) {
            set1.push_back(key);
            i = 0;
            h = h1;
        } else if (set2.size() < PROB_SIZE) {
            set2.push_back(key);
            i = 1;
            h = h2;
        } else {
            mustResize = true;
        }

        release(h1, h2);

        if (mustResize) {
            resize();
            return add(key);
        } else if (!relocate(i, h)) {
            resize();
        }
        return true;
    }

    bool remove(const T& key) {
        int h1 = hash1(key), h2 = hash2(key);
        acquire(h1, h2);

        vector<T> list = table[0][h1]->items;
        auto it = find(list.begin(), list.end(), key);
        if (it != list.end()) {
            table[0][h1]->items.erase(it);
            release(h1, h2);
            return true;
        } 
        list = table[1][h2]->items;
        it = find(list.begin(), list.end(), key);
        if (it != list.end()) {
            table[1][h2]->items.erase(it);
            release(h1, h2);
            return true;
        }
        release(h1, h2);
        return false;
    }

    bool contains(const T& key) {
        int h1 = hash1(key), h2 = hash2(key);
        lock_guard<mutex> lock1(table[0][h1]->lock);
        lock_guard<mutex> lock2(table[1][h2]->lock);
        Bucket* b = table[0][h1].get();
        if (find(b->items.begin(), b->items.end(), key) != b->items.end()) {
            return true;
        }
        b = table[1][h2].get();
        if (find(b->items.begin(), b->items.end(), key) != b->items.end()) {
            return true;
        }

        return false;
    }

    // Populate the table
    void populate(int numKeys, function<T()> keyGen) {
        for (int i = 0; i < numKeys; i++) {
            add(keyGen());
        }
    }

    int size() const {
        int totalSize = 0;
        for (const auto& row : table) {
            for (const auto& bucket : row) {
                totalSize += bucket->items.size();
            }
        }
        return totalSize;
    }
};


int main(int argc, char* argv[]) {
    // Check for required command line arguments
    // if (argc < 4) {
    //     cerr << "Usage: " << argv[0] << " <N> <numKeys> <numThreads>" << endl;
    //     return 1;
    // }

    // Parse command line arguments
    const int N = INT_MAX;
    const int numKeys = 1000000;
    const int numThreads = 12;

    // Initialize the hash table
    Cuckoo<int> cuckoo;

    // Prepopulate half of the range
    cuckoo.populate(numKeys >> 1, []() {
        static int i = 0; 
        return i++;
    });

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
    cout << "HERE" << endl;

    auto startTime = chrono::high_resolution_clock::now();

    for (int t = 0; t < numThreads; t++) {
        threads.emplace_back([&, t]() {
            int startIdx = t * (N / numThreads);
            int endIdx = (t == numThreads - 1) ? N : (t + 1) * (N / numThreads);
            for (int i = startIdx; i < endIdx; ++i) {
                int randNum = randoms[i];
                // 80%: contains, 10%: add, 10%: remove
                if (randNum < 1000) {
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
