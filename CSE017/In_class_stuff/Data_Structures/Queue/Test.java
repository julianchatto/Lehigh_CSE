public class Test {
    public static void main(String[] args) {
        Queue<String> cityQueue = new Queue<>();
        cityQueue.offer("New York");
        cityQueue.offer("San Diego");
        cityQueue.offer("Atlanta");
        cityQueue.offer("Baltimore");
        cityQueue.offer("Pittsburg");
        System.out.println("City Queue (toString): " +
        cityQueue.toString());
        System.out.print("City Queue (poll): ");
        while(!cityQueue.isEmpty())
            System.out.print(cityQueue.poll() + " ");
    }
}
