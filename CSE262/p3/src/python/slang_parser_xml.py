from xml.dom import minidom

import slang_parser
from slang_scanner_xml import xml_escape, xml_unescape


def xmlToString(ast):
    """Produce an Xml representation of the given forest, and write it to
    stdout"""

    doc = minidom.Document()
    root = doc.createElement("Ast")
    root.setAttribute("xmlns", "ast")
    doc.appendChild(root)

    for node in ast:
        to_xml(doc, root, node)

    xml_str = root.toprettyxml(indent=" ")
    return xml_str.strip()


def to_xml(doc, parent, node):
    """Transform an expression into a string representing its corresponding tree of
    XML tokens
    * `expr  ` -- The expression to convert
    * `indent` -- How much (minimum) indentation should the tree nodes have?
    Returns a string representation of an XML tree"""
    if node["type"] == slang_parser.AST_AND:
        elt = doc.createElement("And")
        for e in node["exprs"]:
            to_xml(doc, elt, e)
        parent.appendChild(elt)
    elif node["type"] == slang_parser.AST_APPLY:
        elt = doc.createElement("Apply")
        to_xml(doc, elt, node["func"])
        to_xml(doc, elt, node["args"])
        parent.appendChild(elt)
    elif node["type"] == slang_parser.AST_CALL:
        elt = doc.createElement("Call")
        for e in node["exprs"]:
            to_xml(doc, elt, e)
        parent.appendChild(elt)
    elif node["type"] == slang_parser.AST_BEGIN:
        elt = doc.createElement("Begin")
        for e in node["exprs"]:
            to_xml(doc, elt, e)
        parent.appendChild(elt)
    elif node["type"] == slang_parser.AST_BOOL_TRUE:
        elt = doc.createElement("Bool")
        elt.setAttribute("val", "true")
        parent.appendChild(elt)
    elif node["type"] == slang_parser.AST_BOOL_FALSE:
        elt = doc.createElement("Bool")
        elt.setAttribute("val", "false")
        parent.appendChild(elt)
    elif node["type"] == slang_parser.AST_CHAR:
        elt = doc.createElement("Char")
        elt.setAttribute("val", xml_escape(node["val"]))
        parent.appendChild(elt)
    elif node["type"] == slang_parser.AST_CONS:
        elt = doc.createElement("Cons")
        to_xml(doc, elt, node["car"])
        to_xml(doc, elt, node["cdr"])
        parent.appendChild(elt)
    elif node["type"] == slang_parser.AST_EMPTY_CONS:
        elt = doc.createElement("Empty")
        parent.appendChild(elt)
    elif node["type"] == slang_parser.AST_DBL:
        elt = doc.createElement("Dbl")
        elt.setAttribute("val", str(node["val"]))
        parent.appendChild(elt)
    elif node["type"] == slang_parser.AST_INT:
        elt = doc.createElement("Int")
        elt.setAttribute("val", str(node["val"]))
        parent.appendChild(elt)
    elif node["type"] == slang_parser.AST_STR:
        elt = doc.createElement("Str")
        elt.setAttribute("val", xml_escape(node["val"]))
        parent.appendChild(elt)
    elif node["type"] == slang_parser.AST_SYMBOL:
        elt = doc.createElement("Symbol")
        elt.setAttribute("val", node["val"])
        parent.appendChild(elt)
    elif node["type"] == slang_parser.AST_VEC:
        elt = doc.createElement("Vector")
        for e in node["items"]:
            to_xml(doc, elt, e)
        parent.appendChild(elt)
    elif node["type"] == slang_parser.AST_COND:
        elt = doc.createElement("Cond")
        for e in node["conditions"]:
            c = doc.createElement("Condition")
            t = doc.createElement("Test")
            to_xml(doc, t, e["test"])
            c.appendChild(t)
            a = doc.createElement("Actions")
            for ee in e["exprs"]:
                to_xml(doc, a, ee)
            c.appendChild(a)
            elt.appendChild(c)
        parent.appendChild(elt)
    elif node["type"] == slang_parser.AST_DEFINE_VAR:
        elt = doc.createElement("DefineVar")
        to_xml(doc, elt, node["id"])
        to_xml(doc, elt, node["expr"])
        parent.appendChild(elt)
    elif node["type"] == slang_parser.AST_DEFINE_FUNC:
        elt = doc.createElement("DefineFunc")
        ids = doc.createElement("Identifiers")
        for f in node["ids"]:
            to_xml(doc, ids, f)
        elt.appendChild(ids)
        exprs = doc.createElement("Expressions")
        for e in node["body"]:
            to_xml(doc, exprs, e)
        elt.appendChild(exprs)
        parent.appendChild(elt)
    elif node["type"] == slang_parser.AST_IDENTIFIER:
        elt = doc.createElement("Identifier")
        elt.setAttribute("val", xml_escape(node["id"]))
        parent.appendChild(elt)
    elif node["type"] == slang_parser.AST_IF:
        elt = doc.createElement("If")
        to_xml(doc, elt, node["cond"])
        to_xml(doc, elt, node["if_true"])
        to_xml(doc, elt, node["if_false"])
        parent.appendChild(elt)
    elif node["type"] == slang_parser.AST_LAMBDA_DEF:
        elt = doc.createElement("Lambda")
        formals = doc.createElement("Formals")
        for f in node["formals"]:
            to_xml(doc, formals, f)
        elt.appendChild(formals)
        exprs = doc.createElement("Expressions")
        for e in node["exprs"]:
            to_xml(doc, exprs, e)
        elt.appendChild(exprs)
        parent.appendChild(elt)
    elif node["type"] == slang_parser.AST_OR:
        elt = doc.createElement("Or")
        for e in node["exprs"]:
            to_xml(doc, elt, e)
        parent.appendChild(elt)
    elif node["type"] == slang_parser.AST_QUOTE:
        elt = doc.createElement("Quote")
        to_xml(doc, elt, node["datum"])
        parent.appendChild(elt)
    elif node["type"] == slang_parser.AST_SET:
        elt = doc.createElement("Set")
        to_xml(doc, elt, node["id"])
        to_xml(doc, elt, node["expr"])
        parent.appendChild(elt)
    elif node["type"] == slang_parser.AST_TICK:
        elt = doc.createElement("Tick")
        to_xml(doc, elt, node["datum"])
        parent.appendChild(elt)
    elif node["type"] == slang_parser.AST_LET:
        elt = doc.createElement("Let")
        bindings = doc.createElement("Bindings")
        for e in node["vars"]:
            binding = doc.createElement("Binding")
            to_xml(doc, binding, e["id"])
            to_xml(doc, binding, e["val"])
            bindings.appendChild(binding)
        elt.appendChild(bindings)
        actions = doc.createElement("Actions")
        for e in node["body"]:
            to_xml(doc, actions, e)
        elt.appendChild(actions)
        parent.appendChild(elt)
    else:
        raise RuntimeError(f"Unrecognized node['type']: {node['type']}")


