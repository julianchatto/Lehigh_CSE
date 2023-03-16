import java.util.Scanner;
import java.util.InputMismatchException;
/***
 * Class to model the entity SeatReservation
 * @author Julian Chattopadhyay
 * @version 0.1
 * Date of creation: September 4, 2022
 * Last Date Modified: September 6, 2022
 */
public class SeatReservation {
    public static void main(String[] args) {
        Scanner keyboard = new Scanner(System.in);
        Airplane myAirplane = new Airplane("seatsmap.txt");
        int operation;
        boolean reserving = true;

        while(reserving) { // while loop to continue reserving seats
            System.out.println(myAirplane);
        
        
            System.out.println("Select an operation:");
            System.out.println("1. Reserve a seat");
            System.out.println("2. Free a seat");
            System.out.println("3. Quit");
            try {
                operation = keyboard.nextInt();
                String seatNumber;
                switch(operation) {
                    case 1: // reserve
                        System.out.println("Enter a seat number:");
                        seatNumber = keyboard.next();
                        if(myAirplane.reserveSeat(seatNumber)) {
                            System.out.println(seatNumber + " successfully reserved");
                        } else { // already reserved
                            System.out.println(seatNumber + " already reserved");
                        }
                        break;
                    case 2: // free
                        System.out.println("Enter a seat number:");
                        seatNumber = keyboard.next();
                        if(myAirplane.freeSeat(seatNumber)) {
                            System.out.println(seatNumber + " successfully freed");
                        } else { // already reserved
                            System.out.println(seatNumber + " already free");
                        }
                        break;
                    case 3: // quit
                        System.out.println("Thank you for using my airplane reservation program");
                        reserving = false;
                        myAirplane.saveMap("seatsmap.txt");
                        break; 
                    default:
                        System.out.println("Invalid operation (1 to 3).");    
                        break;
                }
            }
            catch (InvalidSeatException e) { // Seat does not exist
                System.out.println(e.getMessage());

            }  
            catch (InputMismatchException e) { 
                System.out.println("Invalid input opeartion");
                keyboard.next(); // clears keyboard
            }
        }
        keyboard.close();
    }
}