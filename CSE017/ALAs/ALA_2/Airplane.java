import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.PrintWriter;
/***
 * Class to model the entity Airplane
 * @author Julian Chattopadhyay
 * @version 0.1
 * Date of creation: September 4, 2022
 * Last Date Modified: September 5, 2022
 */

public class Airplane {
    private char[][] seatMap;

    /***
	 * Default constructor
	 * No parameters
	 * Initializes seatMap to a 2-D array of empty seats
	 */
    public Airplane() {
        seatMap = new char[9][8];
        for(int i =0; i< seatMap.length; i++) {
            for (int j = 0; j < seatMap[i].length; j++) {
                seatMap[i][j] = '.';
            }
        }
    }
    /***
	 * Constructor with one parameter
	 * @param	filename for the name of a file
	 */
    public Airplane(String fileName) {
        seatMap = new char[9][8];
        readMap(fileName);
    }

    /***
     * Method to either generate a new seat map or read seat map from a file
     * @param   filename for the name of a file
     * no return value
     */
    private void readMap(String filename) {
        //open the file
        File file = new File(filename);
        
        try {
            Scanner readFile = new Scanner(file);
            for(int i =0; i< seatMap.length; i++) {
                for (int j = 0; j < seatMap[i].length; j++) {
                    seatMap[i][j] = readFile.next().charAt(0);
                }
            }
            readFile.close();

        }
        catch (FileNotFoundException e) {
            for(int i =0; i< seatMap.length; i++) {
                for (int j = 0; j < seatMap[i].length; j++) {
                    seatMap[i][j] = '.';
                }
            }
        }
    }
    /***
     * Method to check if a seatNumber exists
     * @param seatNumber for the seatNumber 
     * returns boolean
     * throws InvalidSeatException
     */
    private boolean checkSeatNumber(String seatNumber) throws InvalidSeatException {
        if(seatNumber.matches("[1-9][A-H]")) {
            return true;
        }
        throw new InvalidSeatException("Invalid seat number (row[1-9]column[A-H])");
    }
    /***
     * Method to reserve a seat
     * @param seatNumber for the seatNumber 
     * returns boolean
     * throws InvalidSeatException
     */
    public boolean reserveSeat(String seatNumber) throws InvalidSeatException {
        if(checkSeatNumber(seatNumber)){ // valid seat number
            int row = seatNumber.charAt(0) - '1';
            int col = seatNumber.charAt(1) - 'A';
            if (seatMap[row][col] == '.') { // free
                seatMap[row][col] = 'X';
                return true;
            } 

            return false; // already reserved
            
        }
        return false;
        
    }
    /***
     * Method to free a seat
     * @param seatNumber for the seatNumber 
     * returns boolean
     * throws InvalidSeatException
     */
    public boolean freeSeat(String seatNumber) throws InvalidSeatException {
        if(checkSeatNumber(seatNumber)){ // valid seat number
            int row = seatNumber.charAt(0) - '1';
            int col = seatNumber.charAt(1) - 'A';
            if (seatMap[row][col] == 'X') { // reserved
                seatMap[row][col] = '.';
                return true; 
            } 

            return false; // already free
            
        }
        return false;
    }

    /***
     * Method to save SeatMap to a file
     * @param filename for the name of the file
     * no return value
     */
    public void saveMap (String filename) {
        File file = new File(filename);
        try {
            PrintWriter writeFile = new PrintWriter(file);
            for (int i = 0; i < seatMap.length; i++) {
                for (int j = 0; j < seatMap[i].length; j++) {
                    writeFile.print(seatMap[i][j] + " ");
                }
                writeFile.println(); // new line after one row
            }
            writeFile.close();
        }
        catch (FileNotFoundException e) {
            System.out.println("Cannot write to " + filename);
        }
    }

    /***
     * Method to get the seatMap
     * no parameters
     * returns a string representing the seatMap
     */
    public String toString() {
        String out = "\tA\tB\tC\tD\tE\tF\tG\tH\n";
        for (int i = 0; i<seatMap.length; i++) {
            out += (i+1) + "\t";
            for (int j = 0; j < seatMap[i].length; j++) {
                out += seatMap[i][j] + "\t";
            }
            out += "\n";
        } 
        return out;

    }

}
