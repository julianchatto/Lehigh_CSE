public class Test {
    public static void main(String[] args) {
        PriorityQueue<String> cityPriorityQueue = new PriorityQueue<>();
        cityPriorityQueue.offer("New York");
        cityPriorityQueue.offer("San Diego");
        cityPriorityQueue.offer("Atlanta");
        cityPriorityQueue.offer("Baltimore");
        cityPriorityQueue.offer("Pittsburg");
        System.out.println("\nCity Priority Queue: " + cityPriorityQueue.toString());
        System.out.print("City Priority Queue (poll): ");
        while(!cityPriorityQueue.isEmpty()) {
            System.out.print(cityPriorityQueue.poll() + " ");
        }
        System.out.println();
    }
}
