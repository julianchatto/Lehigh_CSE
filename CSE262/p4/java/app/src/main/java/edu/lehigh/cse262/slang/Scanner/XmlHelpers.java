package edu.lehigh.cse262.slang.Scanner;

/** A wrapper for some helpful XML-related string processing functions */
public class XmlHelpers {
    /** Escape a string for outputting it to XML as an attribute */
    public static String escape(String s) {
        var sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (c == '\\')
                sb.append("\\\\");
            else if (c == '\t')
                sb.append("\\t");
            else if (c == '\n')
                sb.append("\\n");
            else if (c == '\'')
                sb.append("\\'");
            else
                sb.append(c);
        }
        return sb.toString();
    }

    /** Remove escape characters from a string when reading from XML */
    public static String unEscape(String s) {
        // We need to go char by char to get it right
        var sb = new StringBuilder();
        boolean in_escape = false;
        for (char c : s.toCharArray()) {
            if (!in_escape) {
                if (c != '\\')
                    sb.append(c);
                else
                    in_escape = true;
            } else {
                if (c == '\\')
                    sb.append("\\");
                else if (c == 't')
                    sb.append("\t");
                else if (c == 'n')
                    sb.append("\n");
                else if (c == '\'')
                    sb.append("'");
                else {
                    System.err.println("Invalid string?!?");
                    System.exit(1);
                }
                in_escape = false;
            }
        }
        return sb.toString();
    }

}
