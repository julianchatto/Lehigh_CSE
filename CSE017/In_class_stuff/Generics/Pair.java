public class Pair<E1, E2> {
    private E1 first;
    private E2 second;

    public Pair(){
        first = null;
        second = null;
    }

    public Pair(E1 first, E2 second) {
        this.first = first;
        this.second = second;
    
    }

    public E1 getFirst() {
        return first;
    }
    public E2 getSecond() {
        return second;
    }

    public void setFirst(E1 first) {
        this.first = first;
    }

    public void setSecond(E2 second) {
        this.second = second;
    }
    public String toString() {
        return "(" + first.toString() + ", " + second.toString() + ")";
    }

    public boolean equals(Object obj) {
        if (obj instanceof Pair) {
            Pair<E1, E2> p = (Pair<E1, E2>) obj; // downcasting obj to type Pair
            boolean eq1 = this.getFirst().equals(p.getFirst());
            boolean eq2 = this.getSecond().equals(p.getSecond());
            return eq1 & eq2;
        }
        return false;
    }
}