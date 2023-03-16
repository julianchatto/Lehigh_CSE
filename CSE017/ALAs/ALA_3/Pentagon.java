/***
 * Class to model the entity Pentagon
 * @author Julian Chattopadhyay
 * @version 0.1
 * Date of creation: September 7, 2022
 * Last Date Modified: September 13, 2022
 */
public class Pentagon extends Shape {
    private double side; 
    /***
	 * Default constructor
	 * No parameters
	 * Initializes side to 1.0 and calls the super default constructor
	 */
    public Pentagon() {
        super();
        side = 1.0;
    }
    /***
	 * Constructor with two parameters
	 * @param	c for the color of the pentagon
     * @param   s for the side of the pentagon
	 */
    public Pentagon(String c, double s) {
        super(c);
        side = s;
    }

    /***
     * Method to get the length of a side
     * @return side length of a pentagon
     */
    public double getSide(){
        return side;
    }

    /***
     * Method to set the side of a pentagon
     * @param s for the side length of a pentagon
     */
    public void setSide(double s){
        side = s;
    }

    /***
     * Method to print the class information
     * @return String containg the class information
     */
    public String toString() {
        return String.format("%-10s\t%s\t%-43.2f\t%-10.2f\t%-10.2f","Pentagon", super.toString(), side, getArea(), getPerimeter());
    
    }

    /***
     * Method to calculate the area of a pentagon
     * @return the area of a pentagon
     */
    public double getArea() {
        double area = 2*Math.sqrt(5);
        area += 5;
        area *=5;
        area = Math.sqrt(area);
        area *= side * side;
        area /=4;
        return area;
    }
    /***
     * Method to calculate the perimiter of a pentagon
     * @return the perimeter of a pentagon
     */
    public double getPerimeter() {
        return 5*side;
    }
    /***
     * Method to create a deep copy of a pentagon object
     * @return new deep copy of a pentegon object
     */
    public Object clone(){
        return new Pentagon(getColor(), side);
    }

    /***
     * Method to scale the side of a pentagon object
     * no return value
     */
    public void scale(double f) {
        side *= f;
    }
}
