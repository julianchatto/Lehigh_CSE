import java.util.ArrayList;

public class Heap<E extends Comparable<E>> {
    private ArrayList<E> list;

    public Heap(){ // O(1)
        list = new ArrayList<>();
    }
    public int size(){ // O(1)
        return list.size();
    }
    public boolean isEmpty(){ // O(1)
        return list.isEmpty();
    }
    public void clear(){ // O(1)
        list.clear();
    }
    public String toString(){ // O(n)
        return list.toString();
    }
    public boolean contains(E value) { // O(n)
        for(int i=0; i<list.size(); i++) {
            if(list.get(i).equals(value)) {
                return true;
            }
        }
        return false;
    }

    public void add(E value) { // O(log n)
        list.add(value); //append value to the heap
        int currentIndex = list.size()-1;
        //index of the last element
        while(currentIndex > 0) {
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
    }
    public E remove() { // O(log n)
        if(list.size() == 0) {
            return null;
        }
        //copy the value of the last node to root
        E removedItem = list.get(0);
        list.set(0, list.get(list.size()-1));
        //remove the last node from the heap
        list.remove(list.size()-1);
        int currentIndex = 0;

        while (currentIndex < list.size()) {
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
        return removedItem;
    }
}
