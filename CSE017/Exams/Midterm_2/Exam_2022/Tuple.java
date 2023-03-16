public class Tuple<E1, E2, E3, E4> {
    private E1 first;
    private E2 second;
    private E3 third;
    private E4 fourth;

    public Tuple(E1 f, E2 s, E3 t, E4 fo){
        first = f;
        second = s;
        third = t;
        fourth = fo;
    }
    public E1 getFirst(){ return first;}
    public E2 getSecond() { return second;}
    public E3 getThird() { return third;}
    public E4 getFourth() { return fourth;}

    public void setFirst(E1 f) { first = f;}
    public void setSecond(E2 s) { second = s;}
    public void setThird(E3 t) { third = t;}
    public void setFourth(E4 f) { fourth = f;}

    public String toString(){
        return "(" + first.toString() + ", " +
                     second.toString() + ", " +
                     third.toString() + ", " +
                     fourth.toString() + ")";
    }
}