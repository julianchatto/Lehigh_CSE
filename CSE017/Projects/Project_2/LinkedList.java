import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
/**
 * Class LinkedList
 * @authors Houria Oudghiri, Julian Chattopadhyay
 * Date of creation: October 10, 2021
 * Date of last modification: November 4, 2022
 */
public class LinkedList<E> implements Cloneable, List<E>{
    // Data members
	private Node head, tail;
	int size;
    // Inner class Node
    // O(1)
	private class Node{
		E value;
		Node next;
        Node previous;

        /***
         * Constructor with one paramater
         * @param initialValue for the value of the node
         * sets value to initialvalue, next and previous to null
         */
        // O(1)
		Node(E initialValue){
			value = initialValue; 
            next = null;
            previous = null;
		}
	}
    /***
     * Default constructor
     * sets hand and tail to null, size to 0
     */
    // O(1)
	public LinkedList() {
		head = tail = null;
		size = 0;
	}

    /***
     * Method to to add a value to the beginning of list
     * @param item for the value being added
     * @return true if added successfully 
     */
    // O(1)
    public boolean addFirst(E item) {
		Node newNode = new Node(item);
		if(head == null) {
            head = tail = newNode; 
        }
		else { 
            newNode.next = head;
            newNode.previous = null;
			head = newNode;
		}
		size++; 
        return true;
    }
    /***
     * Method to add a value to the end of list
     * @param item for the value being added 
     * @return true if added successfully
     */
    // O(1)
    public boolean addLast(E item) {
		Node newNode = new Node(item);
		if(head == null) { 
            head = tail = newNode; 
        }
		else { 
            newNode.previous = tail;
            tail.next = newNode; 
            tail = newNode; 
        }
		size++; 
        return true;
    }

    /***
     * Method to add an item to list
     * @param item for the value being added
     */
    // O(1)
    public boolean add(E item) {
		return addLast(item);
    }
    /***
     * Method to get the head of the list
     * @return the value of head 
     */
    // O(1)
    public E getFirst() {//O(1)
		if (head == null)
			throw new NoSuchElementException();
		return head.value;
    }
    /***
     * Method to get the tail of the list
     * @return the value of tail
     */
    // O(1)
    public E getLast() {
		if (head == null)
			throw new NoSuchElementException();
		return tail.value;
    } 
    /***
     * Method to remove the head of the list
     * @return true if removed successfully
     */
    // O(1)
    public boolean removeFirst() {
		if (head == null) 
            throw new NoSuchElementException();
		head = head.next;
		if(head == null)
            tail = null;
		size--; 
        return true;
    }
    /***
     * Method to remove the tail of the list
     * @return true if removed successfully
     */
    // O(n)
    public boolean removeLast() {
		if (head == null) 
            throw new NoSuchElementException();
		if(size == 1) 
            return removeFirst();
		Node current = head;
		Node previous = null;
		while(current.next != null) {
            previous = current;
			current = current.next;
		}
		previous.next = null; 
        tail = previous;
		size--; 
        return true;
    } 
    /***
     * Method to print the list 
     * @return the list
     */
    // O(n)
    public String toString() {
		String output = "[";
		Node node = head;
		while(node != null) {
			output += node.value + " ";
			node = node.next;
		}
		output += "]";
		return output;
    }
    /***
     * Method to clear the list
     * sets size to 0, head and tail to null
     */
    // O(1)
    public void clear() {
        head = tail = null; 
        size = 0; 
    }

    /***
     * Method to determine if list is empty
     * @return true if size = 0
     */
    // O(1)
    public boolean isEmpty() {
        return (size == 0);
    }

    /***
     * Method to get the size of list
     * @return the size of list
     */
    // O(1)
    public int size() {
        return size; 
    } 
    // Implementing an iterator for the list

    /***
     * Method to get a new LinkedListIterator
     * @return a new LinkedListIterator
     */
    // O(1)
    public Iterator<E> iterator(){
		return new LinkedListIterator();
    }
    // Inner class to implement the interface Iterator
    private class LinkedListIterator implements Iterator<E>{
		private Node current = head;
        /***
         * Method to determine if a node has a node after it
         * @return true if node has a next node
         */
        // O(1)
		public boolean hasNext() {
			return (current != null);
		}

