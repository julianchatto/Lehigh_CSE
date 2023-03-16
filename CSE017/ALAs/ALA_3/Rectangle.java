/***
 * Class to model the entity Rectangle
 * @author Julian Chattopadhyay
 * @version 0.1
 * Date of creation: September 7, 2022
 * Last Date Modified: September 13, 2022
 */

public class Rectangle extends Shape {
    private double length;
    private double width;

    /***
	 * Default constructor
	 * No parameters
	 * Initializes length and width to 1.0 and calls the super default constructor
	 */
    public Rectangle() {
        super();
        length = 1.0;
        width = 1.0;
    }

    /***
	 * Constructor with three parameters
	 * @param	c for the color of a rectangle
     * @param   l for the length of a rectangle
     * @param   w for the width of a rectangle
	 */
    public Rectangle(String c, double l, double w) {
        super(c);
        length = l;
        width = w;
    }

    /***
     * Method to get the length of rectangle
     * @return length the of a rectangle
     */
    public double getLength() {
        return length;
    }
    /***
     * Method to get the width of rectangle
     * @return width the of a rectangle
     */
    public double getWidth() {
        return width;
    }

    /***
     * Method to set the length of a rectangle
     * @param l for the length of a rectangle
     * no return value
     */
    public void setLength(double l) {
        length = l;
    }

    /***
     * Method to set the width of a rectangle
     * @param w for the width of a rectangle
     * no return value
     */
    public void setWidth(double w) {
        width = w;
    }
    
    /***
     * Method to print the class information
     * @return String containg the class information
     */
    public String toString() {
        return String.format("%-10s\t%s\t%-15.2f\t%-28.2f\t%-10.2f\t%-10.2f","Rectangle", super.toString(), length, width, getArea(), getPerimeter());
    }

    /***
     * Method to calculate the area of a rectangle
     * @return the area of a rectangle
     */
    public double getArea() {
        return length*width;
    }

    /***
     * Method to calculate the perimiter of a rectangle 
     * @return the perimiter of a rectangle
     */
    public double getPerimeter() {
        return 2*(width+length);
    }

     /***
     * Method to create a deep copy of a rectangle object
     * @return new deep copy of a rectangle object
     */
    public Object clone() {
        return new Rectangle(getColor(), length, width);
    }

    /***
     * Method to scale the length and width of a rectangle object
     * no return value
     */
    public void scale(double f) {
        length *= f;
        width *= f;
    }
}
