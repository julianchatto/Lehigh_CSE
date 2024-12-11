#include <cassert>
#include <cstdio>
#include <cstring>
#include <functional>
#include <iostream>
#include <memory>
#include <openssl/rand.h>
#include <openssl/sha.h>
#include <string>
#include <unistd.h>
#include <utility>
#include <vector>

#include "../common/constants.h"
#include "../common/contextmanager.h"
#include "../common/err.h"

#include "authtableentry.h"
#include "gatekeeper.h"
#include "map.h"
#include "map_factories.h"
#include "mru.h"
#include "quotas.h"
#include "storage.h"

using namespace std;

// Forward-declare the function for building a storage factory, so that we can
// create a storage object from this file, without having the implementation on
// hand.
Storage *storage_factory(const string &fname, size_t buckets);

/// MyGatekeeper is the student implementation of the Gatekeeper class
class MyGatekeeper : public Gatekeeper {
	/// The upload quota
	const size_t up_quota;

	/// The download quota
	const size_t down_quota;

	/// The requests quota
	const size_t req_quota;

	/// The number of seconds over which quotas are enforced
	const double quota_dur;

	/// The table for tracking the most recently used keys
	mru_manager *mru;

	/// A table for tracking quotas
	Map<string, Quotas *> *quota_table;

	/// The actual storage object
	Storage *my_storage;

public:
	/// Construct an empty object and specify the file from which it should be
	/// loaded.  To avoid exceptions and errors in the constructor, the act of
	/// loading data is separate from construction.
	///
	/// @param fname   The name of the file to use for persistence
	/// @param buckets The number of buckets in the hash table
	/// @param upq     The upload quota
	/// @param dnq     The download quota
	/// @param rqq     The request quota
	/// @param qd      The quota duration
	/// @param top     The size of the "top keys" cache
	MyGatekeeper(const std::string &fname, size_t buckets, size_t upq, size_t dnq,
               size_t rqq, double qd, size_t top)
      : up_quota(upq), down_quota(dnq), req_quota(rqq), quota_dur(qd),
        mru(mru_factory(top)), quota_table(quotatable_factory(buckets)),
        my_storage(storage_factory(fname, buckets)) {}

	/// Destructor for the gatekeeper object.
	virtual ~MyGatekeeper() {
		delete mru;
		delete my_storage;
		quota_table->clear();
		delete quota_table;
	}

	/// Create a new entry in the Auth table.  If the user already exists, return
	/// an error.  Otherwise, create a salt, hash the password, and then save an
	/// entry with the username, salt, hashed password, and a zero-byte content.
	///
	/// @param user The user name to register
	/// @param pass The password to associate with that user name
	///
	/// @return A result tuple, as described in storage.h
	virtual result_t add_user(const string &user, const string &pass) {
		return my_storage->add_user(user, pass);
	}

	/// Set the data bytes for a user, but do so if and only if the password
	/// matches
	///
	/// @param user    The name of the user whose content is being set
	/// @param pass    The password for the user, used to authenticate
	/// @param content The data to set for this user
	///
	/// @return A result tuple, as described in storage.h
	virtual result_t set_user_data(const string &user, const string &pass, const vector<uint8_t> &content) {
		return my_storage->set_user_data(user, pass, content);
	}

	/// Return a copy of the user data for a user, but do so only if the password
	/// matches
	///
	/// @param user The name of the user who made the request
	/// @param pass The password for the user, used to authenticate
	/// @param who  The name of the user whose content is being fetched
	///
	/// @return A result tuple, as described in storage.h.  Note that "no data" is
	///         an error
	virtual result_t get_user_data(const string &user, const string &pass, const string &who) {
		return my_storage->get_user_data(user, pass, who);
	}

	/// Return a newline-delimited string containing all of the usernames in the
	/// auth table
	///
	/// @param user The name of the user who made the request
	/// @param pass The password for the user, used to authenticate
	///
	/// @return A result tuple, as described in storage.h
	virtual result_t get_all_users(const string &user, const string &pass) {
		return my_storage->get_all_users(user, pass);
	}

	/// Authenticate a user
	///
	/// @param user The name of the user who made the request
	/// @param pass The password for the user, used to authenticate
	///
	/// @return A result tuple, as described in storage.h
	virtual result_t auth(const string &user, const string &pass) {
		return my_storage->auth(user, pass);
	}

	// Check if a user can perform an operation based on their quota
	result_t check_quota(const string &user, size_t amount, quota_tracker *tracker, const string &error_code) {
		// Access quota for the user
		if (!tracker->check_add(amount)) {
			return {false, error_code, {}};
		}
		return {true, RES_OK, {}};
	}
	
	// Get the Quota object for a user
	Quotas* getQuota(const string &user) {
		Quotas *quota;
		if (!quota_table->do_with(user, [&quota](Quotas *q) {
			quota = q;
		})) {
			return nullptr;
		}
		return quota;
	}

