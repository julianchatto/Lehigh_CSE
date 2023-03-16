import java.util.ArrayList;

/***
 * Class to model the entity ServerList
 * @author Julian Chattopadhyay
 * @version 0.1
 * Date of creation: October 12, 2022
 * Last Date Modified: October 15, 2022
 */

 public class ServerList {
    private ArrayList<Server> list;

    /***
     * Constructor with one parameter
     * @param   servers for the number of servers
     */
    public ServerList(int servers) {
        list = new ArrayList<>();
        for (int i = 0; i < servers; i++) {
            list.add(new Server());
        }
        list.trimToSize();
    }

    /***
     * Method to return the index of a free server in list
     * @return  the index of the first free server in list
     */
    public int getFreeServer() {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).isFree()) {
                return i;
            }
        }
        return -1;
    }

    /***
     * Method to set a server status to busy
     * @param   i for the the server number
     * @param   c for the customer
     * @param   serviceTime for the service time
     */
    public void setServerBusy(int i, Customer c, int serviceTime) {
        list.get(i).setCurrentCustomer(c);
        list.get(i).setServiceTime(serviceTime);
        list.get(i).setBusy();
    }    

    /***
     * Method to decrment service time for all busy servers
     */
    public void updateServiceTime() {
        for (Server x: list) {
            if (!x.isFree()) {
                x.decrementServiceTime();
            }
        } 
    }

    /***
     * Method to see if a server has served a customer
     */
    public void checkIfFree() {
        for (Server x: list) {
            if (x.getServiceTime() <= 0) {
                x.setFree();
            }
        }
    }


    /***
     * Method to return the number of busy servers
     * @return  the number of busy servers
     */
    public int getBusyServers() {
        int count = 0;
        for (Server x: list) {
            if (!x.isFree()) {
                count++;
            }
        } 
        return count;
    }
    

    /***
     * Method to print list contents
     */
    public String toString() {
        return list.toString();
    }
 }