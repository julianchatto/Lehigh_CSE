#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include "linkedlist.h"

void printList(List* list) {
	Node* node;

  	// Handle an empty node. Just print a message.
	if(list->head == NULL) {
		printf("\nEmpty List");
		return;
	}
	
  	// Start with the head.
	node = (Node*) list->head;

	printf("\nList: \n\n\t"); 
	while(node != NULL) {
		printf("[ %x ]", node->item);

		// Move to the next node
		node = (Node*) node->next;

		if(node !=NULL) {
			printf("-->");
		}
	}
	printf("\n\n");
}
// Initialize an empty list
void initList(List* list_pointer) {
	if (list_pointer != NULL) { // confirms list_pointer is not null
		// initiate head and tail to NULL
		list_pointer->head = NULL;
		list_pointer->tail = NULL;
	} else {
		exit(99);
	}
}

// Create node containing item, return reference of it.
Node* createNode(void* item) {
	Node* newNode = (Node*)malloc(sizeof(Node)); // allocates memory for newNode
	// sets item and next
	newNode->item = item;
	newNode->next = NULL;
	return newNode;
}

// Insert new item at the end of list.
int insertAtTail(List* list_pointer, void* item) {
	Node* newNode = createNode(item); // creates newNode
	if (newNode == NULL) { // error check
		free(newNode);
		return 1;
	}
	if (list_pointer->head == NULL) { // if list is empty
		list_pointer->head = newNode;
	} else  {
		list_pointer->tail->next = (struct Node*) newNode; // changes current tail to point to the newNode
	}
	list_pointer->tail = newNode; // updates tail to newNode
	return 0;
}

// Insert item at start of the list.
int insertAtHead(List* list_pointer, void* item) {
	Node* newNode = createNode(item); // creates newNode
	if (newNode == NULL) { // error check
		free(newNode);
		return 1;
	}
	if (list_pointer->head == NULL) { // if list is empty
		list_pointer->tail = newNode;
	} else {
 		newNode->next = (struct Node*) list_pointer->head; // cheanges newNode next to the current head
	}
	list_pointer->head = newNode; // updates ehad to newNode
	return 0;
}

// Insert item at a specified index.
int insertAtIndex(List* list_pointer, int index, void* item) {
	int listSize = size(list_pointer);
	if (index < 0 || index > listSize) { // index greater/less than number of elements
		return 1;
	}
	if (index == 0) { // inserting at head
		return insertAtHead(list_pointer, item);
	}
	if (index == listSize-1) { // inserting at tail
		return insertAtTail(list_pointer, item);
	}
	Node* newNode = createNode(item); // create new node
	if (newNode == NULL) { // error check
		free(newNode);
		return 1;
	}
	Node* tempNode = list_pointer->head; 
	int i = 0;
	while (i < index-1) { // advance to the index before where the new node is to be added
		tempNode = (Node*) tempNode->next;
		i++;
	}
	
	newNode->next = tempNode->next; // updating newNode next
	tempNode->next = (struct Node*) newNode; // sets index before addition next to newNode
	return 0;
}

// Remove item from the end of list and return a reference to it
void* removeTail(List* list_pointer) {
	if (list_pointer->head == NULL) { // makes sure list is not empty
		return NULL;
	}
	if (list_pointer->head->next == NULL) { // if there is only one element
		return removeHead(list_pointer);
	}
	Node* tempNode = list_pointer->tail; 
	Node* tempNode2 = list_pointer->head;
	int listSize = size(list_pointer);
	int i = 0;
	while(i < listSize-2) { // advances pointer 
		tempNode2 = (Node*) tempNode2->next;
		i++;
	}
	list_pointer->tail = tempNode2; 
	tempNode2->next = NULL;
	void* x = tempNode->item;
	free(tempNode);
	return x;
}

// Remove item from start of list and return a reference to it
void* removeHead(List* list_pointer) {
	if (list_pointer->head == NULL) {
		return NULL;
	}
	Node* tempNode = list_pointer->head;
	Node* newNode = (Node*) list_pointer->head->next;
	list_pointer->head = newNode;
	if (list_pointer->head == NULL) { // advance index
		list_pointer->tail = NULL;
	}
	void* x = tempNode->item;
	free(tempNode);
	return x;

}

// Insert item at a specified index and return a reference to it
void* removeAtIndex(List* list_pointer, int index) {
	int listSize = size(list_pointer);
	if (index < 0 || index > listSize) { // index greater than number of elements
		return NULL;
	}
	if (index == 0) {
		return removeHead(list_pointer);
	}
	if (list_pointer->head == NULL) { // No elements in the list
		return NULL;
	}
	if (index == listSize-1) {
		return removeTail(list_pointer);
	}
	Node* tempNode = list_pointer->head;
	int i = 0;
	while (i < (index-1)) { // advance to the index before where the new node is to be removed
		tempNode = (Node*) tempNode->next;
		i++;
	}
	Node* removed = (Node*) tempNode->next;
	tempNode->next = removed->next;
	void* x = removed->item;
	free(removed);

	return x;


}

// Return item at index
void* itemAtIndex(List* list_pointer, int index) {
	if (index < 0 || index > size(list_pointer)) { // making sure index is valid
		return NULL;
	}
	if (list_pointer->head == NULL) { // error check
		return NULL;
	}
	Node* tempNode = list_pointer->head;
	int i = 0;
	while(i < index) {  // advance to index
		tempNode = (Node*) tempNode->next;
		i++;
	}
	return tempNode->item;
}

// Return true if the list contains the given item at least once, false otherwise.
bool contains(List* list_pointer, void* item) {
	int *item2 = (int*) item; 
	Node* tempNode = list_pointer->head; 
	while (tempNode != NULL) { // advances pointer
		int *temp1 = (int*) tempNode->item;
		if (*temp1 == *item2) {
			return true;
		}
		tempNode = (Node*) tempNode->next;
	}
	return false;
}

// Returns the size of the list, measured in nodes.
int size(List* list_pointer) {
	int i = 0;
	Node* tempNode = list_pointer->head;
	while (tempNode != NULL) { // advances to end of list
		tempNode = (Node*) tempNode->next;
		i++;
	}
	return i;
}

// returns a void pointer to item
void* makeVoid(int* item) {
	return item;
}

void delList(List* list) {
	while (removeHead(list) != NULL); free(list);
}