	// Ensure that the user is in the quota table
	void ensure_user_quota(const string &user) {
		// If the user does not exist, create quotas
		if (!quota_table->do_with_readonly(user, [](const Quotas *q) {})) {
			Quotas *new_quotas = new Quotas{
				quota_factory(up_quota, quota_dur),   // Upload quota
				quota_factory(down_quota, quota_dur), // Download quota
				quota_factory(req_quota, quota_dur)   // Request quota
			};

			// Insert the new quotas into the table
			quota_table->insert(user, new_quotas, []() {
				// Quotas successfully added
			});
		}
	}

	/// Create a new key/value mapping in the table
	///
	/// @param user The name of the user who made the request
	/// @param pass The password for the user, used to authenticate
	/// @param key  The key whose mapping is being created
	/// @param val  The value to copy into the map
	///
	/// @return A result tuple, as described in storage.h
	virtual result_t kv_insert(const string &user, const string &pass, const string &key, const vector<uint8_t> &val) {
		// Authenticate
		result_t auth_result = my_storage->auth(user, pass);
		if (!auth_result.succeeded) {
			return auth_result;
		}

		ensure_user_quota(user);

		Quotas *quota = getQuota(user);
		if (!quota) {
			return {false, RES_ERR_SERVER, {}};
		}

		// Check request quota
		result_t quota_result = check_quota(user, 1, quota->requests, RES_ERR_QUOTA_REQ);
		if (!quota_result.succeeded) {
			return quota_result;
		}

		// Check upload quota
		quota_result = check_quota(user, val.size(), quota->uploads, RES_ERR_QUOTA_UP);
		if (!quota_result.succeeded) {
			return quota_result;
		}

		// Insert the key-value pair
		result_t result = my_storage->kv_insert(user, pass, key, val, [&]() {
			mru->insert(key);
		});

		return result;
	};

	/// Get a copy of the value to which a key is mapped
	///
	/// @param user The name of the user who made the request
	/// @param pass The password for the user, used to authenticate
	/// @param key  The key whose value is being fetched
	///
	/// @return A result tuple, as described in storage.h
	virtual result_t kv_get(const string &user, const string &pass, const string &key) {
		// Authenticate
		result_t auth_result = my_storage->auth(user, pass);
		if (!auth_result.succeeded) {
			return auth_result;
		}

		ensure_user_quota(user);

		Quotas *quota = getQuota(user);
		if (!quota) {
			return {false, RES_ERR_SERVER, {}};
		}

		// Check request quota
		result_t quota_result = check_quota(user, 1, quota->requests, RES_ERR_QUOTA_REQ);
		if (!quota_result.succeeded) {
			return quota_result;
		}

		result_t res = my_storage->kv_get(user, pass, key, [](size_t val) {});
		if (!res.succeeded) {
			return res;
		}

		// Check download quota
		quota_result = check_quota(user, res.data.size(), quota->downloads, RES_ERR_QUOTA_DOWN);
		if (!quota_result.succeeded) {
			return quota_result;
		}

		// Record key access
		mru->insert(key);

		return res;
	};

	/// Delete a key/value mapping
	///
	/// @param user The name of the user who made the request
	/// @param pass The password for the user, used to authenticate
	/// @param key  The key whose value is being deleted
	///
	/// @return A result tuple, as described in storage.h
	virtual result_t kv_delete(const string &user, const string &pass, const string &key) {
		// Authenticate
		result_t auth_result = my_storage->auth(user, pass);
		if (!auth_result.succeeded) {
			return auth_result;
		}

		ensure_user_quota(user);

		Quotas *quota = getQuota(user);
		if (!quota) {
			return {false, RES_ERR_SERVER, {}};
		}

		// Check request quota
		result_t quota_result = check_quota(user, 1, quota->requests, RES_ERR_QUOTA_REQ);
		if (!quota_result.succeeded) {
			return quota_result;
		}

		// Delete the key-value pair
		return my_storage->kv_delete(user, pass, key, [&]() {
			mru->remove(key);
		});
	};

	/// Insert or update, so that the given key is mapped to the give value
	///
	/// @param user The name of the user who made the request
	/// @param pass The password for the user, used to authenticate
	/// @param key  The key whose mapping is being upserted
	/// @param val  The value to copy into the map
	///
	/// @return A result tuple, as described in storage.h.  Note that there are
	///         two "OK" messages, depending on whether we get an insert or an
	///         update.
	virtual result_t kv_upsert(const string &user, const string &pass, const string &key, const vector<uint8_t> &val) {
		// Authenticate
		result_t auth_result = my_storage->auth(user, pass);
		if (!auth_result.succeeded) {
			return auth_result;
		}

		ensure_user_quota(user);

		Quotas *quota = getQuota(user);
		if (!quota) {
			return {false, RES_ERR_SERVER, {}};
		}

		// Check request quota
		result_t quota_result = check_quota(user, 1, quota->requests, RES_ERR_QUOTA_REQ);
		if (!quota_result.succeeded) {
			return quota_result;
		}

		// Check upload quota
		quota_result = check_quota(user, val.size(), quota->uploads, RES_ERR_QUOTA_UP);
		if (!quota_result.succeeded) {
			return quota_result;
		}

		// Insert the key-value pair
		result_t result = my_storage->kv_upsert(user, pass, key, val, [&]() {
			mru->insert(key);
		});

		if (!result.succeeded) {
			return result;
		}

		// Check download quota
		quota_result = check_quota(user, result.data.size(), quota->uploads, RES_ERR_QUOTA_DOWN);
		if (!quota_result.succeeded) {
			return quota_result;
		}
		return result;
	};

