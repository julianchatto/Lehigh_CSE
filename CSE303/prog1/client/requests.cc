#include <cassert>
#include <cstring>
#include <iostream>
#include <openssl/rand.h>
#include <vector>
#include <iomanip>
#include <cstdint>
#include <type_traits>


#include "../common/constants.h"
#include "../common/contextmanager.h"
#include "../common/crypto.h"
#include "../common/file.h"
#include "../common/net.h"

#include "requests.h"

using namespace std;

// prototypes
vector<uint8_t> send_cmd(int sd, EVP_PKEY *pub, const string &cmd, const string &user, const string &password, const vector<uint8_t> &msg);
vector<uint8_t> rsa_encrypt(EVP_PKEY *pub, vector<uint8_t> input);

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

/// Add the contents of an iterable to a vector
///
/// @param res  The vector to add to
/// @param t    The thing to add
///
/// @tparam T   The type of t
template <class T> void add_it(vector<uint8_t> &res, T t){
    res.insert(res.end(), t.begin(), t.end()); 
}

/// req_key() writes a request for the server's key on a socket descriptor.
/// When it gets a key back, it writes it to a file.
///
/// @param sd      The open socket descriptor for communicating with the server
/// @param keyFile The name of the file to which the key should be written
void req_key(int sd, const string &keyFile) {
	// create REQ_KEY but pad to 256 bytes 
    vector<uint8_t> req_key_vec(256, 0); 
    copy(REQ_KEY.begin(), REQ_KEY.end(), req_key_vec.begin()); // copy REQ_KEY to vector

    // send request to server for key
    if (!send_reliably(sd, req_key_vec)) { // send_reliably is bool val
        cout << "Did not send REQ_KEY." << endl;
        return;
    }

    // read response from socket which should be key 
    vector<uint8_t> response_vec = reliable_get_to_eof(sd);

    // see if bytes match to public key 
    if (response_vec.size() != LEN_RSA_PUBKEY) {
        cout << "Did not receive public key." << endl;
        return;
    }

    // write public key to file 
    if (!write_file(keyFile, response_vec, 0)) { // bool value 
        cout << "Did not write public key to file." << endl;
        return;
    }

}

/// req_reg() sends the REG command to register a new user
///
/// @param sd      The open socket descriptor for communicating with the server
/// @param pubKey  The public key of the server
/// @param user    The name of the user doing the request
/// @param pass    The password of the user doing the request
void req_reg(int sd, EVP_PKEY *pubKey, const string &user, const string &pass, const string &) {
    // create an empty message vector bc no ablock here, dont need to add size 
    vector<uint8_t> msg;

    // send the command using send_cmd() with an empty a block
    vector<uint8_t> response = send_cmd(sd, pubKey, REQ_REG, user, pass, msg);


    // check the response or error 
    if (response.empty()) {
        cout << "Failed to send BYE request or received an empty response." << endl;
        return;
    } 

    string response_str(response.begin(), response.end());

    if (response_str == RES_ERR_CRYPTO) {
        cout << RES_ERR_CRYPTO << endl;
    } else if (response_str.substr(0, 4) == RES_OK) {
        cout << RES_OK << endl;
    } else if (response_str.substr(0, 3) == "ERR") {
        // check for errors 
        if (response_str == RES_ERR_USER_EXISTS) {
            cout << RES_ERR_USER_EXISTS << endl;
        } else if (response_str == RES_ERR_REQ_FMT) {
            cout << RES_ERR_REQ_FMT << endl;
        } else {
            cout << "Received an unknown error response: " << response_str << endl;
        }
    } else {
        cout << "Received an unknown response: " << response_str << endl;
    }
    
}

