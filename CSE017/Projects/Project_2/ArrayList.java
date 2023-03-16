import java.util.Iterator;
import java.util.ListIterator;
/**
 * Class ArrayList
 * @author Houria Oudghiri, Julian Chattopadhyay
 * Date of creation:  October 21, 2021
 * Date of last modification: November 4, 2022
 */
public class ArrayList<E> implements Cloneable, List<E> {
    // data members
    private E[] elements;
    private int size; 
    /***
     * Default constructor
     * sets elements to array of of type E length 10, size to 0
     */
    // O(1)
    public ArrayList() {
        elements = (E[]) new Object[10];
        size = 0;
    }
    /***
     * Constructor with one parameter
     * @param capacity for the length of elements
     * sets elements to array of of type E length capacity, size to 0
     */
    // O(1)
    public ArrayList(int capacity) {
        elements = (E[]) new Object[capacity];
        size = 0;
    }
    /**
     * Method to add value to the list
     * @param value to be added
     * @return true if the addition was successful
     */

    // O(n)
    public boolean add(E item) {
        return add(size, item);
    }
    
    /**
     * Method to add value to the list at a specific index
     * @param index position where value is added
     * @param value to be added
     * @return true if the addition was successful
     */
    // O(n^2)
    public boolean add(int index, E item){
        if(index > size || index < 0) {
            throw new ArrayIndexOutOfBoundsException();
        }
        ensureCapacity(); // O(n)
        for(int i=size-1; i>=index; i--){
            elements[i+1] = elements[i];
        }
        elements[index] = item;
        size++;
        return true;
    }
    /**
     * Method to get the value from the list
     * @param index of the element to get
     * @return value of the element at index
     * throws an exception of type ArrayIndexOutOfBoundsExceptiont if index is invalid
     */

     //O(1)
    public E get(int index) {
        checkIndex(index);
        return elements[index];
    }

    /**
     * Method to modify the value of an element in the list
     * @param index of the element to be modified
     * @param item new value of the element at index
     * @return the old value of the element at index
     * throws an  exception of type ArrayIndexOutOfBoundsException if index is invalid
     */
    // O(1)
    public E set(int index, E item) {
        checkIndex(index);
        E oldItem = elements[index];
        elements[index] = item;
        return oldItem;
    }
    /**
     * Method to get the number of elements in the list
     * @return size of the list
     */
    //O(1)
    public int size() { 
        return size; 
    }

