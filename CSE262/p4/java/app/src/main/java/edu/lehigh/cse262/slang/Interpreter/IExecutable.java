package edu.lehigh.cse262.slang.Interpreter;

import java.util.List;

import edu.lehigh.cse262.slang.Parser.INodeVisitor.INodeVisitorError;

/**
 * IExecutable allows us to wrap native functions with marshalling code for
 * managing the movement of arguments to them, and return values from them.
 * They are the foundation on which we build BuiltInFuncs
 */
public interface IExecutable {
    /**
     * Perform an operation using the provided arguments, in the context of the
     * given environment.
     *
     * @param args A list of (already-evaluated) values to use as arguments
     *
     * @return A value that was computed by the IExecutable
     */
    public Values.Value execute(List<Values.Value> args) throws INodeVisitorError;
}
