# An enum for disambiguating the ast node types
(
    AST_AND,
    AST_APPLY,
    AST_CALL,
    AST_BEGIN,
    AST_COND,
    AST_DEFINE_VAR,
    AST_DEFINE_FUNC,
    AST_IDENTIFIER,
    AST_IF,
    AST_LAMBDA_DEF,
    AST_LET,
    AST_OR,
    AST_QUOTE,
    AST_SET,
    AST_TICK,
    AST_BOOL_TRUE,
    AST_BOOL_FALSE,
    AST_CHAR,
    AST_CONS,
    AST_EMPTY_CONS,
    AST_DBL,
    AST_INT,
    AST_STR,
    AST_SYMBOL,
    AST_VEC,
    AST_ERR,
) = range(26)


def makeBoolTrueNode():
    """Create a dictionary to represent a Bool True datum"""
    return {"type": AST_BOOL_TRUE}


def makeBoolFalseNode():
    """Create a dictionary to represent a Bool False datum"""
    return {"type": AST_BOOL_FALSE}


def makeCharNode(val):
    """Create a dictionary to represent a Cons datum"""
    return {"type": AST_CHAR, "val": val}


def makeConsNode(car, cdr):
    """Create a dictionary to represent a Cons datum"""
    return {"type": AST_CONS, "car": car, "cdr": cdr}


def makeEmptyConsNode():
    """Create a dictionary to represent an Empty Cons datum"""
    return {"type": AST_EMPTY_CONS}


def makeDblNode(val):
    """Create a dictionary to represent a Dbl datum"""
    return {"type": AST_DBL, "val": val}


def makeIntNode(val):
    """Create a dictionary to represent an Int datum"""
    return {"type": AST_INT, "val": val}


def makeStrNode(val):
    """Create a dictionary to represent a Str datum"""
    return {"type": AST_STR, "val": val}


def makeSymbolNode(val):
    """Create a dictionary to represent a Symbol datum"""
    return {"type": AST_SYMBOL, "val": val}


def makeVecNode(items):
    """Create a dictionary to represent a Vec datum"""
    return {"type": AST_VEC, "items": items}


def makeAndNode(exprs):
    """Create a dictionary to represent an And node"""
    return {"type": AST_AND, "exprs": exprs}


def makeApplyNode(func, args):
    """Create a dictionary to represent an Apply node"""
    return {"type": AST_APPLY, "func": func, "args": args}


def makeCallNode(exprs):
    """Create a dictionary to represent a Call node"""
    return {"type": AST_CALL, "exprs": exprs}


def makeBeginNode(exprs):
    """Create a dictionary to represent a Begin node"""
    return {"type": AST_BEGIN, "exprs": exprs}


def makeCondition(test, exprs):
    """Create a dictionary to represent a condition (not an AST node!)"""
    # NB: Dummy type, to simplify the parser
    return {"type": "", "test": test, "exprs": exprs}


def makeCondNode(conditions):
    """Create a dictionary to represent a Cond node"""
    return {"type": AST_COND, "conditions": conditions}


def makeDefineVarNode(id, expr):
    """Create a dictionary to represent a DefineVar node"""
    return {"type": AST_DEFINE_VAR, "id": id, "expr": expr}


def makeDefineFuncNode(ids, body):
    """Create a dictionary to represent a DefineFunc node"""
    return {"type": AST_DEFINE_FUNC, "ids": ids, "body": body}


def makeIdentifierNode(id):
    """Create a dictionary to represent an Identifier node"""
    return {"type": AST_IDENTIFIER, "id": id}


def makeIfNode(cond, if_true, if_false):
    """Create a dictionary to represent an If node"""
    return {"type": AST_IF, "cond": cond, "if_true": if_true, "if_false": if_false}


def makeLambdaDefNode(formals, exprs):
    """Create a dictionary to represent a LambdaDef node"""
    return {"type": AST_LAMBDA_DEF, "formals": formals, "exprs": exprs}


def makeLetDef(id, val):
    """Create a dictionary to represent a let binding (not an AST node!)"""
    # NB: Dummy type, to simplify the parser
    return {"type": "", "id": id, "val": val}


def makeLetNode(vars, body):
    """Create a dictionary to represent a Let node"""
    return {"type": AST_LET, "vars": vars, "body": body}


def makeOrNode(exprs):
    """Create a dictionary to represent an Or node"""
    return {"type": AST_OR, "exprs": exprs}


def makeQuoteNode(datum):
    """Create a dictionary to represent a Quote node"""
    return {"type": AST_QUOTE, "datum": datum}


def makeSetNode(id, expr):
    """Create a dictionary to represent a Set node"""
    return {"type": AST_SET, "id": id, "expr": expr}


def makeTickNode(datum):
    """Create a dictionary to represent a Tick node"""
    return {"type": AST_TICK, "datum": datum}


def makeErrNode(msg):
    """Create a dictionary to represent an error message"""
    return {"type": AST_ERR, "msg": msg}


def make_cons(items):
    """Construct a `cons` node from a list. This can produce a linked list
    of Cons cells."""
    if len(items) == 0:
        return makeErrNode("Cannot construct Cons from empty list")
    elif len(items) == 1:
        return makeConsNode(items[0], makeEmptyConsNode())
    else:
        tail = make_cons(items[1:])
        if tail["type"] == AST_ERR:
            return tail
        return makeConsNode(items[0], tail)
