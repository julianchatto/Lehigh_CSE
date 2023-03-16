import java.util.Comparator;

public class ComparatorByColor implements Comparator<Shape>{
    public int compare(Shape s1, Shape s2){
        return s1.getColor().compareTo(s2.getColor());       
    }
}
