import java.util.ArrayList;
import java.util.Iterator;

public class ShapeAL{
    private ArrayList<Pair<Integer, Integer>> points;
    public ShapeAL() {
        points = new ArrayList<>(10);
    }

    public void add(Pair<Integer, Integer> p) {
        points.add(p);
    }
    public boolean isClosed() {
        return points.get(0).equals(points.get(points.size()-1));
    }
    public boolean containsPoint(Pair<Integer, Integer> p) {
        Iterator<Pair<Integer, Integer>> iter = points.iterator();
        return containsPoint(p, iter);
    }
    public boolean containsPoint(Pair<Integer, Integer> p, Iterator<Pair<Integer,Integer>> iter) {
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