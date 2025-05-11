package edu.lehigh.cse262.slang.Interpreter;

import java.security.InvalidParameterException;
import java.util.List;

import edu.lehigh.cse262.slang.Parser.AstNodes.LambdaDef;

public class Values {
    /**
     * Placing the Value type into the hierarchy helps us to write a correct
     * parser.
     *
     * We can use Value to "tag" certain types, indicating that they are data,
     * not executable forms. If we do this correctly, then the parser can't
     * accidentally put an executable form in a place where a value is expected.
     */
    public static abstract class Value {
        /**
         * `stringify` gives some text to a StringBuilder, as a step toward
         * turning a Value into something that can be printed.
         *
         * @param sb A stringBuilder for creating a result
         */
        public abstract void stringify(StringBuilder sb);
    };

    /**
     * Cons is a pair (cons cell)
     */
    public static class Cons extends Value {
        /** The first value of the pair */
        public Value car;

        /** The second value of the pair */
        public Value cdr;

        /** Create a Cons list from a list of values */
        public static Value makeConsList(List<Value> items) {
            if (items.size() == 0)
                return new EmptyCons();
            if (items.size() == 1)
                return new Cons(items.get(0), new EmptyCons());
            return new Cons(items.get(0), makeConsList(items.subList(1, items.size())));
        }

        /** Create a Cons cell from two values */
        public static Value makeCons(Value car, Value cdr) throws InvalidParameterException {
            if (car == null && cdr == null)
                // return new EmptyCons();
                throw new InvalidParameterException("Arguments to makeCons cannot be null");
            return new Cons(car, cdr);
        }

        /** Construct a `cons` node from two values */
        private Cons(Value car, Value cdr) {
            this.car = car;
            this.cdr = cdr;
        }

        @Override
        public void stringify(StringBuilder sb) {
            sb.append("(");
            car.stringify(sb);
            sb.append(" . ");
            cdr.stringify(sb);
            sb.append(")");
        }

    }

    /** Vec represents a Vector value */
    public static class Vec extends Value {
        /** The values represented by this node */
        public final Value[] items;

        /** Construct a `vec` node from a list of values */
        public Vec(List<Value> items) {
            Value[] tmp = new Value[items.size()];
            this.items = items.toArray(tmp);
        }

        public void stringify(StringBuilder sb) {
            sb.append("#(");
            items[0].stringify(sb);
            for (int i = 1; i < items.length; ++i) {
                sb.append(" ");
                items[i].stringify(sb);
            }
            sb.append(")");
        }
    }

    /**
     * BoolTrue is used to represent the value `true`. We use the *type* to
     * indicate true, instead of some field of the object.
     */
    public static class BoolTrue extends Value {
        /** Construct a `BoolTrue` node */
        public BoolTrue() {
        }

        @Override
        public void stringify(StringBuilder sb) {
            sb.append("#t");
        }
    }

    /**
     * BoolFalse is used to represent the value `false`. We use the *type* to
     * indicate false, instead of some field of the object.
     */
    public static class BoolFalse extends Value {
        /** Construct a `BoolFalse` node */
        public BoolFalse() {
        }

        @Override
        public void stringify(StringBuilder sb) {
            sb.append("#f");
        }
    }

    /** Char is used to represent character values */
    public static class Char extends Value {
        /** The value represented by this node */
        public final char val;

        /** Construct a `char` node from a character value */
        public Char(char val) {
            this.val = val;
        }

        @Override
        public void stringify(StringBuilder sb) {
            if (val == ' ')
                sb.append("#\\space");
            else if (val == '\n')
                sb.append("#\\newline");
            else if (val == '\t')
                sb.append("#\\tab");
            else
                sb.append("#\\" + val);
        }
    }

    /** EmptyCons is used to represent an empty Cons cell */
    public static class EmptyCons extends Value {
        /** Construct an `EmptyCons` */
        public EmptyCons() {
        }

        @Override
        public void stringify(StringBuilder sb) {
            sb.append("()");
        }
    }

    /** Dbl is used to represent double values */
    public static class Dbl extends Value {
        /** The value represented by this node */
        public final double val;

        /** Construct a `dbl` node from a double value */
        public Dbl(double val) {
            this.val = val;
        }

        @Override
        public void stringify(StringBuilder sb) {
            sb.append(val);
        }
    }

    /** Int is used to represent integer values */
    public static class Int extends Value {
        /** The value represented by this node */
        public final int val;

        /** Construct an `int` node from an integer value */
        public Int(int val) {
            this.val = val;
        }

        @Override
        public void stringify(StringBuilder sb) {
            sb.append(val);
        }
    }

    /** Str is used to represent String values */
    public static class Str extends Value {
        /** The value represented by this node */
        public final String val;

        /** Construct a `str` node from a String value */
        public Str(String val) {
            this.val = val;
        }

        @Override
        public void stringify(StringBuilder sb) {
            sb.append(val);
        }
    }

    /**
     * Symbol represents a symbol value
     *
     * NB: Since Scheme is homo-iconic, Symbol and Identifier look identical.
     * However, a Symbol *is* a value, and an Identifier *is not*
     */
    public static class Symbol extends Value {
        /** The name associated with this symbol */
        private final String name;

        /** Construct a `symbol` node from its name */
        public Symbol(String name) {
            this.name = name;
        }

        @Override
        public void stringify(StringBuilder sb) {
            sb.append("'");
            sb.append(name);
        }
    }

    /**
     * BuiltInFunc is a built-in function that can be executed. Remember:
     * Functions are first-class values!
     */
    public static class BuiltInFunc extends Value {
        /** The name associated with this function. Useful for debugging */
        public final String name;

        /** The (Java) code to run to execute this function */
        public final IExecutable func;

        /**
         * Construct a `built in function` from a name and some executable Java
         * code
         */
        public BuiltInFunc(String name, IExecutable func) {
            this.name = name;
            this.func = func;
        }

        @Override
        public void stringify(StringBuilder sb) {
            sb.append("Built-in Function: ");
            sb.append(name);
        }
    }

    /**
     * Lambda **values** are produced by *evaluating* a LambdaDef expression. A
     * LambdaVal is a function that can be "executed". Remember: LambdaVals are
     * first-class values!
     */
    public static class LambdaVal extends Value {
        /** The (static) scope at the time this LambdaVal was created */
        public final Env env;

        /** The lambda definition that is bound to the scope */
        public final LambdaDef lambdaDef;

        /**
         * Construct a `lambda value` from an environment and a LambdaDef
         */
        public LambdaVal(Env env, LambdaDef lambdaDef) {
            this.env = env;
            this.lambdaDef = lambdaDef;
        }

        @Override
        public void stringify(StringBuilder sb) {
            sb.append("Lambda Function");
        }
    }
}
