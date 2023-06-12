#ifndef LINKEDLIST_HPP
#define LINKEDLIST_HPP
#include <stdio.h>
#include <stdlib.h>

struct Node {
	int item;
	Node* next;
};
class LinkedList {
	private: 
		LinkedList* lp;
		Node* head;
		Node* tail;
		// Create node containing item, return reference of it.
		Node* createNode(int item);
	public:
		LinkedList();
		~LinkedList();
		// Insert new item at the end of list.
		int insertAtTail(int item);

		// Insert item at start of the list.
		int insertAtHead(int item);

		// Insert item at a specified index.
		int insertAtIndex(int index, int item);

		// Remove item from the end of list and return a reference to it
		int removeTail();

		// Remove item from start of list and return a reference to it
		int removeHead();

		// Insert item at a specified index and return a reference to it
		int removeAtIndex(int index);

		// Return item at index
		int itemAtIndex(int index);

		// Returns the size of the list, measured in nodes.
		int size();

		// Print LinkedList
		void printList();

		bool contains(int item);


};


#endif