/**
 * text_server.cc
 *
 * Text_server is half of a client/server pair that shows how to receive text
 * from a client and send a reply.
 */

#include <arpa/inet.h>
#include <cstring>
#include <errno.h>
#include <string>
#include <sys/time.h>
#include <unistd.h>

using namespace std;

/** Print a message to inform the user of how to use this program */
void usage(char *progname) {
  printf("%s: Server half of a client/server echo program to demonstrate "
         "sending text over a network.\n",
         basename(progname));
  printf("  -p [int]    Port number of the server\n");
  printf("  -h          Print help (this message)\n");
}

/**
 * In this program, the only useful arguments are a port number and a hostname.
 * We store them in the arg_t struct, which we populate via the get_args()
 * function.
 */
struct arg_t {
  /** The port on which the program will listen for connections */
  size_t port = 0;

  /** Is the user requesting a usage message? */
  bool usage = false;
};

/**
 * Parse the command-line arguments, and use them to populate the provided args
 * object.
 *
 * @param argc The number of command-line arguments passed to the program
 * @param argv The list of command-line arguments
 * @param args The struct into which the parsed args should go
 */
void parse_args(int argc, char **argv, arg_t &args) {
  long opt;
  while ((opt = getopt(argc, argv, "p:h")) != -1) {
    switch (opt) {
    case 'p':
      args.port = atoi(optarg);
      break;
    case 'h':
      args.usage = true;
      break;
    }
  }
}


/**
 * Print an error message that combines some provided text (prefix) with the
 * standard unix error message that accompanies errno, and then exit the
 * program.  This routine makes it easier to see the logic in our program while
 * still correctly handling errors.
 *
 * @param code   The exit code to return from the program
 * @param err    The error code that was generated by the program
 * @param prefix The text to display before the error message
 */
void error_message_and_exit(size_t code, size_t err, const char *prefix) {
	char buf[1024];
	fprintf(stderr, "%s %s\n", prefix, strerror_r(err, buf, sizeof(buf)));
	exit(code);
}

/**
 * Create a server socket that we can use to listen for new incoming requests
 *
 * @param port The port on which the program should listen for new connections
 */
int create_server_socket(size_t port) {
	// A socket is just a kind of file descriptor.  We want our connections to use
	// IPV4 and TCP:
	int sd = socket(AF_INET, SOCK_STREAM, 0);
	if (sd < 0) {
		error_message_and_exit(0, errno, "Error making server socket: ");
	}
	// The default is that when the server crashes, the socket can't be used for a
	// few minutes.  This lets us re-use the socket immediately:
	int tmp = 1;
	if (setsockopt(sd, SOL_SOCKET, SO_REUSEADDR, &tmp, sizeof(int)) < 0) {
		close(sd);
		error_message_and_exit(0, errno, "setsockopt(SO_REUSEADDR) failed: ");
	}

	// bind the socket to the server's address and the provided port, and then
	// start listening for connections
	sockaddr_in addr;
	memset(&addr, 0, sizeof(addr));
	addr.sin_family = AF_INET;
	addr.sin_addr.s_addr = htonl(INADDR_ANY);
	addr.sin_port = htons(port);
	if (bind(sd, (struct sockaddr *)&addr, sizeof(addr)) < 0) {
		close(sd);
		error_message_and_exit(0, errno, "Error binding socket to local address: ");
	}
	if (listen(sd, 0) < 0) {
		close(sd);
		error_message_and_exit(0, errno, "Error listening on socket: ");
	}
	return sd;
}

/**
 * Receive text over the provided socket file descriptor, and send it back to
 * the client.  When the client sends an EOF, return.
 *
 * @param sd      The socket file descriptor to use for the echo operation
 * @param verbose Should stats be printed upon completion?
 */
void echo_server(int sd, bool verbose) {
	// vars for tracking connection duration, bytes transmitted
	size_t xmitBytes = 0;
	struct timeval start_time, end_time;
	if (verbose) {
		gettimeofday(&start_time, nullptr);
	}

	// read data for as long as there is data, and always send it back
	while (true) {
		// Receive up to 16 bytes of data
		//
		// NB: we should receive more data at a time, but we'll keep it small so
		//     that it's easier to see how the server handles full buffers
		char buf[16] = {0};
		// NB: see text_client for explanation of why we receive data like this
		ssize_t rcd = recv(sd, buf, sizeof(buf), 0);
		if (rcd <= 0) {
		if (errno != EINTR) {
			if (rcd == 0) {
				break;
			} else {
				// NB: throughout this function, we are crashing if the client does
				//     something bad.  That's not a good way to write reliable code.
				//     Instead, this function should just print an error and return,
				//     so that it can start servicing another client.
				error_message_and_exit(0, errno, "Error in recv(): ");
			}
		}
		} else {
		// Immediately send back whatever we got
		//
		// NB: see text_client for explanation of why we send data like this
		xmitBytes += rcd;
		char *next_byte = buf;
		size_t remain = rcd;
		while (remain) {
			size_t sent = send(sd, next_byte, remain, 0);
			if (sent <= 0) {
				if (errno != EINTR) {
					error_message_and_exit(0, errno, "Error in send(): ");
				}
			} else {
				next_byte += sent;
				remain -= sent;
			}
		}
		// update the transmission count
		xmitBytes += rcd;
		}
	}
	if (verbose) {
		gettimeofday(&end_time, nullptr);
		printf("Transmitted %ld bytes in %ld seconds\n", xmitBytes, (end_time.tv_sec - start_time.tv_sec));
	}
}



int main(int argc, char *argv[]) {
	// parse the command line arguments
	arg_t args;
	parse_args(argc, argv, args);
	if (args.usage) {
		usage(argv[0]);
		exit(0);
	}

	// Set up the server socket for listening.  This will exit the program on any
	// error.
	int serverSd = create_server_socket(args.port);

	// Use accept() to wait for a client to connect.  When it connects, service
	// it.  When it disconnects, then and only then will we accept a new client.
	while (true) {
		printf("Waiting for a client to connect...\n");
		sockaddr_in clientAddr;
		socklen_t clientAddrSize = sizeof(clientAddr);
		int connSd = accept(serverSd, (sockaddr *)&clientAddr, &clientAddrSize);
		if (connSd < 0) {
			close(serverSd);
			error_message_and_exit(0, errno, "Error accepting request from client: ");
		}
		char clientName[1024];
		printf("Connected to %s\n", inet_ntop(AF_INET, &clientAddr.sin_addr,
											clientName, sizeof(clientName)));
		echo_server(connSd, true);
		// NB: ignore errors in close()
		close(connSd);
	}
	// NB: unreachable, but if we had a way to gracefully shut down, we'd need to
	//     close the server socket like this:
	close(serverSd);
	return 0;
}