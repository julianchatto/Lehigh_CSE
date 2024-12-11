import java.util.Scanner;

public class InputHandler {
    private static Scanner scan;

    public InputHandler() {
        scan = new Scanner(System.in);
    }

    /**
	 * Returns the int entered
	 * @param message the requested int message 
	 * @return the entered int
	 */
	public int getInt(String message, int min, int max) {
		int val = 0;
		while (true) {
			System.out.print(message);
			if (scan.hasNextInt()) {
				val = scan.nextInt();
				if (val >= min && val <= max) return val;
				ErrorHandler.handleError("Please input an integer between " + min + " and " + max + ".");
			} else {
				scan.next();
				ErrorHandler.handleError("Please input an integer.");
			}
		}
	}

	/**
	 * Returns the user string entered
	 * @param message the requested string message
	 * @return the entered string
	 */
	public String getString(String message) {
		System.out.print(message);
		return scan.next(); 
	}

	public String getStringLine(String message) {
		System.out.print(message);
		String val = scan.nextLine();
		return val;
	}

	/**
	 * Returns the double entered
	 * @param message the requested double message
	 * @return the entered double
	 */
	public double getDouble(String message, double min, double max) {
		double val = 0;
		while (true) {
			System.out.print(message);
			if (scan.hasNextDouble()) {
				val = scan.nextDouble();
				if (val >= min && val <= max) return val;
				ErrorHandler.handleError("Please input a double between " + min + " and " + max + ".");
			} else {
				scan.next();
				ErrorHandler.handleError("Please input a double.");
			}
		}
	}

	public void close() {
		scan.close();
	}
}
