package edu.lehigh.cse262.slang.Interpreter;

import java.util.ArrayList;
import java.util.List;

import edu.lehigh.cse262.slang.Parser.INodeVisitor;
import edu.lehigh.cse262.slang.Parser.AstNodes;
import edu.lehigh.cse262.slang.Parser.AstNodes.Let.LetDef;
import edu.lehigh.cse262.slang.Parser.AstNodes.Cond.Condition;

/**
 * Interpreter implements the Visitor pattern to evaluate an AST node to produce
 * a value
 */
public class Interpreter implements INodeVisitor<Values.Value> {
    /** The environment in which to do the evaluation */
    private Env env;

    /** Construct an Interpreter by providing an environment */
    public Interpreter(Env env) {
        this.env = env;
    }

    /**
     * Interpret an `and` expression by interpreting its arguments and returning
     * the last one's value, unless any is BoolFalse.
     */
    @Override
    public Values.Value visitAnd(List<AstNodes.AstNode> expressions) throws INodeVisitorError {
        Values.Value result = new Values.BoolTrue();
        for (AstNodes.AstNode expr : expressions) {
            result = expr.interpret(this);
            if (result instanceof Values.BoolFalse) {
                return result;
            }
        }
        return result;
    }

    /**
     * Interpret an `or` expression by interpreting its arguments and returning
     * the first that's not BoolFalse. Otherwise returns BoolFalse.
     */
    @Override
    public Values.Value visitOr(List<AstNodes.AstNode> expressions) throws INodeVisitorError {
        for (AstNodes.AstNode expr : expressions) {
            Values.Value val = expr.interpret(this);
            if (!(val instanceof Values.BoolFalse)) {
                return val;
            }
        }
        return new Values.BoolFalse();
    }

    /**
     * Interpret a `begin` expression by interpreting its arguments and
     * returning the last one
     */
    @Override
    public Values.Value visitBegin(List<AstNodes.AstNode> expressions) throws INodeVisitorError {
        Values.Value result = new Values.BoolFalse();
        for (AstNodes.AstNode expr : expressions) {
            result = expr.interpret(this);
        }
        return result;
    }

    /** Interpret a BoolTrue expression by returning it, since it's a Datum */
    @Override
    public Values.Value visitBoolTrue(AstNodes.BoolTrue datum) throws INodeVisitorError {
        return new Values.BoolTrue();
    }

    /** Interpret a BoolFalse expression by returning it, since it's a Datum */
    @Override
    public Values.Value visitBoolFalse(AstNodes.BoolFalse datum) throws INodeVisitorError {
        return new Values.BoolFalse();
    }

    /** Interpret a Char expression by returning it, since it's a Datum */
    @Override
    public Values.Value visitChar(char val, AstNodes.Char datum) throws INodeVisitorError {
        return new Values.Char(val);
    }

    /** Interpret a Cons expression by returning it, since it's a Datum */
    @Override
    public Values.Value visitCons(AstNodes.Datum car, AstNodes.Datum cdr, AstNodes.Cons datum)
            throws INodeVisitorError {
        Values.Value carRes = car.interpret(this);
        Values.Value cdrRes = cdr.interpret(this);
        return Values.Cons.makeCons(carRes, cdrRes);
    }

    /** Interpret an EmptyCons expression by returning it, since it's a Datum */
    @Override
    public Values.Value visitEmptyCons(AstNodes.EmptyCons datum) throws INodeVisitorError {
        return new Values.EmptyCons();
    }

    /** Interpret an Int expression by returning it, since it's a Datum */
    @Override
    public Values.Value visitInt(int val, AstNodes.Int datum) throws INodeVisitorError {
        return new Values.Int(val);
    }

    /** Interpret a Dbl expression by returning it, since it's a Datum */
    @Override
    public Values.Value visitDbl(double val, AstNodes.Dbl datum) throws INodeVisitorError {
        return new Values.Dbl(val);
    }

