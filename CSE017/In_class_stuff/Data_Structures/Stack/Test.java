public class Test {
    public static void main(String[] args) {
        Stack<String> cityStack = new Stack<>();
        cityStack.push("New York");
        cityStack.push("San Diego");
        cityStack.push("Atlanta");
        cityStack.push("Baltimore");
        cityStack.push("Pittsburg");
        System.out.println("City Stack (toString): " + cityStack.toString());
        System.out.print("City Stack (pop): ");
        while(!cityStack.isEmpty()) {
            System.out.print(cityStack.pop() + " ");
        }
        System.out.println();
        
    }
}
