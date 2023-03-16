import java.util.Iterator;
import java.util.NoSuchElementException;
/***
 * Class to model the entity LinkedList
 * @author Julian Chattopadhyay
 * @version 0.1
 * Date of creation: October 19, 2022
 * Last Date Modified: October 25, 2022
 */
public class LinkedList<E>{
    // Data members
    private Node head, tail;
    int size;
    
    /***
     * Private class Node
     * Creates a Node that is an object
     */
    private class Node{
        E value;
        Node next;
        Node(E initialValue){
            value = initialValue; 
            next = null;
        }
    }
    /***
     *  Default Constructor
     *  Initializes head and tail to null and size to 0
     */ 
    public LinkedList() { // O(1)
        head = tail = null;
        size = 0;
    }
    /***
     * Method to add item to begining of a linkedList 
     * @param item the object being added
     * @return  true if add was successful
     */
    public boolean addFirst(E item) { // O(1)
        Node newNode = new Node(item);
        if(head == null) { // adding the first element
            head = tail = newNode; 
        } else { 
            newNode.next = head;
            head = newNode;
        }
        size++; 
        return true;
    }
    /***
     * Method to add item to end of a LinkedList
     * @param item the object being added
     * @return true if add was successful
     */
    public boolean addLast(E item) { // O(1)
        Node newNode = new Node(item);
        if(head == null) { 
            head = tail = newNode; 
        } else { 
            tail.next = newNode; 
            tail = newNode; 
        }
        size++; 
        return true;
    }
    /***
     * Helper Method to call addFirst
     * @param item for item being added
     * @return addFirst(item)
     */
    public boolean add(E item) { // O(1)
        return addFirst(item);
    }

    /***
     * Method to get the element at the front
     * @return the element at the front
     */
    public E getFirst() { // O(1)
        if (head == null) {
            throw new NoSuchElementException();
        }    
        return head.value;
    }

    /***
     * Method to get the element at the end
     * @return the element at the end
     */
    public E getLast() { // O(1)
        if (head == null) {
            throw new NoSuchElementException();
        }
        return tail.value;
    }

    /***
     * Method to remove the first element
     * @return true if was successful
     */
    public boolean removeFirst() { // O(1)
        if (head == null) {
            throw new NoSuchElementException();
        }
        head = head.next;
        if(head == null) { // only one element in the linked list
            tail=null;
        }
        size--; 
        return true;
    }

    /***
     * Method to remove the remove the last element
     * @return true if was successful
     */
    public boolean removeLast() { // O(n)
        if (head == null)  {
            throw new NoSuchElementException();
        }
        if(size == 1) {
            return removeFirst();
        }
        // traverse the linked list
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
     * Method to return the contents of the LinkedList
     * @return contents of LinkedList
     */
    public String toString() { // O(n)
        String output = "[";
        Node node = head;
        while(node != null) { // traverse the linked list
            output += node.value + " ";
            node = node.next;
        }
        output += "]";
        return output;
    }
    /***
     * Method to clear the Linked list
     */
    public void clear() {  // O(1)
        head = tail = null; 
        size = 0; 
    }
    /***
     * Method to determine if the LinkedList is empty
     * @return true if empty
     */
    public boolean isEmpty() {  // O(1)
        return (size == 0); 
    }
    /***
     * Method to return the size of the linkedList
     * @return the size of LinkedList
     */
    public int size() { // O(1)
        return size; 
    }

    /***
     * Method to create a LinkedListIterator
     * @return a LinkedListIterator
     */
    public Iterator<E> iterator(){ // O(1)
        return new LinkedListIterator();
    }

    /***
     * private class LinkedListIterator
     * 
     */
    private class LinkedListIterator implements Iterator<E>{
        private Node current = head;
        /***
         * Method to determine if there is another element in the list
         * @return true if there is an element after current element
         */
        public boolean hasNext() { // O(1)
            return (current != null);
        }
        /***
         * Method to get the next element
         * @return the next element
         */
        public E next() { // O(1)
            if(current == null) {
                throw new NoSuchElementException();
            }
            E value = current.value;
            current = current.next; return value;
        }


    }

    /***
     * Method to check if a LinkedList has an object o
     * @return the number of iterations to find o
     */
    // O(n)
    public int contains(Object o) { 
        int iterations = 0;
        E item = (E) o;
        Iterator<E> iter = iterator();
        while(iter.hasNext()) {
            iterations++;
            if(iter.next().equals(item)) {
                return iterations;
            }
        }
        return iterations;
    }

    /***
     * Method to remove an object o from a linked list
     * @return the number of iterations to find and remove o
     */
    // O(n)
    public int remove(Object o) { 
        E item = (E) o;
        Node node = head;
        Node previous = null;
        int iterations = 0;
        while(node!=null) {
            iterations++;
            if(node.value.equals(item)) {
                break;
            }
            previous = node;
            node = node.next;
        }
        if(node!= null) { // value was found 
            if (node == head) {
                removeFirst();
            } else {
                previous.next = node.next;
            }
        }
        return iterations;
    }

    /***
     * Method to add an element at a specified index
     * @param index for the index being added
     * @param item the object being added
     * @return the numeber of iterations to find and add e
     */
    // O(n)
    public int add(int index, E item) { 
        int iterations = 0;
        if(index> size || index < 0) {
            throw new ArrayIndexOutOfBoundsException();
        }
        if (index==0) {
            addFirst(item);
            return iterations;
        }
        if (index == size-1) {
            addLast(item);
            return iterations;
        }
        Node node = head;
        Node previous = null;
        int i = 0;
        while (i < index) { // O(n)
            iterations++;
            i++;
            previous = node;
            node = node.next;
        }

        Node newNode = new Node(item);
        previous.next = newNode;
        newNode.next = node;
        size++;
        return iterations;
    }

}    