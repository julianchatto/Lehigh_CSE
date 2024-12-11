/// This file defines the constants used throughout the program.  For details
/// about the message and file formats that use these constants, please see the
/// README file.

#pragma once

#include <openssl/sha.h>
#include <string>

using namespace std;

/// Maximum length of a user name
const int LEN_UNAME = 32;

/// Maximum length of a user's actual password
const int LEN_PASSWORD = 32;

/// Maximum length of a hashed password
const int LEN_PASSHASH = SHA256_DIGEST_LENGTH;

/// Maximum length of a user's profile file
const int LEN_PROFILE_FILE = 1048576;

/// Max length of a block before RSA encryption
const int LEN_RBLOCK_CONTENT = 128;

/// Length of a block after RSA encryption
const int LEN_RKBLOCK = 256;

/// Length of an RSA public key
const int LEN_RSA_PUBKEY = 451; // NB: Defined by OpenSSL

/// Length of salt
const int LEN_SALT = 16;

/// size of an RSA key
const int RSA_KEYSIZE = 2048;

/// size of an AES key
const int AES_KEYSIZE = 32;

/// size of an AES initialization vector
const int AES_IVSIZE = 16;

/// Request the server's public key
const string REQ_KEY = "KEY_";

/// Request the creation of a new user
const string REQ_REG = "REG_";

/// Force the server to stop
const string REQ_BYE = "EXIT";

/// Force the server to send all its data to disk
const string REQ_SAV = "SAVE";

/// Allow a user to set their profile content
const string REQ_SET = "SETP";

/// Allow a user to request a user's profile content
const string REQ_GET = "GETP";

/// Get all user names
const string REQ_ALL = "ALL_";

/// Response code to indicate that the command was successful
const string RES_OK = "OK__";

/// Response code to indicate that the registered user already exists
const string RES_ERR_USER_EXISTS = "ERR_USER_EXISTS";

/// Response code to indicate that the client gave a bad username or password
const string RES_ERR_LOGIN = "ERR_LOGIN";

/// Response code to indicate that the client request was improperly formatted
const string RES_ERR_REQ_FMT = "ERR_REQ_FMT";

/// Response code to indicate that there is no data to send back
const string RES_ERR_NO_DATA = "ERR_NO_DATA";

/// Response code to indicate that the user being looked up is invalid
const string RES_ERR_NO_USER = "ERR_NO_USER";

/// Response code to indicate that the requested command doesn't exist
const string RES_ERR_INV_CMD = "ERR_INVALID_COMMAND";

/// Response code to indicate that the client didn't get as much data as
/// expected
const string RES_ERR_XMIT = "ERR_XMIT";

/// Response code to indicate that the client data can't be decrypted with the
/// provided AES key
const string RES_ERR_CRYPTO = "ERR_CRYPTO";

/// Response code to indicate that the server had an internal error, such as a
/// bad read from a file, error creating a salt, or failure to fork()
const string RES_ERR_SERVER = "ERR_SERVER";

/// Response code to indicate that something has not been implemented
const string RES_ERR_UNIMPLEMENTED = "ERR_UNIMPLEMENTED";

/// A unique 4-byte code to use as a prefix each time an AuthTable Entry is
/// written to disk.
const string AUTHENTRY = "AUTH";
