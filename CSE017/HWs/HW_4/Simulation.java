import java.util.Scanner;
import java.util.Random;

/***
 * Class to model the entity Simulation 
 * @author Julian Chattopadhyay
 * @version 0.1
 * Date of creation: October 12, 2022
 * Last Date Modified: October 16, 2022
 */
public class Simulation {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        Random r = new Random();
        CustomerQueue customerQueue = new CustomerQueue();
       

        // Gather User information
        System.out.println("Enter the simiulation parameters:");

        System.out.println("Simulation time in minutes: ");
        int simTime = scan.nextInt();

        System.out.println("Number of servers: ");
        int numServers = scan.nextInt();

        System.out.println("Customer arrival rate (customers/hour): ");
        double arrivalRate = scan.nextInt();

        System.out.println("Service time in minutes: ");
        int serviceTime = scan.nextInt();


        ServerList servlt = new ServerList(numServers);
        
        // Run Simulation
        System.out.println("Simulation started ... ");
        int freeServer;
        double totalWaitingTime = 0;
        int customerNo = 0;
        for(int clock = 0; clock < simTime; clock++) {
            servlt.updateServiceTime();
            servlt.checkIfFree();

            // increment waiting time
            if(!customerQueue.isEmpty()) {
                customerQueue.updateWaitingTime();
            }

            // add customer to queue
            if (r.nextDouble() < (arrivalRate/60)) {
                customerNo++;// increases customer number
                customerQueue.addCustomer(new Customer(customerNo, clock, 0));
                System.out.println("Customer number " + customerNo + " arrived at time " + clock);
                 
            }
            
            // assign a customer to a server
            freeServer = servlt.getFreeServer();
            if(freeServer != -1 && !customerQueue.isEmpty()) {
                Customer c = customerQueue.getNextCustomer();
                totalWaitingTime += c.getWaitingTime();
                System.out.println("Customer " + c.getCustomerNo() + " assigned to server " + freeServer);
                servlt.setServerBusy(freeServer, c, serviceTime);
            }

        }
        System.out.println("Simulation completed.\n");

        // Print results of simulation
        System.out.println("The simulation ran for " + simTime + " minutes");
        System.out.println("Number of servers: " + numServers);
        System.out.println("Average service time: " + serviceTime);
        System.out.println("Average number of customers: " + (int) arrivalRate + " customers/hour\n");


        System.out.println("Total number of customers: " + customerNo); 
        System.out.println("Number of customers served: " + (customerNo - customerQueue.size() - servlt.getBusyServers())); 
        System.out.println("Number of customers left in queue: " + customerQueue.size());
        System.out.println("Number of customers being served: " + servlt.getBusyServers() + "\n");

        while(!customerQueue.isEmpty()) { // adds waiting time of customers still in queue
            totalWaitingTime += customerQueue.getNextCustomer().getWaitingTime();
        }
        System.out.println("Total waiting time: " + totalWaitingTime + " minutes"); 
        System.out.println("Average waiting time: " + String.format("%.1f", totalWaitingTime/customerNo) + " minutes");

        scan.close();
    }
}