/// req_bye() writes a request for the server to exit.
///
/// @param sd      The open socket descriptor for communicating with the server
/// @param pubKey  The public key of the server
/// @param user    The name of the user doing the request
/// @param pass    The password of the user doing the request
void req_bye(int sd, EVP_PKEY *pubKey, const string &user, const string &pass, const string &) {
    // empty a block
    vector<uint8_t> msg;

    // call send command w empty a block
    vector<uint8_t> response = send_cmd(sd, pubKey, REQ_BYE, user, pass, msg);

    // check response or error 
    if (response.empty()) {
        cout << "Failed to send BYE request or received an empty response." << endl;
        return;
    } 
    
    string response_str(response.begin(), response.end());
    
    if (response_str == RES_ERR_CRYPTO) {
        cout << RES_ERR_CRYPTO << endl;
    } else if (response_str.substr(0, 4) == RES_OK) {
        cout << RES_OK << endl;
    } else if (response_str.substr(0, 3) == "ERR") {
        // check errors
        if (response_str == RES_ERR_LOGIN) {
            cout << RES_ERR_LOGIN << endl;
        } else if (response_str == RES_ERR_REQ_FMT) {
            cout << RES_ERR_REQ_FMT << endl;
        } else {
            cout << "Received an unknown error response: " << response_str << endl;
        }
    } else {
        cout << "Received an unknown response: " << response_str << endl;
    }
}

/// req_sav() writes a request for the server to save its contents
///
/// @param sd      The open socket descriptor for communicating with the server
/// @param pubKey  The public key of the server
/// @param user    The name of the user doing the request
/// @param pass    The password of the user doing the request
void req_sav(int sd, EVP_PKEY *pubKey, const string &user, const string &pass, const string &) {
	// create empty a block 
    vector<uint8_t> msg;

    // Call send_cmd() with the command, user, password, and empty message block
    vector<uint8_t> response = send_cmd(sd, pubKey, REQ_SAV, user, pass, msg);

    // check response or error 
    if (response.empty()) {
        cout << "Failed to send SAVE request or received an empty response." << endl;
        return;
    } 

    string response_str(response.begin(), response.end());
   
    if (response_str == RES_ERR_CRYPTO) {
        cout << RES_ERR_CRYPTO << endl;
    } else if (response_str.substr(0, 4) == RES_OK) {
        cout << RES_OK << endl;
    } else if (response_str.substr(0, 3) == "ERR") {
        // check errors
        if (response_str == RES_ERR_LOGIN) {
            cout << RES_ERR_LOGIN << endl;
        } else if (response_str == RES_ERR_REQ_FMT) {
            cout << RES_ERR_REQ_FMT << endl;
        } else {
            cout << "Received an unknown error response: " << response_str << endl;
        }
    } else {
        cout << "Received an unknown response: " << response_str << endl;
    }
}

/// If a buffer consists of RES_OK.bbbb.d+, where `.` means concatenation, bbbb
/// is a 4-byte binary integer and d+ is a string of characters, write the bytes
/// (d+) to a file
///
/// @param buf      The buffer holding a response
/// @param filename The name of the file to write
void send_result_to_file(const vector<uint8_t> &buf, const string &filename) {
    // call write_file on 8 bytes --> RES_ is 4 bytes, bbbb is 4 bytes
    if (!write_file(filename, buf, 8)) {
        cout << "Failed to write to file: " << endl;
    }
}

