#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <stdbool.h>

/*
* Method to check if a file exists
* @param path 	the path to the file
* @return 		1 if the file exists, 0 otherwise
*/
int file_exists(char* path) { 
	if (access(path, F_OK) == 0 && access(path, X_OK) == 0) {
		return 1; // file exists and executable
	}
	return 0; // file does not exist
}
/*
* Method to print menu if no arguments are passed
*/
void print1Arg() {
	printf("Usage: /usr/bin/which [options] [--] COMMAND [...]\n");
	printf("Write the full path of COMMAND(s) to standard output.\n\n");
	printf("  --version, -[vV] Print version and exit successfully.\n");
	printf("  --help,          Print this help and exit successfully.\n");
	printf("  --skip-dot       Skip directories in PATH that start with a dot.\n");
	printf("  --skip-tilde     Skip directories in PATH that start with a tilde.\n");
	printf("  --show-dot       Don't expand a dot to current directory in output.\n");
	printf("  --show-tilde     Output a tilde for HOME directory for non-root.\n");
	printf("  --tty-only       Stop processing options on the right if not on tty.\n");
	printf("  --all, -a        Print all matches in PATH, not just the first\n");
	printf("  --read-alias, -i Read list of aliases from stdin.\n");
	printf("  --skip-alias     Ignore option --read-alias; don't read stdin.\n");
	printf("  --read-functions Read shell functions from stdin.\n");
	printf("  --skip-functions Ignore option --read-functions; don't read stdin.\n\n");
	printf("Recommended use is to write the output of (alias; declare -f) to standard\ninput, so that which can show aliases and shell functions. See which(1) for\nexamples.\n\n");
	printf("If the options --read-alias and/or --read-functions are specified then the\noutput can be a full alias or function definition, optionally followed by\nthe full path of each command used inside of those.\n\n");
	printf("Report bugs to <which-bugs@gnu.org>.\n");
}
int main(int argc, char** argv) { // also known as: char* argv[]

	// check if arguments were passed in
	if (argc < 1) {
		printf("No arguments\n");
		return 2;
	}
	if (argc == 1) {
		print1Arg();
		return 255;
	}
	
	char* path = getenv("PATH"); // reading in PATH
	// check if path is null
	if (path == NULL) { 
		printf("NO PATH\n");
		return 2;
	}

	// checking for -a flag
	bool isAFlag = false;
	if (strcmp(argv[1], "-a") == 0 ) { // -a flag found
		isAFlag = true;
	} else if (strncmp(argv[1], "-", 1) == 0) { // invalid flag
		printf("/usr/bin/which: invalid option -- '%s'\n", argv[1]);
		print1Arg();
		return 2;
	}

	int pathCount = 0; // used as terminating condition in later for-loop
	char* paths[1000]; // holds the paths

	// splits PATH by ":"
	char* pathSplit = strtok(path, ":"); 
	for(int i = 0; pathSplit != NULL; i++) {
		paths[i] = pathSplit;
		pathSplit = strtok(NULL, ":");
		pathCount++;
	}
	
	int count = 1; // used to determine if command was found
	int i = 1;
	// increments values if -a flag is present
	if (isAFlag) {
		i++;
		count++;
	}
	for (i; i < argc; i++) { // looping through arguments
		bool found = false;
		int tmpCount = 0;
		for (int j = 0; j < pathCount; j++) {
			char temp[1000]; // made array because it was the only thing that worked

        	strcpy(temp, paths[j]); // copy the path to the temp array

			// concatonate path
       		strcat(temp, "/"); 
        	strcat(temp, argv[i]);

			if (file_exists(temp) == 1) { // checking if file exists and executable
				printf("%s\n", temp);
				found = true;
				if (tmpCount == 0 && isAFlag) { // only increments count once if -a flag is present
					count++;
					tmpCount++;
				}
				if (!isAFlag) { // allows for loop to continue through rest of paths if -a flag is present
					count++;
					break;
				}
			}
		}

		// if command is not found
		if (!found) {
			printf("/usr/bin/which: no %s in (%s)\n", argv[i], getenv("PATH"));
		}
	} 

	if (count == argc) { // if all specified commands are found and executable
		return 0;
	}
	return 1; // if one or more specified commands is nonexistent or not executable
	

}

