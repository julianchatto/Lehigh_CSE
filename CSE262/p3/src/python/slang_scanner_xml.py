from xml.dom import minidom
from slang_scanner import *


def xml_escape(s):
    """Escape a string for outputting it to XML as an attribute"""
    ans = ""
    for i in s:
        if i == "\\":
            ans += "\\\\"
        elif i == "\t":
            ans += "\\t"
        elif i == "\n":
            ans += "\\n"
        elif i == "'":
            ans += "\\'"
        else:
            ans += i
    return ans


def xml_unescape(s):
    """Remove escape characters from a string when reading from XML"""
    ans = ""
    in_escape = False
    for i in s:
        if not in_escape:
            if i != "\\":
                ans += i
            else:
                in_escape = True
        else:
            if i == "\\":
                ans += "\\"
                in_escape = False
            elif i == "t":
                ans += "\t"
                in_escape = False
            elif i == "n":
                ans += "\n"
                in_escape = False
            elif i == "'":
                ans += "'"
                in_escape = False
            if in_escape:
                raise RuntimeError("Invalid string?!?")
    return ans


# A mapping from TokenTypes to the tag names we use in the XML
tokenTagMap = {
    TOK_ABBREV: "AbbrevToken",
    TOK_AND: "AndToken",
    TOK_APPLY: "ApplyToken",
    TOK_BEGIN: "BeginToken",
    TOK_BOOL: "BoolToken",
    TOK_CHAR: "CharToken",
    TOK_COND: "CondToken",
    TOK_DBL: "DblToken",
    TOK_DEFINE: "DefineToken",
    TOK_DOT: "DotToken",
    TOK_EOF: "EofToken",
    TOK_IDENTIFIER: "IdentifierToken",
    TOK_IF: "IfToken",
    TOK_INT: "IntToken",
    TOK_LAMBDA: "LambdaToken",
    TOK_LEFT_PAREN: "LeftParenToken",
    TOK_LET: "LetToken",
    TOK_OR: "OrToken",
    TOK_QUOTE: "QuoteToken",
    TOK_RIGHT_PAREN: "RightParenToken",
    TOK_SET: "SetToken",
    TOK_STR: "StrToken",
    TOK_VECTOR: "VecToken",
    TOK_ERROR: "ErrorToken",
}


def xmlToString(tokens):
    """Produce an Xml representation of the given array of tokens, and turn it
    into a string"""

    doc = minidom.Document()
    root = doc.createElement("Tokens")
    root.setAttribute("xmlns", "tokens")
    doc.appendChild(root)

    for token in tokens:
        tag = tokenTagMap.get(token["type"])
        if not tag:
            raise RuntimeError("Unexpected tag")
        elt = doc.createElement(tag)
        if token["type"] != TOK_EOF:
            elt.setAttribute("col", str(token["col"]))
            elt.setAttribute("line", str(token["line"]))
            if token["type"] in [TOK_STR, TOK_ERROR, TOK_CHAR]:
                elt.setAttribute("val", xml_escape(token["literal"]))
            elif token["type"] == TOK_IDENTIFIER:
                elt.setAttribute("val", xml_escape(token["text"]))
            elif token["type"] == TOK_BOOL:
                elt.setAttribute("val", "true" if token["literal"] else "false")
            elif token["type"] in [TOK_DBL, TOK_INT]:
                elt.setAttribute("val", str(token["literal"]))
        root.appendChild(elt)
        if token["type"] == TOK_ERROR:
            return token["literal"]

    xml_str = root.toprettyxml(indent=" ")
    return xml_str.strip()


# A mapping from XML tag names to TokenTypes
tagTokenMap = {
    "AbbrevToken": TOK_ABBREV,
    "AndToken": TOK_AND,
    "ApplyToken": TOK_APPLY,
    "BeginToken": TOK_BEGIN,
    "BoolToken": TOK_BOOL,
    "CharToken": TOK_CHAR,
    "CondToken": TOK_COND,
    "DblToken": TOK_DBL,
    "DefineToken": TOK_DEFINE,
    "DotToken": TOK_DOT,
    "EofToken": TOK_EOF,
    "IdentifierToken": TOK_IDENTIFIER,
    "IfToken": TOK_IF,
    "IntToken": TOK_INT,
    "LambdaToken": TOK_LAMBDA,
    "LeftParenToken": TOK_LEFT_PAREN,
    "LetToken": TOK_LET,
    "OrToken": TOK_OR,
    "QuoteToken": TOK_QUOTE,
    "RightParenToken": TOK_RIGHT_PAREN,
    "SetToken": TOK_SET,
    "StrToken": TOK_STR,
    "VecToken": TOK_VECTOR,
    "ErrorToken": TOK_ERROR,
}


def readTokensFromXml(xml):
    """Given a string representation of an Xml file, read Tokens from it and
    return them as an array"""
    # Open XML document using minidom parser
    doc = minidom.parseString(xml)
    root = doc.documentElement

    # results go here
    res = []

    # Go through the children of the root to get all tokens
    for token in root.childNodes:
        if token.nodeType == minidom.Node.TEXT_NODE:
            continue
        name = token.localName
        attributes = token.attributes
        line = int(attributes["line"].value) if token.hasAttribute("line") else None
        col = int(attributes["col"].value) if token.hasAttribute("col") else None
        val = str(attributes["val"].value) if token.hasAttribute("val") else None
        type = tagTokenMap.get(name)
        if type == None:
            return [makeToken("", 0, 0, TOK_ERROR, "Unrecognized Tag '" + name + "'")]
        # Try to handle token types that have a value
        if type == TOK_BOOL and val:
            res.append(makeToken(val, line, col, type, val == "true"))
        elif type == TOK_DBL and val:
            res.append(makeToken(val, line, col, type, float(val)))
        elif type == TOK_INT and val:
            res.append(makeToken(val, line, col, type, int(val)))
        # CHAR/STR/IDENTIFIER are all handled the same way :)
        elif type in [TOK_CHAR, TOK_STR, TOK_IDENTIFIER]:
            res.append(makeToken(val, line, col, type, xml_unescape(val)))
        # It's a basic token
        else:
            res.append(makeToken(val, line, col, type, None))
    return res