	/// Return all of the keys in the kv_store, as a "\n"-delimited string
	///
	/// @param user The name of the user who made the request
	/// @param pass The password for the user, used to authenticate
	///
	/// @return A result tuple, as described in storage.h
	virtual result_t kv_all(const string &user, const string &pass) {
		// Authenticate
		result_t auth_result = my_storage->auth(user, pass);
		if (!auth_result.succeeded) {
			return auth_result;
		}

		ensure_user_quota(user);

		Quotas *quota = getQuota(user);
		if (!quota) {
			return {false, RES_ERR_SERVER, {}};
		}

		// Check request quota
		result_t quota_result = check_quota(user, 1, quota->requests, RES_ERR_QUOTA_REQ);
		if (!quota_result.succeeded) {
			return quota_result;
		}

		result_t res = my_storage->kv_all(user, pass, []() {});
		if (!res.succeeded) {
			return res;
		}

		// Check download quota
		quota_result = check_quota(user, res.data.size(), quota->downloads, RES_ERR_QUOTA_DOWN);
		if (!quota_result.succeeded) {
			return quota_result;
		}

		return res;
	};

	/// Return all of the keys in the kv_store's MRU cache, as a "\n"-delimited
	/// string
	///
	/// @param user The name of the user who made the request
	/// @param pass The password for the user, used to authenticate
	///
	/// @return A result tuple, as described in storage.h
	virtual result_t kv_top(const string &user, const string &pass) {
		// Authenticate the user
		result_t auth_result = my_storage->auth(user, pass);
		if (!auth_result.succeeded) {
			return auth_result;
		}

		// Ensure that the user has quotas
		ensure_user_quota(user);

		// Get the user's quotas
		Quotas *quota = getQuota(user);
		if (!quota) {
			return {false, RES_ERR_SERVER, {}};
		}

		// Check request quota
		result_t quota_result = check_quota(user, 1, quota->requests, RES_ERR_QUOTA_REQ);
		if (!quota_result.succeeded) {
			return quota_result;
		}

		// Retrieve MRU keys
		string keys = mru->get();
		vector<uint8_t> response(keys.begin(), keys.end());

		// Check download quota
		size_t data_size = keys.size();
		quota_result = check_quota(user, data_size, quota->downloads, RES_ERR_QUOTA_DOWN);
		if (!quota_result.succeeded) {
			return quota_result;
		}

		return {true, RES_OK, response};
	}


	/// Shut down the gatekeeper when the server stops.  This method needs to
	/// close any open files related to incremental persistence.  It also needs to
	/// clean up any state related to .so files.  This is only called when all
	/// threads have stopped accessing the Gatekeeper object.
	virtual void shutdown() { my_storage->shutdown(); }

	/// Write the entire Gatekeeper object to the file specified by this.filename.
	/// To ensure durability, Gatekeeper must be persisted in two steps.  First,
	/// it must be written to a temporary file (this.filename.tmp).  Then the
	/// temporary file can be renamed to replace the older version of the
	/// Gatekeeper object.
	///
	/// @return A result tuple, as described in storage.h
	virtual result_t save_file() { return my_storage->save_file(); }

	/// Populate the Gatekeeper object by loading this.filename.  Note that load()
	/// begins by clearing the maps, so that when the call is complete, exactly
	/// and only the contents of the file are in the Gatekeeper object.
	///
	/// @return A result tuple, as described in storage.h.  Note that a
	///         non-existent file is not an error.
	virtual result_t load_file() { return my_storage->load_file(); }
};

/// Create an empty Gatekeeper object and specify the file from which it should
/// be loaded.  To avoid exceptions and errors in the constructor, the act of
/// loading data is separate from construction.
///
/// @param fname   The name of the file to use for persistence
/// @param buckets The number of buckets in the hash table
/// @param upq     The upload quota
/// @param dnq     The download quota
/// @param rqq     The request quota
/// @param qd      The quota duration
/// @param top     The size of the "top keys" cache
Gatekeeper *gatekeeper_factory(const std::string &fname, size_t buckets,
                               size_t upq, size_t dnq, size_t rqq, double qd,
                               size_t top) {
  return new MyGatekeeper(fname, buckets, upq, dnq, rqq, qd, top);
}
