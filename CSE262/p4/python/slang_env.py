import operator
import math

from values import *


class Env:
    """An environment/scope in which variables are defined"""

    def __init__(self, outer):
        """Construct an environment.  We need a single global value for false,
        for true, and for empty.  We optionally have a link to an enclosing
        scope"""
        self.__outer = outer
        self.__map = {}

    def put(self, key: str, val):
        """Unconditionally put a key into this environment"""
        self.__map[key] = val

    def get(self, key: str):
        """Look up the value for a given key; recurse to outer environments as
        needed.  Return an error on failure."""
        return self.__map[key] if key in self.__map.keys() else self.__outerGet(key)

    def __outerGet(self, key):
        """Look up the key in the outer scope"""
        if not self.__outer:
            return makeErrorVal("undefined identifier " + key)
        return self.__outer.get(key)

    def update(self, key: str, val):
        """Update a key's value **in the scope where it is defined**"""
        if self.__map.get(key) != None:
            self.__map[key] = val
        else:
            self.__outer.update(key, val)


def makeDefaultEnv():
    """Create a default environment by mapping the standard library into it"""
    e = Env(None)
    addMathFuncs(e)
    addListFuncs(e)
    addStringFuncs(e)
    addVectorFuncs(e)
    return e


def makeInnerEnv(outer: Env):
    """Create an inner scope that links to its parent"""
    return Env(outer)


def ERROR(msg):
    """Return an Error Value from the given message"""
    return makeErrorVal(msg)


class LibHelpers:
    def requireExactArgs(args, expected):
        return ERROR(f"function requires exactly {expected} argument(s)")
    
    def requireMinArgs(args, expected):
        return ERROR(f"function requires at least {expected} argument(s)")
        
    def getInt(args, index):
        if args[index]["type"] != VAL_INT:
            return ERROR("argument is not a Int")
        return args[index]["val"]
    
    def getDouble(args, index):
        if args[index]["type"] == VAL_DBL:
            return args[index]["val"]
        if args[index]["type"] == VAL_INT:
            return float(args[index]["val"])
        return ERROR("Argument is not a Int or Dbl")


