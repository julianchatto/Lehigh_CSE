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
#include <shared_mutex>

using namespace std;

template <typename T>
class Cuckoo {
private:
    // Bucket no longer holds its own lock.
    struct Bucket {
        vector<T> items;
    };

    int capacity = 10; // initial number of buckets per table
    const int LIMIT = 10,  // maximum number of displacements before triggering a resize
              PROB_SIZE = 4,
              THRESHOLD = 2;

    // Two hash tables: each row is a table.
    vector<vector<unique_ptr<Bucket>>> table;

    // For resizing serialization.
    shared_mutex resizeMutex;

    int numStripes;
    vector<mutex> stripeLocks;

    // Compute a “global” stripe index for a given table and bucket index.
    int getStripeIndex(int tableIndex, int bucketIndex) const {
        return (tableIndex * capacity + bucketIndex) % numStripes;
    }

    void acquire(const int bucket0, const int bucket1) {
        int stripe0 = getStripeIndex(0, bucket0);
        int stripe1 = getStripeIndex(1, bucket1);
        // avoid deadlock by always locking in ascending order
        if (stripe0 == stripe1) {
            stripeLocks[stripe0].lock();
        } else if (stripe0 < stripe1) {
            stripeLocks[stripe0].lock();
            stripeLocks[stripe1].lock();
        } else {
            stripeLocks[stripe1].lock();
            stripeLocks[stripe0].lock();
        }
    }

    void release(const int bucket0, const int bucket1) {
        int stripe0 = getStripeIndex(0, bucket0);
        int stripe1 = getStripeIndex(1, bucket1);
        if (stripe0 == stripe1) {
            stripeLocks[stripe0].unlock();
        } else {
            // Order of unlock does not matter.
            stripeLocks[stripe0].unlock();
            stripeLocks[stripe1].unlock();
        }
    }

    void acquireAll() {
        for (int i = 0; i < numStripes; i++) {
            stripeLocks[i].lock();
        }
    }

    void releaseAll() {
        for (int i = 0; i < numStripes; i++) {
            stripeLocks[i].unlock();
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

    // Helper for re-inserting an item during a resize
    // assumes exclusive access
    void reinsertItem(const T& item) {
        int h1 = hash1(item), h2 = hash2(item);
        auto& set1 = table[0][h1]->items;
        auto& set2 = table[1][h2]->items;
        if (set1.size() < THRESHOLD) {
            set1.push_back(item);
        } else if (set2.size() < THRESHOLD) {
            set2.push_back(item);
        } else if (set1.size() < PROB_SIZE) {
            set1.push_back(item);
        } else if (set2.size() < PROB_SIZE) {
            set2.push_back(item);
        } else {
            set1.push_back(item);
        }
    }

    void resize() {
        unique_lock<shared_mutex> resLock(resizeMutex);

        int oldCapacity = capacity;
        acquireAll();  // lock all stripes to block concurrent accesses

        // Check if another thread already resized
        if (capacity != oldCapacity) {
            releaseAll();
            return;
        }

        // Save the old table.
        vector<vector<unique_ptr<Bucket>>> oldTable = move(table);
        capacity *= 2;
        table.clear();
        table.resize(2);
        for (int i = 0; i < 2; i++) {
            table[i].resize(capacity);
            for (int j = 0; j < capacity; j++) {
                table[i][j] = make_unique<Bucket>();
            }
        }

        // Reinsert all items from the old table
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < oldCapacity; j++) {
                for (auto& item : oldTable[i][j]->items) {
                    reinsertItem(item);
                }
            }
        }
        releaseAll();
    }

    // Relocate an item from an overloaded bucket.
    bool relocate(int i, int hi) {
        int hj = 0, j = 1 - i;
        for (int round = 0; round < LIMIT; round++) {
            auto& iSet = table[i][hi]->items;
            if (iSet.empty()) return true;  // nothing to relocate
            T y = iSet[0];

            // Compute new bucket index for y
            if (i == 0)
                hj = hash1(y);
            else
                hj = hash2(y);

            int h1 = hash1(y), h2 = hash2(y);
            acquire(h1, h2);

            auto& jSet = table[j][hj]->items;
            // Try to remove y from the current bucket.
            auto it = find(iSet.begin(), iSet.end(), y);
            if (it != iSet.end()) {
                iSet.erase(it);
                if (jSet.size() < THRESHOLD) {
                    jSet.push_back(y);
                    release(h1, h2);
                    return true;
                } else if (jSet.size() < PROB_SIZE) {
                    jSet.push_back(y);
                    i = 1 - i;
                    hi = hj;
                    j = 1 - j;
                } else {
                    iSet.push_back(y);
                    release(h1, h2);
                    return false;
                }
            } else if (iSet.size() >= THRESHOLD) {
                release(h1, h2);
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
    // Constructor accepts the number of stripe locks (default is 16).
    Cuckoo(int numStripes_ = 1000) : numStripes(numStripes_), stripeLocks(numStripes_) {
        table.resize(2); 
        for (int i = 0; i < 2; i++) {
            table[i].resize(capacity);
            for (int j = 0; j < capacity; j++) {
                table[i][j] = make_unique<Bucket>();
            }
        }
    }

    ~Cuckoo() {}

    bool add(const T& key) {
        shared_lock<shared_mutex> resLock(resizeMutex);
        int h1 = hash1(key), h2 = hash2(key);
        // Check if key already exists.
        if (contains(key)) {
            return false;
        }
        acquire(h1, h2);

        int i = -1, h = -1;
        bool mustResize = false;
        auto& set1 = table[0][h1]->items;
        auto& set2 = table[1][h2]->items;
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
        shared_lock<shared_mutex> resLock(resizeMutex);

        int h1 = hash1(key), h2 = hash2(key);
        acquire(h1, h2);

        auto& list1 = table[0][h1]->items;
        auto it = find(list1.begin(), list1.end(), key);
        if (it != list1.end()) {
            list1.erase(it);
            release(h1, h2);
            return true;
        }

        auto& list2 = table[1][h2]->items;
        it = find(list2.begin(), list2.end(), key);
        if (it != list2.end()) {
            list2.erase(it);
            release(h1, h2);
            return true;
        }
        release(h1, h2);
        return false;
    }

    // Checks if an element is in the table.
    bool contains(const T& key) {
        shared_lock<shared_mutex> resLock(resizeMutex);

        int h1 = hash1(key), h2 = hash2(key);
        acquire(h1, h2);
        auto& items1 = table[0][h1]->items;
        if (find(items1.begin(), items1.end(), key) != items1.end()) {
            release(h1, h2);
            return true;
        }
        auto& items2 = table[1][h2]->items;
        if (find(items2.begin(), items2.end(), key) != items2.end()) {
            release(h1, h2);
            return true;
        }
        release(h1, h2);
        return false;
    }

    // Populate the table with a specified number of keys generated by the given lambda.
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
                if (randNum < 80) {
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
