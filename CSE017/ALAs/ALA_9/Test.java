import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Test {
    public static void main(String[] args) {
        HashMap<String, String> dictionaryHM = new HashMap<>(50000);
        BST<String> dictionaryBST = new BST<>();
        LinkedList<String> dictionaryLL = new LinkedList<>();
        ArrayList<HashMapEntry<String, String>> words = new ArrayList<>();
        readFile(words, "dictionary.txt");
        java.util.Collections.shuffle(words);
        for(HashMapEntry<String, String> x : words) {
            dictionaryLL.add(x.getKey());
            dictionaryBST.add(x.getKey());
            dictionaryHM.put(x.getKey(), x.getValue());
        }
        System.out.printf("%-30s\t%-10s\t%-10s\t%-10s\n", "Word", "LinkedList", "BST", "Hash Table");
        testContains(words, dictionaryHM, dictionaryBST, dictionaryLL);

        System.out.println("Maximum number of collisions: " + dictionaryHM.collisions());

        /***
         * DISCUSSION OF RESULTS
         * Linked list is O(n) because the elements are acsessed in order and therefore, in the worst case, will search through the entire list
         * BST is O(log n) because the elements are sorted so it will not take too many iterations to find the elements
         * Hash map is O(1) because each word is always going to be at a specified location. In the worst case, we would need to iterate over a relativly small linkedlist 
         * 
         */

        
    }

    public static void readFile(ArrayList<HashMapEntry<String, String>> list, String filename) {
        File file = new File(filename);

        try {
            Scanner scan = new Scanner(file);
            while(scan.hasNextLine()) {
                String[] items = scan.nextLine().split("\\|");
                String word = items[0];
                String definition = items[1];
                HashMapEntry<String, String> entry = new HashMapEntry<>(word, definition);
                list.add(entry);
            }
            scan.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            System.exit(0);
        }
    }

    public static void testContains(ArrayList<HashMapEntry<String, String>> list, HashMap<String, String> HM, BST<String> BST, LinkedList<String> LL) {
        int totalLL = 0, totalBST = 0, totalHM = 0;
        for (int i = 0; i < 1000; i++) {
            int randomIndex = (int) (Math.random() * list.size());
            HashMapEntry<String, String> entry = list.get(randomIndex);
            String word = entry.getKey();

            int BSTIter = BST.contains(word);
            totalBST += BSTIter;
            int LLIter = LL.contains(word);
            totalLL += LLIter;
            HM.get(word);
            int HMIter = HashMap.getIterations;
            totalHM += HMIter;

            if(i % 50 == 0) {
                System.out.printf("%-30s\t%-10d\t%-10d\t%-10d\n", word, LLIter, BSTIter, HMIter);
            }
        }
        System.out.printf("%-30s\t%-10d\t%-10d\t%-10d\n", "Average", totalLL/1000, totalBST/1000, totalHM/1000);

    }

}

