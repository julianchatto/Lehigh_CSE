#include "linkedlist.h"
#include <stdio.h>    
#include <stdlib.h>   
#include <string.h> 
#include <unistd.h>

// This file is here for you to test your library as you write your code.
// You can write anything in here; it will not be checked for grading purposes.
int main() {
	FILE *srcFile = fopen("/home/juc226/CSE109/Projects/program-2/src/tests", "r"); // opening tests in read mode
  	if (srcFile == NULL) {
    	fprintf(stderr, "Cannot open tests");
    	exit(99);
  	}

	int totalNumTestsPassed = 0;  // holds number of tests passed 
	int testNum = 1; // holds current testNumber
	
	// Variables for getline()
	char *buffer = NULL;   
  	size_t length = 0;   
  	ssize_t numChars = 0;
	while ((numChars = getline(&buffer, &length, srcFile)) != -1) { // Outer loop that goes through each line in tests
		if ((buffer[0] == '-' && buffer[1] == '-' )|| buffer[0] == '\n') { // checks if the line starts with "--"
			continue;
		}
		List* list = (List*) malloc(sizeof(List)); // initate list
		initList(list);

		int* nodeArray = (int*)malloc(sizeof(int) * length); // allocates memory for array that stores values of the nodes. multiplying by length gaurantees that there is enough allocated memory
		int nodeArrayCurIndex = 0; // holds current index for nodeArray
		int bufferIndex = 0;  // holds current index for buffer

		int numtestPass = 0; // holds the number of tests that have passed
		int numTests = 0; // holds the number of tests being run
		printf("Test number: %d\n", testNum);
		//char* temp; // holds current action values 
		while (buffer[bufferIndex] != 'X') {
			char* temp = (char*)malloc(sizeof(char)*10); // allocates memory for temp
			int j = bufferIndex+1; // sets j so that the letter can be skipped
			int k = 0;
			while (buffer[j] != 'X' && buffer[j] != 'N' && buffer[j] != 'H' && buffer[j] != 'T' && buffer[j] != 'E' && buffer[j] != 'D' && buffer[j] != 'C' && buffer[j] != 'Q' && buffer[j] != 'A' && buffer[j] != 'Z' && buffer[j] != 'S' && buffer[j] != 'W') {
				temp[k] = buffer[j]; // setts the values for temp
				j++, k++;;
			}
			numTests++;
			int index;  // holds index read from sscanf
			int item; // hold item read from sscanf
			switch(buffer[bufferIndex]) {
				case 'N': // insert node
					sscanf(temp, "%d,%d", &index, &item);
					nodeArray[nodeArrayCurIndex] = item; // sets nodeArray to value of item so that each node has a unique memory address for item
					if (insertAtIndex(list, index, makeVoid(&nodeArray[nodeArrayCurIndex])) == 0) {
						printf("insertAtIndex successful");
						numtestPass++;
					} else {
						printf("insetAtIndex failed");
					}
					nodeArrayCurIndex++;
					break;
				case 'H': // insert-head
					sscanf(temp, "%d",  &item); 
					nodeArray[nodeArrayCurIndex] = item; // sets nodeArray to value of item so that each node has a unique memory address for item
					if (insertAtHead(list, makeVoid(&nodeArray[nodeArrayCurIndex])) == 0) {
						printf("insertAtHead successful");
						numtestPass++;
					} else {
						printf("insertAtHead failed");
					}
					nodeArrayCurIndex++;
					break;
				case 'T': // insert=tail
					sscanf(temp, "%d", &item);
					nodeArray[nodeArrayCurIndex] = item; // sets nodeArray to value of item so that each node has a unique memory address for item
					if (insertAtTail(list, makeVoid(&nodeArray[nodeArrayCurIndex])) == 0) {
						printf("insetAtTail successful");
						numtestPass++;
					} else {
						printf("insetAtTail failed");
					}
					nodeArrayCurIndex++;;
					break;
				case 'E': // remove-node
					sscanf(temp, "%d", &index);
					if (removeAtIndex(list, index) != NULL) {
						printf("removeAtIndex successful");
						numtestPass++;
					} else {
						printf("removeAtIndex not successful");
					}
					break;
				case 'D': // remove-head
					if (removeHead(list) != NULL) {
						printf("removeHead successful");
						numtestPass++;
					} else {
						printf("removeHead not successful");
					}
					break;
				case 'C': // remove-tail
					if (removeTail(list) != NULL) {
						printf("removeTail successful");
						numtestPass++;
					} else {
						printf("removeTail not successful");
					}
					break;
				case 'Q': // assert-node
					sscanf(temp, "%d,%d", &index, &item);
					nodeArray[nodeArrayCurIndex] = item; // sets nodeArray to value of item so that each node has a unique memory address for item
					if (strcmp((char*) itemAtIndex(list, index), (char*) makeVoid(&nodeArray[nodeArrayCurIndex])) == 0) { // compares values
						printf("assert-node (Q) successful");
						numtestPass++;
					} else {
						printf("assert-node (Q) failed");
					}
					nodeArrayCurIndex++;;
					break;
				case 'A': // assert-head
					sscanf(temp, "%d", &item);
					nodeArray[nodeArrayCurIndex] = item; // sets nodeArray to value of item so that each node has a unique memory address for item
					if (strcmp((char*) itemAtIndex(list, 0), (char*) makeVoid(&nodeArray[nodeArrayCurIndex])) == 0) { // compares values
						printf("assert-head (A) successful");
						numtestPass++;
					} else {
						printf("assert-head (A) failed");
					}
					nodeArrayCurIndex++;
					break;
				case 'Z': // asser-tail
					sscanf(temp, "%d",  &item);
					nodeArray[nodeArrayCurIndex] = item; // sets nodeArray to value of item so that each node has a unique memory address for item
					if (strcmp((char*) itemAtIndex(list, size(list)-1), (char*) makeVoid(&nodeArray[nodeArrayCurIndex])) == 0) {
						printf("assert-tail (Z) successful");
						numtestPass++;
					} else {
						printf("assert-tail (Z) failed");
					}
					nodeArrayCurIndex++;;
					break;
				case 'S': // assert-size
					sscanf(temp, "%d",  &index);
					int listSize = size(list);
					if (listSize == index) {
						printf("Size is correct!");
						numtestPass++;
					} else {
						printf("Size is incorrect!");
					}
					break;
				case 'W': // assert-contains
					sscanf(temp, "%d",  &item);
					nodeArray[nodeArrayCurIndex] = item; // sets nodeArray to value of item so that each node has a unique memory address for item
					if (contains(list, &nodeArray[nodeArrayCurIndex])) {
						printf("contains successful");
						numtestPass++;
					} else {
						printf("contains failed");
					}
					nodeArrayCurIndex++;;
					break;
				default:
					bufferIndex++;
					break;
			}
			bufferIndex = j;
			printList(list);
			free(temp);
		}
		free(nodeArray);
		delList(list);
		
		printf("\n\nFOR TEST %d, %d TESTS PASSED OUT OF %d\n", testNum, numtestPass, numTests);
		if (numTests == numtestPass) {
			printf("ALL TEST SUCCESSFUL\n");
			totalNumTestsPassed++;
		} else {
			printf("%d TESTS FAILED\n", numTests-numtestPass);
		}
		testNum++;
		
	}
	free(buffer);
	if (totalNumTestsPassed == testNum-1) {
		printf("\n\nALL %d TESTS PASSED\n\n", testNum-1);
	} else {
		printf("\n\nNOT ALL TESTS PASSED: %d FAILED TESTS\n\n", testNum - 1 - totalNumTestsPassed);
	}
	fclose(srcFile);
	return 0;
}
