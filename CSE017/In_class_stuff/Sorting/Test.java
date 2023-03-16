public class Test {
    public static void main(String[] args) {
        int[] list = {67, 33, 21, 84, 49, 50, 75};
        System.out.println("Selection Sort:");
        print(list);
        Sort.selectionSort(list);
        print(list);

        shuffle(list);
        
        System.out.println("Insertion Sort:");
        print(list);
        Sort.insertionSort(list);
        print(list);

        shuffle(list);
        
        System.out.println("Bubble Sort:");
        print(list);
        Sort.bubbleSort(list);
        print(list);

        shuffle(list);
        
        System.out.println("Merge Sort:");
        print(list);
        Sort.mergeSort(list);
        print(list);

        shuffle(list);
        
        System.out.println("Quick Sort:");
        print(list);
        Sort.quickSort(list);
        print(list);

        shuffle(list);
        
        System.out.println("Heap Sort:");
        print(list);
        Sort.heapSort(list);
        print(list);

        shuffle(list);
        
        System.out.println("Bucket Sort:");
        print(list);
        Sort.bucketSort(list);
        print(list);

        shuffle(list);
        
        System.out.println("Radix Sort:");
        print(list);
        Sort.radixSort(list);
        print(list);
    }
    public static void print(int[] list) {
        for (int value: list) {
            System.out.print(value + " ");
        }
        System.out.println();
    }
    public static void shuffle(int[] list) {
        for (int i = 0; i < list.length; i++) {
            int randIndex = (int) (Math.random() * list.length);
            int temp = list[i];
            list[i] = list[randIndex];
            list[randIndex] = temp;
        }
    }
}
