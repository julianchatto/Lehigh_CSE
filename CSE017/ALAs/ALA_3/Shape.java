/***
 * Class to model the entity Shape
 * @author Julian Chattopadhyay
 * @version 0.1
 * Date of creation: September 7, 2022
 * Last Date Modified: September 13, 2022
 */

public abstract class Shape implements Comparable<Shape>, Cloneable, Scalable {
    private String color;

    /***
     * Default constructor
     * No parameters
     * Initializes color to black
     */
    protected Shape() {
        this.color = "Black";
    }

    /***
     * Constructor with one parameter
     * @param color for the color of a shape object
     */
    protected Shape(String color) {
        this.color = color;
    }
    /***
     * Method to get the color of a shape
     * @return the color of a shape
     */
    public String getColor() {
        return color;
    }

    /***
     * Method to set the color of a shape
     * @param c fot the color of a shape
     * no return value
     */
    public void setColor(String c) {
        color = c;
    }

    /***
     * Method to get the class information
     */
    public String toString() {
        return String.format("%-10s",color);
    }


    /***
     * Abstract method to get the area of a shape
     * to be implemented in concrete classes
     */
    public abstract double getArea();
    /***
     * Abstract method to get the perimiter of a shape
     * to be implemented in concrete classes
     */
    public abstract double getPerimeter();

    /***
     * Method to compare areas of a shape
     * @return 0 if equal, -1 if what is being compared is smaller, 1 otherwise
     */
    public int compareTo(Shape s)  {
        if (this.getArea() == s.getArea()) {
            return 0;
        }
        if (this.getArea() < s.getArea()) {
            return -1;
        }
        return 1;
    }

     /***
     * Abstract method to create a deep copy of a shape object
     * no return value
     */
    public abstract Object clone();

    /***
     * Abstract method to scale an atribute of a shape object
     * no return value
     */
    public abstract void scale(double f);
}