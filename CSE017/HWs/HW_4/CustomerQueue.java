import java.util.LinkedList;
import java.util.Queue;

/***
 * Class to model the CustomerQueue 
 * @author Julian Chattopadhyay
 * @version 0.1
 * Date of creation: October 12, 2022
 * Last Date Modified: October 15, 2022
 */
public class CustomerQueue {
    private Queue<Customer> customers;
    
    /***
     * Default constructor
     */
    public CustomerQueue() {
        customers = new LinkedList<Customer>();
        
    }

    /***
     * Method to return and remove the customer at the top of the queue
     * @return  the customer at the top of the queue
     */
    public Customer getNextCustomer() {
        return customers.poll();
    }

    /***
     * Method to incremetn the waiting time for each customer in the Queue
     */
    public void updateWaitingTime() {
        for (Customer x: customers) {
            x.incrementWaitingTime();
        }
    }

    /***
     * Method to add a customer to the queue
     * @param   c for the customer being added to the queue
     */
    public void addCustomer(Customer c) {
        customers.offer(c);
    }

    /***
     * Method to check if customers is empty
     * @return  true if customers is empty, false otherwise
     */
    public boolean isEmpty() {
        return customers.isEmpty();
    }

    /***
     * Method to return the number of customers in the queue
     * @return the number of customers in the queue
     */
    public int size() {
        return customers.size();
    }

    /***
     * Method to return the contents of customers
     */
    public String toString() {
        return customers.toString();
    }
}