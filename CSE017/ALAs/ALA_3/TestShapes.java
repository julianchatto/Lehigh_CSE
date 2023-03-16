import java.util.Arrays;
/***
 * Class to model the entity Test Shapes
 * @author Julian Chattopadhyay
 * @version 0.1
 * Date of creation: September 7, 2022
 * Last Date Modified: September 13, 2022
 */


public class TestShapes {
    public static void main(String[] args) {
        // initilize array of type shape
        Shape[] shapes = new Shape[8];
        shapes[0] = new Circle("Black", 2.5);
        shapes[1] = new Triangle("Green", 6.0, 6.0, 6.0);
        shapes[2] = new Rectangle("Red", 5.0, 3.0);
        shapes[3] = new Pentagon("Yellow", 7.0);
        shapes[4] = (Circle) shapes[0].clone();
        shapes[5] = (Triangle) shapes[1].clone();
        shapes[6] = (Rectangle) shapes[2].clone();
        shapes[7] = (Pentagon) shapes[3].clone();
        shapes[4].scale(2.0);
        shapes[5].setColor("Orange");
        ((Rectangle) shapes[6]).setLength(10.0);
        ((Pentagon)shapes[7]).setSide(4.0);

        // Array before sorting
        System.out.println("Before sorting");
        printArray(shapes);

        // Array after sorting
        Arrays.sort(shapes, new ComparatorByPerimeter());
        System.out.println("\n\nAfter Sorting by Perimeter");
        printArray(shapes);

        Arrays.sort(shapes, new ComparatorByColor());
        System.out.println("\n\nAfter Sorting by color");
        printArray(shapes);

        // average perimeter
        System.out.println("\n\nThe average perimter is: " + getAveragePerimeter(shapes));
    }

    /***
     * Method to get the average perimeter of shape objects in an array
     * @param list for the array of Shape objects 
     * @return  the average perimeter
     */
    public static String getAveragePerimeter(Shape[] list) {
        double total = 0.0;
        for (int i = 0; i < list.length; i++) {
            total += list[i].getPerimeter();
        }
        return String.format("%-10.2f", total/list.length);

    }

    /***
     * Method to print the contents of an array of type shape
     * @param list for the array to be printed
     * no return value
     */
    public static void printArray(Shape[] list) {
        System.out.println(String.format("%-10s\t%-10s\t%-43s\t%-10s\t%-10s", "Shape", "Color", "Dimensions", "Area", "Perimiter"));
        for(Shape s: list) {
            System.out.println(s);
        }
    }
}
