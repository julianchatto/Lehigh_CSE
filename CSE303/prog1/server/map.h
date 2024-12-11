#pragma once

#include <functional>
#include <vector>

using namespace std;

/// Map is an interface for a collection of key/value pairs.  It is templated on
/// both the key and value types.
///
/// We will ultimately produce sequential and concurrent instances of the map,
/// hopefully with reasonable asymptotic guarantees.
///
/// @tparam K The type of the keys in this map
/// @tparam V The type of the values in this map
template <typename K, typename V> class Map {
public:
	/// Destroy the Map
	virtual ~Map() {}

	/// Clear the map
	virtual void clear() = 0;

	/// Insert the provided key/value pair only if there is no mapping for the key
	/// yet.
	///
	/// @param key        The key to insert
	/// @param val        The value to insert
	///
	/// @return true if the key/value was inserted, false if the key already
	///         existed in the table
	virtual bool insert(K key, V val) = 0;

	/// Insert the provided key/value pair if there is no mapping for the key yet.
	/// If there is a key, then update the mapping by replacing the old value with
	/// the provided value
	///
	/// @param key  The key to upsert
	/// @param val  The value to upsert
	///
	/// @return true if the key/value was inserted, false if the key already
	///         existed in the table and was thus updated instead
	virtual bool upsert(K key, V val) = 0;

	/// Apply a function to the value associated with a given key.  The function
	/// is allowed to modify the value.
	///
	/// @param key The key whose value will be modified
	/// @param f   The function to apply to the key's value
	///
	/// @return true if the key existed and the function was applied, false
	///         otherwise
	virtual bool do_with(K key, function<void(V &)> f) = 0;

	/// Apply a function to the value associated with a given key.  The function
	/// is not allowed to modify the value.
	///
	/// @param key The key whose value will be modified
	/// @param f   The function to apply to the key's value
	///
	/// @return true if the key existed and the function was applied, false
	///         otherwise
	virtual bool do_with_readonly(K key, function<void(const V &)> f) = 0;

	/// Remove the mapping from a key to its value
	///
	/// @param key  The key whose mapping should be removed
	///
	/// @return true if the key was found and the value unmapped, false otherwise
	virtual bool remove(K key) = 0;

	/// Apply a function to every key/value pair in the map.  Note
	/// that the function is not allowed to modify keys or values.
	///
	/// @param f    The function to apply to each key/value pair
	virtual void do_all_readonly(function<void(const K, const V &)> f) = 0;
};
