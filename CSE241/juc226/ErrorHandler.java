public class ErrorHandler {
    private static final String RESET ="\u001B[0m", RED = "\u001B[31m";
    
    /**
	 * Handles printing any exceptions
	 * @param message The relevant message to be printed
	 * @param exp the exception thrown
	 */
	public static void handleError(String message, Exception exp) {
		System.out.println(RED + "[ERROR]: " + message + " " + exp.getMessage() + RESET);
	}

	/**
	 * Handles printing any exceptions
	 * @param message The relevant message to be printed
	 */
	public static void handleError(String message) {
		System.out.println(RED + "[ERROR]: " + message + RESET);
	}
}
