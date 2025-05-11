package edu.lehigh.cse262.slang.Interpreter;

import java.util.HashMap;

import edu.lehigh.cse262.slang.Parser.INodeVisitor.INodeVisitorError;
import edu.lehigh.cse262.slang.StdLib.LibLists;
import edu.lehigh.cse262.slang.StdLib.LibMath;
import edu.lehigh.cse262.slang.StdLib.LibString;
import edu.lehigh.cse262.slang.StdLib.LibVector;

/**
 * Env is the environment, or scope. It is a mapping from names to IValues, and
 * Envs can be stitched together in a cactus shape.
 */
public class Env {
    /** The mapping from names to expressions */
    private HashMap<String, Values.Value> map = new HashMap<>();

    /** The enclosing environment */
    private Env outer = null;

    /**
     * The default-constructed environment can only be made by calling
     * makeDefault(). It builds all the standard library code.
     */
    private Env() {
    }

    /**
     * Add a key/value pair to the Environment
     *
     * @param key The string name
     * @param val The expression that serves as the value
     */
    public void put(String key, Values.Value val) {
        map.put(key, val);
    }

    /**
     * Search for `key` in the enclosing environment, or fail gracefully if
     * there isn't one
     */
    private Values.Value outerGet(String key) throws INodeVisitorError {
        if (outer == null)
            throw new INodeVisitorError("undefined identifier " + key);
        return outer.get(key);
    }

    /**
     * Update the value for a given `key` in the enclosing environment, or fail
     * gracefully if there isn't one.
     * 
     * @param key  The key to update
     * @param expr The new value for the key
     */
    private void outerUpdate(String key, Values.Value expr) throws INodeVisitorError {
        if (outer == null)
            throw new INodeVisitorError(key + " not defined at outermost scope");
        outer.update(key, expr);
    }

    /**
     * Get the value for a given key. If the key isn't mapped in this scope,
     * look in outer scopes.
     *
     * @param key The key to look up
     *
     * @return The value associated with the key
     */
    public Values.Value get(String key) throws INodeVisitorError {
        var res = map.get(key);
        return (res == null) ? outerGet(key) : res;
    }

    /**
     * Update the value for a given key. Throw an exception if the key isn't
     * mapped.
     *
     * @param key  The key to update
     * @param expr The new value for the key
     */
    public void update(String key, Values.Value expr) throws INodeVisitorError {
        if (map.get(key) != null)
            map.put(key, expr);
        else
            outerUpdate(key, expr);
    }

    /**
     * Create the default environment
     *
     * @return An environment object that is populated with default methods for
     *         the built-in functions
     */
    public static Env makeDefault() {
        var e = new Env();
        LibMath.populate(e.map);
        LibLists.populate(e.map);
        LibString.populate(e.map);
        LibVector.populate(e.map);
        return e;
    }

    /**
     * Create a nested (inner) environment, suitable for a lambda or closure
     *
     * @param outer The enclosing environment
     */
    public static Env makeInner(Env outer) {
        var e = new Env();
        e.outer = outer;
        return e;
    }
}