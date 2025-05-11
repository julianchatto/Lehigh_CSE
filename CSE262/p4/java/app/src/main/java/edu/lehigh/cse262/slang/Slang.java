package edu.lehigh.cse262.slang;

import edu.lehigh.cse262.slang.Interpreter.Env;
import edu.lehigh.cse262.slang.Interpreter.Interpreter;
import edu.lehigh.cse262.slang.Parser.XmlNodeReader;
import edu.lehigh.cse262.slang.Parser.XmlNodeReader.ParseError;
import edu.lehigh.cse262.slang.Parser.INodeVisitor.INodeVisitorError;

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
            // [CSE 262] You should be sure to understand why we make the
            // environment here
            var defaultEnvironment = Env.makeDefault();

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

                // INTERPRET mode: read an XML file, interpret it, print the
                // results as values.
                if (parsedArgs.mode == Args.Modes.INTERPRET) {
                    try {
                        var forest = new XmlNodeReader().parse(codeToRun);
                        Interpreter evaluator = new Interpreter(defaultEnvironment);
                        for (var e : forest) {
                            StringBuilder sb = new StringBuilder();
                            e.interpret(evaluator).stringify(sb);
                            System.out.println(sb.toString());
                        }
                    } catch (ParseError pe) {
                        System.out.println(pe.getMessage());
                    } catch (INodeVisitorError ie) {
                        System.out.println(ie.getMessage());
                    }
                }
            } while (parsedArgs.fileName.equals(""));
        }
    }
}
