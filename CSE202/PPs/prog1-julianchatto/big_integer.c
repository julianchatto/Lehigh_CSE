/*
 *  CSE202: Big Integer Manipulation Program
 *  Full name: Julian Chattopadhyay 
 *  Full Lehigh Email Address: juc226@lehigh.edu
*/
#include <stdio.h>
#include <string.h>
#include <stdlib.h>


/**
 * @struct to store a big integer as two quadwords
*/
struct two_quadwords{
	unsigned long lsq;
	long msq;
};


/**
 * @typedef big_integer to store a big integer as two quadwords
*/
typedef struct two_quadwords big_integer;


/**
 * @union to store big integers as 16 characters or two quadwords
*/
union value{
	big_integer binary;
	char hex[16];
};


/**
 * Determines the decimal value of a hex
 * @param c the hex character
 * @return the decimal value of the hex character, -1 if not valid
*/
char hexDigit(char c) {
	switch (c) {
        case '0': 
            return 0;
        case '1': 
            return 1;
        case '2': 
            return 2;
        case '3': 
            return 3;
        case '4': 
            return 4;
        case '5': 
            return 5; 
        case '6': 
            return 6;
        case '7': 
            return 7; 
        case '8':
            return 8; 
        case '9':
            return 9;
        case 'A': 
        case 'a': 
            return 10;
        case 'B': 
        case 'b': 
            return 11;
        case 'C': 
        case 'c': 
            return 12; 
        case 'D': 
        case 'd': 
            return 13; 
        case 'E': 
        case 'e':
            return 14; 
        case 'F':
        case 'f': 
            return 15; 
        default:
			return -1;
    }
}


/**
 * Reads a big integer from string input and stores it in the union v
 * @param v the union to store the value in
 * @param input the input string
 * @return 0 if the hexadecimal number is invalid, 1 otherwise
*/
int read_big_integer(union value *v, char *input) {
	if (strlen(input) != 32) { // ensures that the input is 32 characters long
		return 0;
	}
	for (int i = 0; i < 32; i++) { // ensures that the input is a valid hex string
		if (hexDigit(input[i]) == -1) {
			return 0;
		}
	}

	// create hex array of v in little endian
	int curIndex = 15;
	for (int i = 0; i < 32; i += 2) {
		v->hex[curIndex] = (hexDigit(input[i]) << 4) | hexDigit(input[i + 1]); // shift the most significant left 4 bits and or it with the least significant
		curIndex--;
	}
	return 1;
}


/**
 * Writes the value of b to standard output as 32 hex characters
 * @param b the big integer to write
 * @return void
*/
void write_big_integer(union value b) {
	fprintf(stdout, "%016lx %016lx", b.binary.msq, b.binary.lsq);
}


/**
 * Performs b1 & b2 and stores the result in b1_and_b2
 * @param b1 the first big integer
 * @param b2 the second big integer
 * @param b1_and_b2 the result of b1 & b2
 * @return void
*/
void and_big_integers(big_integer b1, big_integer b2, big_integer *b1_and_b2) {
	fprintf(stdout, "%016lx %016lx &\n%016lx %016lx =\n", b1.msq, b1.lsq, b2.msq, b2.lsq);
	
	b1_and_b2->msq = b1.msq & b2.msq;
	b1_and_b2->lsq = b1.lsq & b2.msq;
}


/**
 * Performs b1 | b2 and stores the result in b1_or_b2
 * @param b1 the first big integer
 * @param b2 the second big integer
 * @param b1_or_b2 the result of b1 | b2
 * @return void
*/
void or_big_integers(big_integer b1, big_integer b2, big_integer *b1_or_b2) {
	fprintf(stdout, "%016lx %016lx |\n%016lx %016lx =\n", b1.msq, b1.lsq, b2.msq, b2.lsq);
	
	b1_or_b2->msq = b1.msq | b2.msq;
	b1_or_b2->lsq = b1.lsq | b2.lsq;
}


/**
 * Performs b1 ^ b2 and stores the result in b1_xor_b2
 * @param b1 the first big integer
 * @param b2 the second big integer
 * @param b1_xor_b2 the result of b1 ^ b2
 * @return void
*/
void xor_big_integers(big_integer b1, big_integer b2, big_integer *b1_xor_b2) {
	fprintf(stdout, "%016lx %016lx ^\n%016lx %016lx =\n", b1.msq, b1.lsq, b2.msq, b2.lsq);

	b1_xor_b2->msq = b1.msq ^ b2.msq;
	b1_xor_b2->lsq = b1.lsq ^ b2.lsq;
}


/**
 * Performs ~b and stores the result in b
 * @param b the big integer to not
 * @return void
*/
void not_big_integer(big_integer *b) {
	fprintf(stdout, "~ %016lx %016lx = ", b->msq, b->lsq);

	b->msq = ~b->msq;
	b->lsq = ~b->lsq;
}


/**
 * Performs b << k and stores the result in b
 * @param b the big integer to shift
 * @param k the number of bits to shift left
 * @return void
*/
void shift_big_integer_left(big_integer *b, unsigned k) {
	fprintf(stdout, "%016lx %016lx << %d = ", b->msq, b->lsq, k);

	if (k >= 64) {
		b->msq = b->lsq << (k - 64);
		b->lsq = (b->lsq << 63) << 1; // not doing << 64 since it throws a warning, so shift 63 and then 1
	} else {
		b->msq = (b->msq << k) | (b->lsq >> (64 - k)); // EX: 0110 1100 << 2: 1000 | 0011 = 1011
		b->lsq <<= k; // shift lsq over by k
	}
}


