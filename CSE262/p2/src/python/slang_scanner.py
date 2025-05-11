from enum import Enum, auto

class Token(Enum):
    START = auto()
    ABBREV = auto()
    DOT = auto()
    CLEANBREAK = auto()
    LPAREN = auto()
    RPAREN = auto()
    EOFTOKEN = auto()
    INCOMMENT = auto()
    AND = auto()
    BEGIN = auto()
    COND = auto()
    DEFINE = auto()
    IF = auto()
    LAMBDA = auto()
    LET = auto()
    OR = auto()
    QUOTE = auto()
    SET = auto()
    APPLY = auto()
    VEC = auto()
    VCB = auto()
    CHAR = auto()
    PRECHAR = auto()
    BOOL = auto()
    INID = auto()
    PM = auto()
    IDENTIFIER = auto()
    ININT = auto()
    INT = auto()
    PREDBL = auto()
    INDBL = auto()
    DBL = auto()
    INSTR = auto()
    INSTRP = auto()
    STRING = auto()
    
(
    TOK_ABBREV,
    TOK_AND,
    TOK_APPLY,
    TOK_BEGIN,
    TOK_BOOL,
    TOK_CHAR,
    TOK_COND,
    TOK_DBL,
    TOK_DEFINE,
    TOK_DOT,
    TOK_EOF,
    TOK_IDENTIFIER,
    TOK_IF,
    TOK_INT,
    TOK_LAMBDA,
    TOK_LEFT_PAREN,
    TOK_LET,
    TOK_OR,
    TOK_QUOTE,
    TOK_RIGHT_PAREN,
    TOK_SET,
    TOK_STR,
    TOK_VECTOR,
    TOK_ERROR,
) = range(24)

def makeToken(text, line, col, type, lit):
    """A faux "constructor" for making a `dict` to represent a `token`
    - `text`  The source program characters that led to this token being made
    - `line`  The line within the source code where `text` appears
    - `col`   The column within `line` where `text` appears
    - `type`  The type of the token
    - `lit`   The literal (bool, int, number, str, or None)
    """
    return {"text": text, "line": line, "col": col, "type": type, "literal": lit}

def transition(fromState, toState, peekSet, invertPeek, consume, advance, maker):
    """A faux "constructor" for describing a transition within the scanner

    We transition from `fromState` to `toState` only if:
    - the next character is in `peekSet`; or
    - the next character is not in `peekSet` and `invertPeek` is true; or
    - `peekSet` is undefined

    Note that it is possible to consume without producing a Token (e.g., to
    clear out a comment)

    - `fromState`   The state we must be in for this rule to be valid
    - `toState`     The state to transition to
    - `peekSet`     Values to check against the next character
    - `invertPeek`  Is peek the set of invalid characters?
    - `consume`     Should this transition consume a token string
    - `advance`     Should the transition cause the next peek() to see a new character?
    - `maker`       A function that explains how to produce a value and token type
    """
    return {
        "fromState": fromState,
        "toState": toState,
        "peekSet": peekSet,
        "invertPeek": invertPeek,
        "consume": consume,
        "advance": advance,
        "maker": maker,
    }

ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
DIGITS = "0123456789"
SYMBOLS = "!$%&*/:<=>?~_^"
DOTPLUSMINUS = ".+-"
VALIDID = ALPHABET + SYMBOLS
VALIDINID = ALPHABET + DIGITS + SYMBOLS + DOTPLUSMINUS
INVALIDCHARS = "[]{}|`,|"

