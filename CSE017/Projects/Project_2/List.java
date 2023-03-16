/**
 * Interface List
 * @author Houria Oudghiri
 * Date of creation: October 21, 2022
 * Date of last modification: October 22, 2022
 */
import java.util.Iterator;
import java.util.ListIterator;
/**
 * Interface List to model the common behavior of lists
 */
public interface List<E>{
    /**
     * Method to add value to the list
     * @param value to be added
     * @return true if the addition was successful
     */
    public boolean add(E value);
    
    /**
     * Method to add value to the list at a specific index
     * @param index position where value is added
     * @param value to be added
     * @return true if the addition was successful
     */
    public boolean add(int index, E value);
    
    /**
     * Method to get the value from the list
     * @param index of the element to get
     * @return value of the element at index
     * throws an exception of type ArrayIndexOutOfBoundsExceptiont if index is invalid
     */
    public E get(int index);
    
    /**
     * Method to modify the value of an element in the list
     * @param index of the element to be modified
     * @param value new value of the element at index
     * @return the old value of the element at index
     * throws an  exception of type ArrayIndexOutOfBoundsException if index is invalid
     */
    public E set(int index, E value);
    
     /**
     * Method to get the number of elements in the list
     * @return size of the list
     */
    public int size();
   
    /**
     * Method to clear the list
     */
    public void clear();
    
    /**
     * Method to check if the list is empty
     * @return true if the list is empty
     */
    public boolean isEmpty();
    
     /**
     * Method to remove a value from the list
     * @param o value to find and remove from the list
     * @return true if o was found and removed, false otherwise
     */
    public boolean remove(Object o);
    
     /**
     * Method to remove an element from the list
     * @param index of the element to remove
     * @return true if the element is removed successfully
     * throws an exception of type ArrayIndexOutOfBoundsException if index is invalid
     */
    public boolean remove(int index);
    
     /**
     * Method to search for a value in the list
     * @param o value to be searched for
     * @return true if the value is found, false otherwise
     */
    public boolean contains(Object o);
    
     /**
     * Method to search for another list in the list
     * @param otherList list to be searched for
     * @return true if all the elements in otherList are found in this list, false otherwise
     */
    public boolean containsAll(List<E> otherList);
    
     /**
     * Method to add another list to the list
     * @param otherList list to be added
     * @return true if all the elements in otherList were added to this list successfully
     */
    public boolean addAll(List<E> otherList);
     
     /**
     * Set difference
     * Method to remove the elements of otherList from the list if they are found in the list
     * @param otherList list to be removed from the list
     * @return true if the elements from otherList were removed from this list successfully
     */
    public boolean removeAll(List<E> otherList);
    
     /**
     * Set intersection
     * Method to retain only the elements from otherList in the list
     * @param otherList list of elements to be retained in this list if found
     * @return true if the intersection was performed successfully
     */
    public boolean retainAll(List<E> otherList);
     
     /**
     * Method to get an iterator for the list
     * @return iterator object associated with this list
     */
    public Iterator<E> iterator();
     
     /**
     * Method to get a list iterator for the list
     * @return list iterator object associasted with this list 
     *         the iterator is positioned at the beginning of the list
     */
    public ListIterator<E> listIterator();
     
     /**
     * Method to get a list iterator for the list at a specific position
     * @param index the position where the iterator should start
     * @return list iterator object associated with this list
     *         the iterator is positioned at index if the index is valid
     *         if index = size of the list, the iterator is positioned at the end of the list
     * throws an exception of type ArrayIndexOutOfBoundsException if index is invalid
     */
    public ListIterator<E> listIterator(int index);
    
    /**
     * Method to get the elements of the list as an array of type Object
     * @return array of type Object containing all the elements of this list
     */
    public Object[] toArray();
}