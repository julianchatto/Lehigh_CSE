import java.util.ArrayList;
import java.util.Collections;
public class Test {
    /****
     * DISCUSSION OF RESULTS
     * 
     * Selection sort makes sense because the big o O(n^2) would give the number of iterations with 8 digits with file size 10000
     * Insertion Sort and Bubble sort make sense because for random and reversed the same logic as selection sort applies. However, if the list 
     *                 is sorted then the iterations are much lower because they do not require the whole algorithm to run
     * Merge sort is always the same, for merge and heap sort nlog(n) ~ how many iterations merge/heap sort
     * Quick sort performs logarithmically for random list, but for sorted and reveresed, the algorithm does not run logirthamically (almost O(n^2))
     * Bucket Sort and Radix sort are just O(n), a constant multiplied by some the length of the list
     * */

    public static final int SIZE = 10000;
    public static void main(String[] args) {
        ArrayList<Integer> randomList = new ArrayList<>();
        for (int i = 0; i < SIZE; i++) {
            randomList.add((int) (Math.random() * (SIZE - 1)) + 1);
        }

        ArrayList<Integer> sortedList = (ArrayList<Integer>) randomList.clone();
        ArrayList<Integer> reveresedList = (ArrayList<Integer>) randomList.clone();
        Collections.sort(sortedList); 
        Collections.sort(reveresedList);
        Collections.reverse(reveresedList);

        System.out.println("\nData set size: " + SIZE + "\n");
        System.out.printf("%-20s\t%-15s\t%-15s\t%-15s\n", "Sorting Algorithm", "Random", "Sorted", "Reversed");

        // Selection Sort
        Sort.selectionSort(randomList);
        int randomIterations = Sort.iterations[0];
        Sort.selectionSort(sortedList);
        int sortedIterations = Sort.iterations[0];
        Sort.selectionSort(reveresedList);
        int reversedIterations = Sort.iterations[0];

        System.out.printf("%-20s\t%-15d\t%-15d\t%-15d\n", "Selection Sort", randomIterations, sortedIterations, reversedIterations);

        // Insertion Sort
        Collections.shuffle(randomList);
        Collections.reverse(reveresedList);
       
        Sort.insertionSort(randomList);
        randomIterations = Sort.iterations[1];
        Sort.insertionSort(sortedList);
        sortedIterations = Sort.iterations[1];
        Sort.insertionSort(reveresedList);
        reversedIterations = Sort.iterations[1];

        System.out.printf("%-20s\t%-15d\t%-15d\t%-15d\n", "Insertion Sort", randomIterations, sortedIterations, reversedIterations);


        // Bubble Sort
        Collections.shuffle(randomList);
        Collections.reverse(reveresedList);
        Sort.bubbleSort(randomList);
        randomIterations = Sort.iterations[2];
        Sort.bubbleSort(sortedList);
        sortedIterations = Sort.iterations[2];
        Sort.bubbleSort(reveresedList);
        reversedIterations = Sort.iterations[2];

        System.out.printf("%-20s\t%-15d\t%-15d\t%-15d\n", "Bubble Sort", randomIterations, sortedIterations, reversedIterations);
    

        // Merge Sort
        Sort.iterations[3] = 0;
        Collections.shuffle(randomList);
        Collections.reverse(reveresedList);
        Sort.mergeSort(randomList);
        randomIterations = Sort.iterations[3];
        Sort.iterations[3] = 0;
        Sort.mergeSort(sortedList);
        sortedIterations = Sort.iterations[3];
        Sort.iterations[3] = 0;
        Sort.mergeSort(reveresedList);
        reversedIterations = Sort.iterations[3];

        System.out.printf("%-20s\t%-15d\t%-15d\t%-15d\n", "Merge Sort", randomIterations, sortedIterations, reversedIterations);
    
    
        // Quick Sort
        Collections.shuffle(randomList);
        Collections.reverse(reveresedList);
        Sort.quickSort(randomList);
        randomIterations = Sort.iterations[4];
        Sort.quickSort(sortedList);
        sortedIterations = Sort.iterations[4];
        Sort.quickSort(reveresedList);
        reversedIterations = Sort.iterations[4];

        System.out.printf("%-20s\t%-15d\t%-15d\t%-15d\n", "Quick Sort", randomIterations, sortedIterations, reversedIterations);
    
        // Heap Sort
        Collections.shuffle(randomList);
        Collections.reverse(reveresedList);
        Sort.heapSort(randomList);
        randomIterations = Sort.iterations[5];
        Sort.heapSort(sortedList);
        sortedIterations = Sort.iterations[5];
        Sort.heapSort(reveresedList);
        reversedIterations = Sort.iterations[5];

        System.out.printf("%-20s\t%-15d\t%-15d\t%-15d\n", "Heap Sort", randomIterations, sortedIterations, reversedIterations);

        // Bucket Sort
        Collections.shuffle(randomList);
        Collections.reverse(reveresedList);
        Sort.bucketSort(randomList);
        randomIterations = Sort.iterations[6];
        Sort.bucketSort(sortedList);
        sortedIterations = Sort.iterations[6];
        Sort.bucketSort(reveresedList);
        reversedIterations = Sort.iterations[6];

        System.out.printf("%-20s\t%-15d\t%-15d\t%-15d\n", "Bucket Sort", randomIterations, sortedIterations, reversedIterations);
        
        // Radix Sort
        Collections.shuffle(randomList);
        Collections.reverse(reveresedList);
        Sort.radixSort(randomList);
        randomIterations = Sort.iterations[7];
        Sort.radixSort(sortedList);
        sortedIterations = Sort.iterations[7];
        Sort.radixSort(reveresedList);
        reversedIterations = Sort.iterations[7];

        System.out.printf("%-20s\t%-15d\t%-15d\t%-15d\n", "Radix Sort", randomIterations, sortedIterations, reversedIterations);
    }

    
}
