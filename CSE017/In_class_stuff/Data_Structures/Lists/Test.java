import java.util.Iterator;

public class Test {
    public static void main(String[] args) {
        ArrayList<String> cities = new ArrayList<>();
        cities.add("New York");
        cities.add("San Diego");
        cities.add("Atlanta");
        cities.add("Baltimore");
        cities.add("Pittsburg");
        // display the content of the list
        System.out.println(cities.toString());
        // iterator to display the elements of the list
        Iterator<String> cityIterator = cities.iterator();
        while(cityIterator.hasNext()) {
            System.out.print(cityIterator.next() + " ");
        }
        System.out.println();
        // get(index) to display the elements of the list
        for(int i=0; i<cities.size(); i++) {
            System.out.print(cities.get(i) + " ");
        }
        System.out.println();
        // Testing linked list
        LinkedList<String> cityList;
        cityList = new LinkedList<>();
        cityList.addFirst("Boston");
        cityList.addFirst("Philadelphia");
        cityList.addFirst("San Francisco");
        cityList.addFirst("Washington");
        cityList.addFirst("Portland");
        System.out.println(cityList.toString());
        Iterator<String> LLIterator = cityList.iterator();
        System.out.print("LinkedList (iterator): ");
        while(LLIterator.hasNext()) {
            System.out.print(LLIterator.next() + " ");
        }
        System.out.println();
    }
}