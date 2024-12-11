#include <iostream>
#include <libgen.h>
#include <openssl/rsa.h>
#include <string>
#include <unistd.h>

#include "../common/contextmanager.h"
#include "../common/crypto.h"
#include "../common/err.h"
#include "../common/file.h"
#include "../common/net.h"

#include "parsing.h"
#include "storage.h"

using namespace std;

/// arg_t represents the command-line arguments to the server
struct arg_t {
	int port;                  // The port on which to listen
	string dataFile;           // The file for storing all data
	string keyfile;            // The file holding the AES key
	size_t num_buckets = 1024; // Number of buckets for the server's hash tables

	/// Construct an arg_t from the command-line arguments to the program
	///
	/// @param argc The number of command-line arguments passed to the program
	/// @param argv The list of command-line arguments
	///
	/// @throw An integer exception (1) if an invalid argument is given, or if
	///        `-h` is passed in
	arg_t(int argc, char **argv) {
		long opt;
		// NB:  To keep the python scripts future-proof, we'll include some
		//      arguments that aren't yet in use:
		while ((opt = getopt(argc, argv, "p:f:k:ht:b:i:u:d:r:o:a:")) != -1) {
			switch (opt) {
				case 'p':
					port = atoi(optarg);
					break;
				case 'f':
					dataFile = string(optarg);
					break;
				case 'k':
					keyfile = string(optarg);
					break;
				case 't':
					break;
				case 'b':
					num_buckets = atoi(optarg);
					break;
				case 'i':
					break;
				case 'u':
					break;
				case 'd':
					break;
				case 'r':
					break;
				case 'o':
					break;
				case 'a':
					break;
				default: // on any error, print a help message.  This case subsumes `-h`
					throw 1;
					return;
			}
		}
	}

	/// Display a help message to explain how the command-line parameters for this
	/// program work
	///
	/// @progname The name of the program
	static void usage(char *progname) {
		cout << basename(progname) << ": company user directory server\n"
			<< "  -p [int]    Port on which to listen for incoming connections\n"
			<< "  -f [string] File for storing all data\n"
			<< "  -k [string] Basename of file for storing the server's RSA keys\n"
			<< "  -b [int]    # of buckets for the server's hash tables\n"
			<< "  -h          Print help (this message)\n";
	}
};

int main(int argc, char **argv) {
	// Parse the command-line arguments
	//
	// NB: It would be better not to put the arg_t on the heap, but then we'd need
	//     an extra level of nesting for the body of the rest of this function.
	arg_t *args;
	try {
		args = new arg_t(argc, argv);
	} catch (int i) {
		arg_t::usage(argv[0]);
		return 1;
	}

	// print the configuration
	cout << "Listening on port " << args->port << " using (key/data) = ("<< args->keyfile << ", " << args->dataFile << ")\n";

	// If the key files don't exist, create them and then load the private key.
	EVP_PKEY *pri = init_RSA(args->keyfile);
	if (pri == nullptr)
		return 1;
	ContextManager r([&]() { EVP_PKEY_free(pri); });

	// load the public key file contents
	auto pub = load_entire_file(args->keyfile + ".pub");
	if (pub.size() == 0)
		return 1;

	// If the data file exists, load the data into a Storage object.  Otherwise,
	// create an empty Storage object.
	Storage *storage = storage_factory(args->dataFile, args->num_buckets);
	auto res = storage->load_file();
	if (!res.succeeded)
		return err(1, res.msg.c_str());
	cout << res.msg << endl;

	// Start listening for connections.
	int sd = create_server_socket(args->port);
	ContextManager csd([&]() { close(sd); });

	// On a connection, parse the message, then dispatch.  This will loop until an
	// EXIT message is received.
	accept_client(sd, [&](int sd) { return parse_request(sd, pri, pub, storage); });

	delete args;
	delete storage;
	cout << "Server terminated\n";
}
