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
#include "map.h"
#include "map_factories.h"
#include "persist.h"
#include "storage.h"

using namespace std;

/// MyStorage is the student implementation of the Storage class
class MyStorage : public Storage {
	/// The map of authentication information, indexed by username
	Map<string, AuthTableEntry> *auth_table;

	/// The map of key/value pairs
	Map<string, vector<uint8_t>> *kv_store;

	/// The name of the file from which the Storage object was loaded, and to
	/// which we persist the Storage object every time it changes
	const string filename;

	/// The open file
	FILE *storage_file = nullptr;

public:
	/// Construct an empty object and specify the file from which it should be
	/// loaded. To avoid exceptions and errors in the constructor, the act of
	/// loading data is separate from construction.
	///
	/// @param fname   The name of the file to use for persistence
	/// @param buckets The number of buckets in the hash table
	MyStorage(const string &fname, size_t buckets) : auth_table(authtable_factory(buckets)), kv_store(kvstore_factory(buckets)), filename(fname) {
	}

	/// Destructor for the storage object.
	virtual ~MyStorage() {
		delete auth_table;
		delete kv_store;
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
	
	/// Create a new entry in the Auth table. If the user already exists, return
	/// an error. Otherwise, create a salt, hash the password, and then save an
	/// entry with the username, salt, hashed password, and a zero-byte content.
	///
	/// @param user The user name to register
	/// @param pass The password to associate with that user name
	///
	/// @return A result tuple, as described in storage.h
	virtual result_t add_user(const string &user, const string &pass) {
		// Check if the user already exists
		if (auth_table->do_with(user, [](AuthTableEntry &entry) {})) {
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

		AuthTableEntry entry{user, salt, pass_hash, {}};

		bool inserted = auth_table->insert(user, entry, [&]() {
			log_svv0(storage_file, AUTHENTRY, user, salt, pass_hash);
		});
	
		if (!inserted) {
			return {false, RES_ERR_USER_EXISTS, {}};
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
		
		function<void()> on_ins = [&]() {
			log_sv(storage_file, AUTHENTRY, user, content);
		};

		function<void()> on_upd = [&]() {
			log_sv(storage_file, AUTHDIFF, user, content);
		};

		// Update user
		entry.content = content;
		auth_table->upsert(user, entry
		, [&]() {
			log_sv(storage_file, AUTHENTRY, user, content);
		}, [&]() {
			log_sv(storage_file, AUTHDIFF, user, content);
		});

		return {true, RES_OK, {}};
	}

	/// Return a copy of the user data for a user, but do so only if the password
	/// matches
	///
	/// @param user The name of the user who made the request
	/// @param pass The password for the user, used to authenticate
	/// @param who  The name of the user whose content is being fetched
	///
	/// @return A result tuple, as described in storage.h. Note that "no data" is
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
		auth_table->do_all_readonly([&users](const string &key, const AuthTableEntry &value) { users += key + "\n"; }, []() {});

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

	/// Create a new key/value mapping in the table
	///
	/// @param user The name of the user who made the request
	/// @param pass The password for the user, used to authenticate
	/// @param key  The key whose mapping is being created
	/// @param val  The value to copy into the map
	///
	/// @return A result tuple, as described in storage.h
	virtual result_t kv_insert(const string &user, const string &pass, const string &key, const vector<uint8_t> &val) {
		AuthTableEntry entry;
		if (!auth_table->do_with_readonly(user, [&entry](const AuthTableEntry &e) { entry = e; })) {
			return {false, RES_ERR_LOGIN, {}};
		}

		result_t res = auth(user, pass);
		
		if (!res.succeeded) {
			return {false, res.msg, {}};
		}

		// make sure the key is not empty
		if (key.empty()) {
			return {false, RES_ERR_XMIT, {}};
		}

		// make sure key and value are not too long
		if (key.size() > LEN_KEY || val.size() > LEN_VAL) {
			return {false, RES_ERR_REQ_FMT, {}};
		}

		if (kv_store->insert(key, val
		, [&]() {
			log_sv(storage_file, KVENTRY, key, val);
		})) {
			return {true, RES_OK, {}};
		}

		return {false, RES_ERR_KEY, {}};
	};

	/// Get a copy of the value to which a key is mapped
	///
	/// @param user The name of the user who made the request
	/// @param pass The password for the user, used to authenticate
	/// @param key  The key whose value is being fetched
	///
	/// @return A result tuple, as described in storage.h
	virtual result_t kv_get(const string &user, const string &pass, const string &key) {
		result_t res = auth(user, pass);
		
		if (!res.succeeded) {
			return {false, res.msg, {}};
		}

		// Get the user's data
		vector<uint8_t> value;
		if (!kv_store->do_with_readonly(key, [&value](const vector<uint8_t> &v) { value = v; })) {
			return {false, RES_ERR_KEY, {}};
		}

		return {true, RES_OK, value};
	};

	/// Delete a key/value mapping
	///
	/// @param user The name of the user who made the request
	/// @param pass The password for the user, used to authenticate
	/// @param key  The key whose value is being deleted
	///
	/// @return A result tuple, as described in storage.h
	virtual result_t kv_delete(const string &user, const string &pass, const string &key) {
		result_t res = auth(user, pass);
		
		if (!res.succeeded) {
			return {false, res.msg, {}};
		}

		if (kv_store->remove(key
		, [&]() {
			log_s(storage_file, KVDELETE, key);
		})) {
			return {true, RES_OK, {}};
		}

		return {false, RES_ERR_KEY, {}};
	};

	/// Insert or update, so that the given key is mapped to the give value
	///
	/// @param user The name of the user who made the request
	/// @param pass The password for the user, used to authenticate
	/// @param key  The key whose mapping is being upserted
	/// @param val  The value to copy into the map
	///
	/// @return A result tuple, as described in storage.h. Note that there are
	/// two
	///         "OK" messages, depending on whether we get an insert or an update.
	virtual result_t kv_upsert(const string &user, const string &pass, const string &key, const vector<uint8_t> &val) {
		result_t res = auth(user, pass);
		
		if (!res.succeeded) {
			return {false, res.msg, {}};
		}

		// make sure the val is not too long
		if (val.size() > LEN_VAL) {
			return {false, RES_ERR_REQ_FMT, {}};
		}

		if (kv_store->upsert(key, val
		, [&]() {
			log_sv(storage_file, KVENTRY, key, val);
		}, [&]() {
			log_sv(storage_file, KVUPDATE, key, val);
		})) {
			return {true, RES_OKINS, {}};
		}

		return {true, RES_OKUPD, {}};
	};

	/// Return all of the keys in the kv_store, as a "\n"-delimited string
	///
	/// @param user The name of the user who made the request
	/// @param pass The password for the user, used to authenticate
	///
	/// @return A result tuple, as described in storage.h
	virtual result_t kv_all(const string &user, const string &pass) {
		result_t res = auth(user, pass);
		
		if (!res.succeeded) {
			return {false, res.msg, {}};
		}

		string keys;
		kv_store->do_all_readonly([&keys](const string &key, const vector<uint8_t> &value) { keys += key + "\n"; }, []() {});

		if (keys.empty()) {
			return {false, RES_ERR_NO_DATA, {}};
		}

		return {true, RES_OK, vector<uint8_t>(keys.begin(), keys.end())};
	};

	/// Shut down the storage when the server stops. This method needs to close
	/// any open files related to incremental persistence. It also needs to clean
	/// up any state related to .so files. This is only called when all threads
	/// have stopped accessing the Storage object.
	virtual void shutdown() {
		// close open files
		if (storage_file != nullptr) {
			fclose(storage_file);
		}		
	}

	/// Write the entire Storage object to the file specified by this.filename. To
	/// ensure durability, Storage must be persisted in two steps. First, it must
	/// be written to a temporary file (this.filename.tmp). Then the temporary
	/// file can be renamed to replace the older version of the Storage object.
	///
	/// @return A result tuple, as described in storage.h
	virtual result_t save_file() {
		// Create a temporary file
		string tmp_filename = filename + ".tmp";
		FILE *tmp_file = fopen(tmp_filename.c_str(), "wb");
		if (tmp_file == nullptr) {
			return {false, RES_ERR_SERVER, {}};
		}

		// Write the auth table
		auth_table->do_all_readonly([&tmp_file](const string &key, const AuthTableEntry &value) {
			// write the AUTHENTRY prefix
			fputs(AUTHENTRY.c_str(), tmp_file);

			// write the lengths of the fields
			uint32_t lengths[4] = {static_cast<uint32_t>(value.username.size()), static_cast<uint32_t>(value.salt.size()), static_cast<uint32_t>(value.pass_hash.size()), static_cast<uint32_t>(value.content.size())};
			fwrite(lengths, sizeof(uint32_t), 4, tmp_file);

			// write the fields
			fputs(key.c_str(), tmp_file);
			fwrite(value.salt.data(), 1, lengths[1], tmp_file);
			fwrite(value.pass_hash.data(), 1, lengths[2], tmp_file);
			fwrite(value.content.data(), 1, lengths[3], tmp_file);

			// write padding
			size_t padding = (4 - ((lengths[0] + lengths[1] + lengths[2] + lengths[3]) % 4)) % 4;
			if (padding > 0) {
				// Write padding if needed
				static const char paddingBuffer[4] = {0}; // 4-byte buffer of zeros
				fwrite(paddingBuffer, 1, padding, tmp_file);
			}

		}, []() {});

		// Write the kv store
		kv_store->do_all_readonly([&tmp_file](const string &key, const vector<uint8_t> &value) {
			// write the KVENTRY prefix
			fputs(KVENTRY.c_str(), tmp_file);

			// write the lengths of the fields
			uint32_t lengths[2] = {static_cast<uint32_t>(key.size()), static_cast<uint32_t>(value.size())};
			fwrite(lengths, sizeof(uint32_t), 2, tmp_file);

			// write the fields
			fputs(key.c_str(), tmp_file);
			fwrite(value.data(), 1, lengths[1], tmp_file);

			// write padding
			size_t padding = (4 - ((lengths[0] + lengths[1]) % 4)) % 4;
			if (padding > 0) {
				// Write padding if needed
				static const char paddingBuffer[4] = {0}; // 4-byte buffer of zeros
				fwrite(paddingBuffer, 1, padding, tmp_file);
			}

		}, []() {});

		// close the file
		fclose(tmp_file);

		// rename the file
		if (rename(tmp_filename.c_str(), filename.c_str()) != 0) {
			return {false, RES_ERR_SERVER, {}};
		}

		return {true, RES_OK, {}};
	}

	/// Steven style wrapper for fread
	///
	/// @param ptr the pointer to the buffer
	/// @param size the size of each element to read
	/// @param count the number of elements to read
	/// @param file the file to read from
	/// @return the number of bytes read
	virtual size_t Fread(void* ptr, size_t size, size_t count, FILE* file) {
		size_t bytesRead = fread(ptr, size, count, file);
		if (bytesRead != count) {
			throw runtime_error("Error reading file");
		}
		return bytesRead;
	}

	/// Reads a string from a file
	///
	/// @param file the file to read from
	/// @param length the length of the string
	/// @return a string
	virtual string getString(FILE* file, uint32_t length) {
		vector<char> buffer(length + 1); // +1 for null terminator
		Fread(buffer.data(), 1, length, file);
		buffer[length] = '\0'; // Null-terminate the string
		return string(buffer.data());
	}
	
	/// Handle padding in the file
	///
	/// @param file the file to read from
	virtual void handlePadding(FILE* file) {
		int c;
		while ((c = fgetc(file)) == 0x00) {}
		if (c != EOF) {
			ungetc(c, file); // Put back the non-padding byte
		}
	}

	/// Helper function to read an AUTHENTRY from the file
	///
	/// @param file the file to read from
	virtual void readAuthEntry(FILE* file) {
		// Read the lengths
		uint32_t lengths[4];
		Fread(lengths, sizeof(uint32_t), 4, file);

		// username
		string username = getString(file, lengths[0]);

		// Read salt
		vector<uint8_t> salt(lengths[1]);
		Fread(salt.data(), 1, lengths[1], file);
		
		// Read pass_hash
		vector<uint8_t> pass_hash(lengths[2]);
		Fread(pass_hash.data(), 1, lengths[2], file);

		// Read content
		vector<uint8_t> content(lengths[3]);
		Fread(content.data(), 1, lengths[3], file);

		handlePadding(file);

		auth_table->insert(username, {username, salt, pass_hash, content}, [](){});
	}

	/// Helper function to read a KVENTRY from the file
	///
	/// @param file the file to read from
	virtual void readKVEntry(FILE* file) {
		// Read the lengths
		uint32_t lengths[2];
		Fread(lengths, sizeof(uint32_t), 2, file);

		// key
		string key = getString(file, lengths[0]);

		// value
		vector<uint8_t> value(lengths[1]);
		Fread(value.data(), 1, lengths[1], file);

		handlePadding(file);

		kv_store->insert(key, value, [](){});
	}

	/// Helper function to read an AUTHDIFF from the file
	///
	/// @param file the file to read from
	virtual void readAuthDiff(FILE* file) {
		// Read the lengths
		uint32_t lengths[2];
		Fread(lengths, sizeof(uint32_t), 2, file);

		// username
		string username = getString(file, lengths[0]);

		// content
		vector<uint8_t> content(lengths[1]);
		Fread(content.data(), 1, lengths[1], file);

		handlePadding(file);

		// fetch current entry
		AuthTableEntry entry;
		auth_table->do_with_readonly(username, [&entry](const AuthTableEntry &e) { entry = e; });

		auth_table->upsert(username, {username, entry.salt, entry.pass_hash, content}, [](){}, [](){});
	}

	/// Helper function to read a KVUPDATE from the file
	///
	/// @param file the file to read from 
	virtual void readKVUpdate(FILE* file) {
		// Read the lengths
		uint32_t lengths[2];
		Fread(lengths, sizeof(uint32_t), 2, file);

		// key
		string key = getString(file, lengths[0]);

		// value
		vector<uint8_t> value(lengths[1]);
		Fread(value.data(), 1, lengths[1], file);

		handlePadding(file);

		kv_store->upsert(key, value, [](){}, [](){});
	}

	/// Helper function to read a KVDELETE from the file
	///
	/// @param file the file to read from
	virtual void readKVDelete(FILE* file) {
		// Read the length
		uint32_t length;
		Fread(&length, sizeof(uint32_t), 1, file);

		// key
		string key = getString(file, length);

		handlePadding(file);

		kv_store->remove(key, [](){});
	}
	
	/// Populate the Storage object by loading this.filename. Note that load()
	/// begins by clearing the maps, so that when the call is complete, exactly
	/// and only the contents of the file are in the Storage object.
	///
	/// @return A result tuple, as described in storage.h. Note that a
	///         non-existent file is not an error.
	virtual result_t load_file() {
		auth_table->clear(); // clear the table
		kv_store->clear(); // clear the table

		storage_file = fopen(filename.c_str(), "r+");
		if (storage_file == nullptr) {
			storage_file = fopen(filename.c_str(), "w");
			return {true, "File not found: " + filename, {}};
		}

		// read from the file directly 
		while (feof(storage_file) == 0) {
			// read the prefix
			char prefix[4];
			Fread(prefix, 1, 4, storage_file);

			if (memcmp(prefix, AUTHENTRY.c_str(), 4) == 0) {
				readAuthEntry(storage_file);
			} else if (memcmp(prefix, KVENTRY.c_str(), 4) == 0) {
				readKVEntry(storage_file);
			}  else if (memcmp(prefix, AUTHDIFF.c_str(), 4) == 0) {
				readAuthDiff(storage_file);
			}  else if (memcmp(prefix, KVUPDATE.c_str(), 4) == 0) {
				readKVUpdate(storage_file);
			}  else if (memcmp(prefix, KVDELETE.c_str(), 4) == 0) {
				readKVDelete(storage_file);
			} else {
				return {false, RES_ERR_SERVER, {}};
			}
		}
		
		// Return success
		return {true, "Loaded: " + filename, {}};
	}
};

/// Create an empty Storage object and specify the file from which it should be
/// loaded. To avoid exceptions and errors in the constructor, the act of
/// loading data is separate from construction.
///
/// @param fname   The name of the file to use for persistence
/// @param buckets The number of buckets in the hash table
Storage *storage_factory(const string &fname, size_t buckets) {
	return new MyStorage(fname, buckets);
}
