#include <functional>
#include <iostream>
#include <string>
#include <vector>

#include "map.h"

using namespace std;

/// SequentialMap is a sequential implementation of the Map interface (a
/// Key/Value store).  This map has O(n) complexity.  It's just for p1.
///
/// @tparam K The type of the keys in this map
/// @tparam V The type of the values in this map
template <typename K, typename V> class SequentialMap : public Map<K, V> {
	/// The key/value pairs, as a vector
	///
	/// NB: This is a very bad choice of data structure, but it's OK for p1
	vector<pair<K, V>> entries;

public:
	/// Construct by specifying the number of buckets it should have
	///
	/// @param _buckets (unused) The number of buckets
	SequentialMap(size_t) {}

	/// Destruct the SequentialMap
	virtual ~SequentialMap() {}

	/// Clear the map
	virtual void clear() {
		entries.clear();
	}

	int get(K key) {
		int i = 0;
		for (pair<K, V> p : entries) { // loop over entries
			if (p.first == key) {
				return i;
			}
			i++;
		}
		return -1;
	}

	/// Insert the provided key/value pair only if there is no mapping for the key
	/// yet.
	///
	/// @param key  The key to insert
	/// @param val  The value to insert
	///
	/// @return true if the key/value was inserted, false if the key already
	///         existed in the table
	virtual bool insert(K key, V val) {
		if (get(key) >= 0) { // ensure the key doesn't already exist
			return false;
		}
		pair<K, V> p(key, val);
		entries.push_back(p);
		return true;
	}
	

	/// Insert the provided key/value pair if there is no mapping for the key yet.
	/// If there is a key, then update the mapping by replacing the old value with
	/// the provided value
	///
	/// @param key    The key to upsert
	/// @param val    The value to upsert
	///
	/// @return true if the key/value was inserted, false if the key already
	///         existed in the table and was thus updated instead
	virtual bool upsert(K key, V val) {
		int index = get(key); // get the index of the key
		if (index >= 0) { // if the key exists, update the value
			entries[index].second = val;
			return false;
		}
		pair<K, V> p(key, val);
		entries.push_back(p);
		return true;		
	}

	/// Apply a function to the value associated with a given key.  The function
	/// is allowed to modify the value.
	///
	/// @param key The key whose value will be modified
	/// @param f   The function to apply to the key's value
	///
	/// @return true if the key existed and the function was applied, false
	///         otherwise
	virtual bool do_with(K key, function<void(V &)> f) {
		int index = get(key);
		if (index == -1) return false; // if the key doesn't exist, return false
		
		f(entries[index].second);
		return true;
	}

	/// Apply a function to the value associated with a given key.  The function
	/// is not allowed to modify the value.
	///
	/// @param key The key whose value will be modified
	/// @param f   The function to apply to the key's value
	///
	/// @return true if the key existed and the function was applied, false
	///         otherwise
	virtual bool do_with_readonly(K key, function<void(const V &)> f) {
		int index = get(key);
		if (index == -1) return false; // if the key doesn't exist, return false

		f(entries[index].second); // apply the function

		return true;
	}

	/// Remove the mapping from a key to its value
	///
	/// @param key  The key whose mapping should be removed
	///
	/// @return true if the key was found and the value unmapped, false otherwise
	virtual bool remove(K key) {
		int index = get(key);
		if (index < 0) return false; // if the key doesn't exist, return false
		entries.erase(entries.begin() + index);
		return true;	
	}

	/// Apply a function to every key/value pair in the map.  Note that the
	/// function is not allowed to modify keys or values.
	///
	/// @param f  The function to apply to each key/value pair
	virtual void do_all_readonly(function<void(const K, const V &)> f) {
		for (pair<K, V> p : entries) { 
			f(p.first, p.second); // apply the function to all entries
		}
	}
};