def addMathFuncs(env):
    """Add standard math functions to the given environment"""

    # One function is provided "for free" :)
    def square_int(args):
        if len(args) != 1:
            return LibHelpers.requireExactArgs(args, 1)
        if args[0]["type"] not in [VAL_INT]:
            return ERROR("Argument is not a Int")
        val = args[0]["val"]
        return makeIntVal(val * val)
    env.put("square_int", makeBuiltInFuncVal("square_int", square_int))
    
    # arithmetic
    def plus(args):
        if len(args) < 1:
            return LibHelpers.requireMinArgs(args, 1)
        total = 0
        isDecimal = False
        for i in range(len(args)):
            if args[i]["type"] == VAL_INT:
                total += args[i]["val"]
            elif args[i]["type"] == VAL_DBL:
                total += args[i]["val"]
                isDecimal = True
            else:
                return ERROR("Argument is not a Int or Dbl")
        
        return makeDblVal(total) if isDecimal else makeIntVal(int(total))
    env.put("+", makeBuiltInFuncVal("+", plus))
    
    def minus(args):
        if len(args) < 1:
            return LibHelpers.requireMinArgs(args, 1)
        if len(args) == 1:
            if args[0]["type"] == VAL_INT:
                return makeIntVal(-args[0]["val"])
            elif args[0]["type"] == VAL_DBL:
                return makeDblVal(-args[0]["val"])
            else:
                return ERROR("Argument is not a Int or Dbl")
        total = args[0]["val"]
        isDecimal = False
        for i in range(1, len(args)):
            if args[i]["type"] == VAL_INT:
                total -= args[i]["val"]
            elif args[i]["type"] == VAL_DBL:
                total -= args[i]["val"]
                isDecimal = True
            else:
                return ERROR("Argument is not a Int or Dbl")
        
        return makeDblVal(total) if isDecimal else makeIntVal(int(total))
    env.put("-", makeBuiltInFuncVal("-", minus))
    
    def times(args):
        if len(args) < 1:
            return LibHelpers.requireMinArgs(args, 1)
        total = 1
        isDecimal = False
        for i in range(len(args)):
            if args[i]["type"] == VAL_INT:
                total *= args[i]["val"]
            elif args[i]["type"] == VAL_DBL:
                total *= args[i]["val"]
                isDecimal = True
            else:
                return ERROR("Argument is not a Int or Dbl")
        
        return makeDblVal(total) if isDecimal else makeIntVal(int(total))    
    env.put("*", makeBuiltInFuncVal("*", times))
    
    def divide(args):
        if len(args) < 1:
            return LibHelpers.requireMinArgs(args, 1)
        if len(args) == 1:
            if args[0]["type"] == VAL_INT:
                return makeDblVal(1.0 / args[0]["val"])
            elif args[0]["type"] == VAL_DBL:
                return makeDblVal(1.0 / args[0]["val"])
            else:
                return ERROR("Argument is not a Int or Dbl")
        total = args[0]["val"]
        isDecimal = False
        for i in range(1, len(args)):
            if args[i]["type"] == VAL_INT:
                total /= args[i]["val"]
            elif args[i]["type"] == VAL_DBL:
                total /= args[i]["val"]
                isDecimal = True
            else:
                return ERROR("Argument is not a Int or Dbl")
        
        return makeDblVal(total) if isDecimal else makeIntVal(int(total))
    env.put("/", makeBuiltInFuncVal("/", divide))
    
    def mod(args):
        if len(args) < 1:
            return LibHelpers.requireMinArgs(args, 1)
        
        if len(args) == 1:
            if args[0]["type"] == VAL_INT:
                return makeIntVal(args[0]["val"] % 2)
            elif args[0]["type"] == VAL_DBL:
                return makeDblVal(args[0]["val"] % 2)
            else:
                return ERROR("Argument is not a Int or Dbl")
        total = args[0]["val"]
        isDecimal = False
        for i in range(1, len(args)):
            if args[i]["type"] == VAL_INT:
                total %= args[i]["val"]
            elif args[i]["type"] == VAL_DBL:
                total = math.fmod(total, args[i]["val"])
                isDecimal = True
            else:
                return ERROR("Argument is not a Int or Dbl")
        return makeDblVal(total) if isDecimal else makeIntVal(int(total))
    env.put("%", makeBuiltInFuncVal("%", mod))
    
    # comparisons
    def equals(args):
        if len(args) < 2:
            return ERROR("= requires at least two arguments")

        # found this cool syntax for one line loop
        return makeBoolTrueVal() if all(
            float(args[0]["val"]) == float(item["val"]) for item in args[1:]
        ) else makeBoolFalseVal()
    env.put("=", makeBuiltInFuncVal("=", equals))
    
    def lessThan(args):
        if len(args) < 1:
            return LibHelpers.requireMinArgs(args, 1)
        
        return makeBoolTrueVal() if all(
            float(args[i]["val"]) < float(args[i + 1]["val"]) for i in range(len(args) - 1)
        ) else makeBoolFalseVal()
    env.put("<", makeBuiltInFuncVal("<", lessThan))
    
    def lessThanOrEqual(args):
        if len(args) < 1:
            return LibHelpers.requireMinArgs(args, 1)
        
        return makeBoolTrueVal() if all(
            float(args[i]["val"]) <= float(args[i + 1]["val"]) for i in range(len(args) - 1)
        ) else makeBoolFalseVal()
    env.put("<=", makeBuiltInFuncVal("<=", lessThanOrEqual))
    
    def greaterThan(args):
        if len(args) < 1:
            return LibHelpers.requireMinArgs(args, 1)
        
        return makeBoolTrueVal() if all(
            float(args[i]["val"]) > float(args[i + 1]["val"]) for i in range(len(args) - 1)
        ) else makeBoolFalseVal()
    env.put(">", makeBuiltInFuncVal(">", greaterThan))
    
    def greaterThanOrEqual(args):
        if len(args) < 1:
            return LibHelpers.requireMinArgs(args, 1)
        
        return makeBoolTrueVal() if all(
            float(args[i]["val"]) >= float(args[i + 1]["val"]) for i in range(len(args) - 1)
        ) else makeBoolFalseVal()
    env.put(">=", makeBuiltInFuncVal(">=", greaterThanOrEqual))
    
    # basic math
    def absoluteVal(args):
        if len(args) != 1:
            return LibHelpers.requireExactArgs(args, 1)
        if args[0]["type"] == VAL_INT:
            return makeIntVal(abs(args[0]["val"]))
        if args[0]["type"] == VAL_DBL:
            return makeDblVal(abs(args[0]["val"]))
        return ERROR("Argument is not a Int or Dbl")
    env.put("abs", makeBuiltInFuncVal("abs", absoluteVal))
    
    def sqrt(args):
        if len(args) != 1:
            return LibHelpers.requireExactArgs(args, 1)
        if args[0]["type"] != VAL_INT and args[0]["type"] != VAL_DBL:
            return ERROR("Argument is not a Int or Dbl")
        return makeDblVal(math.sqrt(LibHelpers.getDouble(args, 0)))
    env.put("sqrt", makeBuiltInFuncVal("sqrt", sqrt))
    
    def pow(args):
        if len(args) != 2:
            return LibHelpers.requireExactArgs(args, 2)
        base = LibHelpers.getDouble(args, 0)
        exp = LibHelpers.getDouble(args, 1)
        return makeDblVal(math.pow(base, exp))
    
    # trig
    def sin(args):
        if len(args) != 1:
            return LibHelpers.requireExactArgs(args, 1)
        return makeDblVal(math.sin(LibHelpers.getDouble(args, 0)))
    env.put("sin", makeBuiltInFuncVal("sin", sin))
    
    def cos(args):
        if len(args) != 1:
            return LibHelpers.requireExactArgs(args, 1)
        return makeDblVal(math.cos(LibHelpers.getDouble(args, 0)))
    env.put("cos", makeBuiltInFuncVal("cos", cos))
    
    def tan(args):
        if len(args) != 1:
            return LibHelpers.requireExactArgs(args, 1)
        return makeDblVal(math.tan(LibHelpers.getDouble(args, 0)))
    env.put("tan", makeBuiltInFuncVal("tan", tan))
    
    def asin(args):
        if len(args) != 1:
            return LibHelpers.requireExactArgs(args, 1)
        return makeDblVal(math.asin(LibHelpers.getDouble(args, 0)))
    env.put("asin", makeBuiltInFuncVal("asin", asin))
    
    def acos(args):
        if len(args) != 1:
            return LibHelpers.requireExactArgs(args, 1)
        return makeDblVal(math.acos(LibHelpers.getDouble(args, 0)))
    env.put("acos", makeBuiltInFuncVal("acos", acos))
    
    def atan(args):
        if len(args) != 1:
            return LibHelpers.requireExactArgs(args, 1)
        return makeDblVal(math.atan(LibHelpers.getDouble(args, 0)))
    env.put("atan", makeBuiltInFuncVal("atan", atan))
    
    def sinh(args):
        if len(args) != 1:
            return LibHelpers.requireExactArgs(args, 1)
        return makeDblVal(math.sinh(LibHelpers.getDouble(args, 0)))
    env.put("sinh", makeBuiltInFuncVal("sinh", sinh))
    
    def cosh(args):
        if len(args) != 1:
            return LibHelpers.requireExactArgs(args, 1)
        return makeDblVal(math.cosh(LibHelpers.getDouble(args, 0)))
    env.put("cosh", makeBuiltInFuncVal("cosh", cosh))
    
    def tanh(args):
        if len(args) != 1:
            return LibHelpers.requireExactArgs(args, 1)
        return makeDblVal(math.tanh(LibHelpers.getDouble(args, 0)))
    env.put("tanh", makeBuiltInFuncVal("tanh", tanh))
    
    # logs
    def log10(args):
        if len(args) != 1:
            return LibHelpers.requireExactArgs(args, 1)
        return makeDblVal(math.log10(LibHelpers.getDouble(args, 0)))
    env.put("log10", makeBuiltInFuncVal("log10", log10))
    
    def loge(args):
        if len(args) != 1:
            return LibHelpers.requireExactArgs(args, 1)
        return makeDblVal(math.log(LibHelpers.getDouble(args, 0)))
    env.put("loge", makeBuiltInFuncVal("loge", loge))
    
    # isSomethings
    def isInteger(args):
        if len(args) != 1:
            return LibHelpers.requireExactArgs(args, 1)
        return makeBoolTrueVal() if args[0]["type"] == VAL_INT else makeBoolFalseVal()
    env.put("integer?", makeBuiltInFuncVal("integer?", isInteger))
    
    def isDouble(args):
        if len(args) != 1:
            return LibHelpers.requireExactArgs(args, 1)
        return makeBoolTrueVal() if args[0]["type"] == VAL_DBL else makeBoolFalseVal()
    env.put("double?", makeBuiltInFuncVal("double?", isDouble))
    
    def isNumber(args):
        if len(args) != 1:
            return LibHelpers.requireExactArgs(args, 1)
        return makeBoolTrueVal() if args[0]["type"] in [VAL_INT, VAL_DBL] else makeBoolFalseVal()
    env.put("number?", makeBuiltInFuncVal("number?", isNumber))
    
    def isSymbol(args):
        if len(args) != 1:
            return LibHelpers.requireExactArgs(args, 1)
        return makeBoolTrueVal() if args[0]["type"] == VAL_SYMBOL else makeBoolFalseVal()
    env.put("symbol?", makeBuiltInFuncVal("symbol?", isSymbol))
    
    def isProcedure(args):
        if len(args) != 1:
            return LibHelpers.requireExactArgs(args, 1)
        return makeBoolTrueVal() if args[0]["type"] == VAL_LAMBDA else makeBoolFalseVal()
    env.put("procedure?", makeBuiltInFuncVal("procedure?", isProcedure))
    
    def isNull(args):
        if len(args) != 1:
            return LibHelpers.requireExactArgs(args, 1)
        return makeBoolTrueVal() if args[0]["type"] == VAL_EMPTY else makeBoolFalseVal()
    env.put("null?", makeBuiltInFuncVal("null?", isNull))
    
    # conversions
    def integerToDouble(args):
        if len(args) != 1:
            return LibHelpers.requireExactArgs(args, 1)
        return makeDblVal(float(LibHelpers.getInt(args, 0)))
    env.put("integer->double", makeBuiltInFuncVal("integer->double", integerToDouble))
    
    def doubleToInteger(args):
        if len(args) != 1:
            return LibHelpers.requireExactArgs(args, 1)
        return makeIntVal(int(LibHelpers.getDouble(args, 0)))
    env.put("double->integer", makeBuiltInFuncVal("double->integer", doubleToInteger))
    
    # negate
    def negate(args):
        if len(args) != 1:
            return LibHelpers.requireExactArgs(args, 1)
        return makeBoolFalseVal() if args[0]["type"] == VAL_TRUE else makeBoolTrueVal()
    env.put("not", makeBuiltInFuncVal("not", negate))
                
    
    # constants
    env.put("pi", makeDblVal(math.pi))
    env.put("e", makeDblVal(math.e))
    env.put("tau", makeDblVal(math.tau))
    env.put("inf+", makeDblVal(math.inf))
    env.put("inf-", makeDblVal(-math.inf))
    env.put("nan", makeDblVal(math.nan))


