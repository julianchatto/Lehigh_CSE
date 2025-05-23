#pragma once

#include <openssl/pem.h>
#include <vector>

#include "../common/constants.h"

#include "gatekeeper.h"

using namespace std;

/// In response to a request for a key, do a reliable send of the contents of
/// the pubfile
///
/// @param sd      The socket on which to write the pubfile
/// @param pubfile A vector consisting of pubfile contents
///
/// @return false, to indicate that the server shouldn't stop
bool handle_key(int sd, const vector<uint8_t> &pubfile);

/// Respond to an ALL command by generating a list of all the usernames in the
/// Auth table and returning them, one per line.
///
/// @param sd      The socket onto which the result should be written
/// @param storage The (Gatekeeper) Storage object, which contains the auth
///                table
/// @param ctx     The AES encryption context
/// @param u       The user name associated with the request
/// @param p       The password associated with the request
/// @param req     The unencrypted contents of the request
///
/// @return false, to indicate that the server shouldn't stop
bool handle_all(int sd, Gatekeeper *storage, EVP_CIPHER_CTX *ctx, const string &u, const string &p, const vector<uint8_t> &req);

/// Respond to a SET command by putting the provided data into the Auth table
///
/// @param sd      The socket onto which the result should be written
/// @param storage The (Gatekeeper) Storage object, which contains the auth
///                table
/// @param ctx     The AES encryption context
/// @param u       The user name associated with the request
/// @param p       The password associated with the request
/// @param req     The unencrypted contents of the request
///
/// @return false, to indicate that the server shouldn't stop
bool handle_set(int sd, Gatekeeper *storage, EVP_CIPHER_CTX *ctx, const string &u, const string &p, const vector<uint8_t> &req);

/// Respond to a GET command by getting the data for a user
///
/// @param sd      The socket onto which the result should be written
/// @param storage The (Gatekeeper) Storage object, which contains the auth
///                table
/// @param ctx     The AES encryption context
/// @param u       The user name associated with the request
/// @param p       The password associated with the request
/// @param req     The unencrypted contents of the request
///
/// @return false, to indicate that the server shouldn't stop
bool handle_get(int sd, Gatekeeper *storage, EVP_CIPHER_CTX *ctx, const string &u, const string &p, const vector<uint8_t> &req);

/// Respond to a REG command by trying to add a new user
///
/// @param sd      The socket onto which the result should be written
/// @param storage The (Gatekeeper) Storage object, which contains the auth
///                table
/// @param ctx     The AES encryption context
/// @param u       The user name associated with the request
/// @param p       The password associated with the request
/// @param req     The unencrypted contents of the request
///
/// @return false, to indicate that the server shouldn't stop
bool handle_reg(int sd, Gatekeeper *storage, EVP_CIPHER_CTX *ctx, const string &u, const string &p, const vector<uint8_t> &req);

/// Respond to a BYE command by returning false, but only if the user
/// authenticates
///
/// @param sd      The socket onto which the result should be written
/// @param storage The (Gatekeeper) Storage object, which contains the auth
///                table
/// @param ctx     The AES encryption context
/// @param u       The user name associated with the request
/// @param p       The password associated with the request
/// @param req     The unencrypted contents of the request
///
/// @return true, to indicate that the server should stop, or false on an error
bool handle_bye(int sd, Gatekeeper *storage, EVP_CIPHER_CTX *ctx, const string &u, const string &p, const vector<uint8_t> &req);

/// Respond to a SAV command by persisting the file, but only if the user
/// authenticates
///
/// @param sd      The socket onto which the result should be written
/// @param storage The (Gatekeeper) Storage object, which contains the auth
///                table
/// @param ctx     The AES encryption context
/// @param u       The user name associated with the request
/// @param p       The password associated with the request
/// @param req     The unencrypted contents of the request
///
/// @return false, to indicate that the server shouldn't stop
bool handle_sav(int sd, Gatekeeper *storage, EVP_CIPHER_CTX *ctx, const string &u, const string &p, const vector<uint8_t> &req);

/// Respond to a KVA command by generating a list of all the keys in the
/// KV Store and returning them, one per line.
///
/// @param sd      The socket onto which the result should be written
/// @param storage The (Gatekeeper) Storage object
/// @param ctx     The AES encryption context
/// @param u       The user name associated with the request
/// @param p       The password associated with the request
/// @param req     The unencrypted contents of the request
///
/// @return false, to indicate that the server shouldn't stop
bool handle_kva(int sd, Gatekeeper *storage, EVP_CIPHER_CTX *ctx, const string &u, const string &p, const vector<uint8_t> &req);

/// Respond to a KVI command by putting the provided key/value pair into the KV
/// Store only if it doesn't already exist
///
/// @param sd      The socket onto which the result should be written
/// @param storage The (Gatekeeper) Storage object, which contains the auth
///                table
/// @param ctx     The AES encryption context
/// @param u       The user name associated with the request
/// @param p       The password associated with the request
/// @param req     The unencrypted contents of the request
///
/// @return false, to indicate that the server shouldn't stop
bool handle_kvi(int sd, Gatekeeper *storage, EVP_CIPHER_CTX *ctx, const string &u, const string &p, const vector<uint8_t> &req);

/// Respond to a KVU command by putting the provided key/value pair into the KV
/// Store, or updating if the key is already present
///
/// @param sd      The socket onto which the result should be written
/// @param storage The (Gatekeeper) Storage object, which contains the auth
///                table
/// @param ctx     The AES encryption context
/// @param u       The user name associated with the request
/// @param p       The password associated with the request
/// @param req     The unencrypted contents of the request
///
/// @return false, to indicate that the server shouldn't stop
bool handle_kvu(int sd, Gatekeeper *storage, EVP_CIPHER_CTX *ctx, const string &u, const string &p, const vector<uint8_t> &req);

/// Respond to a KVG command by getting the data for a key
///
/// @param sd      The socket onto which the result should be written
/// @param storage The (Gatekeeper) Storage object, which contains the auth
///                table
/// @param ctx     The AES encryption context
/// @param u       The user name associated with the request
/// @param p       The password associated with the request
/// @param req     The unencrypted contents of the request
///
/// @return false, to indicate that the server shouldn't stop
bool handle_kvg(int sd, Gatekeeper *storage, EVP_CIPHER_CTX *ctx, const string &u, const string &p, const vector<uint8_t> &req);

/// Respond to a KVD command by deleting a key/value mapping
///
/// @param sd      The socket onto which the result should be written
/// @param storage The (Gatekeeper) Storage object, which contains the auth
///                table
/// @param ctx     The AES encryption context
/// @param u       The user name associated with the request
/// @param p       The password associated with the request
/// @param req     The unencrypted contents of the request
///
/// @return false, to indicate that the server shouldn't stop
bool handle_kvd(int sd, Gatekeeper *storage, EVP_CIPHER_CTX *ctx, const string &u, const string &p, const vector<uint8_t> &req);

/// Respond to a KVT command by generating the list of most recent keys used in
/// kv_store operations, and returning them, one per line.
///
/// @param sd      The socket onto which the result should be written
/// @param storage The (Gatekeeper) Storage object
/// @param ctx     The AES encryption context
/// @param u       The user name associated with the request
/// @param p       The password associated with the request
/// @param req     The unencrypted contents of the request
///
/// @return false, to indicate that the server shouldn't stop
bool handle_kvt(int sd, Gatekeeper *storage, EVP_CIPHER_CTX *ctx, const string &u, const string &p, const vector<uint8_t> &req);