import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;

public class Test {
    public static void main(String[] args) {
        
        TreeMap<String, String> tMap = new TreeMap<>(new StringComparator());
        HashMapQP<String, String> hashQP = new HashMapQP<>(100000);
        HashMapSC<String, String> hashSC = new HashMapSC<>(100000);
        readFile(tMap, hashQP, hashSC, "emails.txt");
        ArrayList<String> MLkey = new ArrayList<>();
        readFile(MLkey, "mailingList.txt");

        // #6
        int totalTMap = 0, totalHashQP = 0, totalHashSC = 0;
        System.out.println("Testing get()");
        System.out.printf("%-30s\t%-10s\t%-10s\t%-10s\n", "Username", "Tree Map", "HashMapSC", "HashMapQP");
        for (int i = 0; i < 20; i++) {
            tMap.get(MLkey.get(i));
            hashQP.get(MLkey.get(i));
            hashSC.get(MLkey.get(i));
            totalHashQP += hashQP.getIterations();
            totalHashSC += hashSC.getIterations();
            totalTMap += tMap.getIterations();
            System.out.printf("%-30s\t%-10d\t%-10d\t%-10d\n", MLkey.get(i), tMap.getIterations(),  hashSC.getIterations(), hashQP.getIterations()); 
        }
        System.out.printf("%-30s\t%-10d\t%-10d\t%-10d\n", "Average", totalTMap/20,  totalHashSC/20, totalHashQP/20); 

        /***
         * DISCUSSION OF RESULTS FOR #6
         * In the tree map, the get() method is O(log n) because finding the element requires, in the worst case, to travel to the bottom of the BST
         * The get method must vist many nodes in order to find an element  
         * 
         * In SC and QP, the get() method is O(1) because each key is associated with a specified index. 
         * In both SC and QP, the get() method may visit several locations, however, if the hash function is good and the table is large enough this 
         * will be relativly few, so it is still O(1).
         * In the worst case, the key is not found until either null (QP) or the end of a linked List (SC)
         */


        // #8
        System.out.println("\n\nTesting put() - number of collisions");
        System.out.printf("%-30s\t%-10s\t%-10s\n", "Size", "HashMapSC", "HashMapQP");
        for (int i = 50000; i < 500001; i += 50000) {
            hashQP = new HashMapQP<>(i);
            hashSC = new HashMapSC<>(i);
            readFile(hashQP, hashSC, "emails.txt");
            System.out.printf("%-30d\t%-10d\t%-10d\n", i, hashSC.getCollisions(),  hashQP.getCollisions()); 
        }

        /***
         * DISCUSSION OF RESULTS FOR #8
         * As the size of the hashtable increases, there are few collisions because of the fact that there will be less clustering or elements in a linked List
         * The SC collisions are initialy larger than the QP because the table is too small to house all of the key's w/o large linkedlist's
         * 
         * Different size hash tables return the same number of collisons because the load factor are relativly the same and clustering is low
         */

    }

    public static void readFile(TreeMap<String, String> tMap, HashMapQP<String, String> hashQP, HashMapSC<String, String> hashSC, String fileName) {
        File file = new File(fileName);
        try {
            Scanner scan = new Scanner(file);
            while(scan.hasNextLine()) {
                String[] split = scan.nextLine().split(" ");
                tMap.add(split[0], split[1]);
                hashQP.put(split[0], split[1]);
                hashSC.put(split[0], split[1]);
            }
            scan.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            System.exit(0);
        }
            
        
    }
    public static void readFile(HashMapQP<String, String> hashQP, HashMapSC<String, String> hashSC, String fileName) {
        File file = new File(fileName);
        try {
            Scanner scan = new Scanner(file);
            while(scan.hasNextLine()) {
                String[] split = scan.nextLine().split(" ");
                hashQP.put(split[0], split[1]);
                hashSC.put(split[0], split[1]);
            }
            scan.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            System.exit(0);
        }
    }
    public static void readFile(ArrayList<String> MLkey, String filename) {
        File file = new File(filename);
        try {
            Scanner scan = new Scanner(file);
            while(scan.hasNextLine()) {
                String[] split = scan.nextLine().split(" ");
                MLkey.add(split[0]);
            }
            scan.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            System.exit(0);
        }
    }
}
