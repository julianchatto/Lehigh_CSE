public class Pair<E1, E2> {
    private E1 first;
    private E2 second;
    public Pair (E1 f, E2 s) {
        first = f;
        second = s;
    }
    public E1 getFirst() {
        return first;
    }
    public E2 getSecond() {
        return second;
    }
    public void setFirst(E1 f) {
        first = f;
    } 
    public void setSecond(E2 s) {
        second = s;
    }

    public boolean equals(Object obj) {
        return (((Pair<E1, E2>) obj).getFirst().equals(first)) && (((Pair<E1, E2>) obj).getSecond().equals(second)); 
    }

    public String toString() {
        return "(" + first.toString() + ", " + second.toString() + ")"; 
     }
} 
