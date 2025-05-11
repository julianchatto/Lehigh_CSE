package edu.lehigh.cse262.slang.StdLib;

import java.util.HashMap;
import java.util.List;

import edu.lehigh.cse262.slang.Interpreter.Values;
import edu.lehigh.cse262.slang.Parser.INodeVisitor.INodeVisitorError;

/**
 * The purpose of LibMath is to implement all of the standard library functions
 * that we can do on numbers (Integer or Double), predicates, conversions,
 * boolean operations, and constants
 */
public class LibMath {
    public static void populate(HashMap<String, Values.Value> map) {
        // Provided: square_int
        var square_int = new Values.BuiltInFunc("square_int", (List<Values.Value> args) -> {
            LibHelpers.requireExactArgs(args, 1);
            if (!(args.get(0) instanceof Values.Int))
                throw new INodeVisitorError("Argument is not a Int");
            int i = ((Values.Int) args.get(0)).val;
            return new Values.Int(i * i);
        });
        map.put(square_int.name, square_int);

        // Arithmetic
        var plus = new Values.BuiltInFunc("+", (List<Values.Value> args) -> {
            LibHelpers.requireMinArgs(args, 1);
            double sum = 0; boolean isDecimal = false;
            for (Values.Value v : args) {
                if (v instanceof Values.Int) { 
                    sum += (double) ((Values.Int) v).val; 
                } else if (v instanceof Values.Dbl) { 
                    sum += ((Values.Dbl) v).val; 
                    isDecimal = true; 
                } else {
                    throw new INodeVisitorError("Argument is not a Int or Dbl");
                }    
            }
            return isDecimal ? new Values.Dbl(sum) : new Values.Int((int) sum);
        }); 
        map.put(plus.name, plus);

        var minus = new Values.BuiltInFunc("-", (List<Values.Value> args) -> {
            LibHelpers.requireMinArgs(args, 1);
            
            Values.Value first = args.get(0);
            double sum; 
            boolean isDecimal = false;
            
            if (first instanceof Values.Int) { 
                sum = ((Values.Int) first).val; 
            } else if (first instanceof Values.Dbl) {
                sum = ((Values.Dbl) first).val; 
                isDecimal = true; 
            } else {
                throw new INodeVisitorError("Argument is not a Int or Dbl");
            }
            if (args.size() == 1) {
                sum *= -1;
            } else {
                for (int i = 1; i < args.size(); i++) {
                    Values.Value v = args.get(i);
                    if (v instanceof Values.Int) { 
                        sum -= ((Values.Int) v).val; 
                    } else if (v instanceof Values.Dbl) { 
                        sum -= ((Values.Dbl) v).val;  
                        isDecimal = true; 
                    } else {
                        throw new INodeVisitorError("Argument is not a Int or Dbl");
                    }
                }   
            }
            return isDecimal ? new Values.Dbl(sum) : new Values.Int((int) sum);
        }); 
        map.put(minus.name, minus);

        var times = new Values.BuiltInFunc("*", (List<Values.Value> args) -> {
            LibHelpers.requireMinArgs(args, 1);
            
            double sum = 1; 
            boolean isDecimal = false;
            for (Values.Value v : args) {
                if (v instanceof Values.Int) { 
                    sum *= ((Values.Int) v).val; 
                } else if (v instanceof Values.Dbl) { 
                    sum *= ((Values.Dbl) v).val; 
                    isDecimal = true; 
                } else {
                    throw new INodeVisitorError("Argument is not a Int or Dbl");
                }
            }
            return isDecimal ? new Values.Dbl(sum) : new Values.Int((int) sum);
        }); 
        map.put(times.name, times);

        var divide = new Values.BuiltInFunc("/", (List<Values.Value> args) -> {
            LibHelpers.requireMinArgs(args, 1);
            
            Values.Value first = args.get(0);
            double sum; 
            boolean isDecimal = false;
            if (first instanceof Values.Int) { 
                sum = ((Values.Int) first).val; 
            } else if (first instanceof Values.Dbl) { 
                sum = ((Values.Dbl) first).val; 
                isDecimal = true; 
            } else { 
                throw new INodeVisitorError("Argument is not a Int or Dbl");
            }
            if (args.size() == 1) { 
                sum = 1 / sum; 
                isDecimal = true; 
            } else {
                for (int i = 1; i < args.size(); i++) {
                    Values.Value v = args.get(i);
                    if (v instanceof Values.Int) { 
                        sum /= ((Values.Int) v).val;
                    }
                    else if (v instanceof Values.Dbl) { sum /= ((Values.Dbl) v).val; 
                        isDecimal = true; 
                    }
                    else {
                        throw new INodeVisitorError("Argument is not a Int or Dbl");
                    }
                }           
            }
            return isDecimal ? new Values.Dbl(sum) : new Values.Int((int) sum);
        }); 
        map.put(divide.name, divide);

        var mod = new Values.BuiltInFunc("%", (List<Values.Value> args) -> {
            LibHelpers.requireMinArgs(args, 1);
            
            Values.Value first = args.get(0);
            double sum; 
            boolean isDecimal = false;
            if (first instanceof Values.Int) { 
                sum = ((Values.Int) first).val;  
            } else if (first instanceof Values.Dbl) { 
                sum = ((Values.Dbl) first).val; 
                isDecimal= true; 
            } else {
                throw new INodeVisitorError("Argument is not a Int or Dbl");
            }
            for (int i = 1; i < args.size(); i++) {
                Values.Value v = args.get(i);
                if (v instanceof Values.Int) { 
                    sum %= ((Values.Int) v).val; 
                } else if (v instanceof Values.Dbl) { sum %= ((Values.Dbl) v).val; 
                    isDecimal = true; 
                } else {
                    throw new INodeVisitorError("Argument is not a Int or Dbl");
                }
            }
            return isDecimal ? new Values.Dbl(sum) : new Values.Int((int) sum);
        }); 
        map.put(mod.name, mod);

        // Comparisons
        var equals = new Values.BuiltInFunc("=", (List<Values.Value> args) -> {
            LibHelpers.requireMinArgs(args, 1);
            
            Values.Value first = args.get(0);  
            double prev =  (first instanceof Values.Int) ? ((Values.Int) first).val : ((Values.Dbl) first).val;
            for (int i = 1; i < args.size(); i++) {
                Values.Value current = args.get(i);  
                double cur = (current instanceof Values.Int) ? ((Values.Int) current).val : ((Values.Dbl) current).val;
                if (prev != cur) return new Values.BoolFalse();
                prev = cur;
            }
            return new Values.BoolTrue();
        }); 
        map.put(equals.name, equals);

        var gt = new Values.BuiltInFunc(">", (List<Values.Value> args) -> {
            LibHelpers.requireMinArgs(args, 1);
            
            Values.Value first = args.get(0);  
            double prev =  (first instanceof Values.Int) ? ((Values.Int) first).val : ((Values.Dbl) first).val;
            for (int i = 1; i < args.size(); i++) {
                Values.Value current = args.get(i);  
                double cur = (current instanceof Values.Int) ? ((Values.Int) current).val : ((Values.Dbl) current).val;
                if (prev <= cur) return new Values.BoolFalse();
                prev = cur;
            }
            return new Values.BoolTrue();
        }); 
        map.put(gt.name, gt);

        var ge = new Values.BuiltInFunc(">=", (List<Values.Value> args) -> {
            LibHelpers.requireMinArgs(args, 1);
            
            Values.Value first = args.get(0);  
            double prev =  (first instanceof Values.Int) ? ((Values.Int) first).val : ((Values.Dbl) first).val;
            for (int i = 1; i < args.size(); i++) {
                Values.Value current = args.get(i);  
                double cur = (current instanceof Values.Int) ? ((Values.Int) current).val : ((Values.Dbl) current).val;
                if (prev < cur) return new Values.BoolFalse();
                prev = cur;
            }
            return new Values.BoolTrue();
        }); 
        map.put(ge.name, ge);

        var lt = new Values.BuiltInFunc("<", (List<Values.Value> args) -> {
           LibHelpers.requireMinArgs(args, 1);
            
            Values.Value first = args.get(0);  
            double prev =  (first instanceof Values.Int) ? ((Values.Int) first).val : ((Values.Dbl) first).val;
            for (int i = 1; i < args.size(); i++) {
                Values.Value current = args.get(i);  
                double cur = (current instanceof Values.Int) ? ((Values.Int) current).val : ((Values.Dbl) current).val;
                if (prev >= cur) return new Values.BoolFalse();
                prev = cur;
            }
            return new Values.BoolTrue();
        }); 
        map.put(lt.name, lt);

        var le = new Values.BuiltInFunc("<=", (List<Values.Value> args) -> {
            LibHelpers.requireMinArgs(args, 1);
            
            Values.Value first = args.get(0);  
            double prev =  (first instanceof Values.Int) ? ((Values.Int) first).val : ((Values.Dbl) first).val;
            for (int i = 1; i < args.size(); i++) {
                Values.Value current = args.get(i);  
                double cur = (current instanceof Values.Int) ? ((Values.Int) current).val : ((Values.Dbl) current).val;
                if (prev > cur) return new Values.BoolFalse();
                prev = cur;
            }
            return new Values.BoolTrue();
        }); 
        map.put(le.name, le);

        // basic math
        var abs = new Values.BuiltInFunc("abs", (List<Values.Value> args) -> {
            LibHelpers.requireExactArgs(args, 1);
            
            Values.Value v = args.get(0);
            if (v instanceof Values.Int) 
                return new Values.Int(Math.abs(((Values.Int) v).val));
            if (v instanceof Values.Dbl) 
                return new Values.Dbl(Math.abs(((Values.Dbl) v).val));
            
            throw new INodeVisitorError("Argument is not a Int or Dbl");
        }); 
        map.put(abs.name, abs);

        var sqrt = new Values.BuiltInFunc("sqrt", (List<Values.Value> args) -> {
            LibHelpers.requireExactArgs(args, 1);
            return new Values.Dbl(Math.sqrt(LibHelpers.getDouble(args, 0)));
        }); 
        map.put(sqrt.name, sqrt);

        var pow = new Values.BuiltInFunc("pow", (List<Values.Value> args) -> {
            LibHelpers.requireExactArgs(args, 2);
            return new Values.Dbl(Math.pow(LibHelpers.getDouble(args, 0), LibHelpers.getDouble(args, 1)));
        }); 
        map.put(pow.name, pow);

        // Trig
        var sin = new Values.BuiltInFunc("sin", (List<Values.Value> args) -> {
            LibHelpers.requireExactArgs(args, 1);
            return new Values.Dbl(Math.sin(LibHelpers.getDouble(args, 0)));
        }); 
        map.put(sin.name, sin);
        
        var cos = new Values.BuiltInFunc("cos", (List<Values.Value> args) -> {
            LibHelpers.requireExactArgs(args, 1);
            return new Values.Dbl(Math.cos(LibHelpers.getDouble(args, 0)));
        }); 
        map.put(cos.name, cos);
        
        var tan = new Values.BuiltInFunc("tan", (List<Values.Value> args) -> {
            LibHelpers.requireExactArgs(args, 1);
            return new Values.Dbl(Math.tan(LibHelpers.getDouble(args, 0)));
        }); 
        map.put(tan.name, tan);
        
        var asin = new Values.BuiltInFunc("asin", (List<Values.Value> args) -> {
            LibHelpers.requireExactArgs(args, 1);
            return new Values.Dbl(Math.asin(LibHelpers.getDouble(args, 0)));
        }); 
        map.put(asin.name, asin);
        
        var acos = new Values.BuiltInFunc("acos", (List<Values.Value> args) -> {
            LibHelpers.requireExactArgs(args, 1);
            return new Values.Dbl(Math.acos(LibHelpers.getDouble(args, 0)));
        }); 
        map.put(acos.name, acos);
        
        var atan = new Values.BuiltInFunc("atan", (List<Values.Value> args) -> {
            LibHelpers.requireExactArgs(args, 1);
            return new Values.Dbl(Math.atan(LibHelpers.getDouble(args, 0)));
        });
        map.put(atan.name, atan);
        
        var sinh = new Values.BuiltInFunc("sinh", (List<Values.Value> args) -> {
            LibHelpers.requireExactArgs(args, 1);
            return new Values.Dbl(Math.sinh(LibHelpers.getDouble(args, 0)));
        }); 
        map.put(sinh.name, sinh);
        
        var cosh = new Values.BuiltInFunc("cosh", (List<Values.Value> args) -> {
            LibHelpers.requireExactArgs(args, 1);
            return new Values.Dbl(Math.cosh(LibHelpers.getDouble(args, 0)));
        }); 
        map.put(cosh.name, cosh);
        
        var tanh = new Values.BuiltInFunc("tanh", (List<Values.Value> args) -> {
            LibHelpers.requireExactArgs(args, 1);
            return new Values.Dbl(Math.tanh(LibHelpers.getDouble(args, 0)));
        }); 
        map.put(tanh.name, tanh);

        // logs
        var log10 = new Values.BuiltInFunc("log10", (List<Values.Value> args) -> {
            LibHelpers.requireExactArgs(args, 1);
            return new Values.Dbl(Math.log10(LibHelpers.getDouble(args, 0)));
        }); 
        map.put(log10.name, log10);

        var loge = new Values.BuiltInFunc("loge", (List<Values.Value> args) -> {
            LibHelpers.requireExactArgs(args, 1);
            return new Values.Dbl(Math.log(LibHelpers.getDouble(args, 0)));
        }); 
        map.put(loge.name, loge);

        // isSomethings
        var isInteger = new Values.BuiltInFunc("integer?", (List<Values.Value> args) -> {
            LibHelpers.requireExactArgs(args, 1);
            return args.get(0) instanceof Values.Int ? new Values.BoolTrue() : new Values.BoolFalse();
        }); 
        map.put(isInteger.name, isInteger);

        var isDouble = new Values.BuiltInFunc("double?", (List<Values.Value> args) -> {
            LibHelpers.requireExactArgs(args, 1);
            return args.get(0) instanceof Values.Dbl ? new Values.BoolTrue() : new Values.BoolFalse();
        }); 
        map.put(isDouble.name, isDouble);

        var isNumber = new Values.BuiltInFunc("number?", (List<Values.Value> args) -> {
            LibHelpers.requireExactArgs(args, 1);
            return (args.get(0) instanceof Values.Int || args.get(0) instanceof Values.Dbl) ? new Values.BoolTrue() : new Values.BoolFalse();
        });
        map.put(isNumber.name, isNumber);

        var isSymbol = new Values.BuiltInFunc("symbol?", (List<Values.Value> args) -> {
            LibHelpers.requireExactArgs(args, 1);
            return args.get(0) instanceof Values.Symbol ? new Values.BoolTrue() : new Values.BoolFalse();
        }); 
        map.put(isSymbol.name, isSymbol);

        var isProcedure = new Values.BuiltInFunc("procedure?", (List<Values.Value> args) -> {
            LibHelpers.requireExactArgs(args, 1);
            return (args.get(0) instanceof Values.BuiltInFunc || args.get(0) instanceof Values.LambdaVal) ? new Values.BoolTrue() : new Values.BoolFalse();
        }); 
        map.put(isProcedure.name, isProcedure);

        var isNull = new Values.BuiltInFunc("null?", (List<Values.Value> args) -> {
            LibHelpers.requireExactArgs(args, 1);
            return args.get(0) instanceof Values.EmptyCons ? new Values.BoolTrue() : new Values.BoolFalse();
        }); 
        map.put(isNull.name, isNull);

        // Conversions
        var integerToDouble = new Values.BuiltInFunc("integer->double", (List<Values.Value> args) -> {
            LibHelpers.requireExactArgs(args, 1);
            return new Values.Dbl(LibHelpers.getInt(args, 0));
        }); 
        map.put(integerToDouble.name, integerToDouble);

        var doubleToInteger = new Values.BuiltInFunc("double->integer", (List<Values.Value> args) -> {
            LibHelpers.requireExactArgs(args, 1);
            return new Values.Int((int) LibHelpers.getDouble(args, 0));
        }); 
        map.put(doubleToInteger.name, doubleToInteger);

        // negate
        var negate = new Values.BuiltInFunc("not", (List<Values.Value> args) -> {
            LibHelpers.requireExactArgs(args, 1);
            return args.get(0) instanceof Values.BoolFalse ? new Values.BoolTrue() : new Values.BoolFalse();
        }); 
        map.put(negate.name, negate);

        // Constants
        map.put("pi", new Values.Dbl(Math.PI));
        map.put("e", new Values.Dbl(Math.E));
        map.put("tau", new Values.Dbl(2 * Math.PI));
        map.put("inf+", new Values.Dbl(Double.POSITIVE_INFINITY));
        map.put("inf-", new Values.Dbl(Double.NEGATIVE_INFINITY));
        map.put("nan", new Values.Dbl(Double.NaN));
    }
}
