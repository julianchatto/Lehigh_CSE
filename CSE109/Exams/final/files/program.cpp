#include <stdio.h>
#include <stdlib.h>
#include <netdb.h>
#include <netinet/in.h>
#include <string.h>
#include <string>
#include <vector>
#include <unistd.h>
#include <sys/socket.h>
#include <arpa/inet.h>

typedef unsigned char u8;
typedef unsigned int u32;
typedef unsigned long u64;
typedef std::vector<u8> vec;
typedef std::string string;

#define PACK109_S8    0xaa
#define PACK109_S16   0xab
#define PACK109_A8    0xac
#define PACK109_A16   0xad
#define PACK109_M8    0xAE
#define PACK109_M16   0xaf

//File struct to be sent between client and server
struct File {
	string fileName;
	vec fileInfo;
};
// string serialize function
vec serialize(string item) {
	vec bytes;
	int count = item.size();
	if (count > 65535) { // error check
		throw;
	}
	if (count < 256) { // S8
		bytes.push_back(PACK109_S8); // push tag
		bytes.push_back((u8) count); // push count
	} else { // S16
		bytes.push_back(PACK109_S16); // push tag
		bytes.push_back((u8) 255); // push 255
		bytes.push_back((u8) count - 255); // push remainder
	}
	// Push string
	for (int i = 0 ; i < count; i++) {
		bytes.push_back(item[i]);
	}
	return bytes;
}
// string deseralize function
string deserialize_string(vec bytes) {
	if (bytes.size() < 3) { // error checking
		throw;
	}
	int upperBound = bytes[1] + 2; // s8 
	int lowerBound = 2;
	if (bytes[0] == PACK109_S16) { // if s16
		upperBound += bytes[2] + 1;
		lowerBound++;
	} else if(bytes[0] != PACK109_S8) { // neither s8 nor s16
		throw;
	}
	string temp = ""; 
	for (int i = lowerBound; i < upperBound; i++) { // push bytes of string
		temp += bytes[i];
	}
	return temp;
}

// Crypto function to encrypt/decrypt the messages
// uses 42 as the key
void crypto(char *buffer) {
	for (int i = 0; buffer[i]; i++) {
		buffer[i] ^= 42;
	}
}

// Node class
namespace linkedlist{
	template <typename Key, typename Value>
	class Node {
		public:
			Key key;
			Value value;
			size_t length;
			linkedlist::Node<Key, Value>* next;
			Node();
			Node(Key key, Value value);
			~Node();
		};

   	template <typename Key, typename Value>
    Node<Key, Value>::Node()
    {
       
    }
    template <typename Key, typename Value>
    Node<Key, Value>::Node(Key key, Value value) // stores key, value, next node
    {
        this->key = key;
		this->value = value;
        this->next = NULL;
    }
	template <typename Key, typename Value>
    Node<Key, Value>::~Node()
    {

    }
}
// linkedList class
namespace linkedlist{
template <typename Key, typename Value>
	class LinkedList {
		private:
			linkedlist::Node<Key, Value>* head;
			linkedlist::Node<Key, Value>* tail;
		public:
			size_t length;
			LinkedList();
			~LinkedList();
			size_t insertAtHead(Key item, Value val);
			Node<Key, Value> itemAtIndex(int index);
	};
	// sets head and tail to new nodes
	// sets head, tail = null, length = 0
	template <typename Key, typename Value>
    LinkedList<Key, Value>::LinkedList() 
    {
        head = new Node<Key, Value>();
        tail = new Node<Key, Value>();
        this->head=NULL;
        this->tail=NULL;
        this->length=0;
    }

	// frees all allocated memory
	template <typename Key, typename Value>
    LinkedList<Key, Value>::~LinkedList()
    {
        Node<Key, Value>* temp = this->head;
        while(temp!=NULL)
        {
            Node<Key, Value>* t = temp;
            temp = temp->next;
            free(t);
        }
    }

