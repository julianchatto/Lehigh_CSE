package edu.lehigh.cse262.slang.Scanner;

import java.io.IOException;
import java.io.StringReader;

import java.util.ArrayList;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * XmlTokenReader is a lightweight parser that can convert a stream of Xml
 * elements that was produced by XmlTokenWriter (end of scan phase) back into a
 * list of tokens that is suitable for the parse phase.
 *
 * Warning: This code assumes that (1) XmlTokenWriter is correct, and (2) the
 * xml given to it was created by XmlTokenWriter. If this code is given invalid
 * Xml, it will not handle errors correctly.
 */
public class XmlTokenReader {
    /**
     * Given a string representation of an Xml file, read Tokens from it and
     * return them as a TokenStream.
     *
     * @param xml A string holding Xml that was presumably produced by
     *            XmlTokenWriter
     *
     * @return A TokenStream of the tokens from `xml`, or null on any error
     */
    public TokenStream readTokensFromXml(String xml) {
        var res = new ArrayList<Tokens.Token>();
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
                var token = children.item(i);
                if (token.getNodeType() == Node.ELEMENT_NODE) {
                    var name = token.getNodeName();
                    var attributes = token.getAttributes();
                    var line = name.equals(XmlConstants.tagEof.value) ? -1
                            : Integer.parseInt(attributes.getNamedItem(XmlConstants.attrLine.value).getTextContent());
                    var col = name.equals(XmlConstants.tagEof.value) ? -1
                            : Integer.parseInt(attributes.getNamedItem(XmlConstants.attrColumn.value).getTextContent());
                    var valNode = attributes.getNamedItem("val");
                    var val = valNode == null ? "" : valNode.getTextContent();
                    if (name.equals(XmlConstants.tagAbbrev.value)) {
                        res.add(new Tokens.Abbrev("'", line, col));
                    } else if (name.equals(XmlConstants.tagAnd.value)) {
                        res.add(new Tokens.And("and", line, col));
                    } else if (name.equals(XmlConstants.tagApply.value)) {
                        res.add(new Tokens.Apply("apply", line, col));
                    } else if (name.equals(XmlConstants.tagBegin.value)) {
                        res.add(new Tokens.Begin("begin", line, col));
                    } else if (name.equals(XmlConstants.tagBool.value)) {
                        res.add(new Tokens.Bool(val, line, col, "true".equals(val)));
                    } else if (name.equals(XmlConstants.tagChar.value)) {
                        res.add(new Tokens.Char(val, line, col, XmlHelpers.unEscape(val).charAt(0)));
                    } else if (name.equals(XmlConstants.tagCond.value)) {
                        res.add(new Tokens.Cond("cond", line, col));
                    } else if (name.equals(XmlConstants.tagDbl.value)) {
                        res.add(new Tokens.Dbl(val, line, col, Double.parseDouble(val)));
                    } else if (name.equals(XmlConstants.tagDefine.value)) {
                        res.add(new Tokens.Define("define", line, col));
                    } else if (name.equals(XmlConstants.tagDot.value)) {
                        res.add(new Tokens.Dot("dot", line, col));
                    } else if (name.equals(XmlConstants.tagEof.value)) {
                        res.add(new Tokens.Eof("", line, col));
                    } else if (name.equals(XmlConstants.tagIdentifier.value)) {
                        res.add(new Tokens.Identifier(val, line, col));
                    } else if (name.equals(XmlConstants.tagIf.value)) {
                        res.add(new Tokens.If("if", line, col));
                    } else if (name.equals(XmlConstants.tagInt.value)) {
                        res.add(new Tokens.Int(val, line, col, Integer.parseInt(val)));
                    } else if (name.equals(XmlConstants.tagLambda.value)) {
                        res.add(new Tokens.Lambda("lambda", line, col));
                    } else if (name.equals(XmlConstants.tagLParen.value)) {
                        res.add(new Tokens.LeftParen("(", line, col));
                    } else if (name.equals(XmlConstants.tagLet.value)) {
                        res.add(new Tokens.Let("let", line, col));
                    } else if (name.equals(XmlConstants.tagOr.value)) {
                        res.add(new Tokens.Or("or", line, col));
                    } else if (name.equals(XmlConstants.tagQuote.value)) {
                        res.add(new Tokens.Quote("quote", line, col));
                    } else if (name.equals(XmlConstants.tagRParen.value)) {
                        res.add(new Tokens.RightParen(")", line, col));
                    } else if (name.equals(XmlConstants.tagSet.value)) {
                        res.add(new Tokens.Set("set!", line, col));
                    } else if (name.equals(XmlConstants.tagStr.value)) {
                        res.add(new Tokens.Str(val, line, col, XmlHelpers.unEscape(val)));
                    } else if (name.equals(XmlConstants.tagVector.value)) {
                        res.add(new Tokens.Vec("#{", line, col));
                    } else {
                        System.err.println("Error parsing Xml file " + name);
                        System.exit(1);
                    }
                }
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            return null;
        } catch (SAXException e) {
            return null;
        }
        // Return a TokenStream from the provided tokens
        //
        // NB: Remember: we didn't validate. For example, we didn't check that
        // there is exactly one EofToken, and that it is the very last
        // token.
        return new TokenStream(res);
    }
}