import sys
import slang_scanner
import slang_scanner_xml


def getFromStdin(prompt):
    """Read a line from standard input (typically the keyboard).  If nothing is
    provided, return an empty string."""
    try:
        return input(prompt).rstrip()
    except EOFError:
        # we print a newline, because this is going to cause an exit, and it
        # will be a bit more uniform if all ways of exiting have a newline
        # before the shell is back in control
        print("")
        return ""


def getFile(filename):
    """Read a file and return its contents as a single string"""
    try:
        source_file = open(filename, "r")
        code = source_file.read()
        source_file.close()
        return code
    except FileNotFoundError:
        print("Error: file not found", file=sys.stderr)
        return ""


def printHelp():
    """Print a help message"""
    print("slang -- An interpreter for a Scheme-like language (Python version)")
    print("  Usage: slang [mode] [filename]")
    print("    * If no filename is given, a REPL will run")
    print("    * If a filename is given, the entire file will be loaded and evaluated")
    print("  Modes:")
    print("    -help             Display this message and exit")
    print("    -scan             Turn slang code into XML tokens")


def main(args):
    """Run the Scheme interpreter"""
    # Parse the command-line arguments.  Make sure exactly one valid mode is
    # given, and at most one filename
    filename, mode, numModes, numFiles = "", "", 0, 0
    for a in args:
        if a in ["-help", "-scan"]:
            mode = a
            numModes += 1
        else:
            filename = a
            numFiles += 1
    if numModes != 1 or numFiles > 1 or mode == "-help":
        return printHelp()

    # Run the REPL loop, but only once if we have a valid filename
    while True:
        # Get some code
        codeToRun = ""
        if filename != "":
            codeToRun = getFile(filename)
        else:
            codeToRun = getFromStdin(":> ")
            if codeToRun == "":
                break

        # SCAN mode
        if mode == "-scan":
            tokens = slang_scanner.scanTokens(codeToRun)
            print(slang_scanner_xml.xmlToString(tokens))

        # exit if we just processed a file
        if filename != "":
            break


# In python, this is how we get main() to run when we invoke this program via
# `python3 slang.py ...`
if __name__ == "__main__":
    main(sys.argv[1:])
