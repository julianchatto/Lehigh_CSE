package edu.lehigh.cse262.slang.StdLib;

import java.util.HashMap;
import java.util.List;

import edu.lehigh.cse262.slang.Interpreter.Values;
import edu.lehigh.cse262.slang.Parser.INodeVisitor.INodeVisitorError;

/**
 * The purpose of LibString is to implement all of the standard library
 * functions that we can do on Strings
 */
public class LibString {
    /**
     * Populate the provided `map` with a standard set of string functions
     */
    public static void populate(HashMap<String, Values.Value> map) {
        var strAppend = new Values.BuiltInFunc("string-append", (List<Values.Value> args) -> {
            LibHelpers.requireExactArgs(args, 2);

            Values.Value v1 = args.get(0), v2 = args.get(1);

            if (!(v1 instanceof Values.Str) || !(v2 instanceof Values.Str))
                throw new INodeVisitorError("Argument is not a Str");
            
            StringBuilder sb = new StringBuilder(((Values.Str) v1).val);
            sb.append(((Values.Str) v2).val);
            return new Values.Str(sb.toString());
        });
        map.put(strAppend.name, strAppend);

        // get the length of a string
        var strLen = new Values.BuiltInFunc("string-length", (List<Values.Value> args) -> {
            LibHelpers.requireExactArgs(args, 1);
            
            Values.Value val = args.get(0);
            if (!(val instanceof Values.Str))
                throw new INodeVisitorError("Argument is not a string");
            return new Values.Int(((Values.Str) val).val.length());
        });
        map.put(strLen.name, strLen);

        // build a substring
        var substring = new Values.BuiltInFunc("substring", (List<Values.Value> args) -> {
            LibHelpers.requireExactArgs(args, 3);
            
            Values.Value str = args.get(0);
            if (!(str instanceof Values.Str))
                throw new INodeVisitorError("First argument is not a string");
            
            int from = LibHelpers.getInt(args, 1), to = LibHelpers.getInt(args, 2);
            String s = ((Values.Str) str).val;
            if (from < 0 || to > s.length() || from > to) // bounds check
                throw new INodeVisitorError("Index out of bounds");
            return new Values.Str(s.substring(from, to));
        });
        map.put(substring.name, substring);

        // check if a string is a string
        var isString = new Values.BuiltInFunc("string?", (List<Values.Value> args) -> {
            LibHelpers.requireExactArgs(args, 1);
            return args.get(0) instanceof Values.Str ? new Values.BoolTrue() : new Values.BoolFalse();
        });
        map.put(isString.name, isString);

        // get the character at a given index in a string
        var charAt = new Values.BuiltInFunc("string-ref", (List<Values.Value> args) -> {
            LibHelpers.requireExactArgs(args, 2);

            Values.Value str = args.get(0);
            if (!(str instanceof Values.Str))
                throw new INodeVisitorError("First argument is not a string");
            
            int index = LibHelpers.getInt(args, 1);
            String s = ((Values.Str) str).val;
            if (index < 0 || index >= s.length()) // bounds check
                throw new INodeVisitorError("Index out of bounds");
            return new Values.Char(s.charAt(index));
        });
        map.put(charAt.name, charAt);

        // check if two strings are equal
        var strEquals = new Values.BuiltInFunc("string-equal?", (List<Values.Value> args) -> {
            LibHelpers.requireExactArgs(args, 2);

            Values.Value str1 = args.get(0), str2 = args.get(1);
            if (!(str1 instanceof Values.Str) || !(str2 instanceof Values.Str))
                throw new INodeVisitorError("Argument is not a Str");
            
            String s1 = ((Values.Str) str1).val;
            String s2 = ((Values.Str) str2).val;
            return s1.equals(s2) ? new Values.BoolTrue() : new Values.BoolFalse();
        });
        map.put(strEquals.name, strEquals);

        // build a string from a list of characters
        var makeString = new Values.BuiltInFunc("string", (List<Values.Value> args) -> {
            StringBuilder sb = new StringBuilder();
            for (Values.Value v : args) {
                if (!(v instanceof Values.Char))
                    throw new INodeVisitorError("Argument is not a char");
                sb.append(((Values.Char) v).val);
            }
            return new Values.Str(sb.toString());
        });
        map.put(makeString.name, makeString);
    }
}