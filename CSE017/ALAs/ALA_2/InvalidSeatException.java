/***
 * Class to model the entity InvalidSeatException
 * @author Julian Chattopadhyay
 * @version 0.1
 * Date of creation: September 4, 2022
 * Last Date Modified: September 5, 2022
 */
public class InvalidSeatException extends Exception {

    /***
     * Default Constructor
     * No parameters
     * runs if a seat is out of bounds
     */
    public InvalidSeatException() {
        super("Invalid Seat Number");
    }

    /***
     * Constructor with one parameter
     * @param message
     * runs all invalidSeatExceptions excpet if a seat is out of bounds
     */ 
    public InvalidSeatException(String message) {
        super(message);
    }
}