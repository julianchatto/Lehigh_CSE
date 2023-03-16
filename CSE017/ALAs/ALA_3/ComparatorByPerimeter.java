import java.util.Comparator;

public class ComparatorByPerimeter implements Comparator<Shape> {
    public int compare(Shape s1, Shape s2){
        Double p1 = s1.getPerimeter();
        Double p2 = s2.getPerimeter();
        return p1.compareTo(p2);
    }
}
