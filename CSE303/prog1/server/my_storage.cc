#include <cassert>
#include <cstdio>
#include <cstring>
#include <functional>
#include <iostream>
#include <memory>
#include <openssl/rand.h>
#include <openssl/sha.h>
#include <openssl/evp.h>
#include <string>
#include <unistd.h>
#include <utility>
#include <vector>
#include <fstream>

#include "../common/constants.h"
#include "../common/contextmanager.h"
#include "../common/err.h"
#include "../common/file.h"

#include "authtableentry.h"
#include "map.h"
#include "map_factories.h"
#include "storage.h"

using namespace std;

/// MyStorage is the student implementation of the Storage class
class MyStorage : public Storage {
	/// The map of authentication information, indexed by username
	Map<string, AuthTableEntry> *auth_table;

	/// The name of the file from which the Storage object was loaded, and to
	/// which we persist the Storage object every time it changes
	const string filename;

public:
	/// Construct an empty object and specify the file from which it should be
	/// loaded.  To avoid exceptions and errors in the constructor, the act of
	/// loading data is separate from construction.
	///
	/// @param fname   The name of the file to use for persistence
	/// @param buckets The number of buckets in the hash table
	MyStorage(const string &fname, size_t buckets)
		: auth_table(authtable_factory(buckets)), filename(fname) {}

	/// Destructor for the storage object.
	virtual ~MyStorage() {
		delete auth_table;
	}

	/// Create a SHA-256 hash of the provided salt and password. If the salt is 
	/// not the correct length, return an empty vector. If the hash cannot be
	/// created, return an empty vector.
	/// 
	/// @param salt The salt to use in the hash
	/// @param pass The password to hash
	/// @return A vector containing the SHA-256 hash of the salt and password
	virtual vector<uint8_t> SHA(vector<uint8_t> salt, const string &pass) {
		vector<uint8_t> pass_hash(LEN_PASSHASH);

		// Initialize OpenSSL EVP digest context
		EVP_MD_CTX *mdctx = EVP_MD_CTX_new();
		if (mdctx == nullptr) {
			EVP_MD_CTX_free(mdctx); // Free the OpenSSL digest context (in case of error)
			return {};
		}

		// Initialize the SHA-256 hashing context
		if (EVP_DigestInit_ex(mdctx, EVP_sha256(), nullptr) != 1) {
			EVP_MD_CTX_free(mdctx); // Free the OpenSSL digest context (in case of error)
			return {};
		}

		// Update the hashing context with the password
		if (EVP_DigestUpdate(mdctx, pass.c_str(), pass.length()) != 1) {
			EVP_MD_CTX_free(mdctx); // Free the OpenSSL digest context (in case of error)
			return {};
		}

		// Check the length of the salt
		if (salt.size() != LEN_SALT) {
			EVP_MD_CTX_free(mdctx); // Free the OpenSSL digest context (in case of error)
			return {};
		}

		// Update the hashing context with the salt
		if (EVP_DigestUpdate(mdctx, salt.data(), LEN_SALT) != 1) {
			EVP_MD_CTX_free(mdctx); // Free the OpenSSL digest context (in case of error)
			return {};
		}

		// Finalize the hash
		unsigned int len = LEN_PASSHASH;
		if (EVP_DigestFinal_ex(mdctx, pass_hash.data(), &len) != 1) {
			EVP_MD_CTX_free(mdctx); // Free the OpenSSL digest context (in case of error)
			return {};
		}

		// Free the OpenSSL digest context
		EVP_MD_CTX_free(mdctx);

		return pass_hash;
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
		// Check if the user already exists
		if (auth_table->do_with(user, [](AuthTableEntry &entry) {}) == true) {
			return {false, RES_ERR_USER_EXISTS, {}};
		}

		// Generate a random salt
		vector<uint8_t> salt(LEN_SALT);
		if (RAND_bytes(salt.data(), LEN_SALT) != 1) {
			return {false, RES_ERR_SERVER, {}};
		}

		// Hash the password
		vector<uint8_t> pass_hash = SHA(salt, pass);
		if (pass_hash.empty()) {
			return {false, RES_ERR_SERVER, {}};
		}

		// Insert the new user into the auth table
		if (!auth_table->insert(user, {user, salt, pass_hash, {}})) {
			return {false, RES_ERR_SERVER, {}};
		}

		return {true, RES_OK, {}};
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
		// Check if user exists
		AuthTableEntry entry;
		if (!auth_table->do_with_readonly(user, [&entry](const AuthTableEntry &e) { entry = e; })) {
			return {false, RES_ERR_LOGIN, {}};
		}

		// Authenticate the user
		result_t res = auth(user, pass);
		if (!res.succeeded) {
			return {false, RES_ERR_LOGIN, {}};
		}

		// Check if the content is too long
		if (content.size() > LEN_PROFILE_FILE) {
			return {false, RES_ERR_REQ_FMT, {}};
		}
		
		// Update user
		entry.content = content;
		auth_table->upsert(user, entry);

		return {true, RES_OK, {}};
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
		// Authenticate the user
		result_t res = auth(user, pass);
		if (!res.succeeded) {
			return res;
		}

		// Get the user's data
		AuthTableEntry entry;
		if (!auth_table->do_with_readonly(who, [&entry](const AuthTableEntry &e) { entry = e; })) {
			return {false, RES_ERR_NO_USER, {}};
		}

		// Check if the user has data / exists
		if (entry.content.empty()) {
			return {false, RES_ERR_NO_DATA, {}};
		}

		return {true, RES_OK, entry.content};
	}

