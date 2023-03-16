/***
 * Class to model the entity Circle
 * @author Julian Chattopadhyay
 * @version 0.1
 * Date of creation: September 7, 2022
 * Last Date Modified: September 13, 2022
 */
public class Circle extends Shape {
    private double radius; 
    
    /***
	 * Default constructor
	 * No parameters
	 * Initializes radius to 1.0 and calls the super default constructor
	 */
    public Circle() {
        super();
        radius = 1.0;
    }

    /***
	 * Constructor with two parameters
	 * @param	c for the color of the circle
     * @param   r for the radius of the circle
	 */
    public Circle(String c, double r){
        super(c);
        radius = r;
    }

    /***
     * Method to get the radius of the circle
     * @return radius of the circle
     */
    public double getRadius(){
        return radius;
    }

    /***
     * Method to set the radius of the cricle
     * @param r for the radius of the circle
     * no return value
     */
    public void setRadius(double r){
        radius = r;
    }

    /***
     * Method to print the class information
     * @return String containg the class information
     */
    public String toString() {
        return String.format("%-10s\t%s\t%-43.2f\t%-10.2f\t%-10.2f","Circle", super.toString(), radius, getArea(), getPerimeter());
    
    }

    /***
     * Method to calculate the area of the circle
     * @return area of the circle
     */
    public double getArea() {
        return Math.PI *radius*radius;
    }
    /***
     * Method to calculate the perimiter of the circle
     * @return perimter of the circle
     */
    public double getPerimeter() {
        return 2*Math.PI*radius;
    }

    /***
     * Method to create a deep copy of a circle object
     * @return new deep copy of a circle object
     */
    public Object clone(){
        return new Circle(getColor(), radius);
    }

    /***
     * Method to scale the radius of a circle object
     * no return value
     */
    public void scale(double f) {
        radius *= f;
    }
}
