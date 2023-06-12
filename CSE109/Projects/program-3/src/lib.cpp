#include "HashSet.hpp"
#include "LinkedList.hpp"

// Constructor for hashset
HashSet::HashSet(size_t size) {
    array = (LinkedList**) malloc(sizeof(LinkedList*) * size); // allocate memory for array
    if (array == NULL) { // ensure allocation was successful 
        printf("error allocating memory");
        exit(1);
    }
    this->size = size;
    load = 0.0; 
}
// Free all memory allocated by the hash set
HashSet::~HashSet() {
    unsigned int i = 0;
    while (i < size) {
        if (array[i] != NULL) {
            delete(array[i]); // delete each index
        }
        i++;
    }
    free(array); // free the array
}
  

// Hash an unsigned long into an index that fits into a hash set
unsigned long HashSet::hash(int item) {
    return prehash(item) % size; // gets the hash for the array 
}


// Insert item in the set. Return true if the item was inserted, false if it wasn't (i.e. it was already in the set)
// Recalculate the load factor after each successful insert (round to nearest whole number).
// If the load factor exceeds 70 after insert, resize the table to hold twice the number of buckets.
bool HashSet::insert(int item) {
    if(contains(item)) { // check if in the list
        return false;
    }

    unsigned long hashKeyValue = hash(item); // get the hash 

    if (array[hashKeyValue] == NULL) {  // check if the index has a linkedList
        array[hashKeyValue] = new LinkedList();  // create  new linkedList 
        capacity();
    }
    array[hashKeyValue]->insertAtHead(item); // insert at head becasue it is the quickest
    return true; 
}


// Remove an item from the set. Return true if it was removed, false if it wasn't (i.e. it wasn't in the set to begin with)
bool HashSet::remove(int item) {
    if (!contains(item)) { // check if it is not in the list
        return false;
    }
    unsigned long hashKeyValue = hash(item); // get the hash
    int i = 0;
    while (array[hashKeyValue]->itemAtIndex(i) != item) { // find the index
        i++;
    }
    array[hashKeyValue]->removeAtIndex(i); // remove at the index
    return true;
}

// Return true if the item exists in the set, false otherwise
bool HashSet::contains(int item) {
    unsigned long hashVal = hash(item);
    if (array[hashVal] == NULL) { // make sure that array[hashVal] is a list
        return false;
    }
    return array[hashVal]->contains(item); // call contians
}

// Returns the number of items in the hash set
size_t HashSet::len() {
    unsigned int i = 0;
    size_t length = 0;
    while (i < size) {
        if (array[i] != NULL) { // check if array[i] is a list
            length += array[i]->size(); // add size of linkedlist
        }
        i++;
    }
    return length;
}
 
// Returns the number of empty buckets that can be filled before reallocating
size_t HashSet::capacity() {
    unsigned int i = 0; 
    int count = 0;
    while (i < size) { // counts number of empty buckets
        if (array[i] != NULL) {
            count++;
        }
        i++;
    }

    int numBucketsUntillResize =  .7 * size; // calculate number of buckets that can be filled before reisize
    calcLoadFactor(); 
    if (numBucketsUntillResize - count > 0 ) { // prints out number of buckets until a resize is needed
        printf("There are %d buckets left until resize.\n", numBucketsUntillResize - count);
    }
    return numBucketsUntillResize;
}

// Print Table. You can do this in a way that helps you implement your hash set.
void HashSet::print() {
    unsigned int i = 0;
    while (i < size) {
        if (array[i] == NULL) { // if row is empty
            printf("Row: %d:\t\t[NULL]\n", i);
        } else {
            printf("Row: %d:\t\t", i); 
            array[i]->printList(); // calls print list on linkedList
            printf("\n");
        }
        i++;
    }
    printf("\n\n");
}

unsigned long HashSet::prehash(int item) {
    unsigned long h = 5381;
    while (item != 0) {  // until item is 0 
        h = ((h << 5) + h); // shifts h left 5 
        item /= 10; // divides item by 10
    }
    return h;
}

void HashSet::calcLoadFactor() {
    int bucketsFilled = 0;
    unsigned int i = 0;
    while (i < size) {
        if (array[i] != NULL) { // determine number of filled buckets
            bucketsFilled++; 
        }
        i++;
    }

    load = ((double) bucketsFilled) / size; // calculate and update current load
    if (load >= .7) {
        printf("The load factor is greater than or equal to .7. A resize is necessary\n");
    } {
        printf("The load factor is now %.3f\n", load);
    }
    
}