import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Manager {
    private Connection conn;
    private PreparedStatement ps;
    private InputHandler ih;
    private int managerId;
    private String managerName;

    public Manager(Connection conn, InputHandler ih) {
        this.conn = conn;
        this.ih = ih;
		// Ensure manager exists
		try {
			while (true) {
				managerId = ih.getInt("Enter your manager id ('-1' to exit) (" + getManagers() + "): ", -1, Integer.MAX_VALUE);
				if (managerId == -1) return;

				ps = conn.prepareStatement("select 1 from manager where manager_id = ?");
				ps.setInt(1, managerId);
                ResultSet rs = ps.executeQuery();

				if (rs.next()) {
                    ps = conn.prepareStatement("select name from users where user_id = ?");
                    ps.setInt(1, managerId);
                    rs = ps.executeQuery();
                    rs.next();
                    managerName = rs.getString(1);
					break;
				}

				ErrorHandler.handleError("USER NOT FOUND");
			}
		} catch (SQLException sql) {
			ErrorHandler.handleError("SQL Error", sql);
            return;
		}
        startManager();
    }

    private ArrayList<String> getManagers() {
        ArrayList<String> managers = new ArrayList<>();

        try {
            ps = conn.prepareStatement("select manager_id from manager");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                managers.add(rs.getString("manager_id"));
            }
        } catch (SQLException sql) {
            ErrorHandler.handleError("SQL Error", sql);
        }
        return managers;
    }

    public void startManager() {
        System.out.println("Welcome " + managerName + "!");

        while (true) {
            printMenu();

            switch (ih.getInt("What would you like to do: ", 1, 5)) {
                case 1:
                    createNewCustomer();
                    break;
                case 2:
                    createNewAccount();
                    break;
                case 3:
                    createNewLoan();
                    break;
                case 4:
                    createNewCard();
                    break;
                case 5:
                    return;
                default:
                    ErrorHandler.handleError("Invalid choice. Please try again.");
            }
        }
    }

    private void printMenu() {
        System.out.println("1. Create a new customer");
        System.out.println("2. Create a new account");
        System.out.println("3. Create a new loan");
        System.out.println("4. Create a new card");
        System.out.println("5. Exit");
    }

    private void createNewCustomer() {
        ih.getStringLine("");
        String name = ih.getStringLine("Enter the customer's name: ");
        String address = ih.getStringLine("Enter the customer's address: ");
        String phone = ih.getStringLine("Enter the customer's phone number: ");
        String email = ih.getStringLine("Enter the customer's email: ");

        try {
            // Insert into users table
            ps = conn.prepareStatement("insert into users (name, address, phone_num, email) values (?, ?, ?, ?)",  new String[] { "USER_ID" });
            ps.setString(1, name);

            ps.setString(2, address);
            ps.setString(3, phone);

            ps.setString(4, email);
            int rowsAffected = ps.executeUpdate();


            int newId = getNewId(rowsAffected, ps);

            // Insert into customer table
            ps = conn.prepareStatement("insert into customer (customer_id, join_date) values (?, ?)");

            ps.setInt(1, newId);
            ps.setDate(2, new Date(System.currentTimeMillis()));
            ps.executeUpdate();

            System.out.println("Customer created successfully!");
        } catch (SQLException sql) {
            ErrorHandler.handleError("SQL Error", sql);
        }
        

    }

    private void createNewAccount() {
        int userId = getUserId();
        if (userId == -1) return;

        String accountType;
        while (true) {
            accountType = ih.getString("Would you like a savings account (sa), checking account (ca), or investment account (ia): ");
            if (accountType.equals("sa") || accountType.equals("ca") || accountType.equals("ia")) {
                break;
            }
            ErrorHandler.handleError("Invalid account type. Please try again.");
        }
        double min_balance = 0;
        if (accountType.equals("sa")) {
            min_balance = ih.getDouble("Enter the minimum balance: ", 0, Double.MAX_VALUE);
        }
        try {
            ps = conn.prepareStatement("insert into account (open_date, balance) values (?, ?)", new String[] { "ACCOUNT_ID" });
            ps.setDate(1, new Date(System.currentTimeMillis()));
            ps.setDouble(2, min_balance);
            int rowsAffected = ps.executeUpdate();

            int newAccountId = getNewId(rowsAffected, ps);

            ps = conn.prepareStatement("insert into customeraccounts (customer_id, account_id) values (?, ?)");
            ps.setInt(1, userId);
            ps.setInt(2, newAccountId);
            ps.executeUpdate();

            if (accountType.equals("sa")) {
                handleSavings(newAccountId, min_balance);
            } else if (accountType.equals("ca")) {
                handleChecking(newAccountId);
            } else {
                handleInvestment(newAccountId);
            }
        } catch (SQLException sql) {
            ErrorHandler.handleError("SQL Error", sql);
        }
    }

    private void handleSavings(int newAccountId, double min_balance) { 
        double penalty_fee = ih.getDouble("Enter the penalty fee: ", 0, Double.MAX_VALUE);

        try {
            ps = conn.prepareStatement("insert into savings (savings_id, min_balance, penalty_fee) values (?, ?, ?)");
            ps.setInt(1, newAccountId);
            ps.setDouble(2, min_balance);
            ps.setDouble(3, penalty_fee);
            ps.executeUpdate();

            System.out.println("Savings account created successfully!");
        } catch (SQLException sql) {
            ErrorHandler.handleError("SQL Error", sql);
        }
    }

    private void handleChecking(int newAccountId) {
        try {
            ps = conn.prepareStatement("insert into checking (checking_id, last_withdrawal) values (?, ?)");
            ps.setInt(1, newAccountId);
            ps.setDate(2, new Date(System.currentTimeMillis()));
            ps.executeUpdate();

            System.out.println("Checking account created successfully!");
        } catch (SQLException sql) {
            ErrorHandler.handleError("SQL Error", sql);
        }
    }

    private void handleInvestment(int newAccountId) {
        double return_rate = ih.getDouble("Enter the return rate as a percentage (i.e. 5.6% enter 5.6): ", 0, 100);

        try {
            ps = conn.prepareStatement("insert into investment (investment_id, return) values (?, ?)");
            ps.setInt(1, newAccountId);
            ps.setDouble(2, return_rate);
            ps.executeUpdate();

            System.out.println("Investment account created successfully!");
        } catch (SQLException sql) {
            ErrorHandler.handleError("SQL Error", sql);
        }
    }

    private void createNewLoan() {
        int userId = getUserId();
        if (userId == -1) return;

        double balance = ih.getDouble( "Enter the loan amount: ", 0.01, Double.MAX_VALUE);;
        ih.getStringLine("");
        String loan_for = ih.getStringLine("Enter what the loan is for: ");

        try {
            ps = conn.prepareStatement("insert into loan (open_date, balance, loan_for) values (?, ?, ?)", new String[] { "LOAN_ID" });
            ps.setDate(1, new Date(System.currentTimeMillis()));
            ps.setDouble(2, balance);
            ps.setString(3, loan_for);
            int rowsAffected = ps.executeUpdate();

            int newLoanId = getNewId(rowsAffected, ps);

            ps = conn.prepareStatement("insert into customerloans (customer_id, loan_id) values (?, ?)");
            ps.setInt(1, userId);
            ps.setInt(2, newLoanId);
            ps.executeUpdate();

            String secured = ih.getString("Is the loan secured (y/n): ");

            if (secured.equals("y")) {
                handleSecuredLoan(newLoanId);
            } else {
                handleUnsecuredLoan(newLoanId);
            }
        } catch (SQLException sql) {
            ErrorHandler.handleError("SQL Error", sql);
        }


    }
    
    private void handleSecuredLoan(int newLoanId) {
        ih.getStringLine("");
        String collateral = ih.getStringLine("Enter the collateral: ");

        try {
            ps = conn.prepareStatement("insert into secured (secured_id, secured_by) values (?, ?)");
            ps.setInt(1, newLoanId);
            ps.setString(2, collateral);
            ps.executeUpdate();

            System.out.println("Secured loan created successfully!");
        } catch (SQLException sql) {
            ErrorHandler.handleError("SQL Error", sql);
        }
    }

    private void handleUnsecuredLoan(int newLoanId) {
        double risk_level = ih.getDouble("Enter the risk level: ", 0, 100);
       
        try {
            ps = conn.prepareStatement("insert into unsecured (unsecured_id, risk_level) values (?, ?)");
            ps.setInt(1, newLoanId);
            ps.setDouble(2, risk_level);
            ps.executeUpdate();

            System.out.println("Unsecured loan created successfully!");
        } catch (SQLException sql) {
            ErrorHandler.handleError("SQL Error", sql);
        }
    }

    private void createNewCard() {
        int userId = getUserId();
        if (userId == -1) return;

        String cardType;
        while (true) {
            cardType = ih.getString("Would you like a debit card (dc) or credit card (cc): ");
            if (cardType.equals("dc") || cardType.equals("cc")) {
                break;
            }
            ErrorHandler.handleError("Invalid card type. Please try again.");
        }

        try {
            ps = conn.prepareStatement("insert into card (open_date) values (?)", new String[] { "CARD_ID" });
            ps.setDate(1, new Date(System.currentTimeMillis()));
            int rowsAffected = ps.executeUpdate();

            int newCardId = getNewId(rowsAffected, ps);

            ps = conn.prepareStatement("insert into customercards (customer_id, card_id) values (?, ?)");
            ps.setInt(1, userId);
            ps.setInt(2, newCardId);
            ps.executeUpdate();

            if (cardType.equals("cc")) {
                handleCreditCard(newCardId);
            } else {
                handleDebitCard(userId, newCardId);
            }
        } catch (SQLException sql) {
            ErrorHandler.handleError("SQL Error", sql);
        }        
    }

    private void handleCreditCard(int newCardId) {
        double credit_limit = ih.getDouble("Enter the credit limit: ", 0.01, Double.MAX_VALUE);

        try {
            ps = conn.prepareStatement("insert into creditcard (credit_card_id, credit_limit, running_balance, balance_due) values (?, ?, ?, ?)");
            ps.setInt(1, newCardId);
            ps.setDouble(2, credit_limit);
            ps.setDouble(3, 0);
            ps.setDouble(4, 0);
            ps.executeUpdate();


            System.out.println("Credit card created successfully!");
        } catch (SQLException sql) {
            ErrorHandler.handleError("SQL Error", sql);
        }
    }
    
    private void handleDebitCard(int userId, int newCardId) {
        int checkingId = -1;
        while (true) {
            checkingId = ih.getInt("Enter the checking account id " + getCheckingAccounts(userId) + " ", 1, Integer.MAX_VALUE);
            if (isValidCheckingId(userId, checkingId)) {
                break;
            }
        }

        try {
            ps = conn.prepareStatement("insert into debitcard (debit_card_id, checking_id) values (?, ?)");
            ps.setInt(1, newCardId);
            ps.setInt(2, checkingId);
            ps.executeUpdate();

            System.out.println("Debit card created successfully!");
        } catch (SQLException sql) {
            ErrorHandler.handleError("SQL Error", sql);
        }
    }

    private boolean isValidCheckingId(int userId, int checkingId) {
        try {
            ps = conn.prepareStatement("select 1 from customeraccounts right join checking on account_id = checking_id where customer_id = ? AND account_id = ?");
            ps.setInt(1, userId);
            ps.setInt(2, checkingId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return true;
            }
            ErrorHandler.handleError("CHECKING ACCOUNT NOT FOUND");
        } catch (SQLException sql) {
            ErrorHandler.handleError("SQL Error", sql);
        }
        return false;
    }

    private ArrayList<String> getCheckingAccounts(int userId) {
        ArrayList<String> checkingAccounts = new ArrayList<>();

        try {
            ps = conn.prepareStatement("select account_id from customeraccounts right join checking on account_id = checking_id where customer_id = ?");
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                checkingAccounts.add(rs.getString("account_id"));
            }
        } catch (SQLException sql) {
            ErrorHandler.handleError("SQL Error", sql);
        }
        return checkingAccounts;
    }
    
    private int getNewId(int rowsAffected, PreparedStatement ps) throws SQLException {
        // Retrieve the generated key (userid)
        if (rowsAffected > 0) {
            ResultSet generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                int userId = generatedKeys.getInt(1); // Now this retrieves the USER_ID instead of ROWID
                return userId;
            } else {
                ErrorHandler.handleError("Creating user failed, no ID obtained.");
            }

        }
        return -1;
    }

    private int getUserId() {
        try {
            while (true) {
                int userId = ih.getInt("Enter the customer id (" + getUsers() +  "): ", 1, Integer.MAX_VALUE);

                ps = conn.prepareStatement("select 1 from customer where customer_id = ?");
                ps.setInt(1, userId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    return userId;
                }

                ErrorHandler.handleError("CUSTOMER NOT FOUND");
            }
        } catch (SQLException sql) {
            ErrorHandler.handleError("SQL Error", sql);
        }
        return -1;
    }

    private ArrayList<String> getUsers() {
        ArrayList<String> users = new ArrayList<>();

        try {
            ps = conn.prepareStatement("select customer_id from customer");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                users.add(rs.getString(1));
            }
        } catch (SQLException sql) {
            ErrorHandler.handleError("SQL Error", sql);
        }

        return users;
    }
}
