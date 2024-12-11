#pragma once

#include <string>
#include <vector>
#include <cstdint>

using namespace std;

/// AuthTableEntry represents one user stored in the authentication table
struct AuthTableEntry {
	string username;           // The name of the user
	vector<uint8_t> salt;      // The salt to use with the password
	vector<uint8_t> pass_hash; // The hashed password
	vector<uint8_t> content;   // The user's content
};
