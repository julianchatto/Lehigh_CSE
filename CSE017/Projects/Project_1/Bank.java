import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.Arrays;

/***
 * Class to model the entity Bank
 * @author Julian Chattopadhyay
 * @version 0.1
 * Date of creation: September 11, 2022
 * Last Date Modified: September 19, 2022
 */

public class Bank {
    private BankAccount[] accounts;
    private int count;

    /***
     * Constructor with one parameter
     * @param filename for the name of the file
     */
    public Bank(String filename) {
        accounts = new BankAccount[100];
        count = 0;
        readAccounts(filename);
    }

    /***
     * Method to read accounts from a file
     * @param filename for the name of the file to be read
     * no return value
     */
    private void readAccounts(String filename) {
        File file = new File(filename);
        try {
            Scanner readFile = new Scanner(file);
            
            for (int i = 0; i < accounts.length; i++) {
                if (readFile.hasNext()) {
                    String[] split = readFile.nextLine().split("\\|");
                   
                    if (split[0].equals("Investment")) {
                        accounts[i] = new Investment(Long.valueOf(split[1]), split[2], Double.parseDouble(split[3]), split[4]);
                    } else if (split[0].equals("Checking")) {
                        accounts[i] = new Checking(Long.valueOf(split[1]), split[2], Double.parseDouble(split[3]));
                    } else {
                        accounts[i] = new Savings(Long.valueOf(split[1]), split[2], Double.parseDouble(split[3]), Double.parseDouble(split[4]));
                    }
                    count++;
                } else {
                    break;
                }
                
                
            }
            readFile.close();
        }
        catch (FileNotFoundException e) { // file not found
            System.out.println("Cannont write to file: accounts.txt");
            System.exit(0);
        }
        catch (ArrayIndexOutOfBoundsException e) { // error while reading file
            System.out.println("Array index out of bounds exception while reading file");
            System.exit(0);
        }
    }

    /***
     * Method to find an account
     * @param number for the bank account number
     * @return the account if found
     * @throws AccountNotFoundException
     */
    public BankAccount find(long number) {
        for (int i = 0; i < count; i++) {
            if (accounts[i].getNumber() == number){
                return accounts[i];
            }
        }
        return null;        
    }

    /***
     * Method to add an account to add an account to accounts
     * @param ba for the bank account
     * @return true if account can be added, false otherwise
     */
    public boolean add(BankAccount ba) {
        if(count == 100) {
            System.out.println("Bank accounts array full. New account cannot be added");
            return false;
        }
        if (ba == null) {
            System.out.println("Error");
            return false;
        }
        accounts[count] = ba;
        count++;
        System.out.println("Account added");
        return true;
    }

    /***
     * Method to remove an account
     * @param number for the bank account number
     * @return true if account is found and removed, false otherwise
     */
    public boolean remove(long number) {
        for (int i = 0; i < count; i++) {
            if (number == accounts[i].getNumber()) {
                for(int j = i; j < count - 1; j++) {
                    accounts[j] = accounts[j+1];
                } 
                count--;
                accounts[count] = null;
                System.out.println("\nAccount number " + number + " removed.");
                return true;
            }
        }
        System.out.println("ACCOUNT NOT FOUND");
        return false;
    }

    /***
     * Method to print all accounts
     * no return value
     */
    public void viewAll() {
        System.out.println("Type                          Number            Owner                           Balance   Interest/Investment type");
        for (int i = 0; i < count; i++) {
            System.out.println(accounts[i].toString());
        }
    }

    /***
     * Method to print all accounts which are closable
     * no return value
     */
    public void viewClosable() {
        System.out.println("Type                          Number            Owner                           Balance   Interest/Investment type");
        for (int i = 0; i < count; i++) {
            if (accounts[i].isClosable()) {
                System.out.println(accounts[i].toString());
            }
            
        }
    }
    /***
     * Method to sort accounts based on balance
     * no return value
     */
    public void sort() {
        Arrays.sort(accounts,0,count);             
    }

    /***
     * Method to saveBankAccounts to a file
     * @param filename for the name of the file 
     * no return value
     */
    public void saveAccounts(String filename) {
        File file = new File(filename);
        try {
            PrintWriter writeFile = new PrintWriter(file);
            for (int i = 0; i < count; i++) {
                if (i == (count-1)) {
                    writeFile.print(accounts[i].simpleString());
                } else {
                    writeFile.println(accounts[i].simpleString());
                }

                
            }
            writeFile.close();
        } 
        catch (FileNotFoundException e) {
            System.out.println("Cannot write to " + filename);
        }
    }

}
