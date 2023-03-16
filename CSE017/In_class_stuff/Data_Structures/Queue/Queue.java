import java.util.LinkedList;

public class Queue<E> {
    private LinkedList<E> list;
    public Queue() { // O(1)
        list=new LinkedList<>(); 
    }
    public void offer(E item) { // O(1)
        list.addLast(item);
    }
    public E poll() { // O(1)
        E value = list.getFirst();
        list.removeFirst(); return value;
    }
    public E peek() {// O(1)
        return list.getFirst(); 
    }
    public String toString() {// O(n)
        return "Queue: " + list.toString();
    }
    public int size() { // O(1)
        return list.size(); 
    }
    public void clear() { // O(1)
        list.clear(); 
    }
    public boolean isEmpty() { // O(1)
        return list.size()==0; 
    }
}