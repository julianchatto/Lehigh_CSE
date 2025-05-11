package edu.lehigh.cse262.p1;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

/**
 * ReadList is a wrapper class around the function `read`
 */
public class ReadList<T> {
	/**
	 * Read from stdin until EOF is encountered, and put all of the values into a
	 * list. The order in the list should be the reverse of the order in which the
	 * elements were added.
	 * 
	 * @apiNote You can instantiate this class with any type so long as the type can convert from a string
	 * 
	 * @return A list with the values that were read
	 */
	List<T> read(FromString<T> converter) {
		// I use a LinkedList for O(1) insertions to the front of the list. Therefore there is no need to reverse the list at the end.
		LinkedList<T> list = new LinkedList<>();

		// read from STDIN
		Scanner scanner = new Scanner(System.in);
		try {
			while (scanner.hasNext()) { // NOTE: you need to do ctrl + z + enter to end without and exception (on my computer)
				String s = scanner.next();
				T value = converter.convert(s);
				list.addFirst(value);
			}
		} catch (InputMismatchException e) {
			System.out.println("Invalid input");
		} catch (Exception e) {
			System.out.println("An error occurred");
		}
		
		scanner.close();

		return new ArrayList<>(list);
	}

	public interface FromString<T> {
		T convert(String s);
	}
}