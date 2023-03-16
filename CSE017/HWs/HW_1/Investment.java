import java.util.Random;
/***
 * Class to model the entity investment
 * @author Julian Chattopadhyay
 * @version 0.1
 * Date of creation: August 27, 2022
 * Last Date Modified: August 29, 2022
 */

public class Investment extends BankAccount{
    private String type;
    /***
	 * Default constructor
	 * No parameters
	 * Initializes type and owner to the string "none", number to a random 10-digit long, and balance to 0.0, 
	 */
    public Investment() {
        super();
        type = "none";
    }

    /***
	 * Constructor with three parameters
	 * @param	owner for the name of an investment account owner
	 * @param	balance for the balance of an investment account
	 * @param	type for the type of an investment account
	 */
    public Investment(String owner, double balance, String type) {
        super(owner, balance);
        this.type = type;
    }

    /***
     * Getter for the type of investment account
     * @param no parameters
     * @return the value of the data member type 
     */
    public String getType() {
        return type;
    }

    /***
	 * Setter for the type of investment account
	 * @param	type to set the data member type
	 * no return value
	 */
    public void setType(String type) {
        this.type = type;
    }
    
    /***
     * Method to return investment risk
     * no parameters
     * @return double multiplier that is the risk of investment
     */
    public double applyRisk() {
        Random r = new Random();
        double multiplier = 1.0;
       
        while(multiplier > .05) {
            multiplier = r.nextInt()/1000000000;
            multiplier = multiplier/100;
        }
        if (r.nextDouble() < .5) {
            return -1 * multiplier;
        }
    
        return multiplier;
    }
    /***
	 * Method to get the investment information
	 * no parameters
	 * @return formatted string containing the value of the data members
	 */
    public String toString() {
        String sr = String.format("%-30s","Investment") + super.toString();
        sr += String.format("%-30s", type);
        return sr; 
    }
}