	// inserts k/v pair at the head 
	template <typename Key, typename Value>
    size_t LinkedList<Key, Value>::insertAtHead(Key item, Value val)
    {
       	Node<Key, Value>* node = new Node<Key, Value>(item, val);
        if (node == NULL) {
            return 1;
        }
        //if list is empty.
        if(this->head == NULL) {
            this->head = node;
            this->tail = node;
        } else {
            node->next 	= this->head;
            this->head 	= node;
        }
        this->length++;	
        return 0;
    }

	// returns the item at a specified index
    template <typename Key, typename Value>
    Node<Key, Value> LinkedList<Key, Value>::itemAtIndex(int index) {
		Node<Key, Value>* current = this->head;
		for(int i = 0;i<index;i++)
		{
			if(current->next!=NULL)
			{
				current = current->next;
			}
			else
			{
				exit(1);
			}
		}
		return *current;
	}    
}

// HashMap class
template <typename Key, typename Value>
class HashMap {
	private:
		// The backbone of the hash set. This is an array of Linked List pointers.
		linkedlist::LinkedList<Key, Value>** array;
		// The number of buckets in the array
		size_t size; 
	public:
		// Initialize an empty hash set, where size is the number of buckets in the array
		HashMap(size_t size);
		HashMap();
		// Free all memory allocated by the hash set
		~HashMap();
		// Hash an unsigned long into an index that fits into a hash set
		unsigned long hash(Key item);
		//bool insert(T item);
		Value get(Key item);
		bool insert(Key key, Value value); 
};

// sets each index in hashmap to a linkedlist
template <typename Key, typename Value>
HashMap<Key, Value>::HashMap(size_t size)
{
  this->array = new linkedlist::LinkedList<Key, Value>*[size];
  for(int i =0;i<size;i++)
  {
    this->array[i] = new linkedlist::LinkedList<Key, Value>();
  }
  this->size=size;
}

// default constructor
template <typename Key, typename Value>
HashMap<Key, Value>::HashMap()
{
  this->array = new linkedlist::LinkedList<Key, Value>*[10];
  for(int i =0;i<10;i++)
  {
    this->array[i] = new linkedlist::LinkedList<Key, Value>();
  }
  this->size=10;
}

// frees all allocations of hashMap 
template <typename Key, typename Value>
HashMap<Key, Value>::~HashMap()
{
  for(int i = 0;i<this->size;i++)
  {
    array[i]->~LinkedList(); // calls linkedlist destructor
  }
}

// hash function -> specified for only strings
template <typename Key, typename Value>
unsigned long HashMap<Key, Value>::hash(Key item)
{
	int seed = 131; 
   	unsigned long hash = 0;
  	for(int i = 0; i < item.length(); i++){
      	hash = (hash * seed) + item[i];
   	}
   	return hash % this->size;
}

// inserts key value in map, if key already exists, updates value
template <typename Key, typename Value>
bool HashMap<Key, Value>::insert(Key key, Value value)
{
	unsigned long hashedReturnVal = hash(key); // gets hash
	
	if (this->array[hashedReturnVal] == NULL) { // error check, if null, add item to list 
		this->array[hashedReturnVal] = new linkedlist::LinkedList<Key, Value>;
	}	

	for (int i = 0; i < this->array[hashedReturnVal]->length; i++) { // loops through the linkedlist at the specified hash
		if (this->array[hashedReturnVal]->itemAtIndex(i).key == key) { // if key matches, update value and return true
			this->array[hashedReturnVal]->itemAtIndex(i).value = value;
			return true;
		}
	}
	this->array[hashedReturnVal]->insertAtHead(key, value); // if no matches of key inserAtHead
	return false;
}
// gets the value of a spcified key, throws error if not found
template <typename Key, typename Value>
Value HashMap<Key, Value>::get(Key key) {
	unsigned long hashedReturnVal = hash(key); //gets hash
	if (this->array[hashedReturnVal] == NULL) { // check if index is null
		throw "get error";
	}	
	for (int i = 0; i < this->array[hashedReturnVal]->length; i++) { // loops through list
		if (this->array[hashedReturnVal]->itemAtIndex(i).key == key) { // if match return value
			return this->array[hashedReturnVal]->itemAtIndex(i).value;
		}
	}
	throw "get error";

}

