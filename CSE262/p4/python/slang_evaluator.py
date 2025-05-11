from astnodes import *
import astnodes
import slang_env
from values import *


def interpret_ast(in_expr, in_env):
    """Interpret an AstNode in the context of an Environment"""
    tag = in_expr["type"]
    if tag == AST_ERR:
        return makeErrorVal(in_expr["msg"])
    
    if tag == AST_BOOL_TRUE:
        return makeBoolTrueVal()
    if tag == AST_BOOL_FALSE:
        return makeBoolFalseVal()
    if tag == AST_INT:
        return makeIntVal(in_expr["val"])
    if tag == AST_DBL:
        return makeDblVal(in_expr["val"])
    if tag == AST_STR:
        return makeStrVal(in_expr["val"])
    if tag == AST_SYMBOL:
        return makeSymbolVal(in_expr["val"])
    if tag == AST_CHAR:
        return makeCharVal(in_expr["val"])
    if tag == AST_EMPTY_CONS:
        return makeEmptyVal()
    if tag == AST_CONS:
        car_val = interpret_ast(in_expr["car"], in_env)
        cdr_val = interpret_ast(in_expr["cdr"], in_env)
        return makeConsVal(car_val, cdr_val)
    if tag == AST_VEC:
        items = [interpret_ast(item, in_env) for item in in_expr.get("items", [])]
        return makeVectorVal(items)
    
    if tag == AST_AND:
        result = makeBoolTrueVal()
        for expr in in_expr["exprs"]:
            result = interpret_ast(expr, in_env)
            if result["type"] == VAL_FALSE:
                return result
        return result
    
    if tag == AST_OR:
        for expr in in_expr["exprs"]:
            result = interpret_ast(expr, in_env)
            if result["type"] != VAL_FALSE:
                return result
        return makeBoolFalseVal()
    
    if tag == AST_BEGIN:
        result = makeBoolFalseVal()
        for expr in in_expr["exprs"]:
            result = interpret_ast(expr, in_env)
        return result
    
    if tag == AST_IF:
        test_val = interpret_ast(in_expr["cond"], in_env)
        if test_val["type"] == VAL_FALSE:
            return interpret_ast(in_expr["if_false"], in_env)
        return interpret_ast(in_expr["if_true"], in_env)
    
    if tag == AST_COND:
        for cond in in_expr["conditions"]:
            test_val = interpret_ast(cond.get("test"), in_env)
            if test_val["type"] != VAL_FALSE:
                res = makeBoolFalseVal()
                for expr in cond.get("exprs", []):
                    res = interpret_ast(expr, in_env)
                return res
        return makeBoolFalseVal()
    
    if tag == AST_QUOTE or tag == AST_TICK:
        return interpret_ast(in_expr.get("datum"), in_env)

    if tag == AST_DEFINE_VAR:
        name = in_expr.get("id").get("id")
        val = interpret_ast(in_expr.get("expr"), in_env)
        in_env.put(name, val)
        return val

    if tag == AST_DEFINE_FUNC:
        ids = in_expr.get("ids", [])
        name = ids[0].get("id")
        formals = [ident.get("id") for ident in ids[1:]]
        body = in_expr.get("body", [])
        lam_def = {"formals": formals, "exprs": body}
        val = makeLambdaVal(in_env, lam_def)
        in_env.put(name, val)
        return val

    if tag == AST_LAMBDA_DEF:
        formals = [ident.get("id") for ident in in_expr.get("formals", [])]
        body = in_expr.get("exprs", [])
        lam_def = {"formals": formals, "exprs": body}
        return makeLambdaVal(in_env, lam_def)

    if tag == AST_IDENTIFIER:
        name = in_expr.get("id")
        return in_env.get(name)

    if tag == AST_SET:
        name = in_expr.get("id").get("id")
        val = interpret_ast(in_expr.get("expr"), in_env)
        in_env.update(name, val)
        return val

    if tag == AST_LET:
        inner = slang_env.makeInnerEnv(in_env)
        # eval bindings in outer env
        for binding in in_expr.get("vars", []):
            bval = interpret_ast(binding.get("val"), in_env)
            inner.put(binding.get("id").get("id"), bval)
        # eval body in inner env
        res = makeBoolFalseVal()
        for expr in in_expr.get("body", []):
            res = interpret_ast(expr, inner)
        return res

    if tag == AST_APPLY:
        # eval the function
        val = interpret_ast(in_expr.get("func"), in_env)
        # eval args
        cur = interpret_ast(in_expr.get("args"), in_env)
        
        # extract the args
        arg_list = []
        while cur.get("type") == VAL_CONS:
            arg_list.append(cur.get("car"))
            cur = cur.get("cdr")
        # if its already a built in function we can just execute it
        if val.get("type") == VAL_BUILTIN:
            return val.get("func")(arg_list)
        # if its a lambda function we need to create a new env and bind 
        if val.get("type") == VAL_LAMBDA:
            lam_env = val.get("env")
            lam_def = val.get("func")
            inner = slang_env.makeInnerEnv(lam_env)
            
            # bind formals
            formals = lam_def.get("formals", [])
            if len(formals) != len(arg_list):
                return makeErrorVal("Incorrect number of arguments")
            for i, name in enumerate(formals):
                inner.put(name, arg_list[i])
            
            # eval lambda body
            res = makeBoolFalseVal()
            for expr in lam_def.get("exprs", []):
                res = interpret_ast(expr, inner)
            return res
        return makeErrorVal("Error!")

    # Function call with inline args
    if tag == AST_CALL:
        exprs = in_expr.get("exprs", [])
        val = interpret_ast(exprs[0], in_env)
        args = [interpret_ast(e, in_env) for e in exprs[1:]]
        
        # built in function
        if val.get("type") == VAL_BUILTIN:
            return val.get("func")(args)
        
        # lambda function
        if val.get("type") == VAL_LAMBDA:
            lam_env = val.get("env")
            lam_def = val.get("func")
            inner = slang_env.makeInnerEnv(lam_env)
            
            # bind parameters
            formals = lam_def.get("formals", [])
            if len(formals) != len(args):
                return makeErrorVal("Incorrect number of arguments")
            for i, name in enumerate(formals):
                inner.put(name, args[i])
            
            # eval lambda body
            res = makeBoolFalseVal()
            for expr in lam_def.get("exprs", []):
                res = interpret_ast(expr, inner)
            return res
        return makeErrorVal("undefined identifier x")

    # Unknown node type
    return makeErrorVal(f"Unknown AST node type: {t}")

