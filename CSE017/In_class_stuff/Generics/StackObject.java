public class StackObject {
    private Object[] elements;
    private int count;

    public StackObject() {
        elements = new Object[10]; // stack with capacity 10
        count = 0;
    }
    public void push (Object item) {
        elements[count++] = item; // add at the end of the array list
    }
    public Object pop() {
        int lastIndex = count-1; // index of the top
        Object item = elements[lastIndex]; // value of the top
        count--; // popping the top
        return item;
    }
    public Object peek() {
        return elements[count-1];
    }

    public boolean isEmpty() {
        return count == 0;
    }

    public int size() {
        return count;
    }

    public String toStirng() {
        String out = "Stack: [";
        for(int i = 0; i < count; i++) {
            out += elements[i] + " ";
        }
        out += "]";
        return out;
    }
}
