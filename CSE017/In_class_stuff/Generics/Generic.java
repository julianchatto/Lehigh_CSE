import java.util.ArrayList;

public class Generic {
    public static void main(String[] args) {
        ArrayList<String> words = new ArrayList<>(25);
        ArrayList<Integer> numbers = new ArrayList<>(25);
        
        words.add("Tree");
        words.add("Sea");
        words.add(1, "Mountain");
        System.out.println(words);
        words.set(2, "pond");
        System.out.println(words);
        System.out.println(words.contains("Sea"));
        words.add("Flower");
        System.out.println(words.size());
        System.out.println(words);
        words.add(4,"Hill");
        System.out.println(words);
        words.remove(2);
        System.out.println(words);

        numbers.add(22);
        numbers.add(25);
        numbers.add(65);
        numbers.add(81);
        System.out.println(numbers);

        // stack class
        Stack<String> fruits = new Stack<>();
        fruits.push("Banana");
        fruits.push("Apple");
        fruits.push("Kiwi");

        System.out.println(fruits.toStirng());
        System.out.println(fruits.peek());
        System.out.println(fruits.pop());
        System.out.println(fruits.size());

        StackObject cities = new StackObject();

        cities.push("New York");
        cities.push("Boston");
        cities.push("Philadelphia");
        System.out.println(cities.toStirng());
        System.out.println(cities.peek());
        System.out.println(cities.pop());
        System.out.println(cities.size());

        ArrayList<Pair<Integer, String>> students = new ArrayList<>();
        Pair<Integer, String> s1 = new Pair<>(12345, "Lily Brown");
        students.add(s1);
        students.add(new Pair<Integer, String>(22222, "Paul Park"));

        System.out.println(students);
        ArrayList<Pair<String, String>> states = new ArrayList<>();
        Pair<String, String> state = new Pair<>("New York", "Albany");
        states.add(state);
        states.add(new Pair<>("Pennsylvania", "Harrisburg"));
        System.out.println(states);

        Integer[] numbers1 = {22, 23, 56, 78, 91};
        printArray(numbers1);
        String[] words2 = {"apple", "kiwi", "banana"};
        printArray(words2);

        sort(numbers1);
        sort(words2);
        printArray(numbers1);
        printArray(words2);

       
    }

    public static <E> void printArray(E[] list) {
        System.out.print("{");
        for(E element: list) {
            System.out.print(element + " ");
        }
        System.out.println("}");
    }

    public static <E extends Comparable<E>> void sort(E[] list){
        int currentMinIndex;
        E currentMin;
        for (int i=0; i<list.length-1; i++) {
            currentMinIndex = i;
            currentMin = list[i];
            for(int j=i+1; j<list.length; j++) {
                if(currentMin.compareTo(list[j]) > 0) {
                currentMin = list[j];
                currentMinIndex = j;
            }
        }
            list[currentMinIndex] = list[i];
            list[i] = currentMin;
        }
       }
}