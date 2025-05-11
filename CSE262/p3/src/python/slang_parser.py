from astnodes import *
from slang_scanner import *
from slang_scanner_xml import tagTokenMap 

# ParseError class, stores error message
class ParserError(Exception):
    """Custom exception for parser errors."""
    def __init__(self, message):
        super().__init__(message)
        self.message = message
        self.type = "ParserError"
    
# class Constructor 
# initialize parser with list of tokens
class Parser:
    def __init__(self, tokens):
        self.tokens = tokens
        self.pos = 0 # keep track of current position in token stream
    
    #returns current token or "None" if at end         
    def current(self):
        if self.pos < len(self.tokens):
            return self.tokens[self.pos]
        return None
    
    # return true if tokens left to parse 
    def has_next(self):
        return self.pos < len(self.tokens)
    
    # consume and return current token 
    def pop(self, expected_type=None):
        if not self.has_next():
           raise ParserError("Unexpected end of input")
        
        token = self.current()
        
        if expected_type and self.type(token) != expected_type:
            type2 = "RPAREN" if expected_type == 19 else "LPAREN"
            raise ParserError(f"Expected {type2}")
        
        self.pos += 1
        return token
    
    # extract type from token dictionary 
    def type(self, token):
        return token["type"]
    
    # core method of parser, handle each type accordingly 
    def parse_expression(self):
        token = self.current()
        if self.type(token) == TOK_ABBREV:
            self.pop(TOK_ABBREV)
            return self.parse_datum()
        if self.type(token) == TOK_BOOL:
            self.pop(TOK_BOOL)
            return makeBoolTrueNode() if token["literal"] else makeBoolFalseNode() 
        elif self.type(token) == TOK_INT:
            self.pop(TOK_INT)
            return makeIntNode(token["literal"])
        elif self.type(token) == TOK_DBL:
            self.pop(TOK_DBL)
            return makeDblNode(token["literal"])
        elif self.type(token) == TOK_STR:
            self.pop(TOK_STR)
            return makeStrNode(token["literal"])
        elif self.type(token) == TOK_CHAR:
            self.pop(TOK_CHAR)
            return makeCharNode(token["literal"])
        
        if self.type(token) == TOK_IDENTIFIER:
            self.pop(TOK_IDENTIFIER)
            return makeIdentifierNode(token["literal"])
        
        if not self.type(token) == TOK_LEFT_PAREN:
           raise ParserError(f"Error extracting constant")

        self.pop(TOK_LEFT_PAREN)
        token = self.current()
        
        if self.type(token) == TOK_DEFINE: # used for functions and variables 
            self.pop(TOK_DEFINE)
            token = self.current()
            if (self.type(token) == TOK_LEFT_PAREN):
                self.pop(TOK_LEFT_PAREN)
                token = self.current()
                
                # identifiers 
                ids = []
                while not self.type(token) == TOK_RIGHT_PAREN:
                    if self.type(token) != TOK_IDENTIFIER:
                       raise ParserError("invalid identifier")
                    ids.append(makeIdentifierNode(self.pop(TOK_IDENTIFIER)["literal"]))
                    token = self.current()
                
                self.pop(TOK_RIGHT_PAREN)
                
                # expressions
                body = []
                token = self.current()
                while not self.type(token) == TOK_RIGHT_PAREN:
                    body.append(self.parse_expression())
                    token = self.current()
                    
                if len(body) == 0:
                   raise ParserError("Error extracting constant")
                self.pop(TOK_RIGHT_PAREN)
                return makeDefineFuncNode(ids, body)
            else: # define var
                if not self.type(token) == TOK_IDENTIFIER:
                   raise ParserError("invalid identifier")
                 
                id = makeIdentifierNode(self.pop(TOK_IDENTIFIER)["literal"])
                
                expr = self.parse_expression()
                
                self.pop(TOK_RIGHT_PAREN)
                return makeDefineVarNode(id, expr)
     
        elif self.type(token) == TOK_QUOTE:
            self.pop(TOK_QUOTE)
            
            datum = self.parse_datum()
            self.pop(TOK_RIGHT_PAREN)
            return makeQuoteNode(datum)
     
        # lambdas
        elif self.type(token) == TOK_LAMBDA:
            self.pop(TOK_LAMBDA)
            self.pop(TOK_LEFT_PAREN)
            
            formals = []
            token = self.current()
            while not self.type(token) == TOK_RIGHT_PAREN:
                if self.type(token) != TOK_IDENTIFIER:
                   raise ParserError("invalid identifier")
                formals.append(makeIdentifierNode(self.pop(TOK_IDENTIFIER)["literal"]))
                token = self.current()
                
            self.pop(TOK_RIGHT_PAREN)
            
            body = []
            token = self.current()
            
            # while loop until end of lambda reached 
            while not self.type(token) == TOK_RIGHT_PAREN:
                body.append(self.parse_expression())
                token = self.current()
                
            if len(body) == 0:
               raise ParserError("Error extracting constant")
            
            self.pop(TOK_RIGHT_PAREN)
            return makeLambdaDefNode(formals, body)
       
        elif self.type(token) == TOK_IF:
            self.pop(TOK_IF)
            test = self.parse_expression()
            ifTrue = self.parse_expression()
            ifFalse = self.parse_expression()
            
            self.pop(TOK_RIGHT_PAREN)
            return makeIfNode(test, ifTrue, ifFalse)
        
        elif self.type(token) == TOK_SET:
            self.pop(TOK_SET)
            token = self.current()
            if not self.type(token) == TOK_IDENTIFIER:
               raise ParserError("invalid identifier")
            
            id = makeIdentifierNode(self.pop(TOK_IDENTIFIER)["literal"])
            expr = self.parse_expression()
            self.pop(TOK_RIGHT_PAREN)
            return makeSetNode(id, expr)
        
        elif self.type(token) == TOK_AND:
            self.pop(TOK_AND)
            
            expressions = []
            token = self.current()
            while not self.type(token) == TOK_RIGHT_PAREN:
                expressions.append(self.parse_expression())
                token = self.current()
                
            self.pop(TOK_RIGHT_PAREN)
            return makeAndNode(expressions)
        
        elif self.type(token) == TOK_OR:
            self.pop(TOK_OR)
        
            expressions = []
            token = self.current()
            while not self.type(token) == TOK_RIGHT_PAREN:
                expressions.append(self.parse_expression())
                token = self.current()
                
            self.pop(TOK_RIGHT_PAREN)
            return makeOrNode(expressions)
        
        elif self.type(token) == TOK_BEGIN:
            self.pop(TOK_BEGIN)
            
            expressions = []
            token = self.current()
            while not self.type(token) == TOK_RIGHT_PAREN:
                expressions.append(self.parse_expression())
                token = self.current()
                
            if len(expressions) == 0:
               raise ParserError("Error extracting constant")
            self.pop(TOK_RIGHT_PAREN)
            return makeBeginNode(expressions)
        
        elif self.type(token) == TOK_COND:
            self.pop(TOK_COND)
            
            conditions = []
            token = self.current()
            while not self.type(token) == TOK_RIGHT_PAREN:
                self.pop(TOK_LEFT_PAREN)
                test = self.parse_expression()
                expressions = []
                token = self.current()
                while not self.type(token) == TOK_RIGHT_PAREN:
                    expressions.append(self.parse_expression())
                    token = self.current()
                
                self.pop(TOK_RIGHT_PAREN)
                conditions.append(makeCondition(test, expressions))
                token = self.current()
                
            self.pop(TOK_RIGHT_PAREN)
            return makeCondNode(conditions)
        
        elif self.type(token) == TOK_APPLY:
            self.pop(TOK_APPLY)
            
            func = self.parse_expression()
            args = self.parse_expression()
            
            self.pop(TOK_RIGHT_PAREN)
            return makeApplyNode(func, args)
       
        elif self.type(token) == TOK_LET:
            self.pop(TOK_LET)
            self.pop(TOK_LEFT_PAREN)
            
            vars = []
            token = self.current()
            while not self.type(token) == TOK_RIGHT_PAREN:
                self.pop(TOK_LEFT_PAREN)
                token = self.current()
                if not self.type(token) == TOK_IDENTIFIER:
                   raise ParserError("invalid identifier")
                id = makeIdentifierNode(self.pop(TOK_IDENTIFIER)["literal"])
                
                val = self.parse_expression()
                self.pop(TOK_RIGHT_PAREN)
                vars.append(makeLetDef(id, val))
                token = self.current()
            if len(vars) == 0:
               raise ParserError("Expected LPAREN")
            
            self.pop(TOK_RIGHT_PAREN)
            
            # expressions
            body = []
            token = self.current()
            while not self.type(token) == TOK_RIGHT_PAREN:
                body.append(self.parse_expression())
                token = self.current()
                
            if len(body) == 0:
               raise ParserError("Error extracting constant")
            
            self.pop(TOK_RIGHT_PAREN)
            return makeLetNode(vars, body)
        
        else: #call
            # build calls
            expressions = []
            token = self.current()
            while not self.type(token) == TOK_RIGHT_PAREN:
                expressions.append(self.parse_expression())
                token = self.current()
                
            if len(expressions) == 0:
               raise ParserError("Error extracting constant")
            
            self.pop(TOK_RIGHT_PAREN)
            return makeCallNode(expressions)
               
    # parses data, for quoted data (lists, etc.)
    def parse_datum(self):
        token = self.current()
        if self.type(token) == TOK_BOOL:
            return makeBoolTrueNode() if self.pop()["literal"] else makeBoolFalseNode()
        elif self.type(token) == TOK_INT:
            return makeIntNode(self.pop()["literal"])
        elif self.type(token) == TOK_DBL:
            return makeDblNode(self.pop()["literal"])
        elif self.type(token) == TOK_STR:
            return makeStrNode(self.pop()["literal"])
        elif self.type(token) == TOK_CHAR:
            return makeCharNode(self.pop()["literal"])
        elif self.type(token) == TOK_IDENTIFIER:
            return makeSymbolNode(self.pop()["literal"])
        elif self.type(token) == TOK_LEFT_PAREN:
            self.pop(TOK_LEFT_PAREN)
            
            # check for empty list
            token = self.current()
            if self.type(token) == TOK_RIGHT_PAREN:
                self.pop(TOK_RIGHT_PAREN)
                return makeEmptyConsNode()
            
            items = []
            # collect datums until we hit a dot or the closing parenthesis
            while not self.type(token) == TOK_RIGHT_PAREN and not self.type(token) == TOK_DOT:
                items.append(self.parse_datum())
                token = self.current()
            
            # cons
            if self.type(token) == TOK_DOT:
                self.pop(TOK_DOT)
                
                if len(items) != 1:
                   raise ParserError("Invalid cons syntax in datum: expected exactly one element before dot")
                
                cdr = self.parse_datum()
                self.pop(TOK_RIGHT_PAREN)
                return makeConsNode(items[0], cdr)
            else: 
                while not self.type(token) == TOK_RIGHT_PAREN:
                    items.append(self.parse_datum())
                    token = self.current()
                
                self.pop(TOK_RIGHT_PAREN)
                return make_cons(items)
        elif self.type(token) == TOK_VECTOR:
            self.pop(TOK_VECTOR)
            
            items = []
            
            token = self.current()
            while not self.type(token) == TOK_RIGHT_PAREN:
                items.append(self.parse_datum())
                token = self.current()
                
            self.pop(TOK_RIGHT_PAREN)
            return makeVecNode(items)
        else:
           raise ParserError("Unrecognized datum")
        
    
    
# takes token list and repeatedly called parse_expression() until EOF reached 
def parse_program(stream):
    """Transform a TokenStream into a forest of AstNodes.  It is assumed that the
    TokenStream has an extra EOF at the end.  This is really just a single
    transition: <program> --> <expression>*.
    In terms of implementation, this is a recursive descent parser.  Scheme's
    syntax makes the whole affair quite easy."""
    res = []

    parser = Parser(stream)
    
    while parser.has_next() and parser.current()["type"] != TOK_EOF:
        try: # make list of parsed AST nodes 
            res.append(parser.parse_expression())
        except Exception as e:
            res.append(makeErrNode(e.args[0]))
            break

    return res # return list of parsed AST nodes 