/// req_set() sends the SET command to set the content for a user
///
/// @param sd      The open socket descriptor for communicating with the server
/// @param pubKey  The public key of the server
/// @param user    The name of the user doing the request
/// @param pass    The password of the user doing the request
/// @param setFile The file whose contents should be sent
void req_set(int sd, EVP_PKEY *pubKey, const string &user, const string &pass, const string &setFile) {
    // creating a block: @b = aes(len(@f).@f)

    // get contents from file (@f)
    vector<uint8_t> fileContents = load_entire_file(setFile);
    if (fileContents.empty()) {
        cout << "Failed to read file contents or file is empty: " << setFile << endl;
        return;
    }

    vector<uint8_t> msg; // create a block vector to be sent as msg for send_cmd
    add_size(msg, fileContents);
    add_it(msg, fileContents);

    // call send_cmd
    vector<uint8_t> response = send_cmd(sd, pubKey, REQ_SET, user, pass, msg);

    // check responses
    if (response.empty()) {
        cout << "Failed to send SET request or received an empty response." << endl;
        return;
    } 
    
    string response_str(response.begin(), response.end());

    if (response_str == RES_ERR_CRYPTO) {
        cout << RES_ERR_CRYPTO << endl;
    } else if (response_str.substr(0, 4) == RES_OK) {
        cout << RES_OK << endl;
    } else if (response_str.substr(0, 3) == "ERR") {
        // check errors
        if (response_str == RES_ERR_LOGIN) {
            cout << RES_ERR_LOGIN << endl;
        } else if (response_str == RES_ERR_REQ_FMT) {
            cout << RES_ERR_REQ_FMT << endl;
        } else {
            cout << "Received an unknown error response: " << response_str << endl;
        }
    } else {
        cout << "Received an unknown response: " << response_str << endl;
    }
}

/// req_get() requests the content associated with a user, and saves it to a
/// file called <user>.file.dat.
///
/// @param sd      The open socket descriptor for communicating with the server
/// @param pubKey  The public key of the server
/// @param user    The name of the user doing the request
/// @param pass    The password of the user doing the request
/// @param getname The name of the user whose content should be fetched
void req_get(int sd, EVP_PKEY *pubKey, const string &user, const string &pass, const string &getname) {
	// Create the message: len(@w).@w
    vector<uint8_t> ablock_wsize;
    add_size(ablock_wsize, getname);
    add_it(ablock_wsize, getname);

    // Call send_cmd with the necessary parameters: "GETP", user, pass, msg
    vector<uint8_t> response = send_cmd(sd, pubKey, REQ_GET, user, pass, ablock_wsize);
    send_result_to_file(response, getname + ".file.dat");

    // Check response
    if (response.empty()) {
        cout << "Failed to send GET request or received an empty response." << endl;
        return;
    }

    // Check if the response is an error message
    string response_str(response.begin(), response.end());
    if (response_str == RES_ERR_CRYPTO) {
        cout << RES_ERR_CRYPTO << endl;
    } else if (response_str.substr(0, 4) == RES_OK) {
        cout << RES_OK << endl;
    } else if (response_str.substr(0, 3) == "ERR") {
        // Handle different errors
        if (response_str == RES_ERR_LOGIN) {
            cout << RES_ERR_LOGIN << endl;
        } else if (response_str == RES_ERR_NO_USER) {
            cout << RES_ERR_NO_USER << endl;
        } else if (response_str == RES_ERR_NO_DATA) {
            cout << RES_ERR_NO_DATA << endl;
        } else if (response_str == RES_ERR_REQ_FMT) {
            cout << RES_ERR_REQ_FMT << endl;
        } else {
            cout << "Received an unknown error response: " << response_str << endl;
        }
    }
}

/// req_all() sends the ALL command to get a listing of all users, formatted
/// as text with one entry per line.
///
/// @param sd      The open socket descriptor for communicating with the server
/// @param pubKey  The public key of the server
/// @param user    The name of the user doing the request
/// @param pass    The password of the user doing the request
/// @param allFile The file where the result should go
void req_all(int sd, EVP_PKEY *pubKey, const string &user, const string &pass, const string &allFile) {
    
    vector<uint8_t> msg; // create empty a block

    // send_cmd with empty a block
    vector<uint8_t> response = send_cmd(sd, pubKey, REQ_ALL, user, pass, msg);
    send_result_to_file(response, allFile);

    // check for errors
    string response_str(response.begin(), response.end());
    if (response_str == RES_ERR_CRYPTO) {
        cout << RES_ERR_CRYPTO << endl;
    } else if (response_str.substr(0, 4) == RES_OK) {
        cout << RES_OK << endl;
    } else if (response_str.substr(0, 3) == "ERR") {
        if (response_str == RES_ERR_LOGIN) {
            cout << RES_ERR_LOGIN << endl;
        } else if (response_str == RES_ERR_REQ_FMT) {
            cout << RES_ERR_REQ_FMT << endl;
        } else {
            cout << "Received an unknown error response: " << response_str << endl;
        }
    }

}

