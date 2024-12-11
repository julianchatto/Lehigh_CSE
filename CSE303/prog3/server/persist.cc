#include <unistd.h>
#include <cstdint>
#include <cstring>

#include "persist.h"
#include "../common/constants.h"

using namespace std;

static const char paddingBuffer[3] = {0}; // 4-byte buffer of zeros

/// Write padding to the log file to make the byte count a multiple of 4
///
/// @param logfile the file to write into 
/// @param totalSize the total size of the data written so far
void writePadding(FILE* logfile, size_t totalSize) {
    // Calculate padding to make the byte count a multiple of 4
    size_t padding = (4 - (totalSize % 4)) % 4;
    if (padding > 0) {
        // Write padding if needed
        fwrite(paddingBuffer, 1, padding, logfile);
    }
}

/// Atomically add an incremental update message to the open file
///
/// This variant puts a delimiter, a string, two vectors, and a zero into the
/// file.
///
/// @param logfile The file to write into
/// @param delim   The 8-byte string that starts the log entry
/// @param s       The string to add to the message
/// @param v1      The first vector to add to the message
/// @param v2      The second vector to add to the message
void log_svv0(FILE *logfile, const string &delim, const string &s, const vector<uint8_t> &v1, const vector<uint8_t> &v2) { 
    // Write the delimiter
    fwrite(delim.data(), 1, 4, logfile);

    // Prepare length information for s, v1, v2, and an empty vector v3
    const vector<uint8_t> v3;
    uint32_t lengths[4] = {static_cast<uint32_t>(s.length()), static_cast<uint32_t>(v1.size()), static_cast<uint32_t>(v2.size()), static_cast<uint32_t>(v3.size())};
    fwrite(lengths, sizeof(uint32_t), 4, logfile);

    // Write the actual string and vector data
    fwrite(s.data(), 1, lengths[0], logfile);
    fwrite(v1.data(), 1, lengths[1], logfile);
    fwrite(v2.data(), 1, lengths[2], logfile);

    writePadding(logfile, lengths[0] + lengths[1] + lengths[2]);

    fflush(logfile);
    fsync(fileno(logfile));
}

/// Atomically add an incremental update message to the open file
///
/// This variant puts a delimiter, a string, and a vector into the file.
///
/// @param logfile The file to write into
/// @param delim   The 8-byte string that starts the log entry
/// @param s1      The string to add to the message
/// @param v1      The vector to add to the message
void log_sv(FILE *logfile, const string &delim, const string &s1, const vector<uint8_t> &v1) {
    // Write the delimiter
    fwrite(delim.data(), 1, 4, logfile);

    // Write lengths of s1 and v1
    uint32_t lengths[2] = {static_cast<uint32_t>(s1.length()), static_cast<uint32_t>(v1.size())};
    fwrite(lengths, sizeof(uint32_t), 2, logfile);

    // Write the actual string and vector data
    fwrite(s1.data(), 1, lengths[0], logfile);
    fwrite(v1.data(), 1, lengths[1], logfile);

    writePadding(logfile, lengths[0] + lengths[1]);
    
    fflush(logfile);
    fsync(fileno(logfile));
}

/// Atomically add an incremental update message to the open file
///
/// This variant puts a delimiter and a string into the file
///
/// @param logfile The file to write into
/// @param delim   The 8-byte string that starts the log entry
/// @param s1      The string to add to the message
void log_s(FILE *logfile, const string &delim, const string &s1) {
    // Write the delimiter
    fwrite(delim.data(), 1, 4, logfile);
    // Write length of s1
    uint32_t length = static_cast<uint32_t>(s1.length());
    fwrite(&length, sizeof(uint32_t), 1, logfile);

    // Write the actual string data
    fwrite(s1.data(), 1, length, logfile);

    writePadding(logfile, length);
    
    fflush(logfile);
    fsync(fileno(logfile));
}