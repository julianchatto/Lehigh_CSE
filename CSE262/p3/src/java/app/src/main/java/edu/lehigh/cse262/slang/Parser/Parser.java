package edu.lehigh.cse262.slang.Parser;

import java.util.ArrayList;
import java.util.List;

import edu.lehigh.cse262.slang.Scanner.TokenStream;
import edu.lehigh.cse262.slang.Scanner.Tokens;

/**
 * Parser is the second step in our interpreter. It is responsible for turning a
 * sequence of tokens into an abstract syntax tree.
 */
public class Parser {
    /**
     * An exception class for indicating that the parser encountered an error
     */
    public static class ParseError extends Exception {
        /** Construct a ParseError and attach a message to it */
        public ParseError(String msg) {
            super(msg);
        }
    }
    
    private TokenStream tokens;

    /**
     * Transform a stream of tokens into a forest of AstNodes. It is assumed
     * that the TokenStream has an extra EOF at the end. This is really just a
     * single transition: <program> --> <expression>*
     *
     * @param tokens a stream of tokens
     *
     * @return A list of AstNodes
     */
    public List<AstNodes.AstNode> parse(TokenStream tokens) throws ParseError {
        List<AstNodes.AstNode> expressions = new ArrayList<>();
        this.tokens = tokens;
        // Parse expressions until we hit the EOF token.
        while (tokens.hasNext() && !(tokens.nextToken() instanceof Tokens.Eof)) {
            expressions.add(parseExpression());
        }
        return expressions;
    }

