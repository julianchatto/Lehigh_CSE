import java.util.Iterator;
import java.util.LinkedList;

public class ShapeLL {
    private LinkedList<Pair<Integer, Integer>> points;

    public ShapeLL() {
        points = new LinkedList<>();
    }
    public void add(Pair<Integer, Integer> p) {
        points.add(p);
    }

    public boolean isClosed() {
        
        Iterator<Pair<Integer, Integer>> iter = points.iterator();
        Pair<Integer, Integer> lastP = points.get(0);
        Pair<Integer, Integer> firstP = points.get(0);
        while(iter.hasNext()) {
            lastP = iter.next();
        }
        return lastP.equals(firstP);
    }
    public boolean containsPoint(Pair<Integer, Integer> p) {
        Iterator<Pair<Integer, Integer>> iter = points.iterator();
        
        return containsPoint(p, iter);
    }
    public boolean containsPoint(Pair<Integer, Integer> p, Iterator<Pair<Integer, Integer>> iter) {
        if (!iter.hasNext()) {
            return false;
        }
        Pair<Integer, Integer> check = iter.next();
        if (check.equals(p)) {
            return true;
        }
        
        return containsPoint(p, iter);
    }

    public String toString() {
        return points.toString();
    }
}
