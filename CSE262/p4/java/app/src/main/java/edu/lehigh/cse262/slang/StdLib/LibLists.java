package edu.lehigh.cse262.slang.StdLib;

import java.util.HashMap;
import java.util.List;

import edu.lehigh.cse262.slang.Interpreter.Values;
import edu.lehigh.cse262.slang.Parser.INodeVisitor.INodeVisitorError;

/**
 * The purpose of LibLists is to implement all of the standard library functions
 * that we can do on Cons nodes
 */
public class LibLists {
    /**
     * Populate the provided `map` with a standard set of list functions
     */
    public static void populate(HashMap<String, Values.Value> map) {
        var cons = new Values.BuiltInFunc("cons", (List<Values.Value> args) -> {
            LibHelpers.requireExactArgs(args, 2);
            return Values.Cons.makeCons(args.get(0), args.get(1));
        });
        map.put(cons.name, cons);

        var car = new Values.BuiltInFunc("car", (List<Values.Value> args) -> {
            LibHelpers.requireExactArgs(args, 1);

            Values.Value v = args.get(0);
            if (!(v instanceof Values.Cons))
                throw new INodeVisitorError("Argument is not a Cons");
            return ((Values.Cons) v).car;
        });
        map.put(car.name, car);

        var cdr = new Values.BuiltInFunc("cdr", (List<Values.Value> args) -> {
            LibHelpers.requireExactArgs(args, 1);
            
            Values.Value v = args.get(0);
            if (!(v instanceof Values.Cons))
                throw new INodeVisitorError("Argument is not a Cons");
            return ((Values.Cons) v).cdr;
        });
        map.put(cdr.name, cdr);

        var list = new Values.BuiltInFunc("list", (List<Values.Value> args) -> {
            return Values.Cons.makeConsList(args);
        });
        map.put(list.name, list);

        var isList = new Values.BuiltInFunc("list?", (List<Values.Value> args) -> {
            LibHelpers.requireExactArgs(args, 1);
            
            Values.Value v = args.get(0);
            return (v instanceof Values.Cons || v instanceof Values.EmptyCons) ? new Values.BoolTrue() : new Values.BoolFalse();
        });
        map.put(isList.name, isList);

        var setCar = new Values.BuiltInFunc("set-car!", (List<Values.Value> args) -> {
            LibHelpers.requireExactArgs(args, 2);

            Values.Value v = args.get(0), v2 = args.get(1);
            if (!(v instanceof Values.Cons))
                throw new INodeVisitorError("First argument is not a cons cell");
            ((Values.Cons) v).car = v2; 
            return v2;
        });
        map.put(setCar.name, setCar);

        var setCdr = new Values.BuiltInFunc("set-cdr!", (List<Values.Value> args) -> {
            LibHelpers.requireExactArgs(args, 2);

            Values.Value v = args.get(0), v2 = args.get(1);
            if (!(v instanceof Values.Cons))
                throw new INodeVisitorError("First argument is not a cons cell");
            ((Values.Cons) v).cdr = v2;
            return v2;
        });
        map.put(setCdr.name, setCdr);
    }
}
