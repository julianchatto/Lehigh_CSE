import java.util.ArrayList;
import java.util.Collection;
public class Test {
    public static void main(String[] args) {
        Collection<String> c1 = new ArrayList<String>();
        c1.add("New York"); c1.add("Tokyo"); c1.add("Paris");
        c1.add("Rome"); c1.add("Brasilia");
        System.out.println("Cities in collection 1: " + c1);
        System.out.println("\nIs Paris in the collection? " +
        c1.contains("Paris"));
        c1.remove("Paris");
        System.out.println("\nThere are " + c1.size() + " cities in collection 1");
        Collection<String> c2 = new ArrayList<String>();
        c2.add("Madrid"); c2.add("Bangkok"); c2.add("Moscow");
        c2.add("Beirut"); c2.add("Rome");
        System.out.println("\nCities in collection 1: " + c1);
        System.out.println("\nCities in collection 2: " + c2);

        Collection<String> c3 = (ArrayList<String>)((ArrayList<String>)c1).clone();
        c3.addAll(c2);
        System.out.println("\n\nCities in collection 1 or collection 2: " + c3);
        c3 = (ArrayList<String>) ((ArrayList<String>)c1).clone();
        c3.retainAll(c2);
        System.out.println("\nCities in collection 1 and collection 2: " + c3);
        c3 = (ArrayList<String>)((ArrayList<String>)c1).clone();
        c3.removeAll(c2);
        System.out.println("\nCities in collection 1, but not in collection 2:"+c3);

    }
}