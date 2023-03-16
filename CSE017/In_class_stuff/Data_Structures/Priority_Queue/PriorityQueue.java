import java.util.ArrayList;
import java.util.Comparator;

public class PriorityQueue<E> {
    private ArrayList<E> list;
    private Comparator<E> comparator;
    public PriorityQueue() { // O(1)
        list = new ArrayList<>();
        comparator = null;
    }
    public PriorityQueue(Comparator<E> c) { // O(1)
        list = new ArrayList<>();
        comparator = c;
    }
    public E poll() { // O(1)
        E value = list.get(0);
        list.remove(0); 
        return value;
    }

    public void offer(E item) { // O(n)
        int i, c;
        for(i=0; i<list.size(); i++){
            if (comparator == null) { // using comparable
                c = ((Comparable<E>)item).compareTo(list.get(i));
            } else {
                c = comparator.compare(item, list.get(i));
            }
            if(c < 0) {
                break;
            }
        }
        list.add(i, item);
    }

    public E peek() { // O(1)
        return list.get(0);
    }
    public String toString() { // O(n)
        return "Priority Queue: " + list.toString();
    }
    public int size() { // o(1)
        return list.size();  
    }

    public void clear() { // O(1)
        list.clear(); 
    }
    public boolean isEmpty() { // O(1)
        return list.size() == 0;
    }
    
}