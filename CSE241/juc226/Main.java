import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
	// connection string and colors for terminal output
	private static final String DB_URL = "jdbc:oracle:thin:@edgar1.cse.lehigh.edu:1521:cse241", RESET ="\u001B[0m", GREEN = "\u001B[32m"; 

	// globally accessible objects
	private static Connection conn;
	private static InputHandler ih;

	public static void main(String[] args) {
		try {
			ih = new InputHandler();
			startConnection();
			while (true) {
				printMenu();	

				switch (ih.getInt("How would you like to login: ", 1, 3)) {
					case 1:
						new User(conn, ih);
						break;
					case 2:
						new Manager(conn, ih);
						break;
					case 3:
						quit();
					default:
						ErrorHandler.handleError("Invalid choice. Please try again.");
				}
			}
		} catch (Exception exp) {
			ErrorHandler.handleError("UNEXPECTED ERROR OCCURRED", exp);
			quit();
		}
		
	}

	/**
	 * Prints the main menu
	 */
	private static void printMenu() {
		System.out.println("1. Log in as a user");
		System.out.println("2. Log in as a manager");
		System.out.println("3. Quit");
	}

	/**
	 * Quits the program and closes any relevant globally shared objects
	 */
	private static void quit() {
		try {
			if (conn != null) conn.close();
			if (ih != null) ih.close();
			System.exit(0);
		} catch (SQLException sql) {
			ErrorHandler.handleError("CONNECTION TO DATABASE NOT CLOSED", sql);
		} 
		System.exit(1);
	}

	/**
	 * Method to get the connection to the database
	 * @return the Connection object
	 */
	private static void startConnection() {
		do {
			String userId = ih.getString("enter Oracle user id: ");
			String password = new String(System.console().readPassword("enter your password for " + userId + ": "));
			try {
				conn = DriverManager.getConnection(DB_URL, userId, password);
				System.out.println(GREEN + "Connection established successfully to edgar1!" + RESET);
			} catch (SQLException sql) {
				ErrorHandler.handleError("CONNECTION TO DATABASE NOT ESTABLISHED", sql);
			}
		} while (conn == null);
	}

}
