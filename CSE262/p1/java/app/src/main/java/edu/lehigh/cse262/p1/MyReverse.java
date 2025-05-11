package edu.lehigh.cse262.p1;

import java.util.ArrayList;
import java.util.List;

/** MyReverse is a wrapper class around the function `reverse` */
public class MyReverse<T> {
	/**
	 * Return a list that has all of the elements of `in`, but in reverse order
	 * 
	 * @param in The list to reverse
	 * @return A list that is the reverse of `in`
	 */
	List<T> reverse(List<T> in) {
		List<T> out = new ArrayList<>();
		int n = in.size();
		// Loop through the input list in reverse order, adding each element to the return list
		for (int i = n - 1; i >= 0; i--) {
			out.add(in.get(i));
		}
		return out;
	}
}