def addListFuncs(env):
    """Add standard list functions to the given environment"""
    def car(args):
        if len(args) != 1:
            return LibHelpers.requireExactArgs(args, 1)
        
        if args[0]["type"] != VAL_CONS:
            return ERROR("Argument is not a Cons")
        return args[0]["car"]
    env.put("car", makeBuiltInFuncVal("car", car))

    def cdr(args):
        if len(args) != 1:
            return LibHelpers.requireExactArgs(args, 1)
        
        if args[0]["type"] != VAL_CONS:
            return ERROR("Argument is not a Cons")
        return args[0]["cdr"]
    env.put("cdr", makeBuiltInFuncVal("cdr", cdr))

    def cons(args):
        if len(args) != 2:
            return LibHelpers.requireExactArgs(args, 2)
        return makeConsVal(args[0], args[1])
    env.put("cons", makeBuiltInFuncVal("cons", cons))
    
    def list(args):
        return makeList(args) if args else makeEmptyVal()
    env.put("list", makeBuiltInFuncVal("list", list))
    
    

    def isList(args):
        if len(args) != 1:
            return LibHelpers.requireExactArgs(args, 1)
        return makeBoolTrueVal() if args[0]["type"] == VAL_CONS or args[0]["type"] == VAL_EMPTY else makeBoolFalseVal()
    env.put("list?", makeBuiltInFuncVal("list?", isList))
    
    # used to change fields of a pair 
    def setCar(args):
        if len(args) != 2:
            return LibHelpers.requireExactArgs(args, 2)
        if args[0]["type"] != VAL_CONS:
            return ERROR("First argument is not a cons cell")
        args[0]["car"] = args[1]
        return args[1]
    env.put("set-car!", makeBuiltInFuncVal("set-car!", setCar))

    # used to change fields of a pair 
    def setCdr(args):
        if len(args) != 2:
            return LibHelpers.requireExactArgs(args, 2)
        if args[0]["type"] != VAL_CONS:
            return ERROR("First argument is not a cons cell")
        
        args[0]["cdr"] = args[1]
        return args[1]
    env.put("set-cdr!", makeBuiltInFuncVal("set-cdr!", setCdr))
    

