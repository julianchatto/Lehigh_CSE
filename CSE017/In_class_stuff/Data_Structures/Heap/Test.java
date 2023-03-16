public class Test {
    public static void main(String[] args) {
        Heap<String> heap = new Heap<>();
        heap.add("Kiwi");
        heap.add("Strawberry");
        heap.add("Apple");
        heap.add("Banana");
        heap.add("Orange");
        heap.add("Lemon");
        heap.add("Watermelon");
        System.out.println("Heap: " + heap.toString());
        System.out.println("Removed: " + heap.remove());
        System.out.println("Heap: " + heap.toString());
        System.out.println("Heap contains Pear?: " + heap.contains("Pear"));
    }
}
