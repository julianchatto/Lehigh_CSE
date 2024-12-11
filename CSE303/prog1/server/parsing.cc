#include <cassert>
#include <cstring>
#include <iostream>
#include <string>
#include <vector>

#include "../common/constants.h"
#include "../common/contextmanager.h"
#include "../common/crypto.h"
#include "../common/err.h"
#include "../common/net.h"

#include "parsing.h"
#include "responses.h"

using namespace std;


/// RSA decrypt a block of data
/// 
/// @param pri The private key to use for decryption
/// @param input The data to decrypt
/// @return The decrypted data
vector<uint8_t> rsa_decrypt(EVP_PKEY *pri, const vector<uint8_t>& input) {
    // Create a decryption context
    EVP_PKEY_CTX *ctx = EVP_PKEY_CTX_new(pri, NULL);
    if (ctx == nullptr) {
        cout << "Error calling EVP_PKEY_CTX_new()" << endl;
        exit(1);
    }

    if (1 != EVP_PKEY_decrypt_init(ctx)) { // Initialize the context for decryption
        EVP_PKEY_CTX_free(ctx);
        cout << "Error calling EVP_PKEY_decrypt_init()" << endl;
        exit(1);
    }

    // Decrypt into `dec`, record the #bytes in dec_count
    size_t dec_count;
    if (1 != EVP_PKEY_decrypt(ctx, nullptr, &dec_count, input.data(), input.size())) { // Decrypt 
        EVP_PKEY_CTX_free(ctx);
        cout << "Error computing decrypted buffer size" << endl;
        exit(1);
    }

    vector<uint8_t> decrypted(dec_count);
    if (1 != EVP_PKEY_decrypt(ctx, decrypted.data(), &dec_count, input.data(), input.size())) {
        EVP_PKEY_CTX_free(ctx);
        cout << "Error calling EVP_PKEY_decrypt()" << endl;
        exit(1);
    }
    EVP_PKEY_CTX_free(ctx);
    decrypted.resize(dec_count);
    return decrypted;
}
 
/// Parse the decrypted RSA data
/// 
/// @param rsa_dec The decrypted RSA data
/// @param cmd The command to set
/// @param len_u The length of the user to set
/// @param len_p The length of the password to set
/// @param len_b The length of the ablock to set
/// @param user the user to set
/// @param pass the password to set
/// @param aes_key the aes key to set
void parse_rsa_decrypted(const vector<uint8_t>& rsa_dec, string& cmd, uint32_t& len_u, uint32_t& len_p, uint32_t& len_b, string& user, string& pass, vector<uint8_t>& aes_key) {
    // cmd is 4 bytes 
    cmd = string(rsa_dec.begin(), rsa_dec.begin() + 4);

    // aes key is 48 bytes
    aes_key = vector<uint8_t>(rsa_dec.begin() + 4, rsa_dec.begin() + 52);

    // lengths are 4 bytes
    memcpy(&len_u, rsa_dec.data() + 52, 4);   
    memcpy(&len_p, rsa_dec.data() + 56, 4);  
    memcpy(&len_b, rsa_dec.data() + 60, 4);

    // user and pass are after lengths 
    user = string(rsa_dec.begin() + 64, rsa_dec.begin() + 64 + len_u);
    pass = string(rsa_dec.begin() + 64 + len_u, rsa_dec.begin() + 64 + len_u + len_p);
}

/// Check if the block is a key block
///
/// @param block The block to check 
/// @return true if the block is a key block, false otherwise
bool is_kBlock (vector<uint8_t> &block){
    int block_size = block.size();
    
    if (block_size != LEN_RKBLOCK) {
        return false;
    } 

    for (int i = 0; i < 4; i++){
        if (block.at(i) != REQ_KEY.at(i)) {
            return false;
        }
    }
    return true;
}

/// When a new client connection is accepted, this code will run to figure out
/// what the client is requesting, and to dispatch to the right function for
/// satisfying the request.
///
/// @param sd      The socket on which communication with the client takes place
/// @param pri     The private key used by the server
/// @param pub     The public key file contents, to possibly send to the client
/// @param storage The Storage object with which clients interact
///
/// @return true if the server should halt immediately, false otherwise
bool parse_request(int sd, EVP_PKEY *pri, const vector<uint8_t> &pub, Storage *storage) {
    vector<uint8_t> req_rsa(LEN_RKBLOCK); // reads r block 
    
    int num_bytes_read = reliable_get_to_eof_or_n(sd, req_rsa.begin(), LEN_RKBLOCK);
    if (num_bytes_read < 0){
        cout << "Failed to read data from socket." << endl;
        return{};
    }

    // k block --> if block = KEY_000.. --> this is LEN_Rblock
    // if kblock, return handle key
    if (is_kBlock(req_rsa)) {
        return handle_key(sd, pub);
    }

    vector<uint8_t> rsa_dec = rsa_decrypt(pri, req_rsa);

    // parse the decrypted RSA data
    string cmd, user, pass;
    uint32_t len_u, len_p, len_b; 
    vector<uint8_t> aes_key;
    parse_rsa_decrypted(rsa_dec, cmd, len_u, len_p, len_b, user, pass, aes_key);

    // create AES context for decryption
    EVP_CIPHER_CTX *aes_dec_ctx = create_aes_context(aes_key, false);

    if (!reset_aes_context(aes_dec_ctx, aes_key, false)) {
        cout << "Failed to reset AES context for decryption." << endl;
        reclaim_aes_context(aes_dec_ctx);
        return {};
    }

    vector<uint8_t> decrypted_aes_block;
    if (cmd == REQ_SET || cmd == REQ_GET){
        vector<uint8_t> req_aes(len_b); // reads a block 

        if (reliable_get_to_eof_or_n(sd, req_aes.begin(), len_b) < 0){
            cout << "Failed to read data from socket." << endl;
            return{};
        }

        // decrypt aes block 
        decrypted_aes_block = aes_crypt_msg(aes_dec_ctx, req_aes); // @b
    }

    // aes context for encryption to send to handlers
    EVP_CIPHER_CTX *aes_ctx = create_aes_context(aes_key, true);
    if (!reset_aes_context(aes_ctx, aes_key, true)) {
        cout << "Failed to reset AES context for encryption." << endl;
        reclaim_aes_context(aes_dec_ctx);
        return {};
    }

    // dispatch request 
    vector<string> s = {REQ_REG, REQ_BYE, REQ_SAV, REQ_SET, REQ_GET, REQ_ALL};
    decltype(handle_reg) *cmds[] = {handle_reg, handle_bye, handle_sav, handle_set, handle_get, handle_all};
    for (size_t i = 0; i < s.size(); i++) {
        if (cmd == s[i]) {
            return cmds[i](sd, storage, aes_ctx, user, pass, decrypted_aes_block); 
        }
    }
    return false;
}