/***
 * Class to model the entity savings
 * @author Julian Chattopadhyay
 * @version 0.1
 * Date of creation: August 27, 2022
 * Last Date Modified: August 29, 2022
 */
public class Savings extends BankAccount{
    private double yearlyInterestRate;
     /***
	 * Default constructor
	 * No parameters
	 * Initializes type and owner to the string "none", number to a random 10-digit long, and balance and yearlyInterestRate to 0.0, 
	 */
    public Savings() {
        super();
        yearlyInterestRate = 0.0;
    }

    /***
	 * Constructor with three parameters
	 * @param	owner for the name of an savings account owner
	 * @param	balance for the balance of an savings account
	 * @param	yearlyInterestRate for the yearly interest rate of a savings account
	 */
    public Savings(String owner, double balance, double yearlyInterestRate) {
        super(owner, balance);
        this.yearlyInterestRate = yearlyInterestRate;
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
        return (((yearlyInterestRate / 12) / 100) * balance);
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
}
