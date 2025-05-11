package edu.lehigh.cse262.slang.Parser;

import java.io.OutputStream;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import edu.lehigh.cse262.slang.Scanner.XmlHelpers;

public class XmlNodeWriter implements INodeVisitor<Void> {
    /** The current element, that we can add nodes to */
    private Element current;

    /** The document, in which we can create new elements */
    private Document doc;

    /**
     * Visit each node in a forest of Ast nodes, and serialize them all to the
     * given stream as XML
     */
    public void astToXml(List<AstNodes.AstNode> forest, OutputStream stream) throws INodeVisitorError {
        // Create an XML document
        try {
            var builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            doc = builder.newDocument();
        } catch (ParserConfigurationException e) {
            System.err.println("Unexpected error constructing an XML DocumentBuilder.");
            e.printStackTrace();
            return;
        }
        // Now make the root element
        current = doc.createElement("Ast");
        current.setAttribute("xmlns", "ast");
        doc.appendChild(current);

        for (var n : forest)
            n.serialize(this);

        // Write the Xml document to the provided stream
        try {
            // Configure the transformer, which actually does the writing. We
            // want to skip a leading `<xml>` tag, and we want indentation
            var transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "1");

            // Now go ahead and write the document to the stream
            transformer.transform(new DOMSource(doc), new StreamResult(stream));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Void visitAnd(List<AstNodes.AstNode> expressions) throws INodeVisitorError {
        var old = current;
        current = doc.createElement(XmlConstants.TAG_AND);
        old.appendChild(current);
        for (var e : expressions)
            e.serialize(this);
        current = old;
        return null;
    }

    @Override
    public Void visitCall(List<AstNodes.AstNode> expressions) throws INodeVisitorError {
        var old = current;
        current = doc.createElement(XmlConstants.TAG_CALL);
        old.appendChild(current);
        for (var e : expressions)
            e.serialize(this);
        current = old;
        return null;
    }

    @Override
    public Void visitBegin(List<AstNodes.AstNode> expressions) throws INodeVisitorError {
        var old = current;
        current = doc.createElement(XmlConstants.TAG_BEGIN);
        old.appendChild(current);
        for (var e : expressions)
            e.serialize(this);
        current = old;
        return null;
    }

    @Override
    public Void visitBoolTrue(AstNodes.BoolTrue datum) {
        var node = doc.createElement(XmlConstants.TAG_BOOL);
        node.setAttribute("val", "true");
        current.appendChild(node);
        return null;
    }

    @Override
    public Void visitBoolFalse(AstNodes.BoolFalse datum) {
        var node = doc.createElement(XmlConstants.TAG_BOOL);
        node.setAttribute("val", "false");
        current.appendChild(node);
        return null;
    }

    @Override
    public Void visitChar(char val, AstNodes.Char datum) {
        var node = doc.createElement(XmlConstants.TAG_CHAR);
        node.setAttribute("val", XmlHelpers.escape("" + val));
        current.appendChild(node);
        return null;
    }

    @Override
    public Void visitCons(AstNodes.Datum car, AstNodes.Datum cdr, AstNodes.Cons datum) throws INodeVisitorError {
        var old = current;
        current = doc.createElement(XmlConstants.TAG_CONS);
        old.appendChild(current);
        car.serialize(this);
        cdr.serialize(this);
        current = old;
        return null;
    }

    @Override
    public Void visitEmptyCons(AstNodes.EmptyCons datum) {
        current.appendChild(doc.createElement(XmlConstants.TAG_EMPTY));
        return null;
    }

    @Override
    public Void visitDefineVar(AstNodes.Identifier identifier, AstNodes.AstNode expression) throws INodeVisitorError {
        var old = current;
        current = doc.createElement(XmlConstants.TAG_DEFINE_VAR);
        old.appendChild(current);
        identifier.serialize(this);
        expression.serialize(this);
        current = old;
        return null;
    }

    @Override
    public Void visitDefineFunc(List<AstNodes.Identifier> ids, List<AstNodes.AstNode> body) throws INodeVisitorError {
        var old = current;
        var I = doc.createElement(XmlConstants.TAG_IDENTIFIERS);
        var E = doc.createElement(XmlConstants.TAG_EXPRESSIONS);
        var D = doc.createElement(XmlConstants.TAG_DEFINE_FUNC);
        D.appendChild(I);
        D.appendChild(E);
        old.appendChild(D);
        current = I;
        for (var i : ids)
            i.serialize(this);
        current = E;
        for (var e : body)
            e.serialize(this);
        current = old;
        return null;
    }

    @Override
    public Void visitIdentifier(String name) {
        var node = doc.createElement(XmlConstants.TAG_IDENTIFIER);
        node.setAttribute("val", name);
        current.appendChild(node);
        return null;
    }

    @Override
    public Void visitIf(AstNodes.AstNode test, AstNodes.AstNode ifTrue, AstNodes.AstNode ifFalse)
            throws INodeVisitorError {
        var old = current;
        current = doc.createElement(XmlConstants.TAG_IF);
        old.appendChild(current);
        test.serialize(this);
        ifTrue.serialize(this);
        ifFalse.serialize(this);
        current = old;
        return null;
    }

    @Override
    public Void visitLambdaDef(List<AstNodes.Identifier> formals, List<AstNodes.AstNode> body,
            AstNodes.LambdaDef node) throws INodeVisitorError {
        var old = current;
        var L = doc.createElement(XmlConstants.TAG_LAMBDA);
        var F = doc.createElement(XmlConstants.TAG_FORMALS);
        var E = doc.createElement(XmlConstants.TAG_EXPRESSIONS);
        L.appendChild(F);
        L.appendChild(E);
        old.appendChild(L);
        current = F;
        for (var f : formals)
            f.serialize(this);
        current = E;
        for (var b : body)
            b.serialize(this);
        current = old;
        return null;
    }

    @Override
    public Void visitInt(int val, AstNodes.Int datum) {
        var node = doc.createElement(XmlConstants.TAG_INT);
        node.setAttribute("val", "" + val);
        current.appendChild(node);
        return null;
    }

    @Override
    public Void visitDbl(double val, AstNodes.Dbl datum) {
        var node = doc.createElement(XmlConstants.TAG_DBL);
        node.setAttribute("val", "" + val);
        current.appendChild(node);
        return null;
    }

    @Override
    public Void visitOr(List<AstNodes.AstNode> expressions) throws INodeVisitorError {
        var old = current;
        current = doc.createElement(XmlConstants.TAG_OR);
        old.appendChild(current);
        for (var e : expressions)
            e.serialize(this);
        current = old;
        return null;
    }

    @Override
    public Void visitQuote(AstNodes.Datum datum) throws INodeVisitorError {
        var old = current;
        current = doc.createElement(XmlConstants.TAG_QUOTE);
        old.appendChild(current);
        datum.serialize(this);
        current = old;
        return null;
    }

    @Override
    public Void visitSet(AstNodes.Identifier identifier, AstNodes.AstNode expression) throws INodeVisitorError {
        var old = current;
        current = doc.createElement(XmlConstants.TAG_SET);
        old.appendChild(current);
        identifier.serialize(this);
        expression.serialize(this);
        current = old;
        return null;
    }

    @Override
    public Void visitStr(String value, AstNodes.Str datum) {
        var node = doc.createElement(XmlConstants.TAG_STR);
        node.setAttribute("val", XmlHelpers.escape(value));
        current.appendChild(node);
        return null;
    }

    @Override
    public Void visitSymbol(String name, AstNodes.Symbol datum) {
        var node = doc.createElement(XmlConstants.TAG_SYMBOL);
        node.setAttribute("val", name);
        current.appendChild(node);
        return null;
    }

    @Override
    public Void visitTick(AstNodes.Datum datum) throws INodeVisitorError {
        var old = current;
        current = doc.createElement(XmlConstants.TAG_TICK);
        old.appendChild(current);
        datum.serialize(this);
        current = old;
        return null;
    }

    @Override
    public Void visitVec(AstNodes.Datum[] items, AstNodes.Vec datum) throws INodeVisitorError {
        var old = current;
        current = doc.createElement(XmlConstants.TAG_VECTOR);
        old.appendChild(current);
        for (var i : items)
            i.serialize(this);
        current = old;
        return null;
    }

    @Override
    public Void visitCond(List<AstNodes.Cond.Condition> conditions) throws INodeVisitorError {
        var old = current;
        current = doc.createElement(XmlConstants.TAG_COND);
        old.appendChild(current);
        for (var c : conditions)
            serializeCond(c.test, c.exprs);
        current = old;
        return null;
    }

    public void serializeCond(AstNodes.AstNode test, List<AstNodes.AstNode> expressions) throws INodeVisitorError {
        var old = current;
        var C = doc.createElement(XmlConstants.TAG_CONDITION);
        var T = doc.createElement(XmlConstants.TAG_TEST);
        var A = doc.createElement(XmlConstants.TAG_ACTIONS);
        C.appendChild(T);
        C.appendChild(A);
        old.appendChild(C);
        current = T;
        test.serialize(this);
        current = A;
        for (var a : expressions)
            a.serialize(this);
        current = old;
    }

    @Override
    public Void visitLet(List<AstNodes.Let.LetDef> vars, List<AstNodes.AstNode> body) throws INodeVisitorError {
        var old = current;
        var L = doc.createElement(XmlConstants.TAG_LET);
        var B = doc.createElement(XmlConstants.TAG_BINDINGS);
        var A = doc.createElement(XmlConstants.TAG_ACTIONS);
        L.appendChild(B);
        L.appendChild(A);
        old.appendChild(L);
        current = B;
        for (var v : vars)
            this.serializeLetDef(v.id, v.val);
        current = A;
        for (var e : body)
            e.serialize(this);
        current = old;
        return null;
    }

    public void serializeLetDef(AstNodes.Identifier id, AstNodes.AstNode val) throws INodeVisitorError {
        var old = current;
        current = doc.createElement(XmlConstants.TAG_BINDING);
        old.appendChild(current);
        id.serialize(this);
        val.serialize(this);
        current = old;
    }

    @Override
    public Void visitApply(AstNodes.AstNode func, AstNodes.AstNode args) throws INodeVisitorError {
        var old = current;
        current = doc.createElement(XmlConstants.TAG_APPLY);
        old.appendChild(current);
        func.serialize(this);
        args.serialize(this);
        current = old;
        return null;
    }
}
