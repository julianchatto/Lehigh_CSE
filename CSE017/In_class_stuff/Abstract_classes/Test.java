public class Test {
    public static void main(String[] args) {
        Shape[] shapes = new Shape[2];
        shapes[0] = new Circle("red", 5.5);
        shapes[1] = new Rectangle("Blue", 6.1, 2.3);

        for (Shape s: shapes) {
            System.out.println(s);
        }
    }
}