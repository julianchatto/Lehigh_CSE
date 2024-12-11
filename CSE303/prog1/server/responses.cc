#include <cassert>
#include <iostream>
#include <string>

#include "../common/constants.h"
#include "../common/crypto.h"
#include "../common/net.h"

#include "responses.h"
#include "storage.h"         

using namespace std;
using result_t = Storage::result_t;

/// Add the size of a value to a vector as a 4-byte value
///
/// @param res  The vector to add to
/// @param t    The thing whose size should be added
///
/// @tparam T   The type of t
template <class T> void add_size(vector<uint8_t> &res, T t){
    uint32_t size = static_cast<uint32_t>(t.size());
    res.push_back(size & 0xFF);
    res.push_back((size >> 8) & 0xFF);
    res.push_back((size >> 16) & 0xFF);
    res.push_back((size >> 24) & 0xFF);
}

/// Respond to an ALL command by generating a list of all the usernames in the
/// Auth table and returning them, one per line.
///
/// @param sd      The socket onto which the result should be written
/// @param storage The Storage object, which contains the auth table
/// @param ctx     The AES encryption context
/// @param u       The user name associated with the request
/// @param p       The password associated with the request
/// @param req     The unencrypted contents of the request
///
/// @return false, to indicate that the server shouldn't stop
//bool handle_all(int sd, Storage *storage, EVP_CIPHER_CTX *ctx, const string &u, const string &p, const vector<uint8_t> &) {
bool handle_all(int sd, Storage *storage, EVP_CIPHER_CTX *ctx, const string &u, const string &p, const vector<uint8_t> &) {
    result_t auth_result = storage->auth(u, p); // authenticate user 
    if (!auth_result.succeeded) {
		vector<uint8_t> encrypted_msg = aes_crypt_msg(ctx, auth_result.msg); // run error message
        send_reliably(sd, encrypted_msg);
        return false;
    }

    result_t all_users = storage->get_all_users(u,p); // get all users 
    if (!all_users.succeeded) {
        vector<uint8_t> encrypted_err = aes_crypt_msg(ctx, all_users.msg); // run error if could not get users 
        send_reliably(sd, encrypted_err);
        return false;
    }

    vector<uint8_t> vec_to_client;
    vec_to_client.insert(vec_to_client.end(), all_users.msg.begin(), all_users.msg.end()); // append msg 
    add_size(vec_to_client, all_users.data); // append len @l
	
    // append the size of all_users.data (as a single byte, assuming the size is small)
    vec_to_client.insert(vec_to_client.end(), all_users.data.begin(), all_users.data.end()); // append data which is list of users

    vector<uint8_t> encrypted_response = aes_crypt_msg(ctx, vec_to_client); // encrypt response 
    send_reliably(sd, encrypted_response); // send reliably to server 

    return false;	
}

/// Respond to a SET command by putting the provided data into the Auth table
///
/// @param sd      The socket onto which the result should be written
/// @param storage The Storage object, which contains the auth table
/// @param ctx     The AES encryption context
/// @param u       The user name associated with the request
/// @param p       The password associated with the request
/// @param req     The unencrypted contents of the request
///
/// @return false, to indicate that the server shouldn't stop
bool handle_set(int sd, Storage *storage, EVP_CIPHER_CTX *ctx, const string &u, const string &p, const vector<uint8_t> &req) {
	size_t len_f = *((uint32_t*)&req[0]); 
	vector<uint8_t> data_file = vector<uint8_t>(req.begin() + 4, req.begin() + 4 + len_f);

	// check if the user exists and if the password is correct
    result_t auth_result = storage->auth(u, p);
    if (!auth_result.succeeded) {
        vector<uint8_t> err_msg = aes_crypt_msg(ctx, auth_result.msg);
        send_reliably(sd, err_msg);
        return false;
    }

    // update the user's profile file in the storage
    result_t update_result = storage->set_user_data(u, p, data_file);
    if (!update_result.succeeded) {
        vector<uint8_t> encrypted_err = aes_crypt_msg(ctx, update_result.msg);
        send_reliably(sd, encrypted_err);
        return false;
    }

    // respond with success
    vector<uint8_t> success_msg = aes_crypt_msg(ctx, update_result.msg);
    send_reliably(sd, success_msg);

    return false; 
}

