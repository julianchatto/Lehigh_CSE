import java.util.Random;
/***
 * Class to model the entity bankAccount
 * @author Julian Chattopadhyay
 * @version 0.1
 * Date of creation: August 27, 2022
 * Last Date Modified: August 29, 2022
 */

public class BankAccount {
    private long number;
    private String owner;
    protected double balance;
    /***
	 * Default constructor
	 * No parameters
	 * Initializes owner to the string "none", number to a random 10-digit long, and balance to 0.0, 
	 */
    public BankAccount() {
        number = generateNumber();
        owner = "none";
        balance = 0.0;
    }
    
    /***
	 * Constructor with three parameters
	 * @param	owner for the name of a bankaccount owner
	 * @param	balance for the balance of a backaccount owner
     * Intializes number to a random 10-digit long
	 */
    public BankAccount(String owner, double balance) {
        number = generateNumber();
        this.owner = owner;
        this.balance = balance;
    }

    /***
     * Getter for the type of bankaccount
     * @param no parameters
     * @return the value of the data member number 
     */
    public long getNumber() {
        return number;
    }
    /***
     * Getter for the owner of bankaccount
     * @param no parameters
     * @return the value of the data member owner 
     */
    public String getOwner() {
        return owner;
    }
    /***
     * Getter for the balance of bankaccount
     * @param no parameters
     * @return the value of the data member balance 
     */
    public double getBalance() {
        return balance;
    }
    /***
     * Method to add money to a bankaccount
     * @param amount
     * no return value
     */
    public void deposit(double amount) {
        balance += amount;
    }
    /***
     * Method to determine if a withdrawl is possible
     * @param amount
     * @return true if the money in the account is greater than or equal to the money withdrawn, false otherwise
     */
    public boolean withdrew(double amount) {
        return balance >= amount;
    }
    /***
     * Method to generate a random bank account number
     * @param no parameters
     * @return a random 10 digit long, the bank account number
     */
    private long generateNumber() {
        Random r = new Random();
        long accountNumber = Math.abs(r.nextLong())/1000000000;
        return accountNumber/10;
     }

    /***
	 * Method to get the bankaccount information
	 * no parameters
	 * @return formatted string containing the value of the data members
	 */
    public String toString() {
        return String.format("%-10d\t%-30s\t%-10.2f", number, owner, balance);
    }

    
}

