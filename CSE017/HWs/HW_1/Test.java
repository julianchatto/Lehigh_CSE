import java.util.Scanner;
/***
 * Class to model the entity test
 * @author Julian Chattopadhyay
 * @version 0.1
 * Date of creation: August 27, 2022
 * Last Date Modified: August 30, 2022
 */
public class Test {
    public static void main(String[] args) {
        boolean banking = true;
        Scanner scan = new Scanner(System.in);

        /***
         * Initializes the array people and assigns each index
         */
        BankAccount[] accounts = new BankAccount[10];
        
        accounts[0] = new Checking("William Burst", 10000);
        accounts[1] = new Savings("Lara Stevens", 75000, 9.25);
        accounts[2] = new Checking("Nathan Steward", 24900);
        accounts[3] = new Investment("Tracey Bold", 19500, "Property");
        accounts[4] = new Savings("Isabel Truman", 89250, 10.5);
        accounts[5] = new Savings("Andrew Sullivan", 12734, 12.1);
        accounts[6] = new Investment("Abigail Clark", 11255, "Shares");
        accounts[7] = new Checking("George Duck", 29990);
        accounts[8] = new Savings("Emma Stevens", 31250, 8.89);
        accounts[9] = new Investment("Lily Daves", 8800, "Growth");
        
        /***
         * runs the program
         */
        while(banking) {
            System.out.println("\nChoose a number (1-5):\n");
            System.out.println("1. View List of accounts");
            System.out.println("2. Find accounts");
            System.out.println("3. Sort accounts by balance");
            System.out.println("4. Sort accounts by owner");
            System.out.println("5. Quit program");

            int userInput = scan.nextInt();
            switch(userInput) {
                case 1: // view list of accounts
                    printAccounts(accounts);
                    break;
                case 2: // find accounts
                    System.out.println("Enter an amount:");
                    double money = scan.nextDouble();
                    int count = findAccounts(accounts, money);
                    if (count != 0) { 
                        System.out.println(count + " bank accounts found.");
                    } else {
                        System.out.println("No bank account found.");
                    }
                    break;
                case 3: // Sort accounts by balance
                    sortAccounts(accounts, true);
                    printAccounts(accounts);
                    break;
                case 4: // sort accounts by owner
                    sortAccounts(accounts, false);
                    printAccounts(accounts);
                    break;
                case 5: // quit program
                    System.out.println("Program quit");
                    banking = false;
                    break;
                default: // invalid input
                    System.out.println("Not a valid input. Please enter a number (1-5)");
                    break;
            }
        }
        scan.close();
        

    }

    /***
     * Method to print all bank account information
     * @param list
     * no return value
     */
    public static void printAccounts(BankAccount[] list) {
        System.out.println("Type                          Number            Owner                           Balance   Interest/Investment type");
        for (int i = 0; i < list.length; i++) {
            System.out.println(list[i].toString());
        }
    } 

    /***
     * Method to find and print accounts with a balance less than or equal to the desired amount
     * @param list
     * @param amount
     * @return the number of accounts that meet the criteria
     */
    public static int findAccounts(BankAccount[] list, double amount) {
        int count = 0;
        for (int i = 0; i < list.length; i++) {
            if (list[i].getBalance() <= amount) {
                count++;
                System.out.println(list[i].toString());
            }
        }
        return count;
    }

    /***
     * Method to sort accounts based on balance or owner
     * @param list
     * @param criterion
     * no return value
     */
    public static void sortAccounts(BankAccount[] list, boolean criterion) {
        if (criterion) {
            for (int i=1; i<list.length; i++) {
                //Insert element i in the sorted sub-list
                BankAccount currentVal = list[i];
                int j = i;
                while (j > 0 && currentVal.getBalance() < (list[j - 1].getBalance())){
                     // Shift element (j-1) into element (j)
                     list[j] = list[j - 1];
                     j--;
                }
                // Insert currentVal at position j
                list[j] = currentVal;
             }                   
        } else {
            for (int i=1; i<list.length; i++) {
                //Insert element i in the sorted sub-list
                BankAccount currentVal = list[i];
                int j = i;
                while (j>0 && ((currentVal.getOwner()).compareTo(list[j - 1].getOwner()) < 0)){
                     // Shift element (j-1) into element (j)
                     list[j] = list[j - 1];
                     j--;
                }
                // Insert currentVal at position j
                list[j] = currentVal;
             }                   
        }
        

    }
       
  

}
