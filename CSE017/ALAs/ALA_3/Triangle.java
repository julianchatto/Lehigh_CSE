/***
 * Class to model the entity Triangle
 * @author Julian Chattopadhyay
 * @version 0.1
 * Date of creation: September 7, 2022
 * Last Date Modified: September 13, 2022
 */
public class Triangle extends Shape {
    private double side1;
    private double side2;
    private double side3;

    /***
     * Default constructor
     * No parameters
     * Initilize side1, side2, and side3 to 1.0, calls the super constructor
     */
    public Triangle() {
        super();
        side1 = 1.0;
        side2 = 1.0;
        side3 = 1.0;
    }

    /***
     * Constructor with four paramters
     * @param c for the color of a triangle
     * @param s1 for the length side1 of a triangle
     * @param s2 for the length side2 of a triangle
     * @param s3 for the length side3 of a triangle
     */
    public Triangle(String c, double s1, double s2, double s3) {
        super(c);
        side1 = s1;
        side2 = s2;
        side3 = s3;
    }


    /*** 
     * Method to get the length of side1
     * @return the length of side1
     */
    public double getSide1() {
        return side1;
    }

     /*** 
     * Method to get the length of side2
     * @return the length of side2
     */
    public double getSide2() {
        return side2;
    }

     /*** 
     * Method to get the length of side3
     * @return the length of side3
     */
    public double getSide3() {
        return side3;
    }

    /***
     * Method to set the lenth of side1
     * @param s1 for the length of side1
     * no return value
     */
    public void setSide1(double s1) {
        side1 = s1;
    }

     /***
     * Method to set the lenth of side2
     * @param s2 for the length of side2
     * no return value
     */
    public void setSide2(double s2) {
        side2 = s2;
    }

     /***
     * Method to set the lenth of side3
     * @param s3 for the length of side3
     * no return value
     */
    public void setSide3(double s3) {
        side3 = s3;
    }

   
    /***
     * Method to print the class information
     * @return String containg the class information
     */
    public String toString() {
        return String.format("%-10s\t%s\t%-10.2f\t%-10.2f\t%-10.2f\t%-10.2f\t%-10.2f","Triangle", super.toString(), side1, side2, side3, getArea(), getPerimeter()); 
    }

    /***
     * Method to calculate the area of a triangle
     * @return the area of a triangle
     */
    public double getArea() {
        double p = getPerimeter()/2;
        double a = Math.sqrt(p * (p-side1) * (p-side2) * (p-side3));
        return a;
    }

    /***
     * Method to calculate the perimeter of a triangle
     * @return the perimeter of a triangle
     */
    public double getPerimeter() {
        return side1+side2+side3;
    }

     /***
     * Method to create a deep copy of a triangle object
     * @return new deep copy of a triangle object
     */
    public Object clone() {
        return new Triangle(getColor(), side1, side2, side3);
    }

    /***
     * Method to scale the sides of a triangle object
     * no return value
     */
    public void scale(double f) {
        side1 *= f;
        side2 *= f;
        side3 *= f;
    }
}
