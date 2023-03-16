import java.util.InputMismatchException;

/***
 * Class to model the entity InvalidSeatException
 * @author Julian Chattopadhyay
 * @version 0.1
 * Date of creation: September 13, 2022
 * Last Date Modified: September 13, 2022
 */
public class InvalidAccountNumberException extends InputMismatchException {

    /***
     * Default Constructor 
     * No parameters
     * Calls InputMismatchException
     */
    public InvalidAccountNumberException() {
        super();
    }

    /***
     * Constructor with one parameter
     * @param message for the error message
     * calls InputMismatchException
     */
    public InvalidAccountNumberException(String message) {
        super(message);
    }
}