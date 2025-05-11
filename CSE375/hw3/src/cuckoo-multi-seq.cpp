#include <iostream>
#include <vector>
#include <optional>
#include <functional>
#include <chrono>
#include <climits>
#include <thread>
#include <mutex>
#include <cstdlib>
#include <atomic>

using namespace std;

template <typename T>
class CuckooSEQ {
private:
    vector<optional<T>> table1;
    vector<optional<T>> table2;
    int capacity = 10;
    const int LIMIT = 10;  // maximum number of displacements before triggering a resize

    // Primary hash function
    int hash1(const T& key) const {
        return hash_help(hash<T>{}(key));
    }

    // Secondary hash function
    int hash2(const T& key) const {
        return hash_help(hash<T>{}(key) >> 16);
    }

    int hash_help(size_t val) const {
        return static_cast<int>(val % capacity);
    }

    // Swap elements
    optional<T> swap(int tableNum, int index, const T& key) {
        optional<T> temp;
        if (tableNum == 1) {
            temp = table1[index];
            table1[index] = key;
        } else {
            temp = table2[index];
            table2[index] = key;
        }
        return temp;
    }

    void resize() {
        int oldCapacity = capacity;
        capacity *= 2;
        vector<optional<T>> oldTable1 = table1;
        vector<optional<T>> oldTable2 = table2;
        table1.clear();
        table2.clear();
        table1.resize(capacity, nullopt);
        table2.resize(capacity, nullopt);
        // Reinsert keys from both tables
        for (int i = 0; i < oldCapacity; i++) {
            if (oldTable1[i].has_value()) {
                add(oldTable1[i].value());
            }
            if (oldTable2[i].has_value()) {
                add(oldTable2[i].value());
            }
        }
    }

public:
    CuckooSEQ() {
        table1.resize(capacity, nullopt);
        table2.resize(capacity, nullopt);
    }

    bool add(const T& key) {
        if (contains(key)) {
            return false;
        }
        T currentKey = key;
        for (int i = 0; i < LIMIT; i++) {
            optional<T> temp = swap(1, hash1(currentKey), currentKey);
            if (!temp.has_value()) {
                return true;
            }
            currentKey = temp.value();

            temp = swap(2, hash2(currentKey), currentKey);
            if (!temp.has_value()) {
                return true;
            }
            currentKey = temp.value();
        }

        resize();
        return add(currentKey);
    }

    bool remove(const T& key) {
        int index = hash1(key);
        if (table1[index].has_value() && table1[index].value() == key) {
            table1[index] = nullopt;
            return true;
        }
        index = hash2(key);
        if (table2[index].has_value() && table2[index].value() == key) {
            table2[index] = nullopt;
            return true;
        }
        return false;
    }

    bool contains(const T& key) const {
        int index1 = hash1(key);
        int index2 = hash2(key);
        return (table1[index1].has_value() && table1[index1].value() == key) ||
               (table2[index2].has_value() && table2[index2].value() == key);
    }

    int size() const {
        int size = 0;
        for (const auto& item : table1) {
            if (item.has_value()) {
                size++;
            }
        }
        for (const auto& item : table2) {
            if (item.has_value()) {
                size++;
            }
        }
        return size;
    }
};

template <typename T>
class Cuckoo {
private:
    int numSeq;
    vector<CuckooSEQ<T>> sequentials;
    mutable vector<mutex> sequentialMutexes;

    int seqHash(const T& key) const {
        return static_cast<int>(hash<T>{}(key)) % numSeq;
    }
public:
    Cuckoo(int ct) : numSeq(ct) {
        sequentials.resize(numSeq);
        sequentialMutexes = vector<mutex>(numSeq);    
    }

    bool add(const T& key) {
        int seqIndex = seqHash(key);
        lock_guard<mutex> lock(sequentialMutexes[seqIndex]);
        return sequentials[seqIndex].add(key);
    }

    bool remove(const T& key) {
        int seqIndex = seqHash(key);
        lock_guard<mutex> lock(sequentialMutexes[seqIndex]);
        return sequentials[seqIndex].remove(key);
    }

    bool contains(const T& key) const {
        int seqIndex = seqHash(key);
        lock_guard<mutex> lock(sequentialMutexes[seqIndex]);
        return sequentials[seqIndex].contains(key);
    }

    int size() const {
        int totalSize = 0;
        for (const auto& seq : sequentials) {
            totalSize += seq.size();
        }
        return totalSize;
    }

    void populate(int numKeys, function<T()> keyGen) {
        for (int i = 0; i < numKeys; i++) {
            add(keyGen());
        }
    }
};

int main(int argc, char* argv[]) {
    // Check for required command line arguments
    // if (argc < 4) {
    //     cerr << "Usage: " << argv[0] << " <N> <numKeys> <numThreads>" << endl;
    //     return 1;
    // }

    // Parse command line arguments
    const int N = stoi(argv[2]);
    const int numKeys = 1000000;
    const int numThreads = stoi(argv[3]);
    
    const int numSeq = (numThreads == 1) ? 140 : (numThreads == 16) ? 1400 : 500;
        // Initialize the hash table
    Cuckoo<int> cuckoo(numSeq);

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

    auto startTime = chrono::high_resolution_clock::now();

    for (int t = 0; t < numThreads; t++) {
        threads.emplace_back([&, t]() {
            int startIdx = t * (N / numThreads);
            int endIdx = (t == numThreads - 1) ? N : (t + 1) * (N / numThreads);
            for (int i = startIdx; i < endIdx; ++i) {
                int randNum = randoms[i];
                // 80%: contains, 10%: add, 10%: remove
                if (randNum <= 80) {
                    cuckoo.contains(digit[i]);
                } else if (randNum <= 90) {
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