    // parse single expression from token stream into an AstNode
    private AstNodes.AstNode parseExpression() throws ParseError {
        Tokens.Token token = tokens.nextToken();

        //  abbreviation (tick shorthand)
        if (token instanceof Tokens.Abbrev) {
            tokens.popToken(Tokens.Abbrev.class);
            return parseDatum();
        } 
        
        // Constants
        if (token instanceof Tokens.Bool) { 
            Tokens.Bool boolToken = (Tokens.Bool) token;
            tokens.popAny();
            return boolToken.value ? new AstNodes.BoolTrue() : new AstNodes.BoolFalse();
        } else if (token instanceof Tokens.Int) {
            Tokens.Int intToken = (Tokens.Int) token;
            tokens.popAny();
            return new AstNodes.Int(intToken.value);
        } else if (token instanceof Tokens.Dbl) {
            Tokens.Dbl dblToken = (Tokens.Dbl) token;
            tokens.popAny();
            return new AstNodes.Dbl(dblToken.value);
        } else if (token instanceof Tokens.Str) {
            Tokens.Str strToken = (Tokens.Str) token;
            tokens.popAny();
            return new AstNodes.Str(strToken.value);
        } else if (token instanceof Tokens.Char) {
            Tokens.Char charToken = (Tokens.Char) token;
            tokens.popAny();
            return new AstNodes.Char(charToken.value);
        } 
        
        // identifiers
        if (token instanceof Tokens.Identifier) {
            Tokens.Identifier idToken = (Tokens.Identifier) token;
            tokens.popAny();
            return new AstNodes.Identifier(idToken.tokenText);
        }
        
        // Parenthesized expressions
        if (!(token instanceof Tokens.LeftParen)) {
           throw new ParseError("Error extracting constant");
        } 

        tokens.popToken(Tokens.LeftParen.class);
        Tokens.Token next = tokens.nextToken();
        
        if (next instanceof Tokens.Define) {
            tokens.popToken(Tokens.Define.class);

            // define func
            if (tokens.nextToken() instanceof Tokens.LeftParen) {
                tokens.popToken(Tokens.LeftParen.class);

                // identifiers
                List<AstNodes.Identifier> ids = new ArrayList<>();
                while (!(tokens.nextToken() instanceof Tokens.RightParen)) {
                    if (!(tokens.nextToken() instanceof Tokens.Identifier))
                        throw new ParseError("invalid identifier");
                    
                    Tokens.Identifier idTok = (Tokens.Identifier) tokens.nextToken();
                    tokens.popAny();
                    
                    ids.add(new AstNodes.Identifier(idTok.tokenText));
                }
                
                tokens.popToken(Tokens.RightParen.class); // end of identifier list
                
                // expressions 
                List<AstNodes.AstNode> body = new ArrayList<>();
                while (!(tokens.nextToken() instanceof Tokens.RightParen)) {
                    body.add(parseExpression());
                }
                if (body.size() == 0) {
                    throw new ParseError("Error extracting constant");
                }
                tokens.popToken(Tokens.RightParen.class);
                return new AstNodes.DefineFunc(ids, body); 
            } else { // define var
                if (!(tokens.nextToken() instanceof Tokens.Identifier))
                    throw new ParseError("invalid identifier");
                
                Tokens.Identifier idTok = (Tokens.Identifier) tokens.nextToken();
                tokens.popAny();
                
                AstNodes.Identifier id = new AstNodes.Identifier(idTok.tokenText);
                
                // get the expression
                AstNodes.AstNode expr = parseExpression();
                
                tokens.popToken(Tokens.RightParen.class);
                return new AstNodes.DefineVar(id, expr);
            }
        } else if (next instanceof Tokens.Quote) { 
            tokens.popToken(Tokens.Quote.class);
            
            // get the datum
            AstNodes.Datum datum = parseDatum();
            tokens.popToken(Tokens.RightParen.class);
            
            return new AstNodes.Quote(datum);
        } else if (next instanceof Tokens.Lambda) {
            tokens.popToken(Tokens.Lambda.class);
            tokens.popToken(Tokens.LeftParen.class); // start formals
            
            List<AstNodes.Identifier> formals = new ArrayList<>();
            while (!(tokens.nextToken() instanceof Tokens.RightParen)) {
                if (!(tokens.nextToken() instanceof Tokens.Identifier)) {
                    throw new ParseError("invalid identifier");
                }
                
                Tokens.Identifier idTok = (Tokens.Identifier) tokens.nextToken();
                tokens.popAny();
                
                formals.add(new AstNodes.Identifier(idTok.tokenText));
            }
            
            tokens.popToken(Tokens.RightParen.class); // end formals
            
            // expressions
            List<AstNodes.AstNode> body = new ArrayList<>();
            while (!(tokens.nextToken() instanceof Tokens.RightParen)) {
                body.add(parseExpression());
            }
            if (body.size() == 0) {
                throw new ParseError("Error extracting constant");
            }
            
            tokens.popToken(Tokens.RightParen.class);
            return new AstNodes.LambdaDef(formals, body);
        } else if (next instanceof Tokens.If) { 
            tokens.popToken(Tokens.If.class);
            
            AstNodes.AstNode test = parseExpression();
            AstNodes.AstNode ifTrue = parseExpression();
            AstNodes.AstNode ifFalse = parseExpression();
            
            tokens.popToken(Tokens.RightParen.class);
            return new AstNodes.If(test, ifTrue, ifFalse);
        } else if (next instanceof Tokens.Set) {
            tokens.popToken(Tokens.Set.class);
            if (!(tokens.nextToken() instanceof Tokens.Identifier)) {
                throw new ParseError("invalid identifier");
            }
            
            Tokens.Identifier idTok = (Tokens.Identifier) tokens.nextToken();
            tokens.popAny();
            
            AstNodes.Identifier id = new AstNodes.Identifier(idTok.tokenText);
            
            // for expr
            AstNodes.AstNode expr = parseExpression();
            
            tokens.popToken(Tokens.RightParen.class);
            return new AstNodes.Set(id, expr);
        } else if (next instanceof Tokens.And) {
            tokens.popToken(Tokens.And.class);
            
            // build expressions
            List<AstNodes.AstNode> expressions = new ArrayList<>();
            while (!(tokens.nextToken() instanceof Tokens.RightParen)) {
                expressions.add(parseExpression());
            }
            
            tokens.popToken(Tokens.RightParen.class);
            return new AstNodes.And(expressions);
        } else if (next instanceof Tokens.Or) { 
            tokens.popToken(Tokens.Or.class);
            
            // build expressions
            List<AstNodes.AstNode> expressions = new ArrayList<>();
            while (!(tokens.nextToken() instanceof Tokens.RightParen)) {
                expressions.add(parseExpression());
            }
            
            tokens.popToken(Tokens.RightParen.class);
            return new AstNodes.Or(expressions);
        } else if (next instanceof Tokens.Begin) { 
            tokens.popToken(Tokens.Begin.class);
            
            // build expressions
            List<AstNodes.AstNode> expressions = new ArrayList<>();
            while (!(tokens.nextToken() instanceof Tokens.RightParen)) {
                expressions.add(parseExpression());
            }

            if (expressions.size() == 0) {
                throw new ParseError("Error extracting constant");
            }
            
            tokens.popToken(Tokens.RightParen.class);
            return new AstNodes.Begin(expressions);
        } else if (next instanceof Tokens.Cond) { 
            tokens.popToken(Tokens.Cond.class);
            
            // build conditions
            List<AstNodes.Cond.Condition> conditions = new ArrayList<>();
            while (!(tokens.nextToken() instanceof Tokens.RightParen)) {
                tokens.popToken(Tokens.LeftParen.class);
                // get testv 
                AstNodes.AstNode test = parseExpression();
                
                // get expressions
                List<AstNodes.AstNode> exprs = new ArrayList<>();
                while (!(tokens.nextToken() instanceof Tokens.RightParen)) {
                    exprs.add(parseExpression());
                }
                
                tokens.popToken(Tokens.RightParen.class);
                conditions.add(new AstNodes.Cond.Condition(test, exprs));
            }
            
            tokens.popToken(Tokens.RightParen.class);
            return new AstNodes.Cond(conditions);
        } else if (next instanceof Tokens.Apply) { 
            tokens.popToken(Tokens.Apply.class);

            AstNodes.AstNode func = parseExpression();
            AstNodes.AstNode args = parseExpression();
            
            tokens.popToken(Tokens.RightParen.class);
            return new AstNodes.Apply(func, args);
        } else if (next instanceof Tokens.Let) {
            tokens.popToken(Tokens.Let.class);
            tokens.popToken(Tokens.LeftParen.class); // begin let bindings

            // build LetDefs
            List<AstNodes.Let.LetDef> vars = new ArrayList<>();

            while (!(tokens.nextToken() instanceof Tokens.RightParen)) {
                tokens.popToken(Tokens.LeftParen.class);
                if (!(tokens.nextToken() instanceof Tokens.Identifier))
                    throw new ParseError("invalid identifier");
                // identifier
                Tokens.Identifier idTok = (Tokens.Identifier) tokens.nextToken();
                tokens.popAny();
                // expressions
                AstNodes.Identifier id = new AstNodes.Identifier(idTok.tokenText);
                AstNodes.AstNode val = parseExpression();
                
                tokens.popToken(Tokens.RightParen.class);
                vars.add(new AstNodes.Let.LetDef(id, val));
            }

            if (vars.isEmpty()) {
                throw new ParseError("Expected LPAREN");
            }

            tokens.popToken(Tokens.RightParen.class); // end of let binding list
            
            // expressions
            List<AstNodes.AstNode> body = new ArrayList<>();
            while (!(tokens.nextToken() instanceof Tokens.RightParen)) {
                body.add(parseExpression());
            }
            
            if (body.size() == 0) {
                throw new ParseError("Error extracting constant");
            }

            tokens.popToken(Tokens.RightParen.class);
            return new AstNodes.Let(vars, body);
        } else { // call
            
            // build calls
            List<AstNodes.AstNode> expressions = new ArrayList<>();
            while (!(tokens.nextToken() instanceof Tokens.RightParen)) {
                expressions.add(parseExpression());
            }

            if (expressions.size() == 0) {
                throw new ParseError("Error extracting constant");
            }
            
            tokens.popToken(Tokens.RightParen.class);
            return new AstNodes.Call(expressions);
        }
    }

