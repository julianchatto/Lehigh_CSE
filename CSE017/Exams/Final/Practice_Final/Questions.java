public class Questions {
    // #1
    /*
     * Suppose statement2 may throw an exception in the following code: 
        try {
            statement1;
            statement2;
            statement3;
        }   
        catch(Exception ex1) {
            System.exit(0);
        }
        finally {
            statement4;
        }
        statement5;


        ANSWER:
        
        a) If no exception occurs, will statement3, statement4 or statement5 be executed?
            - All will be executed
        b) If an exception of type Exception is thrown, will statement3, statement4 or statement5 be executed?
            - No
        c) If an exception that is not of type Exception is thrown, will statement3, statement4 or statement5 be executed?
            - 
     */

    // #2
     /***
      * Evaluate the following postfix expression using a stack. Show all the steps.
            20 6 * 6 5 * 31 + 1 - / 9 - *
      *     
      */

    //#3
    /**
     * Determine the exact number of iterations executed by the following code and the time complexity of the code using Big-O notation? Show your work.
        for (int i = n; i > 0; --i) { // iterates n times
	        for (int j = n/20; j > 0; j /= 2) {
                for(int k = 1; k < n; k *= 3) {
         	        int product = i * k + j * (k-2) + k;
                }
            }
        }
      	
     * 
     */  

     // #4
     /**
      * Assume the following list of values {18, 9, 32, 22, 75, 83, 3} to be sorted using Merge sort. Show all the steps to sort the list.
      * 1. {18,9,32,22} {75, 83, 3}
      * 2. {18, 9} {32, 22} {75} {83,3}
      * 3. {18} {9} {32} {22} {75} {83} {3}
      * 4. {9, 18} {22, 32} {75} {3, 83}
      * 5. {9, 18, 22, 32} {3, 75, 83}
      * 6. {3, 9, 18, 22, 32, 75, 83}
      */

      // #5 

      /***
       * Assume the following list of values {16, 80, 22, 55, 64, 95, 25} to be sorted using quicksort. Use a pivot as the first element of the list. Show all the steps to sort the list.
       * {16} {}
       */


}
