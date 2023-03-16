import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
/***
 * Class to model the entity AnimalList
 * @author Julian Chattopadhyay
 * @version 0.1
 * Date of creation: October 25, 2022
 * Last Date Modified: October 25, 2022
 */
public class AnimalList {
    public static void main(String[] args) {
        ArrayList<String> animalAL = new ArrayList<>();
        LinkedList<String> animalLL = new LinkedList<>();
        File file = new File("animals.txt");
        try {
            Scanner read = new Scanner(file);
            while(read.hasNextLine()) {
                String line = read.nextLine();
                animalAL.add(line);
                animalLL.add(line);
            }
            read.close();
        } 
        catch(FileNotFoundException e) {
            System.out.println("File not found");
            System.exit(0);
        }
        testSearch(animalAL, animalLL);
        testRemove(animalAL, animalLL);
        testAdd(animalAL, animalLL);
    }

    /***
     * Method to search for an object in a linkedList and ArrayList
     * @param   AL for the ArrayList being searched
     * @param   LL for the LinkedList being search
     */
    public static void testSearch(ArrayList<String> AL, LinkedList<String> LL) {
        System.out.println("\nComparing the methods contains(Object)");
        System.out.printf("%-30s\t%-15s\t%-15s\n", "Animal name", "Iterations(AL)", "Iterations(LL)");
        int totalAL = 0, totalLL = 0;
        for (int i = 0; i < 20; i++) {
            int randomIndex = (int) (Math.random() * AL.size());
            String randomName = AL.get(randomIndex);
            int ALIterations = AL.contains(randomName);
            int LLIterations = LL.contains(randomName);
            totalAL += ALIterations;
            totalLL += LLIterations;
            System.out.printf("%-30s\t%-15d\t%-15d\n", randomName, ALIterations, LLIterations);
        }
        System.out.printf("%-30s\t%-15d\t%-15d\n", "Average", totalAL/20, totalLL/20);
    }

    /***
     * Method to remove an object in a linkedList and ArrayList
     * @param   AL for the ArrayList being searched
     * @param   LL for the LinkedList being search
     */
    public static void testRemove(ArrayList<String> AL, LinkedList<String> LL) {
        System.out.println("\nComparing the methods remove(Object)");
        System.out.printf("%-30s\t%-15s\t%-15s\n", "Animal name", "Iterations(AL)", "Iterations(LL)");
        int totalAL = 0, totalLL = 0;
        for (int i = 0; i < 20; i++) {
            int randomIndex = (int) (Math.random() * AL.size());
            String randomName = AL.get(randomIndex);
            int ALIterations = AL.remove(randomName);
            int LLIterations = LL.remove(randomName);
            totalAL += ALIterations;
            totalLL += LLIterations;
            System.out.printf("%-30s\t%-15d\t%-15d\n", randomName, ALIterations, LLIterations);
        }
        System.out.printf("%-30s\t%-15d\t%-15d\n", "Average", totalAL/20, totalLL/20);
    }

    /***
     * Method to add an object in a linkedList and ArrayList
     * @param   AL for the ArrayList being searched
     * @param   LL for the LinkedList being search
     */
    public static void testAdd(ArrayList<String> AL, LinkedList<String> LL) {
        System.out.println("\nComparing the methods add(Object)");
        System.out.printf("%-30s\t%-15s\t%-15s\n", "Animal name", "Iterations(AL)", "Iterations(LL)");
        int totalAL = 0, totalLL = 0;
        for (int i = 0; i < 20; i++) {
            int randomIndex = (int) (Math.random() * AL.size());
            String randomName = AL.get(randomIndex);
            int ALIterations = AL.add(randomIndex, randomName);
            int LLIterations = LL.add(randomIndex, randomName);
            totalAL += ALIterations;
            totalLL += LLIterations;
            System.out.printf("%-30s\t%-15d\t%-15d\n", randomName, ALIterations, LLIterations);
        }
        System.out.printf("%-30s\t%-15d\t%-15d\n", "Average", totalAL/20, totalLL/20);
    }
}

/***
 * c.v.
 * 
 * The method contains for both ArrayList and LinkedList were nearly identical. This makes sense because they are both traverising the lists.
 *  this is dependent on the number of elements in the list -> O(n)
 * 
 * The method remove took less iterations for LinkedList because removing an object just requires you to search through the list to find the 
 * element and then remove it by changing the pointer of the previous element to the element after the element being removed. 
 * Doing this in array list requires you to move all the elements down one -> O(n)
 * 
 * The method add took approximatly the same number of iterations for both LL and AL which makes sense because in the LL the algorithm traverses
 * the entire list to get the element before and after the new element. Therfore the algorithm takes the same number of iterations for both 
 * LL and AL based on the number of elements -> O(n)
 */