/// Create r_block helper for send_cmd method: this will create the r block
/// depicted in the read me. Makes sure the format matches in order to send to server.
///
/// @param cmd cmd to send to server
/// @param aes_key aes key to encrypt message
/// @param user user and user length to send to server 
/// @param password password and password length to send to server
/// @param aes_encrypted_msg aes encrypted message to find block and send to server
/// @return the rblock
vector<uint8_t> create_rblock(const string &cmd, vector<uint8_t> aes_key, const string &user, const string &password, vector<uint8_t> aes_encrypted_msg){
    // format: "SETP".@ka.len(@u).len(@p).len(@b).@u.@p

    vector<uint8_t> r_block; // create r block vector
    r_block.insert(r_block.end(), cmd.begin(), cmd.end()); // add cmd
    r_block.insert(r_block.end(), aes_key.begin(), aes_key.end()); // add aes key

    // add user length in little endian 
    vector<uint8_t> user_length;
    add_size(user_length, user);
    r_block.insert(r_block.end(), user_length.begin(), user_length.end());

    // add password length in little endian 
    vector<uint8_t> pass_length;
    add_size(pass_length, password);
    r_block.insert(r_block.end(), pass_length.begin(), pass_length.end());

    // add length of a block - encrypted message
    vector<uint8_t> ablock_length;
    if (cmd == REQ_GET || cmd == REQ_SET) {
        add_size(ablock_length, aes_encrypted_msg);
        r_block.insert(r_block.end(), ablock_length.begin(), ablock_length.end());
    } else
        r_block.insert(r_block.end(), 4, 0); // add 4 bytes of 0 for other commands

    r_block.insert(r_block.end(), user.begin(), user.end()); // add user data 
    r_block.insert(r_block.end(), password.begin(), password.end()); // add password data 

    return r_block; // return block 
}

/**
 * Encrypt an inputs contents and store in a vector
 *
 * @param pub The public key
 * @param input 
 * @return encrypted contents
 */
vector<uint8_t> rsa_encrypt(EVP_PKEY *pub, vector<uint8_t> input) {
	// Create an encryption context
	EVP_PKEY_CTX *ctx = EVP_PKEY_CTX_new(pub, NULL);
	if (ctx == nullptr) {
		cout << "Error calling EVP_PKEY_CTX_new()" << endl;
		exit(1);
	}
	if (1 != EVP_PKEY_encrypt_init(ctx)) {
		EVP_PKEY_CTX_free(ctx);
		cout << "Error calling EVP_PKEY_encrypt_init()" << endl;
		exit(1);
	}

	// First we get the size of the buffer
	size_t enc_count = 0;
	if (1 != EVP_PKEY_encrypt(ctx, nullptr, &enc_count, input.data(), input.size())) {
		EVP_PKEY_CTX_free(ctx);
		cout << "Error computing encrypted buffer size" << endl;
		exit(1);
	}
	// Now make a buffer, encrypt into it, and free the context
	vector<uint8_t> encrypted (enc_count);
	if (1 != EVP_PKEY_encrypt(ctx, encrypted.data(), &enc_count, input.data(), input.size())) {
		EVP_PKEY_CTX_free(ctx);
		cout << "Error calling EVP_PKEY_encrypt()" << endl;
		exit(1);
	}
	EVP_PKEY_CTX_free(ctx);

	return encrypted;

}

