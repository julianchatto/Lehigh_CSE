# An enum for disambiguating the value types
(
    VAL_TRUE,
    VAL_FALSE,
    VAL_BUILTIN,
    VAL_CHAR,
    VAL_CONS,
    VAL_EMPTY,
    VAL_DBL,
    VAL_INT,
    VAL_LAMBDA,
    VAL_STR,
    VAL_SYMBOL,
    VAL_VECTOR,
    VAL_ERROR,
) = range(13)


def makeBuiltInFuncVal(name, func):
    """A built-in function consists of a name and a Python function object"""
    return {"type": VAL_BUILTIN, "name": name, "func": func}


def makeConsVal(car, cdr):
    """A Cons cell is just two items"""
    return {"type": VAL_CONS, "car": car, "cdr": cdr}


def makeLambdaVal(env, func):
    """A Lammbda *value* is just a binding of a LambdaDef to an environment"""
    return {"type": VAL_LAMBDA, "env": env, "func": func}


def makeVectorVal(items):
    """A Vector consists of an array of items"""
    return {"type": VAL_VECTOR, "items": items}


def makeBoolTrueVal():
    """A Bool(True) does not need a special value"""
    return {"type": VAL_TRUE}


def makeBoolFalseVal():
    """A Bool(False) does not need a special value"""
    return {"type": VAL_FALSE}


def makeEmptyVal():
    """An Empty List does not need a special value"""
    return {"type": VAL_EMPTY}


def makeCharVal(val):
    """A Char value just wraps a character"""
    return {"type": VAL_CHAR, "val": val}


def makeDblVal(val):
    """A Dbl value just wraps a float"""
    return {"type": VAL_DBL, "val": val}


def makeIntVal(val):
    """A Int value just wraps a character"""
    return {"type": VAL_INT, "val": val}


def makeStrVal(val):
    """A Str value just wraps a character"""
    return {"type": VAL_STR, "val": val}


def makeSymbolVal(val):
    """A Symbol value just wraps a character"""
    return {"type": VAL_SYMBOL, "val": val}


def makeErrorVal(msg):
    """An Error value just wraps an error message"""
    return {"type": VAL_ERROR, "msg": msg}


def makeList(items):
    """Construct `cons` nodes from a list."""
    if len(items) == 1:
        return makeConsVal(items[0], makeEmptyVal())
    else:
        return makeConsVal(items[0], makeList(items[1:]))
