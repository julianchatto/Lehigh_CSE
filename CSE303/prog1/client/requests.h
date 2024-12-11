#pragma once

#include <openssl/rsa.h>
#include <string>

using namespace std;

/// req_key() writes a request for the server's key on a socket descriptor.
/// When it gets a key back, it writes it to a file.
///
/// @param sd      The open socket descriptor for communicating with the server
/// @param keyFile The name of the file to which the key should be written
void req_key(int sd, const string &keyFile);

// NB: The remaining function declarations have the same signature, so that we
//     can store pointers to them in an array

/// req_reg() sends the REG command to register a new user
///
/// @param sd      The open socket descriptor for communicating with the server
/// @param pubKey  The public key of the server
/// @param user    The name of the user doing the request
/// @param pass    The password of the user doing the request
void req_reg(int sd, EVP_PKEY *pubKey, const string &user, const string &pass, const string &);

/// req_bye() writes a request for the server to exit.
///
/// @param sd      The open socket descriptor for communicating with the server
/// @param pubKey  The public key of the server
/// @param user    The name of the user doing the request
/// @param pass    The password of the user doing the request
void req_bye(int sd, EVP_PKEY *pubKey, const string &user, const string &pass, const string &);

/// req_sav() writes a request for the server to save its contents
///
/// @param sd      The open socket descriptor for communicating with the server
/// @param pubKey  The public key of the server
/// @param user    The name of the user doing the request
/// @param pass    The password of the user doing the request
void req_sav(int sd, EVP_PKEY *pubKey, const string &user, const string &pass, const string &);

/// req_set() sends the SET command to set the content for a user
///
/// @param sd      The open socket descriptor for communicating with the server
/// @param pubKey  The public key of the server
/// @param user    The name of the user doing the request
/// @param pass    The password of the user doing the request
/// @param setFile The file whose contents should be sent
void req_set(int sd, EVP_PKEY *pubKey, const string &user, const string &pass, const string &setFile);

/// req_get() requests the content associated with a user, and saves it to a
/// file called <user>.file.dat.
///
/// @param sd      The open socket descriptor for communicating with the server
/// @param pubKey  The public key of the server
/// @param user    The name of the user doing the request
/// @param pass    The password of the user doing the request
/// @param getName The name of the user whose content should be fetched
void req_get(int sd, EVP_PKEY *pubKey, const string &user, const string &pass, const string &getName);

/// req_all() sends the ALL command to get a listing of all users, formatted
/// as text with one entry per line.
///
/// @param sd      The open socket descriptor for communicating with the server
/// @param pubKey  The public key of the server
/// @param user    The name of the user doing the request
/// @param pass    The password of the user doing the request
/// @param allFile The file where the result should go
void req_all(int sd, EVP_PKEY *pubKey, const string &user, const string &pass, const string &allFile);
