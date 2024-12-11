import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Purchase {
    private Connection conn;
    private PreparedStatement ps;
    private InputHandler ih;
    private int userId;
    
    public Purchase(Connection conn, InputHandler ih, int userId) {
        this.conn = conn;
        this.ih = ih;
        this.userId = userId;

        String purchaseType;
        while (true) {
            purchaseType = ih.getString("Would you like to make a purchase with a credit card (enter cc) or a debit card (enter dc)? ").toLowerCase();
            if (purchaseType.equals("cc") || purchaseType.equals("dc")) {
                break;
            } 

            ErrorHandler.handleError("Invalid choice. Please try again.");
        }

        if (purchaseType.equals("cc")) {
            creditCardPurchase();
        } else {
            debitCardPurchase();
        }
    }

    private void creditCardPurchase() {
        ArrayList<String> creditCards = getCreditCards();

        int cardNum;
        double running_balance = 0, credit_limit = 0;
        while (true) {
            cardNum = ih.getInt("Enter the card number you would like to use (" + creditCards + "): ", 1, Integer.MAX_VALUE);
            ResultSet rs = isValidCreditCard(cardNum);
          
            try {
                if (!rs.next()) {
                    ErrorHandler.handleError("Invalid card number. Please try again.");
                    continue;
                }
                credit_limit = rs.getDouble(2);
                running_balance = rs.getDouble(3);
                break;
            } catch (SQLException sql) {
                ErrorHandler.handleError("SQL Error", sql);
            }
        }

        double amount = ih.getDouble("Enter the amount you would like to purchase: ", 0, credit_limit - running_balance);

        try {
            ps = conn.prepareStatement("update creditcard set running_balance = running_balance + ? where credit_card_id = ?");
            ps.setDouble(1, amount);
            ps.setInt(2, cardNum);
            ps.executeUpdate();
            System.out.println("Payment successful.");
        } catch (SQLException sql) {
            ErrorHandler.handleError("SQL Error", sql);
        }        
    }

    private void debitCardPurchase() {
        int cardNum;
        double balance;
        int checkingId;
        while (true) {
            cardNum = ih.getInt("Enter the card number you would like to use (" + getDebitCards() + "): ", 1, Integer.MAX_VALUE);
            ResultSet rs = isValidDebitCard(cardNum);
            try {
                if (!rs.next()) {
                    ErrorHandler.handleError("Invalid card number. Please try again.");
                    continue;
                }
                balance = rs.getDouble(1);
                checkingId = rs.getInt(2);
                break;
            } catch (SQLException sql) {
                ErrorHandler.handleError("SQL Error", sql);
            }

            ErrorHandler.handleError("Invalid card number. Please try again.");
        }

        double amount = ih.getDouble("Enter the amount you would like to purchase: ", 0, balance);

        try {
            ps = conn.prepareStatement("update account set balance = balance - ? where account_id = ?");
            ps.setDouble(1, amount);
            ps.setInt(2, checkingId);
            ps.executeUpdate(); 
            System.out.println("Payment successful.");
        } catch (SQLException sql) {
            ErrorHandler.handleError("SQL Error", sql);
        }
    }

    private ResultSet isValidDebitCard(int cardNum) {
        try {
            ps = conn.prepareStatement("select balance, dc.checking_id from customercards cc right join card c on c.card_id = cc.card_id right join debitcard dc on c.card_id = debit_card_id left join checking left join account on account_id = checking.checking_id on checking.checking_id = dc.checking_id where c.card_id = ? and customer_id = ?");
            ps.setInt(1, cardNum);
            ps.setInt(2, userId);
            ResultSet rs = ps.executeQuery();
            return rs;
        } catch (SQLException sql) {
            ErrorHandler.handleError("SQL Error", sql);
        }
        return null;
    }

    private ResultSet isValidCreditCard(int cardNum) {
        try {
            ps = conn.prepareStatement("select c.card_id, credit_limit, running_balance from customercards cc right join card c on c.card_id = cc.card_id right join creditcard on credit_card_id = c.card_id where c.card_id = ? AND customer_id = ?");
            ps.setInt(1, cardNum);
            ps.setInt(2, userId);
            ResultSet rs = ps.executeQuery();
            return rs;
        } catch (SQLException sql) {
            ErrorHandler.handleError("SQL Error", sql);
        }
        return null;
    }

    private ArrayList<String> getDebitCards() {
        ArrayList<String> debitCards = new ArrayList<String>();

        try {
            ps = conn.prepareStatement("select c.card_id from customercards cc right join card c on c.card_id = cc.card_id right join debitcard on c.card_id = debit_card_id where customer_id = ?");
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                debitCards.add(rs.getString(1));
            }

        } catch (SQLException sql) {
            ErrorHandler.handleError("SQL Error", sql);
        }

        return debitCards;
    }

    private ArrayList<String> getCreditCards() {
        ArrayList<String> creditCards = new ArrayList<String>();

        try {
            ps = conn.prepareStatement("select c.card_id from customercards cc right join card c on c.card_id = cc.card_id right join creditcard on c.card_id = credit_card_id where customer_id = ?");
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                creditCards.add(rs.getString(1));
            }

        } catch (SQLException sql) {
            ErrorHandler.handleError("SQL Error", sql);
        }

        return creditCards;
    }
}
