#pragma once

#include <cstdio>
#include <string>
#include <vector>

using namespace std;

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
void log_svv0(FILE *logfile, const string &delim, const string &s, const vector<uint8_t> &v1, const vector<uint8_t> &v2);

/// Atomically add an incremental update message to the open file
///
/// This variant puts a delimiter, a string, and a vector into the file.
///
/// @param logfile The file to write into
/// @param delim   The 8-byte string that starts the log entry
/// @param s1      The string to add to the message
/// @param v1      The vector to add to the message
void log_sv(FILE *logfile, const string &delim, const string &s1, const vector<uint8_t> &v1);

/// Atomically add an incremental update message to the open file
///
/// This variant puts a delimiter and a string into the file
///
/// @param logfile The file to write into
/// @param delim   The 8-byte string that starts the log entry
/// @param s1      The string to add to the message
void log_s(FILE *logfile, const string &delim, const string &s1);
