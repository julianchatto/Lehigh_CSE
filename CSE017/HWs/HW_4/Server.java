/***
 * Class to model the entity Server 
 * @author Julian Chattopadhyay
 * @version 0.1
 * Date of creation: October 12, 2022
 * Last Date Modified: October 12, 2022
 */
public class Server {
    private Customer currentCustomer;
    private boolean status; 
    private int serviceTime;

    /***
     * Default constructor
     */
    public Server() {
        status = true;
        serviceTime = 0;
        currentCustomer = null;
    }

    /***
     * Method to return the status
     * @return the status of the server
     */
    public boolean isFree() {
        return status;
    }

    /***
     * Method to set the status of a server to busy
     */
    public void setBusy() {
        status = false;
    }

    /***
     * Method to set the status of a server to free
     */
    public void setFree() {
        status = true;
    }

    /***
     * Method to return the service time
     * @return the service time of a server
     */
    public int getServiceTime() {
        return serviceTime;
    }

    /***
     * Method to set the service time of a server 
     * @param   sTime for the serivce time
     */
    public void setServiceTime(int sTime) {
        serviceTime = sTime;
    }

    /***
     * Method to decrement the service time by 1
     */
    public void decrementServiceTime() {
        serviceTime--;
    }

    /***
     * Method to set the current customer being served
     * @param   c for the current customer being served
     */
    public void setCurrentCustomer(Customer c) {
        currentCustomer = c;
    }

    /***
     * Method to return the cureent customer being served
     * @return the current customer being served
     */
    public Customer getCurrentCustomer() {
        return currentCustomer;
    }

    /***
     * Method to return the contents of a server
     * @return a string of the contents of the server
     */
    public String toString() {
        return "Serving " + currentCustomer.getCustomerNo() + ", service time: " + serviceTime;
    }
}