def addStringFuncs(env):
    """Add standard string functions to the given environment"""
    def strLen(args):
        if len(args) != 1:
            return LibHelpers.requireExactArgs(args, 1)
        if args[0]["type"] != VAL_STR:
            return ERROR("Argument is not a string")
        return makeIntVal(len(args[0]["val"]))
    env.put("string-length", makeBuiltInFuncVal("string-length", strLen))

    def strAppend(args):
        if len(args) != 2:
            return LibHelpers.requireExactArgs(args, 2)
        
        if args[0]["type"] != VAL_STR or args[1]["type"] != VAL_STR:
            return ERROR("Argument is not a Str")
        return makeStrVal(args[0]["val"] + args[1]["val"])
    env.put("string-append", makeBuiltInFuncVal("string-append", strAppend))

    def substring(args):
        if len(args) != 3:
            return LibHelpers.requireExactArgs(args, 3)
        
        # check types of arguments
        if args[0]["type"] != VAL_STR or args[1]["type"] != VAL_INT or args[2]["type"] != VAL_INT:
            return ERROR("Incorrect argument types")
        
        # check if indices are valid
        s, i, j = args[0]["val"], args[1]["val"], args[2]["val"]
        if i < 0 or j > len(s) or i > j:
            return ERROR("Invalid indices")
        return makeStrVal(s[i:j])
    env.put("substring", makeBuiltInFuncVal("substring", substring))

    def isString(args):
        if len(args) != 1:
            return LibHelpers.requireExactArgs(args, 1)
        return makeBoolTrueVal() if args[0]["type"] == VAL_STR else makeBoolFalseVal()
    env.put("string?", makeBuiltInFuncVal("string?", isString))

    def charAt(args):
        if len(args) != 2:
            return LibHelpers.requireExactArgs(args, 2)
        if args[0]["type"] != VAL_STR or args[1]["type"] != VAL_INT:
            return ERROR("Incorrect argument types")
        s, i = args[0]["val"], args[1]["val"]
        if i < 0 or i >= len(s):
            return ERROR("Index out of bounds in `string-ref`")
        return makeCharVal(s[i])
    env.put("string-ref", makeBuiltInFuncVal("string-ref", charAt))

    def strEquals(args):
        if len(args) != 2:
            return LibHelpers.requireExactArgs(args, 2)
        if args[0]["type"] != VAL_STR or args[1]["type"] != VAL_STR:
            return ERROR("Incorrect argument types")
        return makeBoolTrueVal() if args[0]["val"] == args[1]["val"] else makeBoolFalseVal()
    env.put("string-equal?", makeBuiltInFuncVal("string-equal?", strEquals))

    # create string from list of characters given
    def makeString(args):
        if any(a["type"] != VAL_CHAR for a in args):
            return ERROR("Incorrect argument types")
        return makeStrVal("".join(a["val"] for a in args))
    env.put("string", makeBuiltInFuncVal("string", makeString))


