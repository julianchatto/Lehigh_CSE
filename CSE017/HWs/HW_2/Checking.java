/***
 * Class to model the entity checking
 * @author Julian Chattopadhyay
 * @version 0.1
 * Date of creation: August 27, 2022
 * Last Date Modified: September 5, 2022
 */
public class Checking extends BankAccount{
    /***
	 * Default constructor
	 * No parameters
	 * Initializes owner to the string "none", number to a random 10-digit long, and balance to 0.0, 
	 */
    public Checking() {
        super();
    }

    /***
	 * Constructor with two parameters
	 * @param	owner for the name of a checking account owner
	 * @param	balance for the balance of a checking account
	 */
    public Checking(String owner, double balance) {
        super(owner, balance);
    }

    /***
     * Constructor with three parameters
     * @param number for the bankaccount number of a checking account owner
     * @param owner for the owner of a checking account
     * @param balance for the balance number of a checking account 
     */
    public Checking(long number, String owner, double balance) {
        super(number,owner,balance);
    }

    /***
	 * Method to get the checking information
	 * no parameters
	 * @return formatted string containing the value of the data members
	 */
    public String toString() {
        String sr = String.format("%-30s","Checking") + super.toString();
        return sr;
    }
    /***
     * Method to get checking account information 
     * no parameters
     * @return formatted string containing the value of the data members spaced with "|"
     */
    public String simpleString() {
        return "Checking|" + super.simpleString();
    }

}