    /** Interpret a Str expression by returning it, since it's a Datum */
    @Override
    public Values.Value visitStr(String val, AstNodes.Str datum) throws INodeVisitorError {
        return new Values.Str(val);
    }

    /** Interpret a Symbol expression by returning it, since it's a Datum */
    @Override
    public Values.Value visitSymbol(String name, AstNodes.Symbol datum) throws INodeVisitorError {
        return new Values.Symbol(name);
    }

    /** Interpret a Vec expression by returning it, since it's a Datum */
    @Override
    public Values.Value visitVec(AstNodes.Datum[] items, AstNodes.Vec datum) throws INodeVisitorError {
        List<Values.Value> vals = new ArrayList<>();
        for (AstNodes.Datum d : items) {
            vals.add(d.interpret(this));
        }
        return new Values.Vec(vals);
    }

    /**
     * Interpret a DefineVar AST node by setting something in the current
     * environment
     */
    @Override
    public Values.Value visitDefineVar(AstNodes.Identifier identifier, AstNodes.AstNode expression)
            throws INodeVisitorError {
        Values.Value val = expression.interpret(this);
        env.put(identifier.name, val);
        return val;
    }

    @Override
    public Values.Value visitDefineFunc(List<AstNodes.Identifier> ids, List<AstNodes.AstNode> body)
            throws INodeVisitorError {
        String key = ids.get(0).name;
        List<AstNodes.Identifier> formals = ids.subList(1, ids.size());
        AstNodes.LambdaDef lambdaDef = new AstNodes.LambdaDef(formals, body);
        Values.LambdaVal val = new Values.LambdaVal(env, lambdaDef);
        env.put(key, val);
        return val;
    }

    /** Interpret an identifier AST node by looking it up in the environment */
    @Override
    public Values.Value visitIdentifier(String name) throws INodeVisitorError {
        return env.get(name);
    }

    /** Interpret an if expression. Make sure to only evaluate the taken branch */
    @Override
    public Values.Value visitIf(AstNodes.AstNode test, AstNodes.AstNode ifTrue, AstNodes.AstNode ifFalse)
            throws INodeVisitorError {
        Values.Value cond = test.interpret(this);
        if (cond instanceof Values.BoolFalse) {
            return ifFalse.interpret(this);
        } else {
            return ifTrue.interpret(this);
        }
    }

    /**
     * Interpret a lambda definition by creating a lambda Value from it that
     * captures the current environment
     */
    @Override
    public Values.Value visitLambdaDef(List<AstNodes.Identifier> formals, List<AstNodes.AstNode> body,
            AstNodes.LambdaDef lambdaDef) throws INodeVisitorError {
        return new Values.LambdaVal(env, lambdaDef);
    }

    /** Interpret the `quote` special form */
    @Override
    public Values.Value visitQuote(AstNodes.Datum datum) throws INodeVisitorError {
        return datum.interpret(this);
    }

    /**
     * Visit a set! AST node by updating the environment with the result of
     * evaluating its expression portion
     */
    @Override
    public Values.Value visitSet(AstNodes.Identifier identifier, AstNodes.AstNode expression) throws INodeVisitorError {
        Values.Value expr = expression.interpret(this);
        env.update(identifier.name, expr);
        return expr;
    }

    /** Visit a tick by returning its datum as a value */
    @Override
    public Values.Value visitTick(AstNodes.Datum datum) throws INodeVisitorError {
        return datum.interpret(this);
    }

    /**
     * Evaluate a `cond` by testing one condition at a time. Make sure not to
     * evaluate arms that aren't taken.
     */
    @Override
    public Values.Value visitCond(List<AstNodes.Cond.Condition> conditions) throws INodeVisitorError {
        for (Condition cond : conditions) {
            Values.Value testVal = cond.test.interpret(this);
            if (!(testVal instanceof Values.BoolFalse)) {
                Values.Value res = new Values.BoolFalse();
                for (AstNodes.AstNode expr : cond.exprs) {
                    res = expr.interpret(this);
                }
                return res;
            }
        }
        return new Values.BoolFalse();
    }