    // parse quoted data (data structures like lists, vectors, etc)
    private AstNodes.Datum parseDatum() throws ParseError {
        Tokens.Token token = tokens.nextToken();
        // booleans: pop token and return true/false
        if (token instanceof Tokens.Bool) {
            Tokens.Bool boolToken = (Tokens.Bool) token;
            tokens.popAny();
            return boolToken.value ? new AstNodes.BoolTrue() : new AstNodes.BoolFalse();
        } else if (token instanceof Tokens.Int) { // numbers - extract value, pop token, wrap in appropriate AstNodes type
            Tokens.Int intToken = (Tokens.Int) token;
            tokens.popAny();
            return new AstNodes.Int(intToken.value);
        } else if (token instanceof Tokens.Dbl) {
            Tokens.Dbl dblToken = (Tokens.Dbl) token;
            tokens.popAny();
            return new AstNodes.Dbl(dblToken.value);
        } else if (token instanceof Tokens.Str) {
            Tokens.Str strToken = (Tokens.Str) token;
            tokens.popAny();
            return new AstNodes.Str(strToken.value);
        } else if (token instanceof Tokens.Char) {
            Tokens.Char charToken = (Tokens.Char) token;
            tokens.popAny();
            return new AstNodes.Char(charToken.value);
        } else if (token instanceof Tokens.Identifier) {
            Tokens.Identifier idToken = (Tokens.Identifier) token;
            tokens.popAny();
            return new AstNodes.Symbol(idToken.tokenText);
        } else if (token instanceof Tokens.LeftParen) { // lists and cons cells 
            tokens.popToken(Tokens.LeftParen.class); // begin parsing
            
            // Check for empty list
            if (tokens.nextToken() instanceof Tokens.RightParen) {
                tokens.popToken(Tokens.RightParen.class);
                return new AstNodes.EmptyCons();
            }
            

            List<AstNodes.Datum> items = new ArrayList<>();
            // collect datums until we hit a dot or the closing parenthesis
            while (!(tokens.nextToken() instanceof Tokens.RightParen) && !(tokens.nextToken() instanceof Tokens.Dot)) {
                items.add(parseDatum());
            }
            
            // cons
            if (tokens.nextToken() instanceof Tokens.Dot) {
                tokens.popToken(Tokens.Dot.class);
                if (items.size() != 1) {
                    throw new ParseError("Invalid cons syntax in datum: expected exactly one element before dot");
                }
                
                // get next datum
                AstNodes.Datum cdr = parseDatum();
                
                tokens.popToken(Tokens.RightParen.class);
            
                return AstNodes.Cons.makeCons(items.get(0), cdr);
            } else { // list
                while (!(tokens.nextToken() instanceof Tokens.RightParen)) {
                    items.add(parseDatum());
                }
                
                tokens.popToken(Tokens.RightParen.class);
                return AstNodes.Cons.makeConsList(items);
            }
        } else if (token instanceof Tokens.Vec) {
            tokens.popToken(Tokens.Vec.class);
            // build datums
            List<AstNodes.Datum> items = new ArrayList<>();
            while (!(tokens.nextToken() instanceof Tokens.RightParen)) {
                items.add(parseDatum());
            }
            
            tokens.popToken(Tokens.RightParen.class);
            return new AstNodes.Vec(items);
        } else {
            throw new ParseError("Unrecognized datum");
        }
    }
}