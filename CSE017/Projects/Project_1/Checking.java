/***
 * Class to model the entity Checking
 * @author Julian Chattopadhyay
 * @version 0.1
 * Date of creation: September 11, 2022
 * Last Date Modified: September 11, 2022
 */
public class Checking extends BankAccount {
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
    /***
     * Method to determine if a checking account is closable
     * @return true if closable, false otherwise
     */
    public boolean isClosable() {
        if (balance < 200.00) 
            return true;
        return false;
    }
}
