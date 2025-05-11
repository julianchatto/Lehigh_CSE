package edu.lehigh.cse262.slang.Parser;

/**
 * Parser is the second step in our interpreter. It is responsible for turning a
 * sequence of tokens into an abstract syntax tree.
 */
public class Parser {
    /**
     * An exception class for indicating that the parser encountered an error
     */
    public static class ParseError extends Exception {
        /** Construct a ParseError and attach a message to it */
        public ParseError(String msg) {
            super(msg);
        }
    }
}
