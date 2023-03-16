import java.util.InputMismatchException;
import java.util.Scanner;
/***
 * Class to model the entity BankManager
 * @author Julian Chattopadhyay
 * @version 0.1
 * Date of creation: September 11, 2022
 * Last Date Modified: September 20, 2022
 */
public class BankManager {
    public static void main(String[] args) {
        Bank banking = new Bank("accounts.txt");
        Scanner scan = new Scanner(System.in);
        boolean banked = true;

        while (banked) {
            System.out.println("Choose an option (1-7)");
            System.out.println("1. View the list of bank accounts");
            System.out.println("2. Manage Account");
            System.out.println("3. Add a new account");
            System.out.println("4. Remove an existing account");
            System.out.println("5. Sort the list of bank accounts by balance");
            System.out.println("6. View the list of closable accounts");
            System.out.println("7. Exit the program");
            try {
                int option = scan.nextInt();
                switch (option) {
                    case 1: // View bank accounts
                        banking.viewAll();
                        break;
                    case 2: // Manage account
                        boolean managing = true;
                        System.out.println("Enter a bank account number: ");
                        long acctNum = scan.nextLong();
                        if (!checkAccountNumber(acctNum)) {
                            break;
                        }
                        
                        BankAccount acct = banking.find(acctNum);
                        if (acct == null) {
                            System.out.println("Account not found");
                            break;
                        }
                        double bal = acct.getBalance();

                        banking.toString();
                        while (managing) {
                            System.out.println("Choose an option (1-5)");
                            System.out.println("1. Withdraw an amount from the account");
                            System.out.println("2. Deposit an amount to the account");
                            System.out.println("3. Apply the monthly interest for savings accounts only");
                            System.out.println("4. Apply the investment risk for investment accounts only");
                            System.out.println("5. Return to the main menu");
                            int option2 = scan.nextInt();
                            switch(option2) {
                                case 1: // withdraw from account
                                    System.out.println("How much would you like to withdraw?");
                                    System.out.println("Current balance for account " + acctNum + " is: " + bal);
                                    double withdraw = scan.nextDouble();
                                    
                                    if (!acct.withdraw(withdraw)) {
                                        System.out.println("Amount requested greater than available balance");
                                        break;
                                    }
                                    acct.deposit(-withdraw);
                                    System.out.println("New balance for account " + acctNum + " is: " + acct.getBalance());


                                    break;
                                case 2: // depoist to account
                                    System.out.println("How much would you like to deposit? ");
                                    double deposit = scan.nextDouble();
                                    acct.deposit(deposit);

                                    break;
                                case 3: // apply intrest to savings
                                    if (acct.simpleString().charAt(0) == 'S') { // is a savings account
                                        ((Savings) acct).applyInterest();
                                        System.out.println((acct.getBalance() - bal) + " added to account");
                                    } else { // not a savings account
                                        System.out.println("Account " + acctNum + " is not a savings account!");
                                    }
                                    break;
                                case 4: // apply risk to investment
                                    if(acct.simpleString().charAt(0) == 'I') { // is investment account
                                        ((Investment) acct).applyRisk();
                                        if (bal < acct.getBalance()) { // profit
                                            System.out.println(acct.getBalance() - bal + " profit added to account");
                                        } else { // loss
                                            System.out.println(bal - acct.getBalance() + " loss added to account");
                                        }
                                    } else { // not an investment account
                                        System.out.println("Account " + acctNum + " is not an investment account!");
                                    }
                                    break;
                                case 5: // return to main menu
                                    managing = false;
                                    break;
                                default:
                                    System.out.println("Invalid input try again");
                                    break;
                            }
                        }
                        
                        break;
                    case 3: // add new account
                        banking.add(addAccount(scan));
                        break;
                    case 4: // remove account
                        System.out.println("\nEnter an account number:");
                        long accountNum = scan.nextLong();
                        if (checkAccountNumber(accountNum)) {
                            banking.remove(accountNum);
                        }    
                        break;
                    case 5: // sort accounts by balance
                        banking.sort();
                        banking.viewAll();
                        break;
                    case 6: // view closable accounts
                        banking.viewClosable();
                        break;    
                    case 7: // quit
                        banked = false;
                        System.out.println("Program exited");
                        banking.saveAccounts("accounts.txt");
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
                System.out.println("Input mismatch exception");
                scan.next();
            } 
            catch (Exception e) { // all other errors
                System.out.println("Error");
            }
        }
        scan.close();    
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
     * Method to get information to add information to a bankaccount
     * @param scan for the scanner
     * @return new type of bankaccount
     */
    public static BankAccount addAccount(Scanner scan) {
        boolean addingAccount = true;
       
        try {
            while (addingAccount) {
                long acctNum;
                String name, type;
                double bal, yIntRate;
                System.out.println("Enter the account number (10-digits):");
                acctNum = scan.nextLong();
               
                if (checkAccountNumber(acctNum)) {
                    System.out.println("Enter the owners name:");
                    scan.nextLine();
                    name = scan.nextLine();
                    System.out.println("Enter the balance of the account:");
                    bal = scan.nextDouble();
                    
                                
                    System.out.println("What type of account (1-3)");
                    System.out.println("1. Checking");
                    System.out.println("2. Savings");
                    System.out.println("3. Investment");
                    int accountType = scan.nextInt();
                    scan.nextLine();
                    switch(accountType) {
                        case 1: // checking
                            return new Checking(acctNum, name, bal);
                        case 2: // Savings
                            System.out.println("Enter the yearly interst rate:");
                            yIntRate = scan.nextDouble();
                            scan.nextLine();
                            return new Savings(acctNum, name, bal, yIntRate);
                        case 3: // Investment
                            System.out.println("Enter the type of investment account:");
                            type = scan.nextLine();
                            return new Investment(acctNum, name, bal, type);
                        default: 
                            System.out.println("Invalid Input, try again");
                            break;
                    }
                
                
                }
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
                
        }
        
        
        return null;
    }
}
