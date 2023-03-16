import java.util.ArrayList;

public class Heap<E extends Comparable<E>> {
    private ArrayList<E> list;

    /***
     * Defualt Constructor
     * initializes list
     */
    // O(1)
    public Heap(){ 
        list = new ArrayList<>();
    }
    /***
     * Method to get the size of list
     * @return the size of list
     */
    // O(1)
    public int size(){ 
        return list.size();
    }

    /***
     * Method to check if list is empty
     * @return true if list is empty, false otherwise
     */
    // O(1)
    public boolean isEmpty(){ 
        return list.isEmpty();
    }

    /***
     * Method to clear the list
     */
    // O(1)
    public void clear(){ 
        list.clear();
    }
    /***
     * Method to return list as a string
     * @return list as a string
     */
    // O(n)
    public String toString(){ 
        return list.toString();
    }

    /***
     * Method to check if list contains value
     * @param value the object being searched for in list
     * @return the number of iterations to find value
     */
    // O(n)
    public int contains(E value) { 
        int iterations = 0;
        for(int i=0; i<list.size(); i++) {
            iterations++;
            if(list.get(i).equals(value)) {
                return iterations;
            }
        }
        return iterations;
    }

    /***
     * Method to add value to list
     * @param value the object being added
     * @return the number of iterations to add value
     */
    // O(log n)
    public int add(E value) { 
        int iterations = 0;
        list.add(value); //append value to the heap
        int currentIndex = list.size()-1;
        //index of the last element
        while(currentIndex > 0) {
            iterations++;
            int parentIndex = (currentIndex-1)/2;
            //swap if current is greater than its parent
            E current = list.get(currentIndex);
            E parent = list.get(parentIndex);
            if(current.compareTo(parent) > 0) {
                list.set(currentIndex, parent);
                list.set(parentIndex, current);
            } else {
                break; // the tree is a heap
            }
            currentIndex = parentIndex;
        }
        return iterations;
    }
    /***
     * Method to remove an object
     * @return the number of iterations to remove
     */
    // O(log n)
    public int remove() { 
        int iterations = 0;
        if(list.size() == 0) {
            return iterations;
        }
        //copy the value of the last node to root
        E removedItem = list.get(0);
        list.set(0, list.get(list.size()-1));
        //remove the last node from the heap
        list.remove(list.size()-1);
        int currentIndex = 0;

        while (currentIndex < list.size()) {
            iterations++;
            int left = 2 * currentIndex + 1;
            int right = 2 * currentIndex + 2;
            //find the maximum of the left and right nodes
            if (left >= list.size()) {
                break; // no left child
            }
            int maxIndex = left;
            E max = list.get(maxIndex);
            if (right < list.size()) { // right child exists
                if(max.compareTo(list.get(right)) < 0) {
                    maxIndex = right;
                }
            } 
            
            // swap if current is less than max
            E current = list.get(currentIndex);
            max = list.get(maxIndex);
            if(current.compareTo(max) < 0){
                list.set(maxIndex, current);
                list.set(currentIndex, max);
                currentIndex = maxIndex;
            } else {
                break; // the tree is a heap
            }
        }
        return iterations;
    }

    /***
     * Method to get the height of the heap
     * @return helper method (height of tree)
     */
    // O(n)
    public int height() {
        return height(0);
    }

    /***
     * Helper method to get the height of the heap
     * @param node for the current node 
     * @return the height of tree
     */
    // O(n)
    public int height(int node) {// index of the node in the arrayList
        if (node < list.size()) {
            int lHeight = height(2 * node + 1);
            int rHeight = height(2 * node + 2);
            return 1 + Math.max(lHeight, rHeight);
        } 
        return 0;        
    }

    /***
     * Method to determine if the heap is balanced
     * @return helper method 
     */
    // O(n^2)
    public boolean isBalanced() {
        return isBalanced(0);
        
    }
    /***
     * Method to determine if the heap is balanced
     * @param node for the current node
     * @return true if balanced
     */
    // O(n^2)
    public boolean isBalanced(int node) {
        if (node >= list.size()) {
            return true;
        }

        int lHeight = height(2 * node + 1);
        int rHeight = height(2 * node + 2);
        int diff = Math.abs(rHeight-lHeight);
        if(diff > 1) {
            return false;
        }
        return isBalanced(2 * node + 1) && isBalanced(2 * node + 2);
    }
}
