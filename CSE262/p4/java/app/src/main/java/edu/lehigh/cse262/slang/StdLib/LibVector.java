package edu.lehigh.cse262.slang.StdLib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.lehigh.cse262.slang.Interpreter.Values;
import edu.lehigh.cse262.slang.Parser.INodeVisitor.INodeVisitorError;

/**
 * The purpose of LibVector is to implement all of the standard library
 * functions that we can do on vectors
 */
public class LibVector {
    /**
     * Populate the provided `map` with a standard set of vector functions
     */
    public static void populate(HashMap<String, Values.Value> map) {
        // returns the length of the vector
        var vectorLen = new Values.BuiltInFunc("vector-length", (List<Values.Value> args) -> {
            LibHelpers.requireExactArgs(args, 1);
            
            Values.Value v = args.get(0);
            if (!(v instanceof Values.Vec))
                throw new INodeVisitorError("Argument is not a Vec");

            return new Values.Int(((Values.Vec) v).items.length);
        });
        map.put(vectorLen.name, vectorLen);

        // gets the value at index in the vector
        var getAtIndex = new Values.BuiltInFunc("vector-get", (List<Values.Value> args) -> {
            LibHelpers.requireExactArgs(args, 2);
            
            Values.Value v = args.get(0);
            if (!(v instanceof Values.Vec))
                throw new INodeVisitorError("Argument is not a Vec");
            
            int index = LibHelpers.getInt(args, 1);
            Values.Vec vec = (Values.Vec) v;
            if (index < 0 || index >= vec.items.length)
                throw new INodeVisitorError("Index out of bounds");
            
            return vec.items[index];
        });
        map.put(getAtIndex.name, getAtIndex);

        // sets and returns the value at index in the vector
        var setAtIndex = new Values.BuiltInFunc("vector-set!", (List<Values.Value> args) -> {
            LibHelpers.requireExactArgs(args, 3);
            
            Values.Value v = args.get(0);
            if (!(v instanceof Values.Vec))
                throw new INodeVisitorError("Argument is not a Vec");
            
            int index = LibHelpers.getInt(args, 1);
            Values.Vec vec = (Values.Vec) v;
            if (index < 0 || index >= vec.items.length) // check bounds
                throw new INodeVisitorError("Index out of bounds");
            
            vec.items[index] = args.get(2);
            return args.get(2);
        });
        map.put(setAtIndex.name, setAtIndex);

        // builds a vector from args
        var vector = new Values.BuiltInFunc("vector", (List<Values.Value> args) -> {
            return new Values.Vec(args);
        });
        map.put(vector.name, vector);

        // checks if the argument is a vector
        var isVector = new Values.BuiltInFunc("vector?", (List<Values.Value> args) -> {
            LibHelpers.requireExactArgs(args, 1);
            return args.get(0) instanceof Values.Vec ? new Values.BoolTrue() : new Values.BoolFalse();
        });
        map.put(isVector.name, isVector);

        // builds a vector of length n
        var makeVector = new Values.BuiltInFunc("make-vector", (List<Values.Value> args) -> {
            LibHelpers.requireExactArgs(args, 1);
            
            int n = LibHelpers.getInt(args, 0);
            List<Values.Value> list = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                list.add(new Values.BoolFalse());
            }
            return new Values.Vec(list);
        });
        map.put(makeVector.name, makeVector);
    }
}
