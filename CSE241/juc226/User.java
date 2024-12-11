import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class User {
    private Connection conn;
    private PreparedStatement ps;
    private InputHandler ih;
    private int userId;
    private String userName;

    public User(Connection conn, InputHandler ih) {
        this.conn = conn;
        this.ih = ih;

		// Ensure user exists
		try {
			while (true) {
				userId = ih.getInt("Enter your user id ('-1' to exit) (" + getUsers() +  "): ", -1, Integer.MAX_VALUE);
				if (userId == -1) return;

				ps = conn.prepareStatement("select 1 from customer where customer_id = ?");
				ps.setInt(1, userId);
				if (ps.executeQuery().next()) {
                    ps = conn.prepareStatement("select name from users where user_id = ?");
                    ps.setInt(1, userId);
                    ResultSet rs = ps.executeQuery();
                    rs.next();
                    userName = rs.getString(1);
					break;
				}

				ErrorHandler.handleError("CUSTOMER NOT FOUND");
			}
		} catch (SQLException sql) {
			ErrorHandler.handleError("SQL Error", sql);
            return;
		}
        startUser();
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

    public void startUser() {
        System.out.println("Welcome " + userName + "!");

        while (true) {
            printMenu();

            switch (ih.getInt("What would you like to do: ", 1, 5)) {
                case 1:
                    new Accounts(conn, ih, userId);
                    break;
                case 2:
                    new Payments(conn, ih, userId);
                    break;
                case 3:
                    new Purchase(conn, ih, userId);
                    break;
                case 4:
                    printAllAccounts();
                    break;
                case 5:
                    return;
                default:
                    ErrorHandler.handleError("Invalid choice. Please try again.");
            }
        }
    }

    private void printMenu() {
        System.out.println("1. Make a deposit or withdraw");
        System.out.println("2. Pay down a debt");
        System.out.println("3. Make a purchase with one of your cards");
        System.out.println("4. View all accounts");
        System.out.println("5. Exit");
    }

    private void printAllAccounts() {
        System.out.println("====  Savings Accounts  ====");
        System.out.println(String.format("%-12s %-15s %-15s %-15s %-20s %-15s", "Account ID", "Open Date", "Closed Date", "Balance", "Min Balance", "Penalty Fee"));
        for (String account : getSavings()) {
            System.out.println(account);
        }

        System.out.println("====  Checking Accounts  ====");
        System.out.println(String.format("%-12s %-15s %-15s %-15s %-20s", "Account ID", "Open Date", "Closed Date", "Balance", "Last Used"));
        for (String account : getChecking()) {
            System.out.println(account);
        }

        System.out.println("====  Investments  ====");
        System.out.println(String.format("%-12s %-15s %-15s %-15s %-20s", "Account ID", "Open Date", "Closed Date", "Balance", "Return"));
        for (String account : getInvestments()) {
            System.out.println(account);
        }

        System.out.println("==== Loans ====");
        System.out.println(String.format("%-12s %-15s %-15s %-20s", "Loan ID", "Open Date", "Balance", "Loan For"));
        for (String account : getLoans()) {
            System.out.println(account);
        }

        System.out.println("====  Credit Cards  ====");
        System.out.println(String.format("%-12s %-15s %-15s %-20s %-20s", "Card ID", "Open Date", "Credit Limit", "Running Balance", "Balance Due"));
        for (String account : getCreditCards()) {
            System.out.println(account);
        }

        System.out.println("====  Debit Cards  ====");
        System.out.println(String.format("%-12s %-15s %-20s", "Card ID", "Open Date", "Checking ID"));
        for (String account : getDebitCards()) {
            System.out.println(account);
        }


    }

    private ArrayList<String> getSavings() {
        ArrayList<String> savings = new ArrayList<>();

        try {
            ps = conn.prepareStatement("select a.account_id, open_date, closed_date, balance, min_balance, penalty_fee from customeraccounts ca left join account a on ca.account_id = a.account_id left join savings on savings_id = a.account_id where customer_id = ? AND min_balance is not null");
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();


            while (rs.next()) {
                String account = String.format("%-12s %-15s %-15s %-15.2f %-20s %-15s",
                                            rs.getString(1),
                                            rs.getDate(2) != null ? rs.getDate(2).toString() : "N/A",
                                            rs.getDate(3) != null ? rs.getDate(3).toString() : "N/A",
                                            rs.getDouble(4),
                                            rs.getString(5),
                                            rs.getString(6));
                savings.add(account);
            }

        } catch (SQLException sql) {
            ErrorHandler.handleError("SQL Error", sql);
        }
        return savings;
    }

    private ArrayList<String> getChecking() {
        ArrayList<String> checking = new ArrayList<>();

        try {
            ps = conn.prepareStatement("select a.account_id, open_date, closed_date, balance, last_withdrawal from customeraccounts ca left join account a on ca.account_id = a.account_id left join checking on checking_id = a.account_id where customer_id = ? and last_withdrawal is not null");
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String account = String.format("%-12s %-15s %-15s %-15.2f %-20s",
                                            rs.getString(1),
                                            rs.getDate(2) != null ? rs.getDate(2).toString() : "N/A",
                                            rs.getDate(3) != null ? rs.getDate(3).toString() : "N/A",
                                            rs.getDouble(4),
                                            rs.getDate(5) != null ? rs.getDate(5).toString() : "N/A");
                checking.add(account);
            }

        } catch (SQLException sql) {
            ErrorHandler.handleError("SQL Error", sql);
        }
        return checking;
    }

    private ArrayList<String> getInvestments() {
        ArrayList<String> investments = new ArrayList<>();

        try {
            ps = conn.prepareStatement("select a.account_id, open_date, closed_date, balance, return from customeraccounts ca left join account a on ca.account_id = a.account_id left join investment on investment_id = a.account_id where customer_id = ? and return is not null");
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String account = String.format("%-12s %-15s %-15s %-15.2f %-20.2f",
                                            rs.getString(1),
                                            rs.getDate(2) != null ? rs.getDate(2).toString() : "N/A",
                                            rs.getDate(3) != null ? rs.getDate(3).toString() : "N/A",
                                            rs.getDouble(4),
                                            rs.getDouble(5));
                investments.add(account);
            }

        } catch (SQLException sql) {
            ErrorHandler.handleError("SQL Error", sql);
        }

        return investments;
    }

    private ArrayList<String> getLoans() {
        ArrayList<String> loans = new ArrayList<>();

        try {
            ps = conn.prepareStatement("select l.loan_id, open_date, balance, loan_for from customerloans cl left join loan l on cl.loan_id = l.loan_id where customer_id = ?");
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String account = String.format("%-12s %-15s %-15.2f %-20s",
                                            rs.getString(1),
                                            rs.getDate(2) != null ? rs.getDate(2).toString() : "N/A",
                                            rs.getDouble(3),
                                            rs.getString(4));
                loans.add(account);
            }


        } catch (SQLException sql) {
            ErrorHandler.handleError("SQL Error", sql);
        }
        return loans;
    }

    private ArrayList<String> getCreditCards() {
        ArrayList<String> creditCards = new ArrayList<>();

        try {
            ps = conn.prepareStatement("select cc.card_id, open_date, credit_limit, running_balance, balance_due from customercards cc left join card c on cc.card_id = c.card_id left join creditcard on credit_card_id = c.card_id where customer_id = ? AND credit_limit is not null");
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String account = String.format("%-12s %-15s %-15.2f %-20.2f %-20.2f",
                                            rs.getString(1),
                                            rs.getDate(2) != null ? rs.getDate(2).toString() : "N/A",
                                            rs.getDouble(3),
                                            rs.getDouble(4),
                                            rs.getDouble(5));
                creditCards.add(account);
            }
        } catch (SQLException sql) {
            ErrorHandler.handleError("SQL Error", sql);
        }
        return creditCards;
    }

    private ArrayList<String> getDebitCards() {
        ArrayList<String> debitCards = new ArrayList<>();

        try {
            ps = conn.prepareStatement("select cc.card_id, open_date, checking_id from customercards cc left join card c on cc.card_id = c.card_id left join debitcard on debit_card_id = c.card_id where customer_id = ? and checking_id is not null");
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String account = String.format("%-12s %-15s %-20s",
                                rs.getString(1),
                                rs.getDate(2) != null ? rs.getDate(2).toString() : "N/A",
                                rs.getString(3));
                debitCards.add(account);
            }

        } catch (SQLException sql) {
            ErrorHandler.handleError("SQL Error", sql);
        }

        return debitCards;
    }
}