# All of the transitions in our scanner.  Order matters.
transitions = [
    transition(Token.START, Token.START, " \t\r\n", False, False, True, None),

    # Punctuation and comments
    transition(Token.START, Token.ABBREV, "'", False, False, True, 
               lambda text, line, col: makeToken(text, line, col, TOK_ABBREV, None)),
    transition(Token.START, Token.DOT, ".", False, True, True, None),

    transition(Token.ABBREV, Token.START, "", True, False, False, None),
    transition(Token.DOT, Token.CLEANBREAK, ALPHABET + DIGITS, False, False, False,
           lambda text, line, col: makeToken(text, line, col, TOK_ERROR, "Internal Error")),
    transition(Token.DOT, Token.CLEANBREAK, "", True, False, False,
           lambda text, line, col: makeToken(text, line, col, TOK_DOT, ".")),
    transition(Token.CLEANBREAK, Token.START, " \t\r\n", False, False, True, None),
    transition(Token.CLEANBREAK, Token.LPAREN, "(", False, False, True, 
               lambda text, line, col: makeToken(text, line, col, TOK_LEFT_PAREN, None)), 
    transition(Token.CLEANBREAK, Token.RPAREN, ")", False, True, True,                
               lambda text, line, col: makeToken(text, line, col, TOK_RIGHT_PAREN, None)), 
    transition(Token.LPAREN, Token.START, "", True, False, False, None), 
    transition(Token.RPAREN, Token.START, "", True, False, False, None), 
    transition(Token.CLEANBREAK, Token.EOFTOKEN, "\0", False, True, True, None), 
    transition(Token.CLEANBREAK, Token.INCOMMENT, ";", False, False, True, None), 
    transition(Token.INCOMMENT, Token.INCOMMENT, "\0", True, True, True, None),
    transition(Token.INCOMMENT, Token.START, "", False, False, False, None),
    transition(Token.INCOMMENT, Token.EOFTOKEN, "\0", False, True, True, None),
    
    # Vector, char, and bool
    transition(Token.START, Token.VCB, "#", False, False, True, None),
    transition(Token.VCB, Token.VEC, "(", False, True, True,
              lambda text, line, col: makeToken(text, line, col, TOK_VECTOR, "(")),
    transition(Token.VEC, Token.START, "", True, False, False, None),
    transition(Token.VCB, Token.PRECHAR, "\\", False, False, True, None),
    transition(Token.PRECHAR, Token.PRECHAR, " \t\r\n\0", True, True, True, None),
    transition(Token.PRECHAR, Token.CHAR, " \t\r\n\0", False, False, False,
           lambda text, line, col: makeCharToken(text, line, col)),
    transition(Token.VCB, Token.BOOL, "tf", False, True, True,
              lambda text, line, col: makeToken(text, line, col, TOK_BOOL, bool(text == "t"))),
    transition(Token.BOOL, Token.CLEANBREAK, "", True, False, False, None),
    transition(Token.CHAR, Token.CLEANBREAK, "", True, False, False, None),
    
    # Identifiers, keywords, and numbers
    transition(Token.START, Token.INID, VALIDID, False, True, True, None),
    transition(Token.INID, Token.INID, VALIDINID, False, True, True, None),
    transition(Token.INID, Token.IDENTIFIER, "\0", False, False, False,
               lambda text, line, col: keywordMaker(text, line, col)),
    transition(Token.IDENTIFIER, Token.CLEANBREAK, "", True, False, False, None),
    transition(Token.START, Token.PM, "+-", False, True, True, None),
    transition(Token.PM, Token.IDENTIFIER, "\0", False, False, False,
               lambda text, line, col: makeToken(text, line, col, TOK_IDENTIFIER, text)),
    transition(Token.PM, Token.ININT, DIGITS, False, True, True, None),
    transition(Token.START, Token.ININT, DIGITS, False, True, True, None),
    transition(Token.ININT, Token.ININT, DIGITS, False, True, True, None),
    transition(Token.ININT, Token.INT, " \t\r\n();\0", False, False, False, 
           lambda text, line, col: makeToken(text, line, col, TOK_INT, int(text))),
    transition(Token.INT, Token.CLEANBREAK, "", True, False, False, None),
    transition(Token.ININT, Token.PREDBL, ".", False, True, True, None), 
    transition(Token.PREDBL, Token.INDBL, DIGITS, False, True, True, None),
    transition(Token.INDBL, Token.INDBL, DIGITS, False, True, True, None),
    transition(Token.INDBL, Token.DBL, "\0", False, False, False,
               lambda text, line, col: makeToken(text, line, col, TOK_DBL, float(text))),
    transition(Token.DBL, Token.CLEANBREAK, "", True, False, False, None ),
    
    # Strings
    transition(Token.START, Token.INSTR, "\"", False, False, True, None),
    transition(Token.INSTR, Token.INSTR, "\"\\\n\r\t", True, True, True, None),
    transition(Token.INSTR, Token.INSTRP, "\\", False, False, True, None),
    transition(Token.INSTRP, Token.INSTR, "nrt\"\\", False, True, True, None),
    transition(Token.INSTR, Token.STRING, "\"", False, False, True,
               lambda text, line, col: makeToken(text, line, col, TOK_STR, text)),
    transition(Token.STRING, Token.CLEANBREAK, "", True, False, False, None), 
    
    transition(Token.START, Token.CLEANBREAK, "", True, False, False, None),
  
]