/// Respond to a GET command by getting the data for a user
///
/// @param sd      The socket onto which the result should be written
/// @param storage The Storage object, which contains the auth table
/// @param ctx     The AES encryption context
/// @param u       The user name associated with the request
/// @param p       The password associated with the request
/// @param req     The unencrypted contents of the request
///
/// @return false, to indicate that the server shouldn't stop
bool handle_get(int sd, Storage *storage, EVP_CIPHER_CTX *ctx, const string &u, const string &p, const vector<uint8_t> &req) {
	// parse a block 
    size_t len_w = *((uint32_t*)&req[0]);  // len @ l
    string requested_user(reinterpret_cast<const char*>(&req[4]), len_w); // @l

    // authenticate user 
    result_t auth_result = storage->auth(u, p);
    if (!auth_result.succeeded) {
        vector<uint8_t> err_msg = aes_crypt_msg(ctx, auth_result.msg);
        send_reliably(sd, err_msg);
        return false;
    }

    // retrieve the file 
    result_t data_result = storage->get_user_data(u, p, requested_user);
    if (!data_result.succeeded) {
        vector<uint8_t> err_msg = aes_crypt_msg(ctx, data_result.msg);
        send_reliably(sd, err_msg);
        return false;
    }

	// create vector to be sent which has len @ l. l
    vector<uint8_t> vec_to_client;
    vec_to_client.insert(vec_to_client.end(), data_result.msg.begin(), data_result.msg.end()); // msg 
    add_size(vec_to_client, data_result.data); // length
    vec_to_client.insert(vec_to_client.end(), data_result.data.begin(), data_result.data.end()); // file

    // encrypt and send 
    vector<uint8_t> encrypted_response = aes_crypt_msg(ctx, vec_to_client); 
    send_reliably(sd, encrypted_response); 

	return false;
}

/// Respond to a REG command by trying to add a new user
///
/// @param sd      The socket onto which the result should be written
/// @param storage The Storage object, which contains the auth table
/// @param ctx     The AES encryption context
/// @param u       The user name associated with the request
/// @param p       The password associated with the request
/// @param req     The unencrypted contents of the request
///
/// @return false, to indicate that the server shouldn't stop
bool handle_reg(int sd, Storage *storage, EVP_CIPHER_CTX *ctx, const string &u, const string &p, const vector<uint8_t> &) {
	// add new user 
    result_t add_user_result = storage->add_user(u, p);
    if (!add_user_result.succeeded) {
		vector<uint8_t> encrypted_err = aes_crypt_msg(ctx, add_user_result.msg);
		send_reliably(sd, encrypted_err);
		return false;
    }

    // add success message 
    vector<uint8_t> success_msg = aes_crypt_msg(ctx, add_user_result.msg);
    send_reliably(sd, success_msg);

    return false; 
}

/// In response to a request for a key, do a reliable send of the contents of
/// the pubFile
///
/// @param sd The socket on which to write the pubFile
/// @param pubFile A vector consisting of pubFile contents
///
/// @return false, to indicate that the server shouldn't stop
bool handle_key(int sd, const vector<uint8_t> &pubFile) {
	if (pubFile.size() != 451) { // check to make sure pubFile is right size 
        cerr << "Error: Public key size mismatch!" << endl;
        return true;
    }
	send_reliably(sd, pubFile); // send the file which contains key
	return false; 
}

/// Respond to a BYE command by returning false, but only if the user
/// authenticates
///
/// @param sd      The socket onto which the result should be written
/// @param storage The Storage object, which contains the auth table
/// @param ctx     The AES encryption context
/// @param u       The user name associated with the request
/// @param p       The password associated with the request
/// @param req     The unencrypted contents of the request
///
/// @return true, to indicate that the server should stop, or false on an error
bool handle_bye(int sd, Storage *storage, EVP_CIPHER_CTX *ctx, const string &u, const string &p, const vector<uint8_t> &) {
    // authenticate user 
    result_t auth_user = storage->auth(u, p);
    if (!auth_user.succeeded) {
        vector<uint8_t> encrypted_err = aes_crypt_msg(ctx, auth_user.msg);
        send_reliably(sd, encrypted_err);
        return false;
    } else {
        vector<uint8_t> success_msg = aes_crypt_msg(ctx, RES_OK); // send success message
        send_reliably(sd, success_msg);
        return true; // server stops
    }
}

/// Respond to a SAV command by persisting the file, but only if the user
/// authenticates
///
/// @param sd      The socket onto which the result should be written
/// @param storage The Storage object, which contains the auth table
/// @param ctx     The AES encryption context
/// @param u       The user name associated with the request
/// @param p       The password associated with the request
/// @param req     The unencrypted contents of the request
///
/// @return false, to indicate that the server shouldn't stop
bool handle_sav(int sd, Storage *storage, EVP_CIPHER_CTX *ctx, const string &u, const string &p, const vector<uint8_t> &) {
	// authenticate user 
    result_t auth_user = storage->auth(u, p);
    if (!auth_user.succeeded) {
        vector<uint8_t> encrypted_err = aes_crypt_msg(ctx, auth_user.msg);
        send_reliably(sd, encrypted_err);
        return false;
    }
    // save the file 
    result_t save_result = storage->save_file();
    if (!save_result.succeeded) {
        vector<uint8_t> encrypted_err = aes_crypt_msg(ctx, save_result.msg);
        send_reliably(sd, encrypted_err);
        return false; 
    }

    vector<uint8_t> success_msg = aes_crypt_msg(ctx, save_result.msg);
    send_reliably(sd, success_msg);

    return false;
}