    @Override
    public Values.Value visitLet(List<AstNodes.Let.LetDef> vars, List<AstNodes.AstNode> body) throws INodeVisitorError {
        // save current env so we can restore it later
        Env old = this.env;
        // create a new env
        Env inner = Env.makeInner(old);
        
        // Evaluate old env
        List<Values.Value> vals = new ArrayList<>();
        for (LetDef d : vars) {
            vals.add(d.val.interpret(this));
        }
        
        // create new env using evaluated vals
        for (int i = 0; i < vars.size(); i++) {
            inner.put(vars.get(i).id.name, vals.get(i));
        }
        
        // change env to the new env and the eval it
        this.env = inner;
        Values.Value res = new Values.BoolFalse();
        for (AstNodes.AstNode expr : body) {
            res = expr.interpret(this);
        }
        
        // restore old env
        this.env = old;
        return res;
    }

    @Override
    public Values.Value visitApply(AstNodes.AstNode func, AstNodes.AstNode args) throws INodeVisitorError {
        // evaluate the function
        Values.Value f = func.interpret(this);
        
        // eval args
        Values.Value listV = args.interpret(this);
        
        // extract arguments
        List<Values.Value> argList = new ArrayList<>();
        Values.Value cur = listV;
        while (cur instanceof Values.Cons) {
            Values.Cons c = (Values.Cons) cur;
            argList.add(c.car);
            cur = c.cdr;
        }


        // if its already a built in function we can just execute it
        if (f instanceof Values.BuiltInFunc) {
            return ((Values.BuiltInFunc) f).func.execute(argList);
        } 
        // if its a lambda function we need to create a new env and bind 
        if (f instanceof Values.LambdaVal) {
            Values.LambdaVal lam = (Values.LambdaVal) f;
            Env old = this.env;
            Env inner = Env.makeInner(lam.env);
            // bind formals
            List<AstNodes.Identifier> formals = lam.lambdaDef.formals;
            if (formals.size() != argList.size())
                throw new INodeVisitorError("Incorrect number of arguments");
            for (int i = 0; i < formals.size(); i++) {
                inner.put(formals.get(i).name, argList.get(i));
            }

            // eval lambda body
            this.env = inner;
            Values.Value res = new Values.BoolFalse();
            for (AstNodes.AstNode expr : lam.lambdaDef.exprs) {
                res = expr.interpret(this);
            }
            // restore old env
            this.env = old;
            return res;
        }
        
        throw new INodeVisitorError("Error!");    
    }

    /**
     * Visit an application expression by executing the first argument as a
     * function, with subsequent arguments as parameters
     *
     * NB: to match GSI, we evaluate the arguments before determining if the first
     * is a function
     */
    @Override
    public Values.Value visitCall(List<AstNodes.AstNode> expressions) throws INodeVisitorError {
        Values.Value f = expressions.get(0).interpret(this);
        List<Values.Value> args = new ArrayList<>();
        for (int i = 1; i < expressions.size(); i++) {
            args.add(expressions.get(i).interpret(this));
        }

        // built in function
        if (f instanceof Values.BuiltInFunc) {
            return ((Values.BuiltInFunc) f).func.execute(args);
        } 
        // lambda function
        if (f instanceof Values.LambdaVal) {
            Values.LambdaVal lam = (Values.LambdaVal) f;
            Env old = this.env;
            Env inner = Env.makeInner(lam.env);
            
            // bind parameters
            List<AstNodes.Identifier> formals = lam.lambdaDef.formals;
            if (formals.size() != args.size())
                throw new INodeVisitorError("Incorrect number of arguments");
            for (int i = 0; i < formals.size(); i++) {
                inner.put(formals.get(i).name, args.get(i));
            }

            // eval body
            this.env = inner;
            Values.Value res = new Values.BoolFalse();
            for (AstNodes.AstNode expr : lam.lambdaDef.exprs) {
                res = expr.interpret(this);
            }
            
            // restore
            this.env = old;
            return res;
        } 
        throw new INodeVisitorError("Error!");
    }
}
