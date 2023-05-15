#include <iostream>   // for buffered IO
#include <dirent.h>   // for directory functions
#include <errno.h>    // for errno
#include <string.h>   // for strerror and strcmp
#include <array>      // for std::array
#include <algorithm>  // for std::count
#include <sys/stat.h> // for lstat
using namespace std;

#ifndef DEBUG
#define DEBUG 0
#endif

// classes
class AllTypes {
	public:
		std::string type;
		unsigned int count;
		AllTypes();
		AllTypes(std::string type);
		~AllTypes();
		void inc();
		friend ostream & operator <<(ostream &, AllTypes &);
};
AllTypes::AllTypes() {
	this->type = "";
	this->count = 0;
	if(DEBUG) std::cerr << "AllTypes constructor called for type " << type << ", count initialized to " << count << std::endl;
}
AllTypes::AllTypes(std::string type) {
	this->type = type;
	this->count = 0;
	if(DEBUG) std::cerr << "AllTypes constructor called for type " << type << ", count initialized to " << count << std::endl;
}
AllTypes::~AllTypes() {
	if(DEBUG) std::cerr << "AllTypes destructor called for type " << type << ": " << count << " instances" << std::endl;
}
void AllTypes::inc() { 
	count++; 
	if(DEBUG) std::cerr << "AllTypes inc() called for the type " << type << ", count is now " << count << std::endl;
}
ostream &operator <<(ostream &dout, AllTypes &r) {
	dout << endl;
	dout << "Type '" << r.type << "' has " << r.count << " instances";
	return (dout);
};
class TypesWithSize : public AllTypes {
	public: 
		unsigned int bytes;
    TypesWithSize();
	TypesWithSize(std::string type);
	~TypesWithSize();
	void add(unsigned int addedValue);
	friend ostream & operator <<(ostream &, TypesWithSize &);
};  
TypesWithSize::TypesWithSize(std::string type) {
	this->bytes = 0;
	this->type = type;
	count = 0;
	if(DEBUG) std::cerr << "  TypesWithSize constructor called for type " << type << ", bytes initialized to " << bytes << std::endl;
}
TypesWithSize::TypesWithSize(void) {
	this->bytes = 0;
	this->type = type;
	count = 0;
	if(DEBUG) std::cerr << "  TypesWithSize constructor called for type " << type << ", bytes initialized to " << bytes << std::endl;
}
TypesWithSize::~TypesWithSize() {
	if(DEBUG) std::cerr << "  TypesWithSize destructor called for type " << type << ": " << bytes << " bytes" << std::endl;
}
void TypesWithSize::add(unsigned int addedValue) {
	bytes += addedValue;
	if(DEBUG) std::cerr << "TypesWithSize add() called for the type " << type << ", bytes is now " << bytes << std::endl;
}
ostream &operator <<(ostream &dout, TypesWithSize &r) {
	dout << endl;
	dout << "Type '" << r.type << "' has " << r.count << " instances consuming " << r.bytes << " bytes";
	return (dout);
};
class Directories : public TypesWithSize {
	public:
		unsigned int maxDepth;
		std::array<unsigned int, 100> depthArray;
		Directories(std::string type);
		~Directories();
		void depth();
		friend ostream & operator <<(ostream &, Directories &);
};
Directories::Directories(std::string type) {
	maxDepth = 0;
	depthArray.fill(0);
	this->type = type;
	count = 0;
	if(DEBUG) std::cerr << "    Directories constructor called for type " << type << ", maxDepth and depthArray initialized to all 0s" << std::endl;
}
Directories::~Directories() {
	if(DEBUG) std::cerr << "    Directories destructor called for type " << type << ": max depth of directories is " << maxDepth << std::endl;
}
void Directories::depth() {
	maxDepth++;
	if(DEBUG) std::cerr << "Directories depth() called for the type " << type << ", maxDepth is now " << maxDepth << std::endl;
}
ostream &operator <<(ostream &dout, Directories &r) {
	dout << endl;
	dout << "Type '" << r.type << "' has " << r.count << " instances consuming " << r.bytes << " bytes and the following number of directories at various depths: " << r.maxDepth << endl;
	return (dout);
};

// globals
static AllTypes pipes("Pipe");
static AllTypes socks("Socket");
static TypesWithSize devs("Device");
static TypesWithSize regs("Regular");
static TypesWithSize links("Link");
static TypesWithSize unks("Unknown");
static Directories dirs("Directory");



// prototypes
std::string getTopDir(void);
void recurse_directory(std::string dir);  // opens the directory passed as dir and reads all the files
DIR *Opendir(const char *);
struct dirent *Readdir(DIR *);
int Closedir(DIR *dirp);

// main function   NOTE: Do not edit, except perhaps to comment out print statements until 
//                       you have implemented the overloaded insertion operators
int main(void) {
	recurse_directory(getTopDir());

	// print out a listing of the number of files of each time your program encountered
	std::cout << "\nSummary";
	std::cout << pipes;
	std::cout << socks;
	std::cout << devs;
	std::cout << regs;
	std::cout << links;
	std::cout << unks;
	std::cout << dirs;
	std::cout << std::endl;

	return 0;
}



