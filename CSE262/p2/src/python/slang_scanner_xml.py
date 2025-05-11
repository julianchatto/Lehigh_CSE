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
