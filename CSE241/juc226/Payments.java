import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Payments {
    private Connection conn;
    private PreparedStatement ps;
    private InputHandler ih;
    private int userId;

    public Payments(Connection conn, InputHandler ih, int userId) {
        this.conn = conn;
        this.ih = ih;
        this.userId = userId;

        String loanOrCard;
        while (true) {
            loanOrCard = ih.getString("Would you like to pay off a loan or credit card (enter cc)? ").toLowerCase();
            if (loanOrCard.equals("loan") || loanOrCard.equals("cc")) {
                break;
            } 

            ErrorHandler.handleError("Invalid choice. Please try again.");
        }

        if (loanOrCard.equals("loan")) {
            payLoan();
        } else {
            payCreditCard();
        }
    }

    private void payLoan() {
        ArrayList<String> loans = getLoans();

        int loanId;
        double balance = -1;
        while (true) {
            loanId = ih.getInt("Enter the loan id you would like to pay off (" + loans + "): ", 1, Integer.MAX_VALUE);
            balance = isValidLoanId(loanId);
            if (balance >= 0) {
                break;
            }
        }

        if (balance == 0) {
            ErrorHandler.handleError("Loan already paid off.");
            return;
        }

        double amount = ih.getDouble("Enter the amount you would like to pay: ", 0, balance);

        if (!withdraw(amount)) {
            ErrorHandler.handleError("Payment Failed.");
            return;
        } 

        try {
            ps = conn.prepareStatement("update loan set balance = balance - ? where loan_id = ?");
            ps.setDouble(1, amount);
            ps.setInt(2, loanId);
            ps.executeUpdate();
            System.out.println("Payment successful.");
        } catch (SQLException sql) {
            ErrorHandler.handleError("SQL Error", sql);
        }
    }

    private void payCreditCard() {
        int cardNum;
        double cardBalance = -1;
        while (true) {
            cardNum = ih.getInt("Enter the card number you would like to pay off (" + getCreditCards() + "): ", 1, Integer.MAX_VALUE);
            cardBalance = isValidCardNum(cardNum);
            if (cardBalance >= 0) {
                break;
            }
        }

        if (cardBalance == 0) {
            ErrorHandler.handleError("Balance due is already 0. No payment can be made at this time.");
            return;
        }

        double amount = ih.getDouble("Enter the amount you would like to pay: ", 0, cardBalance);

        if (!withdraw(amount)) {
            ErrorHandler.handleError("Payment Failed.");
            return;
        }

        try {
            ps = conn.prepareStatement("update creditcard set balance_due = balance_due - ? where credit_card_id = ?");
            ps.setDouble(1, amount);
            ps.setInt(2, cardNum);
            ps.executeUpdate();
            System.out.println("Payment successful.");
        } catch (SQLException sql) {
            ErrorHandler.handleError("SQL Error", sql);
        }
    }

    private double isValidCardNum(int cardNum) {
        try {
            ps = conn.prepareStatement("select balance_due from creditcard where credit_card_id = ?");
            ps.setInt(1, cardNum);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("balance_due");
            }
            ErrorHandler.handleError("Invalid card number.");
        } catch (SQLException sql) {
            ErrorHandler.handleError("SQL Error", sql);
        }
        return -1;
    }

    private ArrayList<String> getCreditCards() {
        ArrayList<String> cards = new ArrayList<String>();

        try {
            ps = conn.prepareStatement("select card_id from customercards right join creditcard c on credit_card_id = card_id where customer_id = ?");
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                cards.add(rs.getString("card_id"));
            }
        } catch (SQLException sql) {
            ErrorHandler.handleError("SQL Error", sql);
        }

        return cards;
    }

    private ArrayList<String> getLoans() {
        ArrayList<String> loans = new ArrayList<String>();

        try {
            ps = conn.prepareStatement("select loan_id from customerloans where customer_id = ?");
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                loans.add(rs.getString("loan_id"));
            }
        } catch (SQLException sql) {
            ErrorHandler.handleError("SQL Error", sql);
        }

        return loans;
    }

    private boolean withdraw(double amount) {
        ArrayList<String> accounts = getSavingsAccounts();

        int accountNum;
        do {
            accountNum = ih.getInt("Enter the account number you would like to withdraw from (" + accounts + "): ", 1, Integer.MAX_VALUE);
        } while (!isValidAccountNum(accountNum));

        if (isChecking(accountNum)) {
            ErrorHandler.handleError("Cannot make payment from checking account.");
            return false;
        } else {
            return handleSavings(accountNum, amount);
        }
    }
    
    private boolean handleSavings(int accountNum, double amount) {
        try {
            ps = conn.prepareStatement("update account set balance = balance - ? where account_id = ?");
            ps.setDouble(1, amount);
            ps.setInt(2, accountNum);
            ps.executeUpdate();

            ps = conn.prepareStatement("select balance from account where account_id = ?");
            ps.setInt(1, accountNum);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                ErrorHandler.handleError("Account not found.");
            }
            double balance = rs.getDouble(1);

            if (balance >= 0) {
                return true;
            }
            ps = conn.prepareStatement("select penalty_fee from savings where savings_id = ?");
            ps.setInt(1, accountNum);
            rs = ps.executeQuery();
            if (!rs.next()) {
                ErrorHandler.handleError("Account not found.");
            }
            double penalty = rs.getDouble(1);

            ps = conn.prepareStatement("update account set balance = balance - ? where account_id = ?");
            ps.setDouble(1, penalty);
            ps.setInt(2, accountNum);
            ps.executeUpdate();

            ErrorHandler.handleError("Insufficient funds. Penalty of $"  + penalty + " applied");
        } catch (SQLException sql) {
            ErrorHandler.handleError("SQL Error", sql);
        }
        return true;

    }

    private boolean isChecking(int accountNum) {
        try {
            ps = conn.prepareStatement("select 1 from checking where checking_id = ?");
            ps.setInt(1, accountNum);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (SQLException sql) {
            ErrorHandler.handleError("SQL Error", sql);
        }
        return false;
    }

    private boolean isValidAccountNum(int accountNum) {
        try {
            ps = conn.prepareStatement("select 1 from account natural right join customeraccounts where account_id = ? AND customer_id = ?");
            ps.setInt(1, accountNum);
            ps.setInt(2, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return true;
            } 
            ErrorHandler.handleError("Invalid account number.");
        } catch (SQLException sql) {
            ErrorHandler.handleError("SQL Error", sql);
        }
        return false;
    }

    private ArrayList<String> getSavingsAccounts() {
        ArrayList<String> accounts = new ArrayList<String>();
        try {
            ps = conn.prepareStatement("select account_id from customeraccounts right join savings on savings_id = account_id where customer_id = ?");
            ps.setInt(1, userId);
            ResultSet accts = ps.executeQuery();
            while (accts.next()) {
                accounts.add(ps.getResultSet().getString(1));
            }
        } catch (SQLException sql) {
            ErrorHandler.handleError("SQL Error", sql);
        }

        return accounts;
    }

    private double isValidLoanId(int loanId) {
        try {
            ps = conn.prepareStatement("select balance from customerloans cl left join loan l on l.loan_id = cl.loan_id where cl.loan_id = ? and cl.customer_id = ?");
            ps.setInt(1, loanId);
            ps.setInt(2, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("balance");
            }
            ErrorHandler.handleError("Invalid loan id.");
        } catch (SQLException sql) {
            ErrorHandler.handleError("SQL Error", sql);
        }
        return -1;
    }
}
