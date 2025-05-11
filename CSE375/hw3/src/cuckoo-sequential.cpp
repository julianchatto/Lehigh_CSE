#include <iostream>
#include <vector>
#include <optional>
#include <functional>
#include <chrono>
#include <climits>

using namespace std;

template <typename T>
class Cuckoo {
private:
    vector<optional<T>> table1;
    vector<optional<T>> table2;
    int numElements = 0;
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

    // Doubles the capacity of the table and rehash all keys
    void resize() {
        int oldCapacity = capacity;
        capacity *= 2;
       
        // Save and create new tables
        vector<optional<T>> oldTable1 = table1;
        vector<optional<T>> oldTable2 = table2;
        table1.clear();
        table2.clear();
        table1.resize(capacity, nullopt);
        table2.resize(capacity, nullopt);
        numElements = 0; // reset size because add() will reinsert keys

        // Reinsert all keys from the old tables
        for (int i = 0; i < oldCapacity; i++) {
            if (oldTable1[i].has_value()) {
                add(oldTable1[i].value());
            }
            if (oldTable2[i].has_value()) {
                add(oldTable2[i].value());
            }
        }
    }

    // Halves the capacity of the table
    void shrink() {
        int oldCapacity = capacity;
        capacity = max(10, capacity / 2); // prevent capacity becoming smaller than 10
        
        // Save and create new tables
        vector<optional<T>> oldTable1 = table1;
        vector<optional<T>> oldTable2 = table2;
        table1.clear();
        table2.clear();
        table1.resize(capacity, nullopt);
        table2.resize(capacity, nullopt);
        numElements = 0;

        // Reinsert all keys from the old tables
        for (int i = 0; i < oldCapacity; i++) {
            if (oldTable1[i].has_value()) {
                add(oldTable1[i].value());
            }
            if (oldTable2[i].has_value()) {
                add(oldTable2[i].value());
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

    // Checks if the table is shrinkable
    bool shrinkable() const {
        return numElements < capacity / 4;
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
        // Try up to LIMIT times to place the key
        for (int i = 0; i < LIMIT; i++) {
            optional<T> temp = swap(1, hash1(currentKey), currentKey);
            if (!temp.has_value()) {
                numElements++;
                return true;
            }
            currentKey = temp.value();

            temp = swap(2, hash2(currentKey), currentKey);
            if (!temp.has_value()) {
                numElements++;
                return true;
            }
            currentKey = temp.value();
        }
        // Cycle detected: resize and then add the key
        resize();
        return add(currentKey);
    }

    // Removes an element from the table
    // Returns false if the key was not found, true otherwise
    bool remove(const T& key) {
        bool removed = false;
        int index = hash1(key);
        if (table1[index].has_value() && table1[index].value() == key) { // check if it is in the first table
            table1[index] = nullopt;
            removed = true;
        }
        index = hash2(key);
        if (!removed && (table2[index].has_value() && table2[index].value() == key)) { // check if it is in the second table
            table2[index] = nullopt;
            removed = true;
        }
        if (removed) { // decrease the number of elements in the table and check if the table should be shrunk
            numElements--;
            if (shrinkable()) {
                shrink();
            }
        }
        return removed;
    }

    // Check if an element is in the table
    bool contains(const T& key) const {
        int index1 = hash1(key), index2 = hash2(key);
        return (table1[index1].has_value() && table1[index1].value() == key) 
            || (table2[index2].has_value() && table2[index2].value() == key);
    }

    // returns the number of elements in the table
    int size() const {
        return numElements;
    }

    // Populate the table with a specified number of keys randomly generated by a passed in lambda function
    void populate(int numKeys, function<T()> keyGen) {
        for (int i = 0; i < numKeys; i++) {
            add(keyGen());
        }
    }
};

int main() {
    // Parse command line arguments
    const int N = INT_MAX, numKeys = 1000000;

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

    auto start = chrono::high_resolution_clock::now();
    for (int i = 0; i < N; i++) {
        int randNum = randoms[i];
        if (randNum <= 100) {
            cuckoo.contains(digit[i]);
        } else if (randNum <= 90) {
            cuckoo.add(digit[i]);
        } else {
            cuckoo.remove(digit[i]);
            // for threads, keep track of what numbers are inserted, and locally remove them
        }
    }
    auto end = chrono::high_resolution_clock::now();
    cout << chrono::duration_cast<chrono::microseconds>(end - start).count() << endl; 

    return 0;
    
    // int operations = 0;
    // auto start = chrono::high_resolution_clock::now();
    // while(chrono::duration_cast<chrono::seconds>(chrono::high_resolution_clock::now() - start).count() < 5) {
    //     int randNum = randoms[operations];
    //     int key = digit[operations];
    //     if (randNum <= 80) {
    //         cuckoo.contains(key);
    //     } else if (randNum <= 90) {
    //         cuckoo.add(key);
    //     } else {
    //         cuckoo.remove(key);
    //     }
    //     operations++;
    // }
    // auto end = chrono::high_resolution_clock::now();
    // cout << "Operations completed: " << operations << endl;
    return 0;
}
