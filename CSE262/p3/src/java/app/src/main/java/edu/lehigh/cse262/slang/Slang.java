package edu.lehigh.cse262.slang;

import edu.lehigh.cse262.slang.Parser.Parser;
import edu.lehigh.cse262.slang.Parser.XmlNodeWriter;
import edu.lehigh.cse262.slang.Parser.INodeVisitor.INodeVisitorError;
import edu.lehigh.cse262.slang.Parser.Parser.ParseError;
import edu.lehigh.cse262.slang.Scanner.XmlTokenReader;

/** Slang is the entry point into our interpreter */
public class Slang {
    public static void main(String[] args) {
        // Get the command-line arguments. If help is requested or needed,
        // print help and exit immediately.
        var parsedArgs = new Args(args);
        if (parsedArgs.mode == Args.Modes.HELP) {
            Args.printHelp();
            return;
        }

        // Create a SourceLoader and use it to start loading code and scanning /
        // parsing / interpreting it. When given a file, we run exactly one
        // iteration. Otherwise, we run until we get an empty string of text
        // from the sourceLoader.
        //
        // [CSE 262] You should be sure to understand this try-with-resources
        // syntax.
        try (var sourceLoader = new SourceLoader()) {
            do {
                // Get the code to run, break if no code is available
                String codeToRun;
                if (!parsedArgs.fileName.equals("")) {
                    codeToRun = sourceLoader.getFile(parsedArgs.fileName);
                } else {
                    codeToRun = sourceLoader.getFromStdin(":> ");
                    if (codeToRun.equals(""))
                        break;
                }

                // PARSE mode: read an XML file, turn it into an AST, print the
                // AST as XML.
                if (parsedArgs.mode == Args.Modes.PARSE) {
                    try {
                        var reader = new XmlTokenReader();
                        var tokens = reader.readTokensFromXml(codeToRun);
                        var ast = new Parser().parse(tokens);
                        var writer = new XmlNodeWriter();
                        writer.astToXml(ast, System.out);
                    } catch (ParseError pe) {
                        System.out.println(pe.getMessage());
                    } catch (INodeVisitorError ie) {
                        ie.printStackTrace();
                    }
                }
            } while (parsedArgs.fileName.equals(""));
        }
    }
}