// functions
std::string getTopDir(void) {
	std::string dir = "";
	do {
		std::cout << "Enter top-level directory to begin search: ";
		if (!std::getline(std::cin, dir)) { 
			std::cerr << "std::getline error reading from std::cin, exiting" << std::endl; 
			exit(99);
		}
	} while (dir.empty());
	return dir;
}

void recurse_directory(std::string dir) {
	DIR *dp;
	struct dirent *entry;
	std::string pathname;
	struct stat st;
	if ((dp = Opendir(dir.c_str()))) {  // or  &dir[0]
		while ((entry = Readdir(dp))) {
			if (strcmp(entry->d_name, ".") && strcmp(entry->d_name, "..")) {
				pathname = (dir.compare("/") ? dir + "/" : dir) + std::string(entry->d_name);
				if (DEBUG) std::cerr << pathname;
					switch (entry->d_type) {
						case DT_BLK:
							if (DEBUG) std::cerr << " is block file" << std::endl;
							
							break;
						case DT_CHR:
							if (DEBUG) std::cerr << " is device file" << std::endl;
							devs.inc();
							break;
						case DT_REG:
							if (DEBUG) std::cerr << " is regular file" << std::endl;
							regs.inc();
							// get size of file
							if (lstat(pathname.c_str(), &st) == -1) {
								std::cerr << "Unable to stat file '" << pathname << "' - " << strerror(errno) << std::endl;
								exit(errno);
							}
							regs.add(st.st_size);
							break;
						case DT_LNK:
							if (DEBUG) std::cerr << " is link file" << std::endl;
							links.inc();
							// get size of file
							if (lstat(pathname.c_str(), &st) == -1) {
								std::cerr << "Unable to stat file '" << pathname << "' - " << strerror(errno) << std::endl;
								exit(errno);
							}
							links.add(st.st_size);
							break;
						case DT_DIR:
							if (DEBUG) std::cerr << " is directory file" << std::endl;
							dirs.inc();
							// get size of file
							if (lstat(pathname.c_str(), &st) == -1) {
								std::cerr << "Unable to stat file '" << pathname << "' - " << strerror(errno) << std::endl;
								exit(errno);
							}
							dirs.add(st.st_size);
							dirs.depth();
							dirs.depthArray[dirs.maxDepth] = dirs.count;
							recurse_directory(pathname);  // recursive call to descend into the directory
							break;
						case DT_FIFO:
							if (DEBUG) std::cerr << " is pipe file" << std::endl;
							pipes.inc();
							break;
						case DT_SOCK:
							if (DEBUG) std::cerr << " is socket file" << std::endl;
							socks.inc();
							break;
						case DT_UNKNOWN:
							if (DEBUG) std::cerr << " is unknown file" << std::endl;
							unks.inc();
							// get size of file
							if (lstat(pathname.c_str(), &st) == -1) {
								std::cerr << "Unable to stat file '" << pathname << "' - " << strerror(errno) << std::endl;
								exit(errno);
							}
							unks.add(st.st_size);
							break;
					}
			}
		}
		Closedir(dp);  // create a Stevens-style wrapper for closedir... easy peasy!
	}
}

DIR *Opendir(const char *name) {  // Stevens-style wrapper for opendir... nice!
	DIR *dp;
	if ((dp = opendir(name)) == NULL) {
		std::cerr << "Unable to open directory '" << name << "' - " << strerror(errno) << std::endl;  
		// normally, we wouldn't allow any error to proceed, but this is a special situation
		if (errno != 2 && errno != 13) exit(errno);   // 2 = no such file or directory, 13 = permission denied
	}
	return dp;
}

struct dirent *Readdir(DIR *dp) {  // Stevens-style wrapper for readdir... sweet!
	struct dirent *entry;
	errno = 0;
	if (((entry = readdir(dp)) == NULL) && errno) {
		std::cerr << "Unable to read directory - " << strerror(errno) << std::endl;
		exit(errno);
	}
	return entry;
}
int Closedir(DIR *dirp) {  // Stevens-style wrapper for closedir... easy peasy!
	int ret;
	if ((ret = closedir(dirp)) < 0) {
		std::cerr << "Unable to close directory - " << strerror(errno) << std::endl;
		exit(errno);
	}
	return ret;
}

// Q0: Why do we have to pass "dir.c_str()" (or "&dir[0]") to Opendir instead of just "dir"?
// A0: The Opendir function paramter is a const char*, and dir is a string. Therefore, c_str() is used to convert the string to a const char*.
//
// Q1: Why do we have to use "std::string(entry->d_name)" in the pathname assignment? I.e., why can't we just use entry->d_name?
// A1: entry->d_name is a const char*, and pathname is a string. We need to use std::string() to convert it to a string.
//
// Q2: What's the difference between stat() and lstat(), and why is lstat() used?
// A2: The stat() function will return info about the file. lstat() will return info about the link. Therefore we use lstat because we want the link information
//
// Q3: What is the purpose of comparing the directory entry to "." and ".."?
// A3: You don't want to count those directories in your search.
//
// Q4: Why, in Readdir() did DE set errno to 0 before calling readdir() and also check the value of errno in the conditional?
// A4: It sets errno to 0 becasue you don't want any previous errors to affect the outcome. errno is checked in the conditional in case there was an error in the readdir() function.
//
// Q5: Why did DE specify the globals as static? I.e., what does the static keyword do?
// A5: The static keyword prevents them from being accessed outside of the lab9 file. 
//
