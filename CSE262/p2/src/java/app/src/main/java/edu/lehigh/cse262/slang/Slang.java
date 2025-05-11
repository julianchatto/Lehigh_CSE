package edu.lehigh.cse262.slang;

import edu.lehigh.cse262.slang.Scanner.Scanner;
import edu.lehigh.cse262.slang.Scanner.XmlTokenWriter;
import edu.lehigh.cse262.slang.Scanner.Scanner.ScanError;

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

        // Create a SourceLoader and use it to start loading code and scanning
        // it. When given a file, we run exactly one iteration. Otherwise, we
        // run until we get an empty string of text from the sourceLoader.
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

                // SCAN mode: read the source code, turn it into tokens, print
                // the tokens as XML.
                if (parsedArgs.mode == Args.Modes.SCAN) {
                    try {
                        var tokens = new Scanner().scanTokens(codeToRun);
                        var writer = new XmlTokenWriter();
                        writer.writeXmlToStream(tokens, System.out);
                    } catch (ScanError se) {
                        System.out.println(se.getMessage());
                    }
                }
            } while (parsedArgs.fileName.equals(""));
        }
    }
}
