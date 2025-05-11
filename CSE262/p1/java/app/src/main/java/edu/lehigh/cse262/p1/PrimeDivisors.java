package edu.lehigh.cse262.p1;

import java.util.ArrayList;
import java.util.List;

/** PrimeDivisors is a wrapper class around the function `computeDivisors` */
public class PrimeDivisors {
	/**
	 * Compute the prime divisors of `value` and return them as a list
	 *
	 * @param value The value whose prime divisors are to be computed
     * 
     * @apiNote This function is based on the code from https://www.geeksforgeeks.org/java-program-for-efficiently-print-all-prime-factors-of-a-given-number/
     * 
	 * @return A list of the prime divisors of `value`
	 */
	List<Integer> computeDivisors(int value) {
		ArrayList<Integer> divisors = new ArrayList<>();
        
        // Values less than 2 have no prime divisors
        if (value < 2) return divisors;

        // Handle 2 separately
		if (value % 2 == 0) divisors.add(2);
		
        // Since we have handled 2, we can skip all even numbers
        while (value % 2 == 0) {
			value /= 2;
        }
 
        // n must be odd at this point.  So we can skip one element (Note i = i +2)
        for (int i = 3; i <= Math.sqrt(value); i += 2) {
            // While i divides n, add i and divide n
            while (value % i == 0) {
                divisors.add(i);
                value /= i;
            }
        }
 
        // This condition is to handle the case when n is a prime number greater than 2
        if (value > 2)
            divisors.add(value);

		return divisors;
	}
}