/**
 * Performs b >> k and stores the result in b
 * @param b the big integer to shift
 * @param k the number of bits to shift right
 * @return void
*/
void shift_big_integer_right(big_integer *b, unsigned k) {
	fprintf(stdout, "%016lx %016lx >> %d = ", b->msq, b->lsq, k);

	if (k >= 64) {
		b->lsq = b->msq >> (k - 64); // shift the msq right by k - 64
		b->msq = (b->msq >> 63) >> 1; // not doing >> 64 since it throws a warning, so shift 63 and then 1
	} else {
		b->lsq = (b->lsq >> k) | (b->msq << (64 - k)); // EX: 0110 1100 >> 2: 0011 | 1000 = 1011
		b->msq >>= k; // shift msq over by k
	}
}


/**
 * Performs b1 + b2 and stores the result in sum
 * @param b1 the first big integer
 * @param b2 the second big integer
 * @param sum the sum of b1 and b2
 * @return 1 if overflow occurs, 0 otherwise
 */
int add_big_integers(big_integer b1, big_integer b2, big_integer *sum) {
	fprintf(stdout, "%016lx %016lx +\n%016lx %016lx =\n", b1.msq, b1.lsq, b2.msq, b2.lsq);

	// ignore any overflow here
	unsigned long lsqResult = b1.lsq + b2.lsq; // compute sum of lsq
	long msqResult = b1.msq + b2.msq; // compute sum of msq

	// if there is a carry, increment msqResult to account for lsq overflow
	if (lsqResult < b1.lsq || lsqResult < b2.lsq) {
		msqResult++;
	}

	// assign the result to sum
	sum->lsq = lsqResult;
	sum->msq = msqResult;
	
	// check if overflow occured in msq
	if ((b1.msq > 0 && b2.msq > 0 && msqResult < 0) || (b1.msq < 0 && b2.msq < 0 && msqResult > 0)) { 
		return 1;
	}
	return 0;
}


/**
 * Prints the message and value followed by the program usage message
 * @param message the message to print
 * @param value the value that was invalid
 * @return void
 */
void print_usage(const char *message, const char* value) {
	fprintf(stdout, "%s %s\n", message, value);
	exit(1);
}


/**
 * Calls read_big_integer and prints the usage message if the input is invalid
 * @param v the union to store the value in
 * @param input the input string
 * @return void
*/
void isValid(union value *v, char *input) {
	if (read_big_integer(v, input) == 0) print_usage("Invalid hex value:", input);
}


// main method
int main(int argc, char* argv[]) {
    if (argc < 3 || argc > 4) {
		print_usage("arguments:", "too many or too few arguments");
    }

	union value first_big_int, second_big_int, result;

	// determine the argument with the first big integer, if there is a shift operation, the first big integer is the third argument
	int index = 2;
	if (strcmp(argv[1], "sl") == 0 || strcmp(argv[1], "sr") == 0) index++;

	// checks if first big int is valid
	isValid(&first_big_int, argv[index]); 

	/*
		general structure: check second big integer and arguments (if applicable), perform operation, & write result to stdout
	*/ 
	if (strcmp(argv[1], "not") == 0) { // NOT operation
		
		not_big_integer(&first_big_int.binary);
		write_big_integer(first_big_int);
	
	} else if (strcmp(argv[1], "and") == 0) { // AND operation
		
		if (argc != 4) print_usage("and:", "too few arguments");
		isValid(&second_big_int, argv[3]); 
		and_big_integers(first_big_int.binary, second_big_int.binary, &result.binary);
		write_big_integer(result);
	
	} else if (strcmp(argv[1], "or") == 0) { // OR operation
		
		if (argc != 4) print_usage("or:", "too few arguments");
		isValid(&second_big_int, argv[3]);
		or_big_integers(first_big_int.binary, second_big_int.binary, &result.binary);
		write_big_integer(result);
	
	} else if (strcmp(argv[1], "xor") == 0) { // XOR operation
		
		if (argc != 4) print_usage("xor:", "too few arguments");
		isValid(&second_big_int, argv[3]);
		xor_big_integers(first_big_int.binary, second_big_int.binary, &result.binary);
		write_big_integer(result);
	
	} else if (strcmp(argv[1], "add") == 0) { // ADD operation
		
		if (argc != 4) print_usage("add:", "too few arguments");
		isValid(&second_big_int, argv[3]);
		int addResult = add_big_integers(first_big_int.binary, second_big_int.binary, &result.binary);
		write_big_integer(result);
		if (addResult == 1) fprintf(stdout, " - Overflow");
	
	} else if (strcmp(argv[1], "sr") == 0) { // SHIFT RIGHT operation
		
		if (atoi(argv[2]) < 0) print_usage("sr:", "invalid shift amount");
		shift_big_integer_right(&first_big_int.binary, atoi(argv[2]));
		write_big_integer(first_big_int);
	
	} else if (strcmp(argv[1], "sl") == 0) { // SHIFT LEFT operation
	
		if (atoi(argv[2]) < 0) print_usage("sl:", "invalid shift amount");
		shift_big_integer_left(&first_big_int.binary, atoi(argv[2]));
		write_big_integer(first_big_int);
	
	} else { // invalid operation
		print_usage("Invalid operation:", argv[1]);
	}
	fprintf(stdout, "\n\n");
    return 0;
}
