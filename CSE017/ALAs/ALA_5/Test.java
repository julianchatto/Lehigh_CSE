import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;
/***
 * Class to model the entity Test
 * @author Julian Chattopadhyay
 * @version 0.1
 * Date of creation: September 25, 2022
 * Last Date Modified: September 26, 2022
 */
public class Test {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        boolean alaing = true;
        int option;

        // Initialize/fill lists
        ArrayList<Pair<String, String>> states = new ArrayList();
        ArrayList<Pair<String, Integer>> trees = new ArrayList();
        readStates("states.txt", states);
        readTrees("trees.txt", trees);
        
        while (alaing) {
            PrintMenu(); // print menu

            try {
                option = Integer.parseInt(scan.nextLine());

                switch(option) {
                    case 1: // view states
                        print(states);
                        break;
                    case 2: // search for state
                        System.out.println("Enter the name of a state");
                        String state = scan.nextLine();
                        int index = search(states, state);
                        if (index == -1) {
                            System.out.println("State " + state + " not found");
                        } else {
                            System.out.println("State found " + states.get(index));
                        }
                        break;
                    case 3: // sort states: name
                        states.sort(new ComparatorByFirst<>());
                        print(states);
                        break;
                    case 4: // sort states: capital
                        states.sort(new ComparatorBySecond<>());
                        print(states);
                        break;
                    case 5: // view trees
                        print(trees);
                        break;
                    case 6: // search for trees
                        System.out.println("Enter the name of a tree");
                        String tree = scan.nextLine();
                        index = search(trees, tree);
                        if (index == -1) {
                            System.out.println("State " + tree + " not found");
                        } else {
                            System.out.println("State found " + trees.get(index));
                        }
                        break;
                    case 7: // sort states: name
                        trees.sort(new ComparatorByFirst<>());
                        print(trees);
                        break;
                    case 8: // sort states: capital 
                        trees.sort(new ComparatorBySecond<>());
                        print(trees);
                        break;
                    case 9: // exit
                        System.out.println("Goodbye");
                        alaing = false;
                        break;
                    default:
                        System.out.println("Not a valid input.\n\n");
                        break;
                }
            }
            catch (InputMismatchException e) {
                System.out.println("Invalid Input");
            }
            catch (Exception e) {
                System.out.println("Error");
            }
            
        }
        scan.close();
        

    }

    /***
     * Method to print the menu
     * no return value
     */
    public static void PrintMenu() {
        System.out.println("Choose an option (1-9): ");
        System.out.println("1. View the list of states");
        System.out.println("2. Search for a state capital");
        System.out.println("3. Sort states by name"); 
        System.out.println("4. Sort states by capital");
        System.out.println("5. View the list of trees");
        System.out.println("6. Search for a tree");
        System.out.println("7. Sort trees by name");
        System.out.println("8. Sort trees by height");
        System.out.println("9. Exit");
    }
    
    /*** 
     * Method to read information to ArrayList of type Pair<String, String>
     * @param   fileName for the name of the file
     * @param   states for the name of the ArrayList
     * no return value
     */
    public static void readStates(String fileName, ArrayList<Pair<String, String>> states) {
        File file = new File(fileName);
        try {
            Scanner readFile = new Scanner(file);
            while(readFile.hasNextLine()) {
                String line = readFile.nextLine();
                String[] tokens = line.split("\\|");
                String name = tokens[0];
                String capital = tokens[1];
                Pair<String, String> pair = new Pair<>(name, capital);
                states.add(pair);
            }                
            readFile.close();
        }
        catch (FileNotFoundException e) { // file not found
            System.out.println("Cannont write to file: " + fileName);
            System.exit(0);
        }
    }
    /*** 
     * Method to read information to ArrayList of type Pair<String, Integer>
     * @param   fileName for the name of the file
     * @param   states for the name of the ArrayList
     * no return value
     */
    public static void readTrees(String fileName, ArrayList<Pair<String, Integer>> trees) {

        File file = new File(fileName);
        try {
            Scanner readFile = new Scanner(file);
            while(readFile.hasNextLine()) {
                String line = readFile.nextLine();
                String[] tokens = line.split("\\|");
                String name = tokens[0];
                Integer height = Integer.parseInt(tokens[1]);
                Pair<String, Integer> pair = new Pair<>(name, height);
                trees.add(pair);
            }                
            readFile.close();
        }
        catch (FileNotFoundException e) { // file not found
            System.out.println("Cannont write to file: " + fileName);
            System.exit(0);
        }
    }

    /***
     * Method to search for a key in a list of type Pair<E1, E2>
     * @param   list for the name of the ArrayList
     * @param   key for what is being searched for
     * @return
     */
    public static <E1, E2> int search(ArrayList<Pair<E1, E2>> list, E1 key) {
        for(int i = 0; i < list.size(); i++){
            E1 first = list.get(i).getFirst(); // get first of the pair at index i
            if (first.equals(key)) {
                return i;
            }
        }
        return -1;
    }

    /***
     * Method to print the contents of list
     * @param list
     * no return value
     */
    public static <E1, E2> void print(ArrayList<Pair<E1, E2>> list) {
        for (int i = 0; i <list.size(); i++) {
            System.out.println(list.get(i));
        }
    }
}
