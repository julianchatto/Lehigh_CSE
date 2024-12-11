#include <atomic>
#include <dlfcn.h>
#include <iostream>
#include <memory>
#include <mutex>
#include <shared_mutex>
#include <string>
#include <unordered_map>
#include <utility>
#include <vector>

#include "../common/constants.h"
#include "../common/contextmanager.h"
#include "../common/err.h"
#include "../common/file.h"

#include "functable.h"
#include "functypes.h"

using namespace std;

/// func_table is a table that stores functions that have been registered with
/// our server, so that they can be invoked by clients on the key/value pairs in
/// kv_store.
class my_functable : public FuncTable {
	struct Registration {
        void* so_handle;             // handle for shared object
        map_func map_function;       // pointer to map function
        reduce_func reduce_function; // pointer to reduce function
    };

    unordered_map<string, Registration> loaded_funcs; // map of function names to their registration details
    atomic<int> uid_generator = 0;                    // unique IDs generator
    shared_mutex lock;

    /// helper method to generate unique filenames for .so files
    string create_so_filename(const string& mrname) {
        return SO_PREFIX + "/" + mrname + "_" + to_string(uid_generator++) + ".so";
    }

public:
	/// Construct a function table for storing registered functions
	my_functable() = default;

	/// Destruct a function table
	virtual ~my_functable() {
		shutdown();
	}

	/// Register the map() and reduce() functions from the provided .so, and
	/// associate them with the provided name.
	///
	/// @param mrname The name to associate with the functions
	/// @param so     The so contents from which to find the functions
	///
	/// @return a status message
	virtual string register_mr(const string &mrname, const vector<uint8_t> &so) {

        // check if function name is already registered
        if (loaded_funcs.find(mrname) != loaded_funcs.end()) {
            return RES_ERR_FUNC; // if error, function already exists
        }

        // make sure function name not greater than max length
        if (mrname.length() > LEN_FNAME) {
            return RES_ERR_REQ_FMT; // return invalid request format
        }

        // make sure shared object not greater than max length max length
        if (so.size() > LEN_SO) {
            return RES_ERR_SO; 
        }

        // create a unique filename for the .so file
        string tempFile = create_so_filename(mrname);

        // write shared object to a file
        if (!write_file(tempFile, so, 0)) {
            return RES_ERR_SERVER; // file write error
        }

        // load shared object
        void* so_handle = dlopen(tempFile.c_str(), RTLD_NOW);
        if (!so_handle) {
            return RES_ERR_SO; // failed to load .so
        }

        // use RAII to clean up the shared object in case of failure
    	ContextManager cleanup([&]() { dlclose(so_handle); });

        // retrieve map and reduce function pointers using constants
        auto map_function = reinterpret_cast<map_func>(dlsym(so_handle, MAP_FUNC_NAME.c_str()));
        auto reduce_function = reinterpret_cast<reduce_func>(dlsym(so_handle, REDUCE_FUNC_NAME.c_str()));

        if (!map_function || !reduce_function) {
            return RES_ERR_SO; // missing required functions
        }

        // store function pointers and handle in map
        Registration registration = {so_handle, map_function, reduce_function};
        unique_lock<shared_mutex> lock_guard(lock); // lock for loaded_func
        loaded_funcs[mrname] = registration;

        // successfully registered and release cleanup
        cleanup.disable();
        return RES_OK;
	}

	/// Get the (already-registered) map() and reduce() functions associated with
	/// a name.
	///
	/// @param name The name with which the functions were mapped
	///
	/// @return A pair of function pointers, or {nullptr, nullptr} on error
	virtual pair<map_func, reduce_func> get_mr(const string &mrname) {
		shared_lock<shared_mutex> lock_guard(lock);

        // find func in the map
        auto it = loaded_funcs.find(mrname);
        if (it == loaded_funcs.end()) {
            return {nullptr, nullptr}; // return null if func not found
        }
        return {it->second.map_function, it->second.reduce_function};
	}

	/// When the function table shuts down, we need to de-register all the .so
	/// files that were loaded.
	virtual void shutdown() {
		unique_lock<shared_mutex> lock_guard(lock);

        // close shared object handles
        for (auto &entry : loaded_funcs) {
            dlclose(entry.second.so_handle);
        }
        loaded_funcs.clear();
    }
};

/// Create a FuncTable
FuncTable *functable_factory() { return new my_functable(); };