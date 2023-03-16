import java.util.ArrayList;

public class Stack<E> {
    private ArrayList<E> elements;
    
    public Stack() {
        elements = new ArrayList<>(); // stack with capacity 10
    }
    public void push (E item) {
        elements.add(item); // add at the end of the array list
    }
    public E pop() {
        int lastIndex = elements.size()-1; // index of the top
        E item = elements.get(lastIndex); // value of the top
        elements.remove(lastIndex); // popping the top
        return item;
    }
    public E peek() {
        return elements.get(elements.size()-1);
    }

    public boolean isEmpty() {
        return elements.isEmpty();
    }

    public int size() {
        return elements.size();
    }

    public String toStirng() {
        return "Stack: " + elements.toString();
    }
}
