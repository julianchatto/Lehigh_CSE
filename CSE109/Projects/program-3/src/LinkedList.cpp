#include "LinkedList.hpp"

// LinkedList constructor 
LinkedList::LinkedList() {
	lp = (LinkedList*) malloc(sizeof(LinkedList)); // allocate memory for list
	if (lp == NULL) { // make sure allocation was succseful 
		exit(1);
	}
	// set head and tail to NULL
	head = NULL;
	tail = NULL;
}
LinkedList::~LinkedList() {
	while (removeHead() != -1){ // remove all the nodes

	} 
	free(lp); // free list 
}

//Create node and return reference of it.
Node* LinkedList::createNode(int item) {
	Node* nNode;

	nNode = (Node *) malloc(sizeof(Node)); // allocate memory for node
	if (nNode == NULL) { // make sure allocation is succesful 
		return NULL;
	}

	// characteristics of node
	nNode->item = item; 
	nNode->next = NULL;

	return nNode;
}


//Add new item at the end of list.
int LinkedList::insertAtTail(int item) {
	Node* node;
	node = createNode(item);
	if (node == NULL) {
		return 1;
	}

	//if list is empty.
	if(head == NULL) {
		head = node;
		tail = node;
	} else {
		tail->next = node;
		tail = tail->next;
	}	
	return 0;	
}


//Add new item at beginning of the list.
int LinkedList::insertAtHead(int item) {
	Node* node;
	node = createNode(item);
	if (node == NULL) {
		return 1;
	}


	//if list is empty.
	if(head == NULL) {
		head = node;
		tail = node;
	} else {
		node->next = head;
		head = node;
	}		
	return 0;
}

//Add new item at beginning of the list.
int LinkedList::insertAtIndex(int index, int item) {
	Node* to_insert;
	to_insert = createNode(item);
	if (to_insert == NULL) {
		return 0;
	}

	int i = 0;
	Node* prev;
	Node* node = head;
	while (node != NULL) {
		if (i == index) {
			prev->next = to_insert;
			to_insert->next = node;
			return 0;
		} else if (i > index) {
			return 0;
		} else {
			i++;
			prev = node;
			node = node->next;
		}
	}	
	return	0; 
}

int LinkedList::removeAtIndex(int index) {
	int listSize = size();
	if (index < 0 || index > listSize) { // index greater than number of elements
		return -1;
	}
	if (index == 0) {
		return removeHead();
	}
	if (head == NULL) { // No elements in the list
		return -1;
	}
	if (index == listSize-1) {
		return removeTail();
	}
	Node* tempNode = head;
	int i = 0;
	while (i < (index-1)) { // advance to the index before where the new node is to be removed
		tempNode = (Node*) tempNode->next;
		i++;
	}
	Node* removed = (Node*) tempNode->next;
	tempNode->next = removed->next;
	int x = removed->item;
	free(removed);

	return x;
}

//Delete item from Start of list.
int LinkedList::removeHead() {
	int item;

	if(head == NULL) {	
		// LinkedList is Empty	
		return -1;
	} else {
		item = head->item;
		Node* old_head = head;
		head = head->next;	
		free(old_head);	
	}	
	return item;
}

//Delete item from the end of list.
int LinkedList::removeTail() {
	struct Node * temp;
	
	int item;

	if(tail == NULL) {	
		// LinkedList is Empty	
		return -1;
	}
	else {
		temp = head;

		// Iterate to the end of the list
		while(temp->next != tail) { 
			temp = temp->next;
		}

		item = tail->item;

		Node* old_tail = tail;
		tail = temp;
		tail->next = NULL;	
		free(old_tail);	
	}	
	return item;
}

int LinkedList::itemAtIndex(int index) {
	int i = 0;
	Node* node = head;
	while (node != NULL) {
		if (i == index) {
			return node->item;
		} else if (i > index) {
			return -1;
		} else {
			i++;
			node = node->next;
		}
	}
	return -1;
}

void LinkedList::printList() {
	Node* node;

  // Handle an empty node. Just print a message.
	if(head == NULL) {
		printf("\nEmpty LinkedList");
		return;
	}
	
  // Start with the head.
	node = (Node*) head;
 
	while(node != NULL) {
		printf("[ %d ]", node->item);

    // Move to the next node
		node = (Node*) node->next;

		if(node != NULL) {
			printf("-->");
    }
	}
	
}

// Returns the size of the listmeasured in nodes.
int LinkedList::size() {
	int i = 0;
	Node* tempNode = head;
	while (tempNode != NULL) { // advances to end of list
		tempNode = (Node*) tempNode->next;
		i++;
	}
	return i;
}

bool LinkedList::contains(int item) {
	Node* tempNode = head; 
	while (tempNode != NULL) { // advances pointer
		int temp1 = tempNode->item;
		if (temp1 == item) {
			return true;
		}
		tempNode = (Node*) tempNode->next;
	}
	return false;
}
