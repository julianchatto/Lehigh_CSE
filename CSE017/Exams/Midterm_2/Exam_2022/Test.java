import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;

public class Test{
    public static void main(String[] args){
        ArrayList<Tuple<Integer, String, String, Double>> elements = new ArrayList<>();
        readElements(elements, "elements.txt");
        Scanner keyboard = new Scanner(System.in);
        System.out.print("Enter the symbol of an element: ");
        String symbol = keyboard.next();
        int index = search(elements, symbol);
        if(index == -1){
            System.out.println("Element with symbol " + symbol + " not found.");
        }
        else{
            System.out.println("Element found: " + elements.get(index));
        }
        System.out.print("\nSelect the sort criterion (number/symbol): ");
        String type = keyboard.next();
        switch(type){
            case "number":
                // Uncomment the line below when you define ComparatorByFirst
                elements.sort(new ComparatorByFirst());
                printList(elements);
                break;
            case "symbol":
                // Uncomment the line below when you define ComparatorByThird
                elements.sort(new ComparatorByThird());
                printList(elements);
                break;
            default:
                System.out.println("Invalid criterion. Should be number or symbol");
        }
        index = findMax(elements);
        System.out.println("\nThe element with the largest atomic mass: " + elements.get(index));
        keyboard.close();
    }

    public static void readElements(ArrayList<Tuple<Integer, String, String, Double>> list, 
                                    String filename){
        File file = new File(filename);
        try{
            Scanner read = new Scanner(file);
            while(read.hasNext()){
                int number = read.nextInt();
                String name = read.next();
                String symbol = read.next();
                double mass = read.nextDouble();
                Tuple<Integer, String, String, Double> tuple = new Tuple<>(number, name, symbol, mass);
                list.add(tuple);
            }
            read.close();
        }
        catch(FileNotFoundException e){
            System.out.println("File not found.");
            System.exit(0);
        }
    }
    // Define search here
    // O(n)
    public static <E1, E2, E3, E4> int search(ArrayList<Tuple<E1, E2, E3, E4>> list, E3 key){
        Iterator<Tuple<E1, E2, E3, E4>> iter = list.iterator();
        int index = 0;
        return search(key, iter, index);
        
    }
 
    // O(n)
    public static <E1, E2, E3, E4> int search(E3 key, Iterator<Tuple<E1, E2, E3, E4>> iter, int index) {
        if (!iter.hasNext()) {
            return -1;
        }
        E3 item = iter.next().getThird();
        if (item.equals(key)) {
            return index;
        }

        index++;
        return search(key, iter, index);

    }
   
    // Define findMax here
    // O(n)
    public static <E1, E2, E3, E4 extends Comparable<E4>> int findMax(ArrayList<Tuple<E1, E2, E3, E4>> list){
        int index = 0;
        E4 max =  list.get(0).getFourth();
        for (int i = 1; i < list.size(); i++) {
            E4 test = list.get(i).getFourth();
            if (test.compareTo(max) > 0) { 
                max = test;
                index = i;
            }
        }
        return index;
    }


    public static <E1, E2, E3, E4> void printList(ArrayList<Tuple<E1, E2, E3, E4>> list){
        for(Tuple<E1, E2, E3, E4> t: list){
            System.out.println(t);
        }
    }
}