def addVectorFuncs(env):
    """Add standard vector functions to the given environment"""
    def vector(args):
        return makeVectorVal(args)
    env.put("vector", makeBuiltInFuncVal("vector", vector))
    
    def vectorLen(args):
        if len(args) != 1:
            return LibHelpers.requireExactArgs(args, 1)
        if args[0]["type"] != VAL_VECTOR:
            return ERROR("Argument is not a Vec")
        return makeIntVal(len(args[0]["items"]))
    env.put("vector-length", makeBuiltInFuncVal("vector-length", vectorLen))

    def getAtIndex(args):
        if len(args) != 2:
            return LibHelpers.requireExactArgs(args, 2)
        if args[0]["type"] != VAL_VECTOR or args[1]["type"] != VAL_INT:
            return ERROR("Argument is not a Vec")
        vec, idx = args[0]["items"], args[1]["val"]
        if idx < 0 or idx >= len(vec):
            return ERROR("Index out of bounds")
        return vec[idx]
    env.put("vector-get", makeBuiltInFuncVal("vector-get", getAtIndex))

    def setAtIndex(args):
        if len(args) != 3:
            return LibHelpers.requireExactArgs(args, 3)

        if args[0]["type"] != VAL_VECTOR or args[1]["type"] != VAL_INT:
            return ERROR("Incorrect argument types")
        vec, idx, val = args[0]["items"], args[1]["val"], args[2]
        if idx < 0 or idx >= len(vec):
            return ERROR("Index out of bounds")
        vec[idx] = val
        return val
    env.put("vector-set!", makeBuiltInFuncVal("vector-set!", setAtIndex))

    def makeVector(args):
        if len(args) != 1:
            return LibHelpers.requireExactArgs(args, 1)
        if args[0]["type"] != VAL_INT:
            return ERROR("Incorrect argument type")
        return makeVectorVal([makeBoolFalseVal()] * args[0]["val"])
    env.put("make-vector", makeBuiltInFuncVal("make-vector", makeVector))

    def isVector(args):
        if len(args) != 1:
            return LibHelpers.requireExactArgs(args, 1)
        return makeBoolTrueVal() if args[0]["type"] == VAL_VECTOR else makeBoolFalseVal()
    env.put("vector?", makeBuiltInFuncVal("vector?", isVector))

