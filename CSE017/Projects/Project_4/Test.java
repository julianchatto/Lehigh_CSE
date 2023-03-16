import java.io.File;
import java.io.PrintWriter;
import java.util.Random;

public class Test {
    public static void main(String[] args) {
        File file = new File("data.txt");

        writeFile(file, 100000);
        externalMergeSort("data.txt");
        System.out.println("\nFile of size 100,000 sorted!\n");
        
        file.delete();

        System.out.printf("%-15s\t%-10s\n", "Filesize", "Iterations");
        for (int i = 10000; i < 100001; i += 10000) {
            writeFile(file, i);
            MergingSort.setIterations0();
            externalMergeSort("data.txt");
            System.out.printf("%-15d\t%-10d\n", i, MergingSort.getIterations());
            file.delete();
        }

        /***
         * DISCUSSION OF RESULTS
         * Complexity: both split and merge are O(n), split is called, in the worst case, logn times because the list is split into elements as a function of the length
         * of the list. The worst case is when the list is reveresed. Merge is O(n). This occurs when the list is sorted. The entire alogirthm
         * runs with O(n logn) complexity because of the while loop. 
         * 
         *  The space complexity is O(n) since there are only two files which both have a linear memory requirment. O(n) + O(n) = O(2n) = O(n)
         * based on the size of the list
         * 
         * The iterations make sense since they are growing logarithmically, plotting the iterations show a coefficient of determination of .8 showing
         * strong logarithmic growth
         */
    }

    public static void writeFile(File file, int size) {
        Random r = new Random();
        try {
            PrintWriter fWriter = new PrintWriter(file);
            for(int i = 0; i < size; i++) {
                fWriter.println(r.nextInt(10001));
            }
            fWriter.close();
        } catch (Exception e) {}
    }

    public static void externalMergeSort(String filename) {
        File file = new File("tempFile1.txt");
        File file2 = new File("tempFile2.txt");
        while (!MergingSort.split(filename, "tempFile1.txt", "tempFile2.txt")) {
            MergingSort.merge("tempFile1.txt", "tempFile2.txt", filename);
        }
        file.delete();
        file2.delete();
    }
}
