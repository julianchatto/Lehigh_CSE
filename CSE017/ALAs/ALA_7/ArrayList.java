import java.util.Iterator;
/***
 * Class to model the entity ArrayList
 * @author Julian Chattopadhyay
 * @version 0.1
 * Date of creation: October 19, 2022
 * Last Date Modified: October 25, 2022
 */
public class ArrayList<E> {
    private E[] elements;
    private int size;
    
    /***
     * Default constructor
     * Initializes elements to an array of length 10 and size 0
     */
    public ArrayList() { // O(1)
        elements = (E[]) new Object[10];
        size = 0;
    }
    /***
     * Constructor with 1 parameter
     * @param capacity for the size of the array
     */
    public ArrayList(int capacity) { //O(1)
        elements = (E[]) new Object[capacity];
        size = 0;
    }
    // Adding an item to the list (2 methods)
    public int add(E item) { // O(n)
        return add(size, item); // adding at the last index
    }

    /***
     * Method to get the element at a specified index
     * @param index for the index
     * @return the element at index
     */
    public E get(int index) { // O(1)
        checkIndex(index);
        return elements[index];
    }
    /***
     * Method to set a specfied index to item
     * @param index for the item
     * @param item for the object being set 
     * @return the original object
     */
    public E set(int index, E item) { // O(1)
        checkIndex(index);
        E oldItem = elements[index];
        elements[index] = item;
        return oldItem;
    }
    /***
     * Method to get the size of the array
     * @return the size of the array
     */
    public int size() {  // O(1)
        return size; 
    }

    /***
     * Method to clear the array
     */
    public void clear() {  // O(1)
        size = 0; 
    }

    /***
     * Method to check if the array is empty
     * @return true if empty
     */
    public boolean isEmpty() {  // O(1)
        return (size == 0);
    }

    
    /***
     * Method to remove an object at a specified index
     * @param index the specified index
     * @return the original item
     */    
    public E remove(int index) { // O(n)
        checkIndex(index);
        E item = elements[index];
        for(int i=index; i<size-1; i++) {
            elements[i] = elements[i+1];
            size--;
        }
        return item;
    }

    /***
     * Method to reduce the size of the arrayList to the number of objects
     */
    public void trimToSize() {  // O(n)
        if (size != elements.length) {
            E[] newElements = (E[]) new Object[size]; // fixing the capacity to size
            for(int i=0; i<size; i++) {
                newElements[i] = elements[i];
            }
            elements = newElements;
        }
    }
    /***
     * Increases the size of the array if full
     * @return the number of iterations 
     */
    private int ensureCapacity() { // worst case O(n)
        int iterations = 0;
        if(size >= elements.length) {
            int newCap = (int) (elements.length * 1.5);
            E[] newElements = (E[]) new Object[newCap];
            for(int i=0; i<size; i++) {
                iterations++;
                newElements[i] = elements[i];
            }
            elements = newElements;
        }
        return iterations;
    }

    /***
     * Method to check if an index is valid
     * @param index the index to be checked
     */
    private void checkIndex(int index){ // O(1)
        if(index < 0 || index >= size) {
            throw new ArrayIndexOutOfBoundsException("Index out of bounds. Must be between 0 and "+ (size-1));
        }
    }
    /***
     * Method to print out the contents of ArrayList
     * @return the formatted string
     */
    public String toString() { // O(n)
        String output = "[";
        for(int i=0; i<size-1; i++) {
            output += elements[i] + " ";
        }
        output += elements[size-1] + "]";
        return output;
    }

    /***
     * Creates a new ArrayIterator 
     * @return ArrayIterator
     */
    public Iterator<E> iterator(){ // O(1)
        return new ArrayIterator();
    }
    // Inner class that implements Iterator<E>
    private class ArrayIterator implements Iterator<E>{
        private int current = -1;
        /***
         * Method to check if there is another element in the list
         * @return true if there is another element
         */
        public boolean hasNext() {  // O(1)
            return current < size-1; 
        }
        /***
         * Method to get the next element
         * @return the next element
         */
        public E next() { // O(1)
            return elements[++current]; 
        }

    }
    /***
     * Method to check if the ArrayList contains a specified object
     * @param o for the specified object
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
     * Method to remove a specified object from the ArrayList 
     * @param o for the specified object
     * @return the number of iterations to remove o
     */
    // O(n)
    public int remove(Object o) { 
        int iterations = 0;
        E item = (E) o;
        for(int i=0; i<size; i++) { // O(n)
            iterations++;
            if(elements[i].equals(item)) {
                for(int j = i; j<size-1; j++) { // O(n)
                    iterations++;
                    elements[j] = elements[j+1];
                }
                size--;
                return iterations;
            }
        }        
        return iterations;
    }
    /***
     * Method to add a specified object from the ArrayList 
     * @param o for the specified object
     * @return the number of iterations to add o
     */
    // O(n)
    public int add(int index, E item) { 
        int iterations = 0;
        if(index > size || index < 0) {
            throw new ArrayIndexOutOfBoundsException();
        }
        iterations = ensureCapacity(); // O(n)
         
        for(int i=size-1; i>=index; i--){ // O(n)
            iterations++;
            elements[i+1] = elements[i];
        }
        elements[index] = item;
        size++;
        return iterations;
    }
}
