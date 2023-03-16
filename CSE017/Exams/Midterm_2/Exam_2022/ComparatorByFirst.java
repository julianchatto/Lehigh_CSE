import java.util.Comparator;

public class ComparatorByFirst<E1 extends Comparable<E1>,E2,E3,E4> implements Comparator<Tuple<E1,E2,E3,E4>> {
 
    
    public int compare(Tuple<E1,E2,E3,E4> t1, Tuple<E1,E2,E3,E4> t2) {
        return t1.getFirst().compareTo(t2.getFirst());
    }
}
