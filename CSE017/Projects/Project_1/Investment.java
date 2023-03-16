
/***
 * Class to model the entity Investment
 * @author Julian Chattopadhyay
 * @version 0.1
 * Date of creation: September 11, 2022
 * Last Date Modified: September 20, 2022
 */
public class Investment extends BankAccount {
    private String type;
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
	 * Constructor with four parameters
     * @param   number for the investment account number
	 * @param	owner for the name of an investment account owner
	 * @param	balance for the balance of an investment account
	 * @param	type for the type of an investment account
	 */
    public Investment(long number, String owner, double balance, String type) {
        super(number, owner, balance);
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
       
        double multiplier = Math.random();
        multiplier /= 20;
        
        if (Math.random() < .5) {
            balance -= multiplier*balance;
            return -1 * multiplier;
        }
        balance += multiplier*balance;
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

    /***
     * Method to get investment account information 
     * no parameters
     * @return formatted string containing the value of the data members spaced with "|"
     */
    public String simpleString() {
        return "Investment|" + super.simpleString() + "|" + type;
    }
    
    /***
     * Method to determine if an investment account is closable
     * @return true if closable, false otherwise
     */
    public boolean isClosable() {
        if (balance < 200.00) 
            return true;
        return false;
    }
}
