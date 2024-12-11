#pragma once

#include <memory>
#include <string>
#include <utility>
#include <vector>

#include "functypes.h"

using namespace std;

/// FuncTable is an interface describing a class that stores functions that
/// have been registered with our server, so that they can be invoked by clients
/// on the key/value pairs in kv_store.
class FuncTable {

public:
	/// Destruct a function table
	virtual ~FuncTable() {}

	/// Register the map() and reduce() functions from the provided .so, and
	/// associate them with the provided name.
	///
	/// @param mrname The name to associate with the functions
	/// @param so     The so contents from which to find the functions
	///
	/// @return a status message
	virtual string register_mr(const string &mrname, const vector<uint8_t> &so) = 0;

	/// Get the (already-registered) map() and reduce() functions associated with
	/// a name.
	///
	/// @param name The name with which the functions were mapped
	///
	/// @return A pair of function pointers, or {nullptr, nullptr} on error
	virtual pair<map_func, reduce_func>
	get_mr(const string &mrname) = 0;

	/// When the function table shuts down, we need to de-register all the .so
	/// files that were loaded.
	virtual void shutdown() = 0;
};

/// Create a FuncTable
FuncTable *functable_factory();