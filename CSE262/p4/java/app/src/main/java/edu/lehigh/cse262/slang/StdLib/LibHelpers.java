package edu.lehigh.cse262.slang.StdLib;

import java.util.List;

import edu.lehigh.cse262.slang.Interpreter.Values;
import edu.lehigh.cse262.slang.Parser.INodeVisitor.INodeVisitorError;

/**
 * LibHelpers has a few static methods that are useful when defining standard
 * library functions.
 */
public class LibHelpers {
    /**
     * Require exactly expected arguments from args
     * @param args the list of arguments
     * @param expected the expected number of arguments
     * @throws INodeVisitorError if the number of arguments is not as expected
     */
    public static void requireExactArgs(List<Values.Value> args, int expected) throws INodeVisitorError {
        if (args.size() != expected)
            throw new INodeVisitorError("function requires exactly " + expected + " argument(s)");
    }

    /**
     * Require at least `min` arguments from args
     * @param args the list of arguments
     * @param min the minimum number of arguments
     * @throws INodeVisitorError if the number of arguments is less than min
     */
    public static void requireMinArgs(List<Values.Value> args, int min) throws INodeVisitorError {
        if (args.size() < min)
            throw new INodeVisitorError("function requires at least " + min + " argument(s)");
    }

    /**
     * Extract and check if an argument is an int at position index
     * @param args the list of arguments
     * @param index the index of the argument to extract
     * @return the int value of the argument
     * @throws INodeVisitorError if the argument is not an int
     */
    public static int getInt(List<Values.Value> args, int index) throws INodeVisitorError {
        Values.Value v = args.get(index);
        if (!(v instanceof Values.Int))
            throw new INodeVisitorError("argument is not a Int");
        return ((Values.Int) v).val;
    }

    /**
     * Extract and check if an argument is a double (or an int) at position index
     * @param args the list of arguments
     * @param index the index of the argument to extract
     * @return the double value of the argument
     * @throws INodeVisitorError if the argument is not a double or an int
     */
    public static double getDouble(List<Values.Value> args, int index) throws INodeVisitorError {
        Values.Value v = args.get(index);
        if (v instanceof Values.Int)
            return ((Values.Int) v).val;
        if (v instanceof Values.Dbl)
            return ((Values.Dbl) v).val;
        
        throw new INodeVisitorError("Argument is not a Int or Dbl");
    }
}
