import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
public class AnimalTree {
    public static void main(String[] args) {
        ArrayList<String> animalAl  = new ArrayList<>();
        BST<String> animalBST = new BST<>();
        Heap<String> animalHeap = new Heap<>();
        readFile(animalAl, animalBST, animalHeap, "animals.txt");
        testContains(animalAl, animalBST, animalHeap);
        testRemove(animalAl, animalBST, animalHeap);

        System.out.println("Height(BST): " + animalBST.height());
        System.out.println("Height(Heap): " + animalHeap.height());
        System.out.println("isBalanced(BST)? " + animalBST.isBalanced());
        System.out.println("isBalanced(Heap)? " + animalHeap.isBalanced());

        java.util.Collections.sort(animalAl);
        animalBST.clear();
        animalHeap.clear();

        for(String animalName: animalAl) {
            animalBST.add(animalName);
            animalHeap.add(animalName);
        }
        System.out.println("After sorting: ");
        System.out.println("Height(BST): " + animalBST.height());
        System.out.println("Height(Heap): " + animalHeap.height());
        System.out.println("isBalanced(BST)? " + animalBST.isBalanced());
        System.out.println("isBalanced(Heap)? " + animalHeap.isBalanced());

        /***
         * DISCUSSION OF RESULTS:
         * 
         * CONTAINS: The BST is quicker because it can run in lograthmic time, 
         *          but the heap is linear so it has many more iterations
         * 
         * ADD: Both the heap and BST, both are logarithmic but the heap 
         *      is quicker because it is balanced
         * 
         * REMOVE: The BST is a log scale but the heap is consistently 8 or 9 
         *          which makes sense for 2^8 and 2^9 is between the # of animals 
         *          8/9 are the longest paths to be traversed in a heap
         * 
         * AFTER SORTING:
         * The heap's height was identical because heaps are by nature balanced
         * and therefore sorting the array doesn't change where the data is inserted
         * However, in the BST, sorting the array makes insertion occur in order and 
         * therefore data is inserted like a list. Every object will be to the right 
         * of the parent. IE the height is the number of parents
         */
    }

    /***
     * Method to read data from a file
     * @param al for the arrayList
     * @param bst for the BST
     * @param heap for the heap
     * @param filename for the name of the file being read
     */
    public static void readFile(ArrayList<String> al, BST<String> bst, Heap<String> heap, String filename) {
        File file = new File(filename);
        try {
            Scanner scan = new Scanner(file);
            int count = 0;
            int totalIterBST = 0;
            int totalIterHeap = 0;
            System.out.println("Testing Add");
            while (scan.hasNextLine()) {
                String line = scan.nextLine();
                al.add(line);
                int bstIter = bst.add(line);
                int heapIter = heap.add(line);
                totalIterBST += bstIter;
                totalIterHeap += heapIter;
                if (count % 24 == 0) {
                    System.out.printf("%-20s\t%-5d\t%-5d\n", line, bstIter, heapIter);
                }
                count++;
            }
            System.out.printf("%-20s\t%-5d\t%-5d\n", "Average", totalIterBST/al.size(), totalIterHeap/al.size());
            scan.close();
        } catch (FileNotFoundException e) {
            System.out.println("file not found");
            System.exit(0);
        }
    }

    /***
     * Method to test contains functions for data structures
     * @param al for the arrayList
     * @param bst for the BST
     * @param heap for the heap
     */
    public static void testContains(ArrayList<String> al, BST<String> bst, Heap<String> heap) {
        int totalBST = 0, totalHeap = 0;
        System.out.println("Testing contains");
        System.out.printf("%-20s\t%-5s\t%-5s\n", "Animal Name", "BST", "Heap");
        for (int i = 0; i < 20; i ++) {
            int randomIndex = (int) (Math.random() *al.size());
            String animalName = al.get(randomIndex);
            int bstIter = bst.contains(animalName);
            int heapIter = heap.contains(animalName);
            totalBST += bstIter;
            totalHeap += heapIter;
            System.out.printf("%-20s\t%-5d\t%-5d\n", animalName, bstIter, heapIter);
        }
        System.out.printf("%-20s\t%-5d\t%-5d\n", "Average", totalBST/20, totalHeap/20);
    }

    /***
     * Method to test remove functions for data structures
     * @param al for the arrayList
     * @param bst for the BST
     * @param heap for the heap
     */
    public static void testRemove(ArrayList<String> al, BST<String> bst, Heap<String> heap) {
        int totalBST = 0, totalHeap = 0;
        System.out.println("Testing remove");
        System.out.printf("%-20s\t%-5s\t%-5s\n", "Animal Name", "BST", "Heap");
        for (int i = 0; i < 20; i ++) {
            int randomIndex = (int) (Math.random() *al.size());
            String animalName = al.get(randomIndex);
            int bstIter = bst.remove(animalName);
            int heapIter = heap.remove();
            totalBST += bstIter;
            totalHeap += heapIter;
            System.out.printf("%-20s\t%-5d\t%-5d\n", animalName, bstIter, heapIter);
        }
        System.out.printf("%-20s\t%-5d\t%-5d\n", "Average", totalBST/20, totalHeap/20);
    }
}