	/// Return a newline-delimited string containing all of the usernames in the
	/// auth table
	///
	/// @param user The name of the user who made the request
	/// @param pass The password for the user, used to authenticate
	///
	/// @return A result tuple, as described in storage.h
	virtual result_t get_all_users(const string &user, const string &pass) {
		AuthTableEntry entry;
		if (!auth_table->do_with_readonly(user, [&entry](const AuthTableEntry &e) { entry = e; })) {
			return {false, RES_ERR_LOGIN, {}};
		}

		result_t res = auth(user, pass);
		
		if (!res.succeeded) {
			return {false, res.msg, {}};
		}

		string users;
		auth_table->do_all_readonly([&users](const string &key, const AuthTableEntry &value) { users += key + "\n"; });

		return {true, RES_OK, vector<uint8_t>(users.begin(), users.end())};
	}

	/// Authenticate a user
	///
	/// @param user The name of the user who made the request
	/// @param pass The password for the user, used to authenticate
	///
	/// @return A result tuple, as described in storage.h
	virtual result_t auth(const string &user, const string &pass) {
		// Check if the user exists
		AuthTableEntry entry;
		if (!auth_table->do_with_readonly(user, [&entry](const AuthTableEntry &e) { entry = e; })) {
			return {false, RES_ERR_LOGIN, {}};
		}

		// Hash the password
		vector<uint8_t> pass_hash = SHA(entry.salt, pass);
		if (pass_hash.empty()) {
			return {false, RES_ERR_LOGIN, {}};
		}

		// Compare the hashes
		if (memcmp(pass_hash.data(), entry.pass_hash.data(), LEN_PASSHASH) != 0) {
			return {false, RES_ERR_LOGIN, {}};
		}

		return {true, RES_OK, entry.content};
	}
	
