#include <iostream>
#include <string>

using namespace std;

// to export a function, it must have C linkage
extern "C" {
	/// Print a newline-terminated message to the console
	///
	/// @param message The message to print
	void simple_print(string message) { 
		cout << message << endl; 
	}
}