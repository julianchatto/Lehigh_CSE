#include <deque>
#include <iostream>
#include <mutex>
#include <algorithm> // for find

#include "mru.h"

using namespace std;

/// my_mru maintains a listing of the K most recent elements that have been
/// given to it.  It can be used to produce a "top" listing of the most recently
/// accessed keys.
class my_mru : public mru_manager {
private:
	mutex lock;
    deque<string> mru_listing; 
    size_t max_size;

public:
	/// Construct the mru_manager by specifying how many things it should track
	///
	/// @param elements The number of elements that can be tracked
	my_mru(size_t elements) : max_size(elements) {}

	/// Destruct the mru_manager
	virtual ~my_mru() {
		// clear deque to release any elements
		lock_guard<mutex> guard(lock); 
        mru_listing.clear();
	}

	/// Insert an element into the mru_manager, making sure that (a) there are no
	/// duplicates, and (b) the manager holds no more than /max_size/ elements.
	///
	/// @param elt The element to insert
	virtual void insert(const string &elt) {
		lock_guard<mutex> guard(lock); 

        // check if element already exists in mru_listing
        auto it = find(mru_listing.begin(), mru_listing.end(), elt);
        if (it != mru_listing.end()) { // if iterator is not at the end, element found
            mru_listing.erase(it); // erase element to move it to the front
        }

        // insert element at the front
        mru_listing.push_front(elt);

        // if mru_listing size exceeds max_size, remove the most back element
        if (mru_listing.size() > max_size) {
            mru_listing.pop_back();
        }
	}

	/// Remove an instance of an element from the mru_manager.  This can leave the
	/// manager in a state where it has fewer than max_size elements in it.
	///
	/// @param elt The element to remove
	virtual void remove(const string &elt) {
		lock_guard<mutex> guard(lock); 

        // find and remove the element if it exists
        auto it = find(mru_listing.begin(), mru_listing.end(), elt);
        if (it != mru_listing.end()) {
            mru_listing.erase(it); // erase element if found
        }
	}

	/// Clear the mru_manager
	virtual void clear() { 
		lock_guard<mutex> guard(lock); 
        mru_listing.clear(); 
	}

	/// Produce a concatenation of the top entries, in order of popularity
	///
	/// @return A newline-separated list of values
	virtual string get() {
		lock_guard<mutex> guard(lock); 

        // concatenate entries as a string
        string result;
        for (const auto& elt : mru_listing) {
            result += elt + "\n";
        }
        return result;
    }
};

/// Construct the mru_manager by specifying how many things it should track
///
/// @param elements The number of elements that can be tracked in MRU fashion
///
/// @return An mru manager object
mru_manager *mru_factory(size_t elements) { return new my_mru(elements); }