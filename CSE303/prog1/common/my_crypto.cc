#include <cassert>
#include <openssl/err.h>
#include <openssl/pem.h>
#include <vector>

#include "err.h"

using namespace std;

/// Run the AES symmetric encryption/decryption algorithm on a buffer of bytes.
/// Note that this will do either encryption or decryption, depending on how the
/// provided CTX has been configured.  After calling, the CTX cannot be used
/// again until it is reset.
///
/// @param ctx The pre-configured AES context to use this operation
/// @param start A buffer of bytes to encrypt or decrypt
/// @param count The number of bytes in the buffer start
/// @return A vector of bytes containing the encrypted or decrypted data
vector<uint8_t> aes_crypt_msg(EVP_CIPHER_CTX *ctx, const unsigned char *start, int count) {
    int b_size = EVP_CIPHER_block_size(EVP_CIPHER_CTX_cipher(ctx)); // find how big the block size is 
    vector<uint8_t> output_buf(count + b_size); // add extra block for padding 
    int output_len = 0; // sets length to 0 to count

    // encrypt or decrypt the buffer using cipherupdate
    if (count > 0) {
        if (!EVP_CipherUpdate(ctx, output_buf.data(), &output_len, start, count)) {
            cout << "Error in EVP_CipherUpdate" << endl;
            return {};
        }
    }

    // do cipherfinal for any remaining blocks in padding 
    int ending_len = 0;
    if (!EVP_CipherFinal_ex(ctx, output_buf.data() + output_len, &ending_len)) {
        cout << "Error in EVP_CipherFinal_ex" << endl;
        return {};
    }

    output_buf.resize(output_len + ending_len); // resize output buffer to the actual size of the encrypted/decrypted data
    return output_buf;
}
