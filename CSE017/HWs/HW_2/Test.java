import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.InputMismatchException;
import java.util.Scanner;
/***
 * Class to model the entity test
 * @author Julian Chattopadhyay
 * @version 0.1
 * Date of creation: September 5, 2022
 * Last Date Modified: September 8, 2022
 */
public class Test {
    public static void main(String[] args) {
        BankAccount[] bankAccounts = new BankAccount[50];
        Scanner scan = new Scanner(System.in);

        boolean banking = true;
        File file = new File("accounts.txt");
        int numObjects = 0;

        // pulls information from file
        try {
            Scanner readFile = new Scanner(file);
            
            for (int i = 0; i < bankAccounts.length; i++) {
                if (readFile.hasNext()) {
                    String[] split = readFile.nextLine().split("\\|");
                   
                    if (split[0].equals("Investment")) {
                        bankAccounts[i] = new Investment(Long.valueOf(split[1]), split[2], Double.parseDouble(split[3]), split[4]);
                    } else if (split[0].equals("Checking")) {
                        bankAccounts[i] = new Checking(Long.valueOf(split[1]), split[2], Double.parseDouble(split[3]));
                    } else {
                        bankAccounts[i] = new Savings(Long.valueOf(split[1]), split[2], Double.parseDouble(split[3]), Double.parseDouble(split[4]));
                    }
                    numObjects++;
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

        while (banking) {
            System.out.println("Choose an option (1-6)");
            System.out.println("1. View the list of bank accounts");
            System.out.println("2. Search accounts by number");
            System.out.println("3. Add a new account");
            System.out.println("4. Remove an existing account");
            System.out.println("5. Sort the list of bank accounts");
            System.out.println("6. Exit the program");
            try {
                int option = scan.nextInt();
                switch (option) {
                    case 1: // View bank accounts
                        int x = -1;
                        while (x < 0) {
                            System.out.println("How many accounts would you like to print");
                            System.out.println("Number of accounts to print: " + numObjects);
                            x = scan.nextInt();
                        }
                        printBankAccounts(bankAccounts, x, numObjects);
                        break;
                    case 2: // search for account
                        int count = -1;
                        while (count < 0) {
                            System.out.println("How many accounts would you like to check through");
                            System.out.println("Number of accounts to check: " + numObjects);
                            count = scan.nextInt();
                        }
                        System.out.println("\nEnter an account number:");
                        long accountNum = scan.nextLong();
                        if (checkAccountNumber(accountNum)) {
                            int foundAccount = findBankAccount(bankAccounts, count, accountNum, numObjects);
                            if (foundAccount < 0) {
                                System.out.println("\nAccount not found");
                            } else {
                                System.out.println("\nAccount found at index " + foundAccount + "\n");
                                System.out.println(bankAccounts[foundAccount].simpleString() + "\n");
                            }
                        }
                        
                        break;
                    case 3: // add new account
                        boolean addingAccount = true;
                        while(addingAccount) {
                            long acctNum;
                            String name, type;
                            double bal, yIntRate;

                            System.out.println("Enter the account number (10-digits):");
                            acctNum = scan.nextLong();
                            scan.nextLine();
                            if (checkAccountNumber(acctNum)) {
                                System.out.println("Enter the owners name:");
                                name = scan.nextLine();
                                System.out.println("Enter the balance of the account:");
                                bal = scan.nextDouble();
                                scan.nextLine();
                                
                                System.out.println("What type of account (1-3)");
                                System.out.println("1. Checking");
                                System.out.println("2. Savings");
                                System.out.println("3. Investment");
                                int accountType = scan.nextInt();
                                scan.nextLine();
                                switch(accountType) {
                                    case 1: // checking
                                        bankAccounts[numObjects] = new Checking(acctNum, name, bal);
                                       
                                        addingAccount = false;
                                        break;
                                    case 2: // Savings
                                        System.out.println("Enter the yearly interst rate:");
                                        yIntRate = scan.nextDouble();
                                        bankAccounts[numObjects] = new Savings(acctNum, name, bal, yIntRate);
                                        addingAccount = false;
                                        break;
                                    case 3: // Investment
                                        System.out.println("Enter the type of investment account:");
                                        type = scan.nextLine();
                                        bankAccounts[numObjects] = new Investment(acctNum, name, bal, type);
                                        addingAccount = false;
                                        break;
                                    default: 
                                        System.out.println("Invalid Input, try again");
                                        break;
                                }
                                System.out.println("Account added");
                                numObjects++;
                            }
                            
                        }
                        break;
                    case 4: // remove account
                        System.out.println("Enter an account number");
                        long aNum = scan.nextLong();
                        if(checkAccountNumber(aNum)) {
                            for (int i = 0; i < bankAccounts.length; i++) {
                                if (aNum == bankAccounts[i].getNumber()) {
                                    for(int j = i; j < numObjects - 1; j++) {
                                        bankAccounts[j] = bankAccounts[j+1];
                                    } 
                                    numObjects--;
                                    bankAccounts[numObjects] = null;
                                    System.out.println("\nAccount number " + aNum + " removed.");
                                    break;
                                }
                            }
                        }
                        break;
                    case 5: // sort accounts
                        int acctsNum = bankAccounts.length;
                        while (acctsNum >= bankAccounts.length || acctsNum < 0) {
                            System.out.println("How many accounts would you like to sort");
                            System.out.println("Number of accounts to sort: " + numObjects);
                            acctsNum = scan.nextInt();
                        }
                        sortBankAccounts(bankAccounts, acctsNum, numObjects);
                        break;
                    case 6: // quit
                        banking = false;
                        System.out.println("Program exited");
                        saveBankAccounts("accounts.txt", numObjects, bankAccounts);
                    
                        break;
                    default:
                        System.out.println("Invalid input try again");
                        break;
                }

            }
            catch (InvalidAccountNumberException e) { // incompatible
                System.out.println(e.getMessage());
                
            }
            catch (InputMismatchException e) { // wrong inputs
                System.out.println(e.getMessage());
                scan.next();

            } 
            catch (Exception e) { // all other errors
                System.out.println("Error");
                // scan.next(); // clears keyboard
            }
            
            
        }
        scan.close();

    }

    /***
     * Method to print the first count number of ellemnts of an array named list
     * @param list for a list of type BankAccount
     * @param count for the count
     * @param numObj for the number of objects in list
     * no return value
     */
    public static void printBankAccounts(BankAccount[] list, int count, int numObj) {
        if (count > numObj) { 
            count = numObj;
        }  
        System.out.println("Type                          Number            Owner                           Balance   Interest/Investment type");
        for (int i = 0; i < count; i++) {
            System.out.println(list[i].toString());
        }
    }

    /***
     * Method to  search for a BankAccount object in the first count number of elements in the array list using number as the search key
     * @param list
     * @param count
     * @param number
     * @param numObj for the number of objects in list
     * @return index if found -1 otherwise
     */
    public static int findBankAccount(BankAccount[] list, int count, long number, int numObj) {
        if (count > numObj) { 
            count = numObj;
        }  
        for (int i = 0; i < count; i++) {
            if (list[i].getNumber() == number)
                return i;
        }
        return -1;
    }
    /***
     * Method to sort the  BankAccounts in the first count of elements of the array list based on number
     * @param list for the list of type BankAccount
     * @param count for the count
     * @param numObj for the number of objects in list
     * no return value
     */
    public static void sortBankAccounts(BankAccount[] list, int count, int numObj) {
        if (count > numObj) { 
            count = numObj;
        }  
        for (int i=1; i<count; i++) {
            //Insert element i in the sorted sub-list
            BankAccount currentVal = list[i];
            int j = i;
            while (j > 0 && currentVal.getNumber() < (list[j - 1].getNumber())){
                 // Shift element (j-1) into element (j)
                 list[j] = list[j - 1];
                 j--;
            }
            // Insert currentVal at position j
            list[j] = currentVal;
        }                   
    }
    
    /***
     * Method to check if an AccountNumber is valid
     * @param number for the bankAccount number
     * @return true if valid
     * throws InvalidAccountNumberException
    */
    public static boolean checkAccountNumber(long number) throws InvalidAccountNumberException {
        if (String.valueOf(number).length() == 10) 
            return true;
        throw new InvalidAccountNumberException("Invalid account number");
    }
    
    /***
     * Method to saveBankAccounts to a file
     * @param filename for the name of the file 
     * @param numObj for the number of objects in the array
     * @param list for the list of BankAccount objects
     */
    public static void saveBankAccounts (String filename, int numObj, BankAccount[] list) {
        File file = new File(filename);
        try {
            PrintWriter writeFile = new PrintWriter(file);
            for (int i = 0; i < numObj; i++) {
                if (i == (numObj-1)) {
                    writeFile.print(list[i].simpleString());
                } else {
                    writeFile.println(list[i].simpleString());
                }

                
            }
            writeFile.close();
        } 
        catch (FileNotFoundException e) {
            System.out.println("Cannot write to " + filename);
        }
    }


    
}
