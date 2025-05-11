from xml.dom import minidom


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
