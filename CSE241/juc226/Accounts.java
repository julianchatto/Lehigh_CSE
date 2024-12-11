import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Accounts {
    private Connection conn;
    private PreparedStatement ps;
    private InputHandler ih;
    private int userId;

    public Accounts(Connection conn, InputHandler ih, int userId) {
        this.conn = conn;
        this.ih = ih;
        this.userId = userId;

        String depositOrWithdraw;
        while (true) {
            depositOrWithdraw = ih.getString("Would you like to deposit or withdraw? ").toLowerCase();
            if (depositOrWithdraw.equals("deposit") || depositOrWithdraw.equals("withdraw")) {
                break;
            } 

            ErrorHandler.handleError("Invalid choice. Please try again.");
        }

        if (depositOrWithdraw.equals("deposit")) {
            deposit();
        } else {
            withdraw();
        }
    }

    private void deposit() {
        ArrayList<String> accounts = getAccounts();
        
        int accountNum;
        do {
            accountNum = ih.getInt("Enter the account number you would like to deposit to (" + accounts + "): ", 1, Integer.MAX_VALUE);
        } while (!isValidAccountNum(accountNum));
        
        double amount = ih.getDouble("Enter the amount you would like to deposit: ", 0, Double.MAX_VALUE);

        try {
            ps = conn.prepareStatement("update account set balance = balance + ? where account_id = ?");
            ps.setDouble(1, amount);
            ps.setInt(2, accountNum);            
            ps.executeUpdate();

            System.out.println("Deposit successful.");
        } catch (SQLException sql) {
            ErrorHandler.handleError("SQL Error", sql);
        }
    }

    private void withdraw() {
        ArrayList<String> accounts = getAccounts();

        int accountNum;
        do {
            accountNum = ih.getInt("Enter the account number you would like to withdraw from (" + accounts + "): ", 1, Integer.MAX_VALUE);
        } while (!isValidAccountNum(accountNum));

        double amount = ih.getDouble("Enter the amount you would like to withdraw: ", 0, Double.MAX_VALUE);

        if (isChecking(accountNum)) {
            handleChecking(accountNum, amount);
        } else {
            handleSavings(accountNum, amount);
        }
    }
    
    private void handleChecking(int accountNum, double amount) {
        try {
            ps = conn.prepareStatement("select balance from account where account_id = ?");
            ps.setInt(1, accountNum);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                ErrorHandler.handleError("Account not found.");
            }
            double balance = rs.getDouble(1);

            if (balance < amount) {
                ErrorHandler.handleError("Insufficient funds.");
                return;
            }

            ps = conn.prepareStatement("update account set balance = balance - ? where account_id = ?");
            ps.setDouble(1, amount);
            ps.setInt(2, accountNum);
            ps.executeUpdate();

            System.out.println("Withdrawal successful.");
        } catch (SQLException sql) {
            ErrorHandler.handleError("SQL Error", sql);
        }
    }

    private void handleSavings(int accountNum, double amount) {
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

            ps = conn.prepareStatement("select penalty_fee, min_balance from savings where savings_id = ?");
            ps.setInt(1, accountNum);
            rs = ps.executeQuery();
            if (!rs.next()) {
                ErrorHandler.handleError("Account not found.");
            }
            double penalty = rs.getDouble(1);
            if (balance >= rs.getDouble(2)) {
                System.out.println("Withdrawal successful.");
                return;
            }

            ps = conn.prepareStatement("update account set balance = balance - ? where account_id = ?");
            ps.setDouble(1, penalty);
            ps.setInt(2, accountNum);
            ps.executeUpdate();

            ErrorHandler.handleError("Balance fell below minimum. Penalty of $"  + penalty + " applied");
        } catch (SQLException sql) {
            ErrorHandler.handleError("SQL Error", sql);
        }
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
            ps = conn.prepareStatement("SELECT 1 FROM customeraccounts ca LEFT JOIN investment i ON ca.account_id = i.investment_id WHERE ca.customer_id = ? AND i.investment_id IS NULL AND ca.account_id = ?");
            ps.setInt(1, userId);
            ps.setInt(2, accountNum);
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

    private ArrayList<String> getAccounts() {
        ArrayList<String> accounts = new ArrayList<String>();
        try {
            ps = conn.prepareStatement("SELECT ca.account_id FROM customeraccounts ca LEFT JOIN investment i ON ca.account_id = i.investment_id WHERE ca.customer_id = ? AND i.investment_id IS NULL");
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
}