def val_to_scheme(val):
    """Pretty print values
    * `val` -- The Value to print
    Returns a string representation of the AstNode"""
    if val["type"] == VAL_TRUE:
        return "#t"
    elif val["type"] == VAL_FALSE:
        return "#f"
    elif val["type"] == VAL_INT:
        return str(val["val"])
    elif val["type"] == VAL_DBL:
        return str(val["val"])
    elif val["type"] == VAL_STR:
        return val["val"]
    elif val["type"] == VAL_VECTOR:
        res = "#("
        for e in val["items"]:
            res = res + str(val_to_scheme(e)) + " "
        return res[: len(res) - 1] + ")"
    elif val["type"] == VAL_SYMBOL:
        return f"'{val['val']}"
    elif val["type"] == VAL_CHAR:
        if val["val"] == " ":
            return "#\\space"
        elif val["val"] == "\n":
            return "#\\newline"
        elif val["val"] == "\t":
            return "#\\tab"
        else:
            return f"#\\{val['val']}"
    elif val["type"] == VAL_BUILTIN:
        return f"Built-in Function: {val['name']}"
    elif val["type"] == VAL_LAMBDA:
        return "Lambda Function"
    elif val["type"] == VAL_EMPTY:
        return "()"
    elif val["type"] == VAL_ERROR:
        return val["msg"]
    elif val["type"] == VAL_CONS:
        l = val_to_scheme(val["car"])
        r = val_to_scheme(val["cdr"])
        return f"({l} . {r})"
