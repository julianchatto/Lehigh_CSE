import java.util.ArrayList;
import java.util.Iterator;
public class Test {
    public static void main(String[] args) {
        ArrayList<String> al = new ArrayList<>();
        al.add("New York"); 
        al.add("Tokyo");
        al.add("Paris"); 
        al.add("Rome");
        al.add("Brasilia");

        Iterator<String> iter = al.iterator();
        System.out.print("\n[ ");
        while(iter.hasNext()) {
            System.out.print(iter.next().toUpperCase() + " ");
        }  
        System.out.print("]\n");
    }
}