import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ListIterator;
/**
 * Class Test
 * @author Houria Oudghiri, Julian Chattopadhyay
 * Date of creation: October 21, 2021
 * Date of last modification: November 4, 2022
 */
public class Test{
    public static void main(String[] args){
        List<String> countriesAL = new ArrayList<String>();
        readFile(countriesAL, "countries.txt");
        List<String> countriesLL = new LinkedList<String>();
        readFile(countriesLL, "countries.txt");
        // Testing the arraylist
        System.out.println("Testing ArrayList");
        List<String> copyAL = (List<String>)(((ArrayList<String>)countriesAL).clone());
        boolean returnValue = copyAL.containsAll(countriesAL);
        System.out.println("ArrayList containsAll() returned: " + returnValue);
        copyAL.addAll(countriesAL);
        System.out.println("Size of ArrayList after addAll(): " + copyAL.size());
        copyAL.removeAll(countriesAL);
        System.out.println("Size of ArrayList after removeAll(): " + copyAL.size());
        copyAL.retainAll(countriesAL);
        System.out.println("Size of ArrayList after retainAll(): " + copyAL.size());
        Object[] objectsAL = copyAL.toArray();
        System.out.println("ArrayList toArray() returned an array with size: " + objectsAL.length);
        // Testing the linkedlist
        System.out.println("Testing LinkedList");
        List<String> copyLL = (List<String>)(((LinkedList<String>)countriesLL).clone());
        int index = (int) (Math.random() * copyLL.size());
        String country = copyLL.get(index);
        System.out.println("LinkedList get(" + index + ") returned: " + country);
        country = copyLL.set(index, country);
        System.out.println("LinkedList set(" + index + ", " + country +") returned: " + country);
        returnValue = copyLL.remove("any country");
        System.out.println("LinkedList remove(anycountry) returned: " + returnValue);
        returnValue = copyLL.remove(index);
        System.out.println("LinkedList remove(" + index + ") returned: " + returnValue);
        returnValue = copyLL.add(index, country);
        System.out.println("LinkedList add(" + index + ", " + country + ") returned: " + returnValue);
        returnValue = copyLL.containsAll(countriesLL);
        System.out.println("LinkedList containsAll() returned: " + returnValue);
        
        copyLL.addAll(countriesLL);
        System.out.println("Size of LinkedList after addAll(): " + copyLL.size());
        copyLL.removeAll(countriesLL);
        
        System.out.println("Size of LinkedList after removeAll(): " + copyLL.size());
        copyLL.retainAll(countriesLL);
        
        System.out.println("Size of LinkedList after retainAll(): " + copyLL.size());
        Object[] objectsLL = copyLL.toArray();
        System.out.println("LinkedList toArray() returned an array with size: " + objectsLL.length);

        // Printing the lists forward
        System.out.println("\nArraylist forward: ");
        printListForward(countriesAL);
        System.out.println("\nLinkedlist forward: ");
        printListForward(countriesLL);
        // Printing the lists backward
        System.out.println("\nArraylist backward: ");
        printListBackward(countriesAL);
        System.out.println("\nLinkedlist backward: ");
        printListBackward(countriesLL);
    }
    /***
     * Method to read information from a file
     * @param list for the list information will be added
     * @param filename for the file being red from 
     */
    public static void readFile(List<String> list, String filename){
        File file = new File(filename);
        try{
            Scanner read = new Scanner(file);
            while(read.hasNextLine()){
                list.add(read.nextLine());
            }
            read.close();
        }
        catch(FileNotFoundException e){
            System.out.println("File not found.");
            System.exit(0);
        }
    }
    /***
     * Method to print list forward using the iterator returned by listIterator()
     * @param list for the list 
     */
    // O(n)
    public static <E> void printListForward(List<E> list){
        ListIterator<E> iter = list.listIterator();
        System.out.print("[");
        while(iter.hasNext()) {
            E next = iter.next();
            if(iter.hasNext()) {
                System.out.print(next  + ", ");
            } else {
                System.out.println(next + "]");
            }
            
        }
        
    }
    /***
     *  Method to print list backward using the iterator returned by listIterator(list.size())
     * @param list for the list 
     */
    // O(n)
    public static <E> void printListBackward(List<E> list){
        ListIterator<E> iter = list.listIterator(list.size()-1);
        System.out.print("[");
        while(iter.hasPrevious()) {
            E next = iter.previous();
            if(iter.hasPrevious()) {
                System.out.print(next  + ", ");
            } else {
                System.out.println(next + "]");
            }
            
        }
        
    }
}