int main( int argc, char** argv) {
	int sockfd, newsockfd, portno;
	socklen_t clilen;
	char buffer[65536];
	struct sockaddr_in serv_addr, cli_addr;
	int  n;

	string connectType = "localhost";

	// First call to socket() function 
	sockfd = socket(AF_INET, SOCK_STREAM, 0);
	if (sockfd < 0) { // error check
		perror("ERROR opening socket");
		exit(1);
	}

	portno = 8081; // default port number
	if (argc == 3) { // if the num arguments is 3 check for hostname flag
		if (strcmp(argv[1], "--hostname") == 0) { // if we have the hostname flag
			string arg2 = argv[2]; // get the second argument, the connection type and port number
			int colonIndex = arg2.find(":"); // index of the colon (where to split)
			if (colonIndex < 0) {
				fprintf(stderr, "No colon passed");
				exit(1);
			}
			connectType = arg2.substr(0, colonIndex); // get the connection type
			portno = atoi(arg2.substr(colonIndex+1).c_str()); // get the port number
		}
	}
	printf("Server listening on: %s:%d\n", connectType.c_str(), portno); // output server 

	// Initialize socket structure 
	bzero((char *) &serv_addr, sizeof(serv_addr));
	
	
	serv_addr.sin_family = AF_INET;
	serv_addr.sin_addr.s_addr = INADDR_ANY;
	serv_addr.sin_port = htons(portno);

	// bind the host address using bind() call
	if (bind(sockfd, (struct sockaddr *) &serv_addr, sizeof(serv_addr)) < 0) {
		fprintf(stderr, "Error: Could not start server: Os { code: 97, kind: Other, message: \"Address family not supported by protocol\" }\n");
		exit(1);
	}

    HashMap<string, File> hashm; // initilize the hashmap
	while (1) { // infinite loop
		// try-catch to prevent any errors
		try { 
			printf("---------------------------------------------\n");
			// listening for the clients, process will go in sleep mode and will wait for the incoming connection
			listen(sockfd,5);
			clilen = sizeof(cli_addr);
			
			// Accept connection from the client 
			newsockfd = accept(sockfd, (struct sockaddr *) &cli_addr, &clilen);
			
			if (newsockfd < 0) { // error check
				perror("ERROR on accept");
				exit(1);
			}
			
			// If connection is established then start communicating 
			bzero(buffer, 65536);
			n = read(newsockfd, buffer, 65536);
			
			if (n < 0) { // error check
				perror("ERROR reading from socket");
				exit(1);
			}
			// get client ip and port
			char* client_ip = inet_ntoa(cli_addr.sin_addr);
			int client_port = ntohs(cli_addr.sin_port);
			printf("New connection: %s:%d\n", client_ip, client_port);


			crypto(buffer); // decrypt the message

			vec bvec{}; // convert buffer to bytes
			for (int i = 0; buffer[i]; i++) {
				bvec.push_back(buffer[i]);
			}
			
			if (bvec.size() == 0) { // error check
				printf("No message.\n");
				printf("Terminating connection with %s:%d\n", client_ip, client_port);
				printf("Connection terminated.\n", bvec.size());
				continue;
			}

			// Beginning reading bytes
			printf("Reading %d bytes.\n", bvec.size()); 

			if (bvec[0] != PACK109_M8) { // error check
				printf("ERROR: Invalid message\n");
				continue;
			}

			if (bvec.size() < 3 || (bvec[0] != PACK109_M8 && bvec[0] != PACK109_M16)) { // error check
				throw;
			}

			int i = 2;
			if (bvec[2] != PACK109_S8) { // error check
				throw;
			}

			vec tempVec{};
			for (; i < bvec[3] + 4; i++) { // reading file type
				tempVec.push_back(bvec[i]);
			}
			string type = deserialize_string(tempVec);
			
			if (type == "File") {
				File newFile; // create file return type

				if (bvec[i] != PACK109_M8) { // error check
					throw;
				}

				while(bvec[i] != PACK109_S8) { // advancing i to the filename
					i++;
				}
				i++;

				while(bvec[i] != PACK109_S8) { // advancing i to the filename
					i++;
				}

				vec tV{}; // bytes for the filename
				int stopVal = bvec[i+1] + i + 2;
				for (; i < stopVal; i++) { // reading filename
					tV.push_back(bvec[i]);
				}
			
				newFile.fileName = deserialize_string(tV); // set newFile's filename

				printf("Recieved File: \"%s\"\n", newFile.fileName.c_str());

				while(bvec[i] != PACK109_S8) { // advancing i to the fileInfo
					i++;
				}
				i++;
				while(bvec[i] != PACK109_A8 || bvec[i] != PACK109_A8) { // advancing i to the fileInfo
					i++;
				}

				stopVal = i + 2*bvec[i+1] + 3;
				for (; i < stopVal; i++) { // reading file info
					newFile.fileInfo.push_back(bvec[i]);
				}

				if (hashm.insert(newFile.fileName, newFile)) {
					printf("File replaced.\n");
				} else {
					printf("File stored.\n");
				}

			} else { // Request
				while(bvec[i] != PACK109_S8) { // advancing i to the key
					i++;
				}
				i++;

				while(bvec[i] != PACK109_S8) { // advancing i to the key
					i++;
				}
			
				vec tempVec{}; // converting to a vec
				int upperBound = i + bvec[i + 3] + 2;
				for (; i < upperBound; i++) { // reading key
					tempVec.push_back(bvec[i]);
				}

				string key = deserialize_string(tempVec); // deserialize the key

				printf("Recieved Request: \"%s\"\n", key.c_str());

				File newFile; // create file return type

				// error check to ensure that any error thrown from get is handeled
				try {
					newFile = hashm.get(key); // get(key)
				} catch (...) {
					printf("File not found: \"%s\"\n", key.c_str());
					printf("Terminating connection with %s:%d\n", client_ip, client_port);
			
					n = write(newsockfd, " ", 1);
					if (n < 0) {
						perror("ERROR writing to socket");
						exit(1);
					}
					printf("Connection terminated.\n", bvec.size());
				}
				
				// vector to be sent to client
				vec retVec{};
				retVec.push_back(PACK109_M8);
				retVec.push_back(0x01);
				retVec.push_back(PACK109_S8);
				retVec.push_back(0x04);
				retVec.push_back(0x46);
				retVec.push_back(0x69);
				retVec.push_back(0x6c);
				retVec.push_back(0x65);
				retVec.push_back(PACK109_M8);
				retVec.push_back(0x02);
				
				vec tempVec2;	
				tempVec2 = serialize("name"); 
				for (int i = 0; i < tempVec2.size(); i++) { // pushing name to retVec
					retVec.push_back(tempVec2[i]);
				}

				vec tempVec3;	
				tempVec3 = serialize(newFile.fileName);
				for (int i = 0; i < tempVec3.size(); i++) { // pushing the filename to retVec
					retVec.push_back(tempVec3[i]);
				}
				
				vec tempVec4;	
				tempVec4 = serialize("bytes"); 
				for (int i = 0; i < tempVec4.size(); i++) { // pushing bytes to retVec
					retVec.push_back(tempVec4[i]);
				}

				for (int i = 0; i < newFile.fileInfo.size(); i++) { // pushing fileinfo to retVec
					retVec.push_back(newFile.fileInfo[i]);
				}

				char* ret2 = (char*)retVec.data(); // convert vector to char*
				crypto(ret2); // encrypt message

				// Write a response to the client 
				printf("Sending \"%s\" -- %d bytes\n", newFile.fileName.c_str(), retVec.size()-1);

				n = write(newsockfd, ret2, retVec.size()); // write to client
				if (n < 0) { // error check
					perror("ERROR writing to socket");
					exit(1);
				}

				printf("Message sent.\n");
			} 
			// end connection
			printf("Terminating connection with %s:%d\n", client_ip, client_port);
			printf("Connection terminated.\n", bvec.size());
		} catch (...) {
			printf("ERROR: caught\n");
		}
	}
	
   	return 0;
}