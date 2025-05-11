package edu.lehigh.cse262.slang.Parser;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import edu.lehigh.cse262.slang.Scanner.XmlHelpers;

/**
 * XmlNodeReader is a lightweight parser that can convert a stream of Xml
 * elements that was produced by XmlNodeWriter (end of parse phase) back into a
 * forest of nodes that is suitable for the interpret phase.
 *
 * Warning: This code assumes that (1) XmlNodeWriter is correct, and (2) the
 * Xml given to it was created by XmlNodeWriter. If this code is given invalid
 * Xml, it will not handle errors correctly
 */
public class XmlNodeReader {
    /**
     * An exception class for indicating that the parser encountered an error
     */
    public static class ParseError extends Exception {
        /** Construct a ParseError and attach a message to it */
        public ParseError(String msg) {
            super(msg);
        }
    }

    /**
     * Get all the non-text children of a node, converting to AstNodes as we go
     * along
     */
    private static List<AstNodes.AstNode> getList(Node node) throws ParseError {
        var res = new ArrayList<AstNodes.AstNode>();
        var children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); ++i)
            if (children.item(i).getNodeType() == Node.ELEMENT_NODE)
                res.add(parseNode(children.item(i)));
        return res;
    }

    /** Get all the non-text children of a node as nodes */
    private static List<Node> getNodes(Node node) {
        var res = new ArrayList<Node>();
        var children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); ++i)
            if (children.item(i).getNodeType() == Node.ELEMENT_NODE)
                res.add(children.item(i));
        return res;
    }

    /**
     * A helper method that produces an AstNode from an Xml object
     *
     * @param o The Xml object
     *
     * @return An AstNode
     */
    private static AstNodes.AstNode parseNode(Node node) throws ParseError {
        // NB: this code is mostly symmetric with JsonNodeWriter, which means
        // that most of the nodes are very easy to parse.
        var name = node.getNodeName();
        switch (name) {
            case XmlConstants.TAG_AND:
                return new AstNodes.And(getList(node));
            case XmlConstants.TAG_CALL:
                return new AstNodes.Call(getList(node));
            case XmlConstants.TAG_BEGIN:
                return new AstNodes.Begin(getList(node));
            case XmlConstants.TAG_BOOL: {
                var val = node.getAttributes().getNamedItem("val").getTextContent();
                return (val.equals("true")) ? new AstNodes.BoolTrue() : new AstNodes.BoolFalse();
            }
            case XmlConstants.TAG_EMPTY:
                return new AstNodes.EmptyCons();
            case XmlConstants.TAG_CONS: {
                var kids = getNodes(node);
                return AstNodes.Cons.makeCons((AstNodes.Datum) parseNode(kids.get(0)),
                        (AstNodes.Datum) parseNode(kids.get(1)));
            }
            case XmlConstants.TAG_DEFINE_FUNC: {
                var kids = getNodes(node);
                var ids = getList(kids.get(0)).stream().map(v -> (AstNodes.Identifier) v).collect(Collectors.toList());
                var body = getList(kids.get(1));
                return new AstNodes.DefineFunc(ids, body);
            }
            case XmlConstants.TAG_APPLY: {
                var kids = getList(node);
                return new AstNodes.Apply(kids.get(0), kids.get(1));
            }
            case XmlConstants.TAG_LET: {
                var vars = new ArrayList<AstNodes.Let.LetDef>();
                var kids = getNodes(node);
                var bindings = getNodes(kids.get(0));
                for (var b : bindings) {
                    var vals = getList(b);
                    vars.add(new AstNodes.Let.LetDef((AstNodes.Identifier) vals.get(0), vals.get(1)));
                }
                var actions = getList(kids.get(1));
                return new AstNodes.Let(vars, actions);
            }
            case XmlConstants.TAG_DEFINE_VAR: {
                var kids = getList(node);
                return new AstNodes.DefineVar((AstNodes.Identifier) kids.get(0), kids.get(1));
            }
            case XmlConstants.TAG_IDENTIFIER: {
                var val = node.getAttributes().getNamedItem("val").getTextContent();
                return new AstNodes.Identifier(val);
            }
            case XmlConstants.TAG_IF: {
                var kids = getList(node);
                return new AstNodes.If(kids.get(0), kids.get(1), kids.get(2));
            }
            case XmlConstants.TAG_LAMBDA: {
                var kids = getNodes(node);
                var formals = getList(kids.get(0)).stream().map(v -> (AstNodes.Identifier) v)
                        .collect(Collectors.toList());
                var body = getList(kids.get(1));
                return new AstNodes.LambdaDef(formals, body);
            }
            case XmlConstants.TAG_INT: {
                var val = node.getAttributes().getNamedItem("val").getTextContent();
                return new AstNodes.Int(Integer.parseInt(val));
            }
            case XmlConstants.TAG_DBL: {
                var val = node.getAttributes().getNamedItem("val").getTextContent();
                return new AstNodes.Dbl(Double.parseDouble(val));
            }
            case XmlConstants.TAG_OR:
                return new AstNodes.Or(getList(node));
            case XmlConstants.TAG_QUOTE: {
                var datum = parseNode(getNodes(node).get(0));
                return new AstNodes.Quote((AstNodes.Datum) datum);
            }
            case XmlConstants.TAG_SET: {
                var kids = getList(node);
                return new AstNodes.Set((AstNodes.Identifier) kids.get(0), kids.get(1));
            }
            case XmlConstants.TAG_STR: {
                var val = node.getAttributes().getNamedItem("val").getTextContent();
                return new AstNodes.Str(XmlHelpers.unEscape(val));
            }
            case XmlConstants.TAG_CHAR: {
                var val = node.getAttributes().getNamedItem("val").getTextContent();
                return new AstNodes.Char(XmlHelpers.unEscape(val).charAt(0));
            }
            case XmlConstants.TAG_SYMBOL: {
                var val = node.getAttributes().getNamedItem("val").getTextContent();
                return new AstNodes.Symbol(val);
            }
            case XmlConstants.TAG_TICK: {
                var datum = parseNode(node.getChildNodes().item(0));
                return new AstNodes.Tick((AstNodes.Datum) datum);
            }
            case XmlConstants.TAG_VECTOR: {
                var kids = getList(node);
                return new AstNodes.Vec(kids.stream().map(v -> (AstNodes.Datum) v).collect(Collectors.toList()));
            }
            case XmlConstants.TAG_COND: {
                // Instead of using a helper method to extract the Conditions,
                // we'll just do it right here. That's not great for testing,
                // but there really insn't all that much code to test anyway :)
                var conditions = new ArrayList<AstNodes.Cond.Condition>();
                var arms = getNodes(node);
                for (var c : arms) {
                    var exprs = getNodes(c);
                    var test = parseNode(getNodes(exprs.get(0)).get(0));
                    var body = getNodes(exprs.get(1)).stream().map(e -> {
                        try {
                            return parseNode(e);
                        } catch (ParseError e1) {
                            return null;
                        }
                    }).collect(Collectors.toList());
                    conditions.add(new AstNodes.Cond.Condition(test, body));
                }
                return new AstNodes.Cond(conditions);
            }
        }
        throw new ParseError("Unexpected XML Tag name " + name);
    }

    /**
     * Given a string representation of an Xml file, build the corresponding
     * AST. Note that we need a bit of recursion here, since we're dealing with
     * a context-free grammar, so we can't just do the whole transformation in
     * this method.
     *
     * @param xml A string holding Json that was presumably produced by
     *            XmlNodeWriter
     *
     * @return An array of AstNodes
     */
    public ArrayList<AstNodes.AstNode> parse(String xml) throws ParseError {
        var res = new ArrayList<AstNodes.AstNode>();

        try {
            // Open the Xml document. If it isn't a valid Xml document at all,
            // then this will throw, but if it's just not the right kind of Xml
            // document, we'll crash later.
            var dbf = DocumentBuilderFactory.newInstance();
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            var db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(new StringReader(xml)));
            doc.getDocumentElement().normalize();

            // Get the children of the root element, and parse them in order
            //
            // NB: We don't even bother to verify that the root tag is an
            // XmlConstants.tagRoot
            var root = doc.getDocumentElement();
            var children = root.getChildNodes();
            for (int i = 0; i < children.getLength(); ++i) {
                var node = children.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE)
                    res.add(parseNode(node));
            }
            return res;
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            return null;
        } catch (SAXException e) {
            return null;
        }
    }
}