def children(node):
    """Return the non-text children of `node`"""
    return filter(lambda n: n.nodeType != minidom.Node.TEXT_NODE, node.childNodes)


def parse_node(node):
    """Transform an XML node into an AST Node"""
    val = node.attributes["val"].value if node.attributes else None
    if node.nodeType == minidom.Node.TEXT_NODE:
        return ""
    if node.localName == "Identifier":
        return slang_parser.makeIdentifierNode(str(val))
    elif node.localName == "Bool" and str(val) == "false":
        return slang_parser.makeBoolFalseNode()
    elif node.localName == "Bool" and str(val) == "true":
        return slang_parser.makeBoolTrueNode()
    elif node.localName == "Int":
        return slang_parser.makeIntNode(int(val))
    elif node.localName == "Dbl":
        return slang_parser.makeDblNode(float(val))
    elif node.localName == "Char":
        return slang_parser.makeCharNode(xml_unescape(str(val)))
    elif node.localName == "Str":
        return slang_parser.makeStrNode(xml_unescape(str(val)))
    elif node.localName == "Symbol":
        return slang_parser.makeSymbolNode(str(val))
    # Now we get to the harder ones...
    elif node.localName == "DefineVar":
        kids = list(children(node))
        identifier = parse_node(kids[0])
        expression = parse_node(kids[1])
        return slang_parser.makeDefineVarNode(identifier, expression)
    elif node.localName == "If":
        kids = list(children(node))
        cond = parse_node(kids[0])
        if_true = parse_node(kids[1])
        if_false = parse_node(kids[2])
        return slang_parser.makeIfNode(cond, if_true, if_false)
    elif node.localName == "Set":
        parts = list(children(node))  # turn the iterator into a list
        identifier = parse_node(parts[0])
        expression = parse_node(parts[1])
        return slang_parser.makeSetNode(identifier, expression)
    elif node.localName == "Quote":
        parts = list(children(node))  # turn the iterator into a list
        datum = parse_node(parts[0])
        return slang_parser.makeQuoteNode(datum)
    elif node.localName == "Tick":
        datum = parse_node(node.childNodes[0])
        return slang_parser.makeTickNode(datum)
    elif node.localName == "Lambda":
        kids = list(children(node))
        formals = map(parse_node, children(kids[0]))
        body = map(parse_node, children(kids[1]))
        return slang_parser.makeLambdaDefNode(formals, body)
    elif node.localName == "And":
        return slang_parser.makeAndNode(map(parse_node, children(node)))
    elif node.localName == "Call":
        return slang_parser.makeCallNode(map(parse_node, list(children(node))))
    elif node.localName == "Or":
        return slang_parser.makeOrNode(map(parse_node, children(node)))
    elif node.localName == "Begin":
        return slang_parser.makeBeginNode(map(parse_node, children(node)))
    elif node.localName == "Vector":
        return slang_parser.makeVecNode(map(parse_node, children(node)))
    elif node.localName == "Cond":
        return slang_parser.makeCondNode(map(parse_node, children(node)))
    elif node.localName == "Condition":
        kids = list(children(node))
        test = parse_node(list(children(kids[0]))[0])
        exprs = map(parse_node, children(kids[1]))
        return slang_parser.makeCondition(test, exprs)
    elif node.localName == "Cons":
        parts = list(children(node))  # turn the iterator into a list
        car = parse_node(parts[0])
        cdr = parse_node(parts[1])
        return slang_parser.makeConsNode(car, cdr)
    elif node.localName == "Empty":
        return slang_parser.makeEmptyConsNode()
    elif node.localName == "Apply":
        kids = list(children(node))
        func = parse_node(kids[0])
        args = parse_node(kids[1])
        return slang_parser.makeApplyNode(func, args)
    elif node.localName == "DefineFunc":
        kids = list(children(node))
        ids = map(parse_node, children(kids[0]))
        body = map(parse_node, children(kids[1]))
        return slang_parser.makeDefineFuncNode(ids, body)
    elif node.localName == "Let":
        vars = []
        (bindings, actions) = list(children(node))  # turn the iterator into a list
        for binding in children(bindings):
            b = list(children(binding))
            vars.append(slang_parser.makeLetDef(parse_node(b[0]), parse_node(b[1])))
        body = map(parse_node, children(actions))
        return slang_parser.makeLetNode(vars, body)
    else:
        raise RuntimeError(f"Unexpected XML Tag name {node.localName}")


def readForestFromXml(xml):
    """Given a string representation of an Xml file, read Ast nodes from it and
    return them as an array"""
    doc = minidom.parseString(xml)
    root = doc.documentElement
    res = []
    for node in children(root):
        res.append(parse_node(node))
    return res
