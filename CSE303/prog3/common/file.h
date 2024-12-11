#pragma once

#include <string>
#include <vector>

using namespace std;

/// Determine if a file exists. Note that using this is not a good way to avoid
/// TOCTOU bugs, but it is acceptable for this class project.
///
/// @param filename The name of the file whose existence is being checked
///
/// @return true if the file exists, false otherwise
bool file_exists(const string &filename);

/// Load a file and return its contents
///
/// @param filename The name of the file to open
///
/// @return A vector with the file contents. On error, returns an empty
///         vector
vector<uint8_t> load_entire_file(const string &filename);

/// Create or truncate a file and populate it with the provided data
///
/// @param filename The name of the file to create/truncate
/// @param data     The data to write
/// @param skip     The number of bytes from the front of `data` that should be
///                 skipped
///
/// @return false on error, true if the file was written in full
bool write_file(const string &filename, const vector<uint8_t> &data, size_t skip);
