#pragma once

#include <string>
#include <vector>

using namespace std;

/// A pointer to a function that takes a string and vector and returns a
/// vector
typedef vector<uint8_t> (*map_func)(string &, vector<uint8_t> &);

/// A pointer to a function that takes a vector of vectors and returns a vector
typedef vector<uint8_t> (*reduce_func)(vector<vector<uint8_t>> &);

/// A prefix to use when generating unique names for .so files
const string SO_PREFIX = "./codecache";

/// The C name for map functions extracted from .so files
const string MAP_FUNC_NAME = "map";

/// The C name for reduce functions extracted from .so files
const string REDUCE_FUNC_NAME = "reduce";