/// Send a message to the server, using the common format for secure messages,
/// then take the response from the server, decrypt it, and return it.
///
/// @param sd       The open socket descriptor for communicating with the server
/// @param pub      The server's public key, for encrypting the aes key
/// @param cmd      The command that is being sent
/// @param user     The username for the request
/// @param password The password for the request
/// @param msg      The contents that should be AES-encrypted --> a block 
///
/// @return a vector with the (decrypted) result, or an empty vector on error
vector<uint8_t> send_cmd(int sd, EVP_PKEY *pub, const string &cmd, const string &user, const string &password, const vector<uint8_t> &msg){
	// call send_reliably at end 
    // create new aes key
    vector<uint8_t> aes_key = create_aes_key();
    if (aes_key.empty()) {
        cout << "Failed to create AES key." << endl;
        return {};
    }
    
    // create aes context for key 
    EVP_CIPHER_CTX *aes_ctx = create_aes_context(aes_key, true); // true for encryption
    if (aes_ctx == nullptr) {
        cout << "Failed to create AES context." << endl;
        return {};
    }

	// reset aes context
	bool reset_success = reset_aes_context(aes_ctx, aes_key, true); // true for encryption
	if (!reset_success) {
		cout << "Failed to reset AES context for encryption." << endl;
		reclaim_aes_context(aes_ctx);
		return {};
	}	

    // encrypt aes message
    vector<uint8_t> aes_encrypted_msg = aes_crypt_msg(aes_ctx, msg);
    if (aes_encrypted_msg.empty()) {
        cout << "Failed to encrypt message." << endl;
        reclaim_aes_context(aes_ctx);
        return {};
    }

    // create the RSA block
    vector<uint8_t> rsa_block = create_rblock(cmd, aes_key, user, password, aes_encrypted_msg);
    if (rsa_block.empty()) {
        cout << "Failed to create RSA block." << endl;
        reclaim_aes_context(aes_ctx);
        return {};
    }

    // encrypt the RSA block
    vector<uint8_t> rsa_block_encrypted = rsa_encrypt(pub, rsa_block);
    if (rsa_block_encrypted.empty()) {
        cout << "Failed to encrypt RSA block." << endl;
        reclaim_aes_context(aes_ctx);
        return {};
    }

    // combine the two in one vector to send 
    vector<uint8_t> combined_message;
    combined_message.insert(combined_message.end(), rsa_block_encrypted.begin(), rsa_block_encrypted.end());
    if (cmd == REQ_GET || cmd == REQ_SET)
        combined_message.insert(combined_message.end(), aes_encrypted_msg.begin(), aes_encrypted_msg.end());

    // send reliably 
    bool success = send_reliably(sd, combined_message); // Check if message sending was successful
    if (!success) {
        cout << "Failed to send message reliably to the server." << endl;
        reclaim_aes_context(aes_ctx);
        return {};
    }
    // check for response
    vector<uint8_t> response = reliable_get_to_eof(sd);

    if (response.empty()) {
        cout << "Received an empty response." << endl;
        return {RES_ERR_REQ_FMT.begin(), RES_ERR_REQ_FMT.end()};
    }

    // decrypt the response
    EVP_CIPHER_CTX *aes_ctx_decrypt = create_aes_context(aes_key, false); // false for decryption
    if (!aes_ctx_decrypt) {
        cout << "Failed to create AES context for decryption." << endl;
        return {};
    }

    // reset the aes context for decryption
    bool reset_success_dec = reset_aes_context(aes_ctx_decrypt, aes_key, false);
    if (!reset_success_dec) {
		cout << "Failed to reset AES context for decryption." << endl;
		reclaim_aes_context(aes_ctx_decrypt);
		return {};
    }

    // decrypt the response
    vector<uint8_t> decrypted_response = aes_crypt_msg(aes_ctx_decrypt, response);
    reclaim_aes_context(aes_ctx_decrypt); // reclaim context when decrypt
    if (decrypted_response.empty()) {
        cout << "Failed to decrypt response." << endl;
        return {};
    }

    return decrypted_response;
}