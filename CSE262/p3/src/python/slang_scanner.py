# We'll use this as a simple approximation of an enum for the different Token
# types
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


def append_EOF(tokens):
    """Append an extra EOF to the list of tokens, to make parsing easier"""
    tokens.append(makeToken("", 0, 0, TOK_EOF, None))
