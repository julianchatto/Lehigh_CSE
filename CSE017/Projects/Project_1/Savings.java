/***
 * Class to model the entity Savings
 * @author Julian Chattopadhyay
 * @version 0.1
 * Date of creation: September 11, 2022
 * Last Date Modified: September 20, 2022
 */
public class Savings extends BankAccount {
     private double yearlyInterestRate;
    /***
	 * Constructor with three parameters
	 * @param	owner for the name of a savings account owner
	 * @param	balance for the balance of an savings account
	 * @param	yearlyInterestRate for the yearly interest rate of a savings account
	 */
    public Savings(String owner, double balance, double yearlyInterestRate) {
        super(owner, balance);
        this.yearlyInterestRate = yearlyInterestRate;
    } 

    /***
     * Constructor with four paramters
     * @param number for the bankaccount number of a savings account 
     * @param owner for the name of a savings account owner
     * @param balance for the balance of a savings account
     * @param yInterestRate for the yearly interest rate of a savings account
     */
    public Savings(long number, String owner, double balance, double yInterestRate) {
        super(number, owner, balance);
        yearlyInterestRate = yInterestRate;
    }

    
    /***
     * Getter for the yearly interest rate of a savings account
     * @param no parameters
     * @return the value of the data member yearlyInterestRate 
     */
    public double getYearlyInterestRate() {
        return yearlyInterestRate;
    }

    /***
     * Getter for the monthly interest rate of a savings account
     * @param no parameters
     * @return the value of the data member yearlyInterestRate/12 
     */
    public double getMonthlyInterest() {
        return yearlyInterestRate/12;
    }

    /***
	 * Setter for the yearly interest rate of a savings account
	 * @param	type to set the data member type
	 * no return value
	 */
    public void setYearlyInterest(double y) {
        yearlyInterestRate = y;
    }

    /***
     * Method to add the value of the monthly interest to the balance
     * no parameters
     * @return the value of the amount added to the balance

     */
    public double applyInterest() {
        double interest = (((yearlyInterestRate / 12) / 100) * balance);
        balance += balance*interest;
        return interest;
    }

    /***
	 * Method to get the savings information
	 * no parameters
	 * @return formatted string containing the value of the data members
	 */
    public String toString() {
        String sr = String.format("%-30s","Savings") + super.toString();
        sr += String.format("%-10.2f",yearlyInterestRate);
        return sr; 
    }
    /***
     * Method to get savings account information 
     * no parameters
     * @return formatted string containing the value of the data members spaced with "|"
     */
    public String simpleString() {
        return "Savings|" + super.simpleString() + "|" + yearlyInterestRate;
    }

    /***
     * Method to determine if a savings account is closable
     * @return true if closable, false otherwise
     */
    public boolean isClosable() {
        if (balance < 200.00) 
            return true;
        return false;
    }
}