        /***
         * Method to get the value of the next node
         * @return value of next
         */
        // O(1)
	    public E next() {
            if(current == null)
			    throw new NoSuchElementException();
			E value = current.value;
			current = current.next; 
            return value;
		}
    }
    

    /***
     * Method to get a deep copy of list
     * @return a deep copy of list
     */
    // O(n)
    public Object clone(){
        LinkedList<E> copy = new LinkedList<>();
        for(Node node = head; node!=null; node = node.next){
            copy.add(node.value);
        }
        return copy;
    }

    /*
     * NEW METHODS FROM THIS POINT
     */

     /**
     * Set intersection
     * Method to retain only the elements from otherList in the list
     * @param otherList list of elements to be retained in this list if found
     * @return true if the intersection was performed successfully
     */
    // O(n^3)
    public boolean retainAll(List<E> otherList) {
        Iterator<E> iter = otherList.iterator();
        while(iter.hasNext()) {
            E rem = iter.next();
            if(!contains(rem)) { // O(n)
                remove(rem); // O(n)
            }
        }
        return true;
        
    }

    /**
     * Method to search for a value in the list
     * @param o value to be searched for
     * @return true if the value is found, false otherwise
     */

     // O(n)
    public boolean contains(Object o) {
        Iterator<E> iter = iterator();
        while(iter.hasNext()) {
            if(iter.next().equals(o)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Set difference
     * Method to remove the elements of otherList from the list if they are found in the list
     * @param otherList list to be removed from the list
     * @return true if the elements from otherList were removed from this list successfully
     */
    // O(n^3)
    public boolean removeAll(List<E> otherList) {
        Iterator<E> iter = otherList.iterator();
        while(iter.hasNext()) {
            E rem = iter.next();
            if(contains(rem)) { // O(n)
                remove(rem); // O(n)
            }
        }
        return true;
    }

    private class ListIterators implements ListIterator<E> {
        private Node current;
        /***
         * Default constructor
         * sets current to head
         */
        // O(1)
        ListIterators() {
            current = head;

        }

        /***
         * Constructor with one parameter
         * @param index for the index of where the iterator should start
         */
        // O(n)
        ListIterators(int index) {
            current = head;
            for (int i = 0; i < index; i++) {
                current = current.next;
            }
        }

        /***
         * Method to determine if current has a next node
         * @return true if current has a next node
         */
        // O(1)
        public boolean hasNext() {
            return current != null;
        }
        
        /***
         * Method to get the value of current's next node
         * @return the value of current's next node
         */
        // O(1)
        public E next() {
            E next = current.value;
            current = current.next;
            return next;

        }

        /***
         * method to determien if current has a previous node
         * @return true if current has a previous node
         */
        // O(1)
        public boolean hasPrevious() {
            return current != null;

        }

        /***
         * Method to get the value of previous
         * @return the value of current's previous node
         */
        // O(1)
        public E previous() {
            E cur = current.value;
            current = current.previous;
            return cur;
        }

        // O(1)
        public void set(E value) {
            throw new UnsupportedOperationException();
        }
        // O(1)
        public int nextIndex() {
            throw new UnsupportedOperationException();
        }
        // O(1)
        public int previousIndex() {
            throw new UnsupportedOperationException();
        }
        // O(1)
        public void remove() {
            throw new UnsupportedOperationException();
        }
        // O(1)
        public void add(E value) {
            throw new UnsupportedOperationException();
        }

    }

    /**
     * Method to get a list iterator for the list
     * @return list iterator object associasted with this list 
     *         the iterator is positioned at the beginning of the list
     */
    // O(1)
    public ListIterator<E> listIterator() {
        return new ListIterators();
    }
     
    /**
     * Method to get a list iterator for the list at a specific position
     * @param index the position where the iterator should start
     * @return list iterator object associated with this list
     *         the iterator is positioned at index if the index is valid
     *         if index = size of the list, the iterator is positioned at the end of the list
     * throws an exception of type ArrayIndexOutOfBoundsException if index is invalid
     */
    // O(n)
    public ListIterator<E> listIterator(int index) {
        if (index >= size) {
            throw new ArrayIndexOutOfBoundsException();
        } 
        return new ListIterators(index);
    }

    /**
     * Method to get the elements of the list as an array of type Object
     * @return array of type Object containing all the elements of this list
     */
    // O(n)
    public Object[] toArray() {
        Object[] list = new Object[size];
        Iterator<E> iter = iterator();
        for(int i = 0; i < size; i++) {
            if (iter.hasNext()) {
                list[i] = iter.next();
            }
        }
        return list;
    }

    /**
     * Method to add another list to the list
     * @param otherList list to be added
     * @return true if all the elements in otherList were added to this list successfully
     */
    // O(n)
    public boolean addAll(List<E> otherList) {
        Iterator<E> iter = otherList.iterator();
        while(iter.hasNext()) {
            addLast(iter.next()); 
        }
        return true;
    }

    /**
     * Method to search for another list in the list
     * @param otherList list to be searched for
     * @return true if all the elements in otherList are found in this list, false otherwise
     */
     // O(n^2)
    public boolean containsAll(List<E> otherList) {
        Iterator<E> iter = otherList.iterator();
        while(iter.hasNext()) {
            if(!contains(iter.next())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Method to remove a value from the list
     * @param o value to find and remove from the list
     * @return true if o was found and removed, false otherwise
     */

    // O(n)
    public boolean remove(Object o) {
        Node n = head;
        if (n == null) {
            return false;
        }
        if (n.value.equals(o)) { // edge case: first element
            return removeFirst();
        }
        while(n != null) {
            if(n.value.equals(o)) {
                if (n.next == null) { // edge case: last element
                    return removeLast();
                }
                // not at head or tail
                size--;
                n.previous.next = n.next;
                n.next.previous = n.previous;
                return true;
            }
            n = n.next;
        }
        return false;
    }
    
  
    /**
     * Method to remove an element from the list
     * @param index of the element to remove
     * @return true if the element is removed successfully
     * throws an exception of type ArrayIndexOutOfBoundsException if index is invalid
     */
     // O(n)
    public boolean remove(int index) {
        if (index >= size) {
            throw new ArrayIndexOutOfBoundsException();
        }
        Node n = head;
         // decrement size for all cases
        if (index == 0) {// removing head
            return removeFirst();
        }
        if (index == (size - 1)) { // removing tail
            return removeLast();
        }

        // removing in all other cases
        for (int i = 0; i < index; i++) {
            n = n.next;
        }
        n.previous.next = n.next;
        n.next.previous = n.previous;
        size--;
        return true;
        
    }
    /**
     * Method to add value to the list at a specific index
     * @param index position where value is added
     * @param value to be added
     * @return true if the addition was successful
     */
    // O(n)
    public boolean add(int index, E value) {
        Node n = head; 
        Node newNode = new Node(value);
        
        if (index == 0) { // adding at start
            return addFirst(value);
        }
        
        if (index >= size) { // adding as last element
            return addLast(value);
        }

        for (int i = 0; i < index; i++) {
            n = n.next;
        }
        
        size++;
        newNode.previous= n.previous;
        newNode.next = n;
        n.previous.next = newNode;
        n.previous = newNode;
        return true;
    }

    /**
     * Method to get the value from the list
     * @param index of the element to get
     * @return value of the element at index
     * throws an exception of type ArrayIndexOutOfBoundsExceptiont if index is invalid
     */
    // O(n)
    public E get(int index) {
        if (index >= size) {
            throw new ArrayIndexOutOfBoundsException();
        }
        if (index == 0) { // first item in list
            return getFirst();
        }
        if (index == size-1) { // last item in list
            return getLast();
        }
        Iterator<E> iter = iterator();
        
        while (index > 0) {
            iter.next();
            index--;
        }
        return iter.next();

    }
    

     /**
     * Method to modify the value of an element in the list
     * @param index of the element to be modified
     * @param value new value of the element at index
     * @return the old value of the element at index
     * throws an  exception of type ArrayIndexOutOfBoundsException if index is invalid
     */
    // O(n)
    public E set(int index, E value) {
        if (index >= size || index < 0) {
            throw new ArrayIndexOutOfBoundsException();
        }
        Node n = head;         
        for (int i = 0; i < index; i++) {
            n = n.next;
        }
        E val = n.value;
        n.value = value;
        return val;
    }
    
}