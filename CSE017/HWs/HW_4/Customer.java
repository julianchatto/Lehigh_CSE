/***
 * Class to model the entity Customer
 * @author Julian Chattopadhyay
 * @version 0.1
 * Date of creation: October 12, 2022
 * Last Date Modified: October 12, 2022
 */
public class Customer {
    private int customerNo;
    private int arrivalTime;
    private int waitingTime;

    /***
     * Constructor with 3 parameters
     * @param   cn for the customer number
     * @param   at for the arrival time
     * @param   wt for the waiting time
     */
    public Customer(int cn, int at, int wt) {
        customerNo = cn;
        arrivalTime = at;
        waitingTime = wt;
    } 

    /*** 
     * Method to return the customer number
     * @return the customer number
     */
    public int getCustomerNo() {
        return customerNo;
    }

    /***
     * Method to return the arrival time
     * @return the arrival time
     */
    public int getArrivalTime() {
        return arrivalTime;
    }

    /***
     * Method to return the waiting time
     * @return the waiting time
     */
    public int getWaitingTime() {
        return waitingTime;
    }

    /*** 
     * Method to set the customer number
     * @param   cn for the customer number
     */
    public void setCustomerNo(int cn) {
        customerNo = cn;
    }

    /***
     * Method to set the arrival time
     * @param   at for the arrival time
     */
    public void setArrivalTime(int at) {
        arrivalTime = at;
    }

    /***
     * Method to set the waiting time
     * @param   wt for the waiting time 
     */
    public void setWaitingTime(int wt) {
        waitingTime = wt;
    }

    /***
     * Method to increase the waiting time by 1
     */
    public void incrementWaitingTime() {
        waitingTime++;
    }

    /***
     * Method to print the customer information
     * @return a string with the contents of the customer
     */
    public String toString() {
        return "Customer number " + customerNo + " arrived at time " + arrivalTime;
    }
}