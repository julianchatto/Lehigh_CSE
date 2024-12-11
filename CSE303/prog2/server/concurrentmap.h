#include <openssl/evp.h>  
#include <openssl/sha.h>  
#include <functional>
#include <iostream>
#include <list>
#include <mutex>
#include <string>
#include <vector>
#include <cstring>


#include "map.h"

using namespace std;

/// ConcurrentMap is a concurrent implementation of the Map interface (a
/// Key/Value store).  It is implemented as a vector of buckets, with one lock
/// per bucket.  Since the number of buckets is fixed, performance can suffer if
/// the thread count is high relative to the number of buckets.  Furthermore,
/// the asymptotic guarantees of this data structure are dependent on the
/// quality of the bucket implementation.  If a vector is used within the bucket
/// to store key/value pairs, then the guarantees will be poor if the key range
/// is large relative to the number of buckets.  If an unordered_map is used,
/// then the asymptotic guarantees should be strong.
///
/// The ConcurrentMap is templated on the Key and Value types.
///
/// This map uses hash to map keys to positions in the vector.  A
/// production map should use something better.
///
/// This map provides strong consistency guarantees: every operation uses
/// two-phase locking (2PL), and the lambda parameters to methods enable nesting
/// of 2PL operations across maps.
///
/// @tparam K The type of the keys in this map
/// @tparam V The type of the values in this map
template <typename K, typename V> class ConcurrentMap : public Map<K, V> {
	// Hint: The reference solution uses a vector of structs, where the struct
	// combines a mutex with a collection.  It also has a field for the number of
	// buckets.  You should think carefully about whether your vector should hold
	// those structs directly, or hold pointers to those structs.  Your decision
	// will determine if you need a real destructor or not.

// private to maintain thread safety
private:
    // create struct for bucket
    struct Bucket {
        list<pair<K, V>> data;
        mutex bucket_mutex; 
    };

    vector<Bucket*> buckets; // vector of buckets 
    size_t num_buckets;  // number of buckets 
	hash<K> hashingFunc;

public:

	/// Construct by specifying the number of buckets it should have
	///
	/// @param _buckets The number of buckets
	ConcurrentMap(size_t _buckets) {
		num_buckets = _buckets;
		buckets.resize(num_buckets);

		// initializes each bucket
    	for (size_t i = 0; i < num_buckets; ++i) {
        	buckets[i] = new Bucket();  
    	}

	}

	/// Destruct the ConcurrentMap
	virtual ~ConcurrentMap() {
		// delete all buckets 
		for (Bucket* bucket : buckets) {
			if (bucket) {
				delete bucket;  // delete buckets safely
			}
    	}
		cout << "Server terminated" << endl;
	}

	/// Clear the map.  This operation needs to use 2pl
	virtual void clear() {
		// lock all buckets
		for (Bucket* bucket : buckets) {
			bucket->bucket_mutex.lock();
		}
		// clear all the buckets
		for (Bucket* bucket : buckets) {
			bucket->data.clear();  // clear all the data in the bucket
		}
		// unlock all the bucket
		for (Bucket* bucket : buckets) {
			bucket->bucket_mutex.unlock(); 
		}
	}

	/// Insert the provided key/value pair only if there is no mapping for the key
	/// yet.
	///
	/// @param key        The key to insert
	/// @param val        The value to insert
	///
	/// @return true if the key/value was inserted, false if the key already
	///         existed in the table
	virtual bool insert(K key, V val) {
		size_t bucket_index = hashingFunc(key) % num_buckets; // creates hash value using key and makes sure fits in bucket
		Bucket& bucket = *buckets[bucket_index]; // gets bucket with that hashed val

		bucket.bucket_mutex.lock(); // lock this bucket

		// check if key exists 
		for (auto& pair : bucket.data) {
			if (pair.first == key) {
				bucket.bucket_mutex.unlock(); // if exists, unlock
				return false;
			}
		}

		bucket.data.push_back({key, val}); // insert k/v pair into bucket
		bucket.bucket_mutex.unlock(); // unlock

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
		// find bucket with the key 
		size_t bucket_index = hashingFunc(key) % num_buckets;
		Bucket& bucket = *buckets[bucket_index];

		bucket.bucket_mutex.lock(); // lock that bucket 

		// if key is found, update the value 
		for (auto& pair : bucket.data) {
            if (pair.first == key) {
                pair.second = val;
				bucket.bucket_mutex.unlock(); 
                return false;
            }
        }

		// if not found, add k/v pair to bucket list 
		bucket.data.push_back({key, val});
		bucket.bucket_mutex.unlock(); // made sure to unlock

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
		// get the bucket of key 
		size_t bucket_index = hashingFunc(key) % num_buckets;
		Bucket& bucket = *buckets[bucket_index];

		bucket.bucket_mutex.lock(); // lock this bucket

		// if key is found, then apply function to the val
		for (auto& pair : bucket.data) {
			if (pair.first == key) {
				f(pair.second);
				bucket.bucket_mutex.unlock(); // unlock the bucket
				return true;
			}
		}
		// if not found, unlock bucket and return false 
		bucket.bucket_mutex.unlock();
		return false;
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
		// get the bucket of key 
		size_t bucket_index = hashingFunc(key) % num_buckets;
		Bucket& bucket = *buckets[bucket_index];

		bucket.bucket_mutex.lock(); // lock this bucket

		// if key is found, then apply function to the val
		for (auto& pair : bucket.data) {
			if (pair.first == key) {
				f(pair.second);
				bucket.bucket_mutex.unlock(); // unlock the bucket
				return true;
			}
		}
		// if not found, unlock bucket and return false 
		bucket.bucket_mutex.unlock();
		return false;
	}

	/// Remove the mapping from a key to its value
	///
	/// @param key        The key whose mapping should be removed
	///
	/// @return true if the key was found and the value unmapped, false otherwise
	virtual bool remove(K key) {
		size_t bucket_index = hashingFunc(key) % num_buckets;
		Bucket& bucket = *buckets[bucket_index];
		bucket.bucket_mutex.lock();

		// remove the mapping of certain key 
		auto entry = bucket.data.begin();
		while (entry != bucket.data.end()) {
			if (entry->first == key) {
				bucket.data.erase(entry);
				bucket.bucket_mutex.unlock();
				return true;
			}
			++entry;
		}

		bucket.bucket_mutex.unlock(); // make sure to unlock
		return false;
	}

	/// Apply a function to every key/value pair in the map. Note that the
	/// function is not allowed to modify keys or values.
	///
	/// @param f    The function to apply to each key/value pair
	/// @param then A function to run when this is done, but before unlocking...
	///             useful for 2pl
	virtual void do_all_readonly(function<void(const K, const V &)> f, function<void()> then) {
		// lock all bucket for 2pl
		for (Bucket* bucket : buckets) {
			bucket->bucket_mutex.lock();
		}
		// apply function to all k/v pair
		for (Bucket* bucket : buckets) {
			for (const auto& pair : bucket->data) {
				f(pair.first, pair.second);
			}
		}
		then();
		// unlock buckets 
		for (Bucket* bucket : buckets) {
			bucket->bucket_mutex.unlock();
		}
	}
};
