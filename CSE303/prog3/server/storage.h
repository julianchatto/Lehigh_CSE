#pragma once

#include <memory>
#include <string>
#include <utility>
#include <vector>

using namespace std;

/// Storage is an interface that describes the main object that our server will
/// use. In general, it is not good to use polymorphism (virtual methods) in
/// C++. Furthermore, our program really doesn't need polymorphism. The reason
/// we have it is as a convenience for the professor and students. The students
/// benefit when the professor can share compiled `.o` files for the various
/// phases of the assignment, so that they can get full credit even if they did
/// not complete previous assignments. The instructor benefits if no source
/// code solutions are provided, since that reduces inter-semester cheating. The
/// cost is performance... every call to a method of Storage will have vtable
/// indirection overhead.
///
/// Storage provides access to three concurrent maps. The first is an
/// authentication table. The authentication table holds user names, salts, and
/// hashed passwords, as well as a single vector of content per user. The
/// second is a key/value store that can be accessed by all threads. The third
/// is used for managing quotas.
///
/// The public interface of Storage provides functions that correspond 1:1 with
/// the data requests that a client can make. In that manner, the server
/// command handlers need only parse a request, send its parts to the Storage
/// object, and then format and return the result.
///
/// Note that the functions that correspond to client requests all return a
/// tuple, consisting of a bool, a string, and a vector. When the bool is
/// *false*, it means that the operation did not succeed, and the string is an
/// error message that can be sent to the client. When the bool is *true*, it
/// means that the operation succeeded. In this case the string is probably
/// RES_OK, and the vector, if not empty, is additional data to send to the
/// client.
class Storage {
public:
	/// Result_t is the tuple that is sent to the caller after any operation
	/// requested by a client.
	struct result_t {
		bool succeeded;  // True if the operation succeeded, false otherwise
		string msg; // The message to send to the client
		vector<uint8_t> data; // Optional additional content to send
	};

	/// Destructor for the storage object.
	virtual ~Storage() {}

	/// Populate the Storage object by loading the filename that was provided to
	/// the constructor. Note that load_file() begins by clearing the maps, so
	/// that when the call is complete, exactly and only the contents of the file
	/// are in the Storage object.
	///
	/// @return A result tuple, as described above. Note that a non-existent file
	///         is not an error.
	virtual result_t load_file() = 0;

	/// Create a new entry in the Auth table. If the user already exists, return
	/// an error. Otherwise, create a salt, hash the password, and then save an
	/// entry with the username, salt, hashed password, and a zero-byte content.
	///
	/// @param user The user name to register
	/// @param pass The password to associate with that user name
	///
	/// @return A result tuple, as described above
	virtual result_t add_user(const string &user, const string &pass) = 0;

	/// Set the data bytes for a user, but do so if and only if the password
	/// matches
	///
	/// @param user    The name of the user whose content is being set
	/// @param pass    The password for the user, used to authenticate
	/// @param content The data to set for this user
	///
	/// @return A result tuple, as described above
	virtual result_t set_user_data(const string &user, const string &pass, const vector<uint8_t> &content) = 0;

	/// Return a copy of the user data for a user, but do so only if the password
	/// matches
	///
	/// @param user The name of the user who made the request
	/// @param pass The password for the user, used to authenticate
	/// @param who  The name of the user whose content is being fetched
	///
	/// @return A result tuple, as described above. Note that "no data" is an
	///         error
	virtual result_t get_user_data(const string &user, const string &pass, const string &who) = 0;

	/// Return a newline-delimited string containing all of the usernames in the
	/// auth table
	///
	/// @param user The name of the user who made the request
	/// @param pass The password for the user, used to authenticate
	///
	/// @return A result tuple, as described above
	virtual result_t get_all_users(const string &user, const string &pass) = 0;

	/// Authenticate a user
	///
	/// @param user The name of the user who made the request
	/// @param pass The password for the user, used to authenticate
	///
	/// @return A result tuple, as described above
	virtual result_t auth(const string &user, const string &pass) = 0;

	/// Write the entire Storage object to the filename that was provided to the
	/// constructor. To ensure durability, Storage must be persisted in two
	/// steps. First, it must be written to a temporary file (this.filename.tmp).
	/// Then the temporary file can be renamed to replace the older version of the
	/// Storage object.
	///
	/// @return A result tuple, as described above
	virtual result_t save_file() = 0;

	/// Create a new key/value mapping in the table
	///
	/// @param user The name of the user who made the request
	/// @param pass The password for the user, used to authenticate
	/// @param key  The key whose mapping is being created
	/// @param val  The value to copy into the map
	///
	/// @return A result tuple, as described above
	virtual result_t kv_insert(const string &user, const string &pass, const string &key, const vector<uint8_t> &val) = 0;

	/// Get a copy of the value to which a key is mapped
	///
	/// @param user The name of the user who made the request
	/// @param pass The password for the user, used to authenticate
	/// @param key  The key whose value is being fetched
	///
	/// @return A result tuple, as described above
	virtual result_t kv_get(const string &user, const string &pass, const string &key) = 0;

	/// Delete a key/value mapping
	///
	/// @param user The name of the user who made the request
	/// @param pass The password for the user, used to authenticate
	/// @param key  The key whose value is being deleted
	///
	/// @return A result tuple, as described above
	virtual result_t kv_delete(const string &user, const string &pass, const string &key) = 0;

	/// Insert or update, so that the given key is mapped to the give value
	///
	/// @param user The name of the user who made the request
	/// @param pass The password for the user, used to authenticate
	/// @param key  The key whose mapping is being upserted
	/// @param val  The value to copy into the map
	///
	/// @return A result tuple, as described above. Note that there are two "OK"
	///         messages, depending on whether we get an insert or an update.
	virtual result_t kv_upsert(const string &user, const string &pass, const string &key, const vector<uint8_t> &val) = 0;

	/// Return all of the keys in the kv_store, as a "\n"-delimited string
	///
	/// @param user The name of the user who made the request
	/// @param pass The password for the user, used to authenticate
	///
	/// @return A result tuple, as described above
	virtual result_t kv_all(const string &user, const string &pass) = 0;

	/// Shut down the storage when the server stops. This method needs to close
	/// any open files related to incremental persistence. It also needs to clean
	/// up any state related to .so files. This is only called when all threads
	/// have stopped accessing the Storage object.
	virtual void shutdown() = 0;
};

/// Create an empty Storage object and specify the name of the file that should
/// be used for loading/storing. To avoid exceptions and errors in the
/// constructor, the act of loading data is separate from construction.
///
/// @param fname   The name of the file to use for persistence
/// @param buckets The number of buckets in the hash table
Storage *storage_factory(const string &fname, size_t buckets);
