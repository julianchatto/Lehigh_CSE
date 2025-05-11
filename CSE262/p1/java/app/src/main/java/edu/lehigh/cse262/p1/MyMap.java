package edu.lehigh.cse262.p1;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/** MyMap is a wrapper class around the function `map` */
public class MyMap<T> {
	/**
	 * Apply `func` to every element in `list`, and return a list containing the
	 * results
	 * 
	 * @param list The list of elements that should be passed to func
	 * @param func The function to apply to each element in the list
	 * @return A list of the results
	 */
	List<T> map(List<T> list, Function<T, T> func) {
		List<T> result = new ArrayList<>();

		// Apply the function to each element in the list
		for (T item : list) {
			if (item == null) {
				result.add(null);
			} else {
				result.add(func.apply(item));
			}
		}

		return result;
	}
}