    /**
     * Method to clear the list
     */
    // O(1)
    public void clear() { 
        size = 0; 
    }
     /**
     * Method to check if the list is empty
     * @return true if the list is empty
     */
    // O(1)
    public boolean isEmpty() { 
        return (size == 0);
    }
    /**
     * Method to remove a value from the list
     * @param o value to find and remove from the list
     * @return true if o was found and removed, false otherwise
     */
    // O(n)
    public boolean remove(Object o) {
        E item = (E) o;
        for(int i=0; i<size; i++)
            if(elements[i].equals(item)){
                remove(i);
                return true;
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
        checkIndex(index);
        E item = elements[index];
        for(int i=index; i<size-1; i++) {
            elements[i] = elements[i+1];
        }
        size--;
        return true;
    }
    /***
     * Method to reduce size of elements to number of elements
     */
    // O(n)
    public void trimToSize() {
        if (size != elements.length) {
            E[] newElements = (E[]) new Object[size];// capacity = size
            for(int i=0; i<size; i++) {
                newElements[i] = elements[i];
            }
            elements = newElements;
        }
    }
    /**
     * Method to make sure there is enough space to add more elements
     */
    // O(n)
    private void ensureCapacity() {
        if(size >= elements.length) {
            int newCap = (int) (elements.length * 1.5);
            E[] newElements = (E[]) new Object[newCap];
            for(int i=0; i<size; i++) {
                newElements[i] = elements[i];
            }
            elements = newElements;
        }
    }
    /***
     * Method to check if index is valud
     * @param index for the index
     */
    // O(1)
    private void checkIndex(int index){
        if(index < 0 || index >= size) {
            throw new ArrayIndexOutOfBoundsException("Index out of bounds. Must be between 0 and "+(size-1));
        }
    }
    /***
     * Method to print contents of ArrayList
     * @return a string containing the contents of ArrayList
     */
    // O(n)
    public String toString() {
        String output = "[";
        for(int i=0; i<size-1; i++) {
            output += elements[i] + " ";
        }
        output += elements[size-1] + "]";
        return output;
       }
    

    /***
     * Method to get a new ArrayIterator 
     * @return a new ArrayIterator
     */
    // O(1)
    public Iterator<E> iterator(){
        return new ArrayIterator();
    }
    /***
     * Inner class that implements the interface Iterator<E>
     */
    private class ArrayIterator implements Iterator<E>{
        private int current = -1;
   
        /**
         * Method to check if there is another element
         * @return true if current < size-1
         */
        // O(1)
        public boolean hasNext() { 
           return current < size-1; 
        }
   
        /***
         * Method to get the next element
         * @return the next element
         */
        // O(1)
        public E next() { 
           return elements[++current]; 
        }
    }
    /***
     * Method to get a deep clone of list
     * @return a deep clone of list
     */
    // O(n)
    public Object clone(){
        ArrayList<E> copy = new ArrayList<>();
        for(int i=0; i<size; i++){
            copy.add(elements[i]);
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
        for(int i = 0; i < size; i++) {
            E curObj = (E) elements[i];
            boolean found = false;
            for (int j = 0; j < otherList.size(); j++) {
                if (curObj.equals(otherList.get(j))) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                remove(i); // O(n)
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
        for(int i = 0; i < size; i++) {
            if(o.equals(elements[i])) {
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
        for(int i = 0; i < size; i++) {
            E curObj = (E) elements[i];
            boolean found = false;
            for (int j = 0; j < otherList.size(); j++) {
                if (curObj.equals(otherList.get(j))) {
                    found = true;
                    break;
                }
            }
            if (found) {
                remove(i);
            }
        }
        return true;
    }

    
    private class ListIterators implements ListIterator<E> {
        private int curIndex;
        /***
         * Default constructor
         * sets curIndex to 0
         */
        // O(1)
        ListIterators() {
            curIndex = 0;

        }
        /***
         * Constructor with one parameter
         * @param index for the starting index
         */
        // O(1)
        ListIterators(int index) {
            curIndex = index;
        }

        /***
         * Method to check if there is another element after current
         * @return true if there is another element after current
         */
        // O(1)
        public boolean hasNext() {
            return curIndex < size;
        }
        /***
         * Method to get the next element
         * @return the next element
         */
        // O(1)
        public E next() {
            E cur = elements[curIndex];
            curIndex++;
            return cur;

        }
        /***
         * Method to check if there is another element before current
         * @return true if there is another element before current
 
         */

         // O(1)
        public boolean hasPrevious() {
            return curIndex >= 0;

        }
        /***
         * Method to get the previous element
         * @return the previous element
         */
        // O(1)
        public E previous() {
            E prev = elements[curIndex];
            curIndex--;
            return prev;
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
    // O(1)
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
        for (int i = 0; i < size; size++) {
            list[i] = elements[i];
        }
        return list;
    }
    
    /**
     * Method to add another list to the list
     * @param otherList list to be added
     * @return true if all the elements in otherList were added to this list successfully
     */

     // O(n^3)
    public boolean addAll(List<E> otherList) {
        for (int i = 0; i < otherList.size(); i++) {
            add(otherList.get(i)); // O(n^2)
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
        for (int i = 0; i < otherList.size(); i++ ) {
            E curObj = otherList.get(i);
            boolean found = false;
            for (int j = 0; j < size; j++) {
                if (curObj.equals(elements[j])) {
                    found = true;
                }
            }
            if (!found) {
                return false;
            }
        }
        return true;
    }
}