	/// Write the entire Storage object to the file specified by this.filename. To
	/// ensure durability, Storage must be persisted in two steps.  First, it must
	/// be written to a temporary file (this.filename.tmp).  Then the temporary
	/// file can be renamed to replace the older version of the Storage object.
	/// @return A result tuple, as described in storage.h
	virtual result_t save_file() {
		vector<uint8_t> data;

		// Iterate through all users
		auth_table->do_all_readonly([&data](const string &key, const AuthTableEntry &value) {
			data.insert(data.end(), AUTHENTRY.begin(), AUTHENTRY.end());

			// Write the lengths of the fields
			uint32_t lengths[4] = {static_cast<uint32_t>(value.username.size()), static_cast<uint32_t>(value.salt.size()), static_cast<uint32_t>(value.pass_hash.size()), static_cast<uint32_t>(value.content.size())};
			for (size_t i = 0; i < 4; i++) {
				uint8_t *length = reinterpret_cast<uint8_t*>(&lengths[i]);
				data.insert(data.end(), length, length + sizeof(uint32_t));
			}
			
			// Write the fields
			data.insert(data.end(), key.begin(), key.end());
			data.insert(data.end(), value.salt.begin(), value.salt.end());
			data.insert(data.end(), value.pass_hash.begin(), value.pass_hash.end());
			data.insert(data.end(), value.content.begin(), value.content.end());

			// Write padding
			size_t padding = (4 - (data.size() % 4)) % 4;
			data.insert(data.end(), padding, '0');
		});
		
		// write to the file
		if (!write_file(filename + ".tmp", data, 0)) {
			return {false, RES_ERR_SERVER, {}};
		}

		// rename the file
		if (rename((filename + ".tmp").c_str(), filename.c_str()) != 0) {
			return {false, RES_ERR_SERVER, {}};
		}

		return {true, RES_OK, {}};
	}

	/// Populate the Storage object by loading this.filename. Note that load()
	/// begins by clearing the maps, so that when the call is complete, exactly
	/// and only the contents of the file are in the Storage object.
	/// @return A result tuple, as described in storage.h.  Note that a
	///         non-existent file is not an error.
	virtual result_t load_file() {
		auth_table->clear(); // clear the table

		FILE *file = fopen(filename.c_str(), "rb");
		if (file == nullptr) {
			return {true, "File not found: " + filename, {}};
		}

		vector<uint8_t> data = load_entire_file(filename); // load the file

		size_t i = 0, fileSize = data.size();
		while (i < fileSize) {
			// Check for the AUTHENTRY prefix
			if (fileSize - i < 4 || memcmp(&data[i], "AUTH", 4) != 0) {
				return {false, RES_ERR_SERVER, {}};  
			}
			i += 4;

			// Read the lengths (4 bytes each)
			if (fileSize - i < 16) {
				return {false, RES_ERR_SERVER, {}};
			}
			uint32_t lengths[4];
			memcpy(&lengths, &data[i], 16);
			i += 16;

			// username
			if (fileSize - i < lengths[0]) {
				return {false, RES_ERR_SERVER, {}};
			}
			string username(data.begin() + i, data.begin() + i + lengths[0]);
			i += lengths[0];

			// salt
			if (fileSize - i < lengths[1]) {
				return {false, RES_ERR_SERVER, {}};
			}
			vector<uint8_t> salt(data.begin() + i, data.begin() + i + lengths[1]);
			i += lengths[1];

			// pass_hash
			if (fileSize - i < lengths[2]) {
				return {false, RES_ERR_SERVER, {}};
			}
			vector<uint8_t> pass_hash(data.begin() + i, data.begin() + i + lengths[2]);
			i += lengths[2];

			// content
			if (fileSize - i < lengths[3]) {
				return {false, RES_ERR_SERVER, {}};
			}
			vector<uint8_t> content(data.begin() + i, data.begin() + i + lengths[3]);
			i += lengths[3];

			// padding
			while (i < fileSize && data[i] == '0') { i++; }

			auth_table->insert(username, {username, salt, pass_hash, content});
		}

		// Return success
		return {true, "Loaded: " + filename, {}};
	}
};

/// Create an empty Storage object and specify the file from which it should be
/// loaded.  To avoid exceptions and errors in the constructor, the act of
/// loading data is separate from construction.
///
/// @param fname   The name of the file to use for persistence
/// @param buckets The number of buckets in the hash table
Storage *storage_factory(const string &fname, size_t buckets) {
	return new MyStorage(fname, buckets);
}
