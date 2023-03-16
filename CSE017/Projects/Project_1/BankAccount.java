/***
 * Class to model the entity BankAccount
 * @author Julian Chattopadhyay
 * @version 0.1
 * Date of creation: September 11, 2022
 * Last Date Modified: September 11, 2022
 */
public abstract class BankAccount implements Comparable<BankAccount>, Closable{
    private long number;
    private String owner;
    protected double balance;

    /***
     * Constructor with two parameters
     * @param owner for the owner of the BankAccount
     * @param balance for the balance of the BankAccount
     * Initializes number to the long 0000000000
     */
    protected BankAccount(String owner, double balance) {
        this.owner = owner;
        this.balance = balance;
        number = 0000000000;
    }

    /***
     * Constructor with three parameters
     * @param number for the BankAccount number of the account
     * @param owner for the owner of the BankAccount
     * @param balance for the balance of the BankAccount
     */
    protected BankAccount(long number, String owner, double balance) {
        this.number = number;
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
    public boolean withdraw(double amount) {
        return balance >= amount;
    }
    /***
     * Method to generate a random bank account number
     * @param no parameters
     * @return a random 10 digit long, the bank account number
     */

    /***
	 * Method to get the bankaccount information
	 * no parameters
	 * @return formatted string containing the value of the data members
	 */
    public String toString() {
        return String.format("%-10d\t%-30s\t%-10.2f", number, owner, balance);
    }
    /***
     * Method to get bankaccount information 
     * no parameters
     * @return formatted string containing the value of the data members spaced with "|"
     */
    public String simpleString() {
        return number + "|" + owner + "|" + balance;
    }

    /***
     * Method to determine which bankAccount has a greater balance
     * @param ba for the bankaccount being compared
     * @return 0 if both accounts have same balance, -1 if account being compared to is greater, 1 otherwise
     */
    public int compareTo(BankAccount ba) {
    
        if (this.getBalance() == ba.getBalance()) {
            return 0;
        }
        if (this.getBalance() < ba.getBalance()) {
            return -1;
        }
        return 1;
    }

    /***
     * Method to dermine if an account is closable
     * @return ture if account has balance less than 200.00
     * no parameters
     */
    public boolean isClosable() {
        if (balance < 200.00) 
            return true;
        return false;
    }
}