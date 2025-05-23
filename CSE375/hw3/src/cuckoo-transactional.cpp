#include <iostream>
#include <vector>
#include <optional>
#include <functional>
#include <chrono>
#include <climits>
#include <cstdlib>
#include <thread>

using namespace std;

// https://en.cppreference.com/w/cpp/language/transactional_memory

template <typename T>
class Cuckoo {
private:
    vector<optional<T>> table1;
    vector<optional<T>> table2;
    int capacity = 10;
    const int LIMIT = 10;  // maximum number of displacements before triggering a resize

    // Primary hash function
    int hash1(const T& key) const transaction_safe{
        return hash_help(hash<T>{}(key));
    }

    // Secondary hash function
    int hash2(const T& key) const transaction_safe {
        return hash_help(hash<T>{}(key) >> 16);
    }

    int hash_help(size_t val) const transaction_safe {
        return static_cast<int>(val % capacity);
    }

    // Doubles the capacity of the table and rehash all keys
    void resize() {
        int oldCapacity = capacity;
        int newCapacity = capacity * 2;
        vector<optional<T>> newTable1(newCapacity, nullopt);
        vector<optional<T>> newTable2(newCapacity, nullopt);

        // copy old data
        vector<optional<T>> oldTable1 = table1;  
        vector<optional<T>> oldTable2 = table2;

        // needs to be synchronized because it is the same as having a global lock
        synchronized {
            capacity = newCapacity;
            table1 = move(newTable1);  
            table2 = move(newTable2);

            // Reinsert the items
            for (int i = 0; i < oldCapacity; i++) {
                if (oldTable1[i].has_value()) {
                    add(oldTable1[i].value());
                }
                if (oldTable2[i].has_value()) {
                    add(oldTable2[i].value());
                }
            }
        }

        
    }

    // Swap key with the element in one of the tables
    // Returns the replaced element
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

public:
    Cuckoo() {
        table1.resize(capacity, nullopt);
        table2.resize(capacity, nullopt);
    }

    ~Cuckoo() {}

    // Adds a key into the hash table
    // Returns false if the key already exists, true otherwise
    bool add(const T& key) {
        if (contains(key)) {
            return false;
        }
        T currentKey = key;
        int h1 = hash1(currentKey), h2 = hash2(currentKey);

        // Try up to LIMIT times to place the key
        atomic_noexcept {
            for (int i = 0; i < LIMIT; i++) {
                optional<T> temp = swap(1, h1, currentKey);
                if (!temp.has_value()) {
                    return true;
                }
                currentKey = temp.value();

                temp = swap(2, h2, currentKey);
                if (!temp.has_value()) {
                    return true;
                }
                currentKey = temp.value();
                h1 = hash1(currentKey);
                h2 = hash2(currentKey);
            }
        }
        // Cycle detected: resize and then add the key
        resize();
        return add(currentKey);
        
    }

    // Removes an element from the table
    // Returns false if the key was not found, true otherwise
    bool remove(const T& key) {
        int index = hash1(key), index2 = hash2(key);
        atomic_noexcept  { 
            if (table1[index].has_value() && table1[index].value() == key) { // check if it is in the first table
                table1[index] = nullopt;
                return true;
            }
            if (table2[index2].has_value() && table2[index2].value() == key) { // check if it is in the second table
                table2[index2] = nullopt;
                return true;
            }
        }
        return false;
    }

    // Check if an element is in the table
    bool contains(const T& key) const {
        int index1 = hash1(key), index2 = hash2(key);
        atomic_noexcept   { 
            return (table1[index1].has_value() && table1[index1].value() == key) 
                || (table2[index2].has_value() && table2[index2].value() == key);
        }
    }
    
    // returns the number of elements in the table
    int size() const {
        int totalSize = 0;
        for (const auto& item : table1) {
            if (item.has_value()) {
                totalSize++;
            }
        }
        for (const auto& item : table2) {
            if (item.has_value()) {
                totalSize++;
            }
        }
        return totalSize;
    }

    // Populate the table with a specified number of keys randomly generated by a passed in lambda function
    void populate(int numKeys, function<T()> keyGen) {
        for (int i = 0; i < numKeys; i++) {
            add(keyGen());
        }
    }
};
int main(int argc, char* argv[]) {


    const int N = stoi(argv[1]); // Number of operations
    const int numKeys = 1000000;
    const int numThreads = stoi(argv[3]); // Number of threads

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