def keywordMaker(text, line, col):
    """Make a token for a keyword or an identifier"""
    keywords = {
       "and": TOK_AND,
       "begin": TOK_BEGIN,
       "cond": TOK_COND,
       "define": TOK_DEFINE,
       "if": TOK_IF,
       "lambda": TOK_LAMBDA,
       "or": TOK_OR,
       "quote": TOK_QUOTE,
       "set!": TOK_SET,
       "let": TOK_LET,
       "apply": TOK_APPLY,
    }
    tokenType = keywords.get(text, TOK_IDENTIFIER) # Default to an identifier
    # set it to None if it is an identifier
    return makeToken(text, line, col, tokenType, None if tokenType != TOK_IDENTIFIER else text)


def makeCharToken(text, line, col):
    """Generater a char token"""
    if text == "":
        return makeToken("", line, col, TOK_ERROR, "Invalid character after '#\\'")
    mapping = {"space": " ", "newline": "\n", "tab": "\t"}
    if text in mapping:
        return makeToken(text, line, col, TOK_CHAR, mapping[text])
    elif len(text) == 1:
        # single character
        return makeToken(text, line, col, TOK_CHAR, text)
    else:
        # More than one character that isn't an allowed name.
        return makeToken("", line, col, TOK_ERROR, "Invalid character literal")


def convertEscape(ch):
    """Convert an escape character to its actual value"""
    if ch == 'n':
        return "\n"
    elif ch == 't':
        return "\t"
    elif ch == 'r':
        return "\\r"
    else:
        return ch

# The index of the first character of the in-progress token
start = 0
# Count the newlines we consume
current_line = 1
# index in `source` where current line begins
line_start_char = 0
# The current index of the scanner
index = 0
# The current state of the scanner
state = Token.START

def col():
    """Return the current column number"""
    return start - line_start_char + 1

def advance(c):
    """Advance the index and update the current line and line_start_char if necessary"""
    global index, current_line, line_start_char
    index += 1
    if c == "\n":
        current_line += 1
        line_start_char = index

def isEnd(source, peek=False):
    """Check if we have reached the end of source.
    Peek checks if we are at the end of the source or one character before the end
    """
    if peek:
        return index >= len(source)
    else:
        return index >= len(source) - 1

def scanTokens(source):
    """Work through the `source` and transform it into a list of tokens.
    This will always put an EOF token at the end, unless there is an error.  If
    there is an error, then it returns an array with exactly one entry.
    """
    global start, index
    state = Token.START
    tokens = []
    token_text = ""
    start = index

    while not isEnd(source, True):
        c = source[index] if not isEnd(source) else "\0"
        if c in INVALIDCHARS and state == Token.START:
            return [makeToken("", current_line, col(), TOK_ERROR, "Invalid Character")]
        found_transition = False
        for transition in transitions:
            # check if the from state matches the current state
            # if not then we can't use this transition
            if transition["fromState"] != state:
                continue
            # check if the next character is in the peekSet 
            # if not then we can't use this transition
            if c not in transition["peekSet"] and not transition["invertPeek"]:
                continue
            # check if the next character is not in the peekSet
            # if it is then we can't use this transition
            if c in transition["peekSet"] and transition["invertPeek"]:
                continue
            # take note of the previous state
            prev_state = state
            # update the current state
            state = transition["toState"]
            # note that we found a valid transition
            found_transition = True
            
            # consume a character if necessary
            if transition["consume"]:
                # If we are consuming a character, check if we are coming from an escape state.
                if prev_state == Token.INSTRP:
                    token_text += convertEscape(c)
                else:
                    token_text += c
            
            # advance the index if necessary
            if transition["advance"]:
                advance(c)
            
            # if we have a maker function, then we can make a token
            if transition["maker"]:
                tokens.append(transition["maker"](token_text, current_line, col()))
                # reset 'start' and 'token_text for the next token since we just finished one
                start = index
                token_text = ""
            else:
                # for whitespace
                if state == Token.START:
                    start = index
            break
        # if we did not find a valid transition then we are done
        if not found_transition:
            break

    # check if we are in a valid end state
    if state in (Token.START, Token.CLEANBREAK, Token.EOFTOKEN):
        tokens.append(makeToken("", current_line, col(), TOK_EOF, None))
        return tokens

    # Find the correct error message
    error = ""
    if state == Token.INID:
        error = "Invalid character in ID"
    elif state == Token.PRECHAR:
        error = "Invalid character after '#\\'"
    elif state == Token.CHAR:
        error = "Invalid character literal"
    elif state == Token.PM:
        error = "Invalid Character after +/-"
    elif state == Token.ININT:
        error = "Invalid character in INT"
    elif state == Token.PREDBL:
        error = "DBL cannot end with '.'"
    elif state == Token.INSTRP:
        error = "Invalid Escape Character in String"
    elif state == Token.INSTR:
        error = "Invalid Whitespace in String"

    return [makeToken("", current_line, col(), TOK_ERROR, error)]
