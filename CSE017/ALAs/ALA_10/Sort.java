import java.util.ArrayList;
public class Sort {
    public static int[] iterations = new int[8]; 
    public static int maxIterations;
    public static <E extends Comparable<E>> void selectionSort(ArrayList<E> list) { // O(n^2)
        int minIndex;
        iterations[0] = 0;
        for (int i=0; i<list.size()-1; i++) {
            iterations[0]++;
            E min = list.get(i);
            minIndex = i;
            for (int j=i; j<list.size(); j++){
                iterations[0]++;
                if (list.get(j).compareTo(min) < 0){
                    min = list.get(j);
                    minIndex = j;
                }
            }
            swap(list, i, minIndex);
        }
    }
    public static <E extends Comparable<E>> void insertionSort(ArrayList<E> list) {
        iterations[1] = 0;
        for (int i=1; i<list.size(); i++) {
            iterations[1]++;
            //Insert element i in the sorted sub-list
            E currentVal = list.get(i);
            int j = i;
            while (j > 0 && currentVal.compareTo(list.get(j - 1)) < 0) {
                iterations[1]++;
                // Shift element (j-1) into element (j)
                list.set(j, list.get(j-1));
                j--;
            }
            // Insert currentVal at position j
            list.set(j, currentVal);
        }
    }

    public static <E extends Comparable<E>> void bubbleSort(ArrayList<E> list) { // O(n^2)
        iterations[2] = 0;
        boolean sorted = false;
        for (int k=1; k < list.size() && !sorted; k++) {
            iterations[2]++;
            sorted = true;
            for (int i=0; i<list.size()-k; i++) {
                iterations[2]++;
                if (list.get(i).compareTo(list.get(i+1)) > 0) {
                    // swap
                    swap(list, i, i+1);
                    sorted = false;
                }
            }
        }
    }
    public static <E> ArrayList<E> subList(ArrayList<E> list, int start, int end) {
        if (start < 0 || start >= list.size() || end < 0 || end  > list.size() || start > end) {
            throw new ArrayIndexOutOfBoundsException();
        }
        ArrayList<E> newList = new ArrayList<>();
        for (int i = start; i < end; i++) {
            iterations[3]++;
            newList.add(list.get(i));
        }
        return newList;
    }


    public static <E extends Comparable<E>> void mergeSort(ArrayList<E> list) { // O(n log(n))
        iterations[3]++;
        if (list.size() > 1) { // ==1: base case
            ArrayList<E>  firstHalf =  subList(list, 0, list.size()/2);
            ArrayList<E>  secondHalf = subList(list, list.size()/2, list.size());
            try {
                mergeSort(firstHalf);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                System.exit(0);
            }
            // O(log n)
            mergeSort(secondHalf); // O(log n)
            merge(firstHalf, secondHalf, list);
        }
    }

    public static <E extends Comparable<E>> void merge(ArrayList<E> list1, ArrayList<E> list2, ArrayList<E> list) { 
        int list1Index = 0;
        int list2Index = 0;
        int listIndex = 0;
        while( list1Index < list1.size() && list2Index < list2.size()) {
            iterations[3]++;
            if (list1.get(list1Index).compareTo(list2.get(list2Index)) < 0) {
                list.set(listIndex++,  list1.get(list1Index++));
            } else {
                list.set(listIndex++,  list2.get(list2Index++));
            }
        }
        while(list1Index < list1.size()) {
            iterations[3]++;
            list.set(listIndex++,  list1.get(list1Index++));
        }
        while(list2Index < list2.size()) {
            iterations[3]++;
            list.set(listIndex++,  list2.get(list2Index++));
        }
    }    

    public static <E extends Comparable<E>> void quickSort(ArrayList<E> list) {
        iterations[4] = 0;
        quickSort(list, 0, list.size()-1);
    }
    public static <E extends Comparable<E>> void quickSort(ArrayList<E> list, int first, int last) {
        iterations[4]++;
        if (last > first) {
            int pivotIndex = partition(list, first, last);
            quickSort(list, first, pivotIndex-1);
            quickSort(list, pivotIndex+1, last);
        }
    }

