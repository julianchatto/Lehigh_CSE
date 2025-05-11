package edu.lehigh.cse262.slang;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

/**
 * SourceLoader is responsible for getting code as a String and passing it to
 * the rest of the interpreter. The code could come from a file, or from the
 * keyboard (stdin). It does extremely little syntactic analysis.
 */
public class SourceLoader implements AutoCloseable {
    /**
     * inputScanner is the mechanism we use for reading from stdin. It is a
     * good practice to make one instance of Scanner and re-use it throughout
     * the program, so we make it a field here. Note that we risk a resource
     * leak, though, so SourceLoader will implement AutoCloseable, and its
     * `close()` method will take care of cleanup. This works especially nicely
     * with try-with-resources blocks.
     */
    private Scanner inputScanner = new Scanner(System.in);

    /**
     * Get a line of text from stdin. Note that the line will be trimmed (on
     * the right and left).
     *
     * @param prompt A prompt to display, so the user knows to enter something
     *
     * @return A String that is expected to be some slang code, or `""`.
     */
    public String getFromStdin(String prompt) {
        System.out.print(prompt);
        System.out.flush();
        if (!inputScanner.hasNextLine())
            return "";
        return inputScanner.nextLine().trim();
    }

    /**
     * Read the entire contents of a file as a String. On error, a message will
     * be printed, and an empty string will be returned.
     *
     * @param fileName The name of the file to open
     *
     * @return An empty string (`""`) on any error. Otherwise, the contents of
     *         the file
     */
    public String getFile(String fileName) {
        try {
            Path path = Path.of(fileName);
            return Files.readString(path);
        } catch (Exception ex) {
            System.err.println("Error: Unable to open " + fileName);
            return "";
        }
    }

    /**
     * When the interpreter is done, it is supposed to call this, to release
     * resources. Since we implement AutoCloseable, try-with-resources does it
     * for us.
     */
    public void close() {
        inputScanner.close();
    }
}