    public static <E extends Comparable<E>> int partition(ArrayList<E> list, int first, int last) {
        E pivot;
        int index, pivotIndex;
        pivot = list.get(first);// pivot is the first element
        pivotIndex = first;
        for (index = first + 1; index <= last; index++) {
            iterations[4]++;
            if (list.get(index).compareTo(pivot) < 0){
                pivotIndex++;
                swap(list, pivotIndex, index);
            }
        }
        swap(list, first, pivotIndex);
        return pivotIndex;
    }

    public static <E> void swap(ArrayList<E> list, int first, int pivotIndex) {
        E temp = list.get(first);
        list.set(first, list.get(pivotIndex));
        list.set(pivotIndex, temp);
    }

    public static <E extends Comparable<E>> void heapSort(ArrayList<E> list) {
        Heap<E> heap = new Heap<>();
        iterations[5] = 0;
        for(int i=0; i<list.size(); i++){
            iterations[5]++;
            heap.add(list.get(i));
        }
        for (int i=list.size()-1; i>=0; i--) {
            iterations[5]++;
            list.set(i, heap.remove());
        }
    }

    public static int max(ArrayList<Integer> list) {
        int max = list.get(0);
        maxIterations = 0;
        for (int x: list) {
            maxIterations++;
            if (x > max) {
                max = x;
            }
        }
        return max;
    }


    public static void bucketSort(ArrayList<Integer> list) { //O(n+t)
        int t = max(list);
        iterations[6] = maxIterations;        
        ArrayList<ArrayList<Integer>> buckets;
        buckets = new ArrayList<>(t+1);
        for(int i=0; i<t+1; i++) { // O(t+1)
            iterations[6]++;
            buckets.add(new ArrayList<>());// bucket i
        }

        //Distribute the data on the buckets
        for(int i=0; i<list.size(); i++) { // O(n)
            iterations[6]++;
            ArrayList<Integer> bucket = buckets.get(list.get(i));
            bucket.add(list.get(i));
        }
        // Move the data from the buckets back to the list
        int k = 0;
        for(int i=0; i<buckets.size(); i++) { // O(t+1)
            iterations[6]++;
            ArrayList<Integer> bucket = buckets.get(i);
            for(int j=0; j<bucket.size(); j++) {
                iterations[6]++;
                list.set(k++ , bucket.get(j));
            }
        }
    }

    public static void radixSort(ArrayList<Integer> list) { // O(d*n)
        iterations[7] = maxIterations;
        ArrayList<ArrayList<Integer>> buckets;
        buckets = new ArrayList<>(10);// 10 buckets
        Integer maxValue = max(list); // O(n)
        int digits = maxValue.toString().length();
        for(int d=0; d<digits; d++) { 
            iterations[7]++;// #iterations = digits
            for(int j=0; j<10; j++) { // create buckets for iteration d // O(1)
                iterations[7]++;
                buckets.add(new ArrayList<>());
            }
            //Distribute the data on the buckets
            for(int j=0; j<list.size(); j++){ // O(n)
                iterations[7]++;
                int digit = (list.get(j) % (int)(Math.pow(10, d+1))) / (int)(Math.pow(10,d));
                ArrayList<Integer> bucket = buckets.get(digit);
                bucket.add(list.get(j));
            }
            // Move the data from the buckets back to the list
            int k=0;
            for(int l=0; l<10; l++) { // O(n)
                iterations[7]++;
                ArrayList<Integer> bucket = buckets.get(l);
                for(int j=0; j<bucket.size(); j++) {
                    iterations[7]++;
                    list.set(k++, bucket.get(j));
                }
            }
            buckets.clear(); // for next iteration
        }
    }
       
}