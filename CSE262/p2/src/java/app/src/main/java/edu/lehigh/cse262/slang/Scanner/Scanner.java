package edu.lehigh.cse262.slang.Scanner;

import java.util.ArrayList;
import java.util.List;

import edu.lehigh.cse262.slang.Scanner.TokenStream;
import edu.lehigh.cse262.slang.Scanner.Tokens;


/**
	* Scanner is responsible for taking a string that is the source code of a
	* program, and transforming it into a stream of tokens.
	*
	* It is tempting to think "if my code doesn't crash when I give it good input,
	* then I have done a good job". However, a good scanner needs to be able to
	* handle incorrect programs. The bare minimum is that the scanner should not
	* crash if the input is invalid. Even better is if the scanner can print a
	* useful diagnostic message about the point in the source code that was
	* incorrect. Best, of course, is if the scanner can somehow "recover" and keep
	* on scanning, so that it can report additional syntax errors.
	*
	* **In this class, "even better" is good enough for full credit**
	*
	* This is the only java file that you need to edit in p2. The reference
	* solution has ~405 lines of code (plus 41 blank lines and ~92 lines of
	* comments). Your code may be longer or shorter... the line-of-code count is
	* just a reference.
	*
	* You are allowed to add private methods and fields to this class. You may also
	* add imports.
	*/
public class Scanner {
	/** The index of the first character of the in-progress token */
	private int start = 0;

	/** Count the newlines we consume */
	private int current_line = 1;

	/** index in `source` where current line begins */
	private int line_start_char = 0;

	/** Compute the column number where the current token starts */
	private int col() {
		return start - line_start_char + 1;
	}

	private char[] source;
	private int n;
	private int index = 0;
	private List<Tokens.Token> tokens = new ArrayList<>();
	private final String SYMBOLS = "!$%&*/:<=>?~_^";
	private final String MATH = "+-.";
	private boolean inQuote = false;


	/**
		* An exception to capture the situation where the scanner encounters an
		* error
		*/
	public class ScanError extends Exception {
		/** The line number where the error arose */
		public int line;
		/** The column number where the column arose */
		public int col;

		/** Construct a ScanError from a message and a location */
		public ScanError(String message) {
			super(message);
			line = current_line;
			col = col();
		}
	}

	/**
		* scanTokens works through the source and transforms it into a list of
		* tokens. It adds an EOF token at the end, unless there is an error.
		*
		* [CSE 262] You will probably find it easiest to implement the different
		* "nodes" of the FSM as functions. It is also helpful to have an enum type
		* to represent the current state of the finite state machine.
		*/
	public TokenStream scanTokens(String source) throws ScanError {
		this.source = source.toCharArray();
		n = this.source.length - 1;

		while (!isEnd()) {
			start = index;
			scan();
		}

		tokens.add(new Tokens.Eof("", current_line, col()));
		return new TokenStream(tokens);
	}


	/**
	 * Deterimine if we have reached the end of the source
	 * @return true if we have reached the end of the source
	 */
	private boolean isEnd() {
		return index >= n;
	}

	/**
	 * Return the current character and advance the index
	 * @return the current character
	 */
	private char advance() {
		return source[index++];
	}

	/**
	 * Return the current character without advancing the index
	 * @return the current character
	 */
	private char peek() {
		return source[index];
	}

	/**
	 * Return the next character without advancing the index
	 * @return the next character
	 */
	private char peekNext() {
		return index + 1 >= n ? '\0' : source[index + 1];
	}   

	/**
	 * Scan the source and produce tokens
	 * @throws ScanError
	 */
	private void scan() throws ScanError {
		char c = advance();
		switch(c) {
			case ')':
				tokens.add(new Tokens.RightParen(")", current_line, col()));
				break;
			case '(':
				tokens.add(new Tokens.LeftParen("(", current_line, col()));
				break;
			case '\'':
				tokens.add(new Tokens.Abbrev("'", current_line, col()));
				inQuote = !inQuote;
				break;
			case '.':
				handleDot();
				break;
			case ';':
				// Skip comments
				while (peek() != '\n' && !isEnd()) {
					advance();
				}
				break;
			case ' ':
			case '\r':
			case '\t':
				// do nothing
				break;
			case '\n':
				current_line++;
				line_start_char = index;
				break;
			case '"':
				handleString();
				break;
			case '#':
				handleHashtag();
				break;
			case '+':
			case '-':
				if (Character.isDigit(peek())) { // number
					handleNumber();
				} else { // start of an identifier
					handleIdentifier();
				}
				break;
			default:
				if (Character.isDigit(c)) { // number
					handleNumber();
				} else if (Character.isLetter(c) && SYMBOLS.indexOf(c) == -1 && MATH.indexOf(c) == -1) { // potential keyword
					try { 
						handleKeyword();
					} catch (ScanError e) { // if it is not a keyword, then we can see if it is an identifier
						handleIdentifier();
					}
				} else if (SYMBOLS.indexOf(c) != -1 || Character.isAlphabetic(c)) { // identifier
					handleIdentifier();
				} else { // invalid character
					throw new ScanError("Invalid Character");
				}
				break;
		}
	}

	/**
	 * Handle the case where we encounter a '.'
	 * @throws ScanError
	 */
	private void handleDot() throws ScanError {
		if (Character.isDigit(peek())) { // number
			handleNumber();
		} else if (isEnd() || inQuote) { // if we are in a quote or at then end, then we can assume it is a dot
			tokens.add(new Tokens.Dot(".", current_line, col()));
		} else { // invalid character
			throw new ScanError("Internal Error");
		}
	}

	/**
	 * Handle the case where we encounter a '#'
	 * @throws ScanError
	 */
	private void handleHashtag() throws ScanError {
		if (isEnd()) { // if we are at the end, then we have an invalid character
			throw new ScanError("Unexpected end after '#'");
		}
		char next = peek();
		if (next == '(') { // vector token
			// consume '(' and produce a VEC token.
			advance();
			tokens.add(new Tokens.Vec("#(" , current_line, col()));
		} else if (next == 't' || next == 'f') { // boolean token
			char bool = advance();
			tokens.add(new Tokens.Bool("#" + bool, current_line, col(), bool == 't'));
		} else if (next == '\\') {
			advance(); // consume '\'
			switch (peek()) { // need to see if we have a \newline, \space, or \tab
				case 'n':
					// newline
					for (char c: "newline".toCharArray()) {
						if (peek() != c) {
							throw new ScanError("Invalid character literal");
						}
						advance();
					}
					tokens.add(new Tokens.Char("#\\n", current_line, col(), '\n'));
					break;
				case 't':
					// tab
					for (char c: "tab".toCharArray()) {
						if (peek() != c) {
							throw new ScanError("Invalid character literal");
						}
						advance();
					}
					tokens.add(new Tokens.Char("#\\t", current_line, col(), '\t'));
					break;
				case 's':
					// space
					for (char c: "space".toCharArray()) {
						if (peek() != c) {
							throw new ScanError("Invalid character literal");
						}
						advance();
					}
					tokens.add(new Tokens.Char("#\\ ", current_line, col(), ' '));
					break;
				default:
					if (isEnd()) {
						throw new ScanError("Invalid character after '#\\'");
					}
					char value = advance();
					if (Character.isDigit(value)) { // if we have a digit, then we have an invalid character literal
						throw new ScanError("Invalid character literal");
					}
					String text = "#\\" + value;
					tokens.add(new Tokens.Char(text, current_line, col(), value));
			}
		} else if (next == ' ' || next == '\t' || next == '\n' || next == '\r') { // whitespace
			char value = advance();
			String text = "#\\" + value;
			tokens.add(new Tokens.Char(text, current_line, col(), value));
		} else {
			throw new ScanError("Invalid character after '#'");
		}
	}

	/**
	 * Handle the case where we encounter a number
	 * @throws ScanError
	 */
	private void handleNumber() throws ScanError {
		if (peek() == '.' && !inQuote && !Character.isDigit(source[index - 1])) {
			throw new ScanError("Internal Error");
		}
		
		while (Character.isDigit(peek())) { // consume all digits
			advance();
		}

		if (peek() == '.') { // floating point number
			if (!Character.isDigit(peekNext())) { // if the next character is not a digit, then we have an invalid number
				throw new ScanError("DBL cannot end with '.'");
			}
			advance();
			while (Character.isDigit(advance())); // consume all digits

			String tokenText = new String(source, start, index - start); // build the token text
			try {
				double val = Double.parseDouble(tokenText);
				tokens.add(new Tokens.Dbl(tokenText, current_line, col(), val));
			} catch (NumberFormatException e) {
				throw new ScanError("Internal Error");
			}
		} else { // integer
			if (Character.isAlphabetic(peek())) { // if the next character is a letter, then we have an invalid number
				throw new ScanError("Invalid character in INT");
			}

			String tokenText = new String(source, start, index - start); // build the token text

			try {
				int val = Integer.parseInt(tokenText);
				tokens.add(new Tokens.Int(tokenText, current_line, col(), val));
			} catch (NumberFormatException e) {
				throw new ScanError("Internal Error");
			}
		}
	}

	/**
	 * Handle the case where we encounter a keyword
	 * @throws ScanError
	 */
	private void handleKeyword() throws ScanError {
		char c = peek();
		while (Character.isLetter(c)) { // consume all letters
			advance();
			c = peek();
		}

		if (!isEnd() && peek() == '!') { // to handle set!
			index++;
		} 

		String text = new String(source, start, index - start); // build the text
		switch (text) {
			case "and":
				tokens.add(new Tokens.And(text, current_line, col()));
				break;
			case "begin":
				tokens.add(new Tokens.Begin(text, current_line, col()));
				break;
			case "cond":
				tokens.add(new Tokens.Cond(text, current_line, col()));
				break;
			case "define":
				tokens.add(new Tokens.Define(text, current_line, col()));
				break;
			case "if":
				tokens.add(new Tokens.If(text, current_line, col()));
				break;
			case "lambda":
				tokens.add(new Tokens.Lambda(text, current_line, col()));
				break;
			case "or":
				tokens.add(new Tokens.Or(text, current_line, col()));
				break;
			case "quote":
				tokens.add(new Tokens.Quote(text, current_line, col()));
				break;
			case "set!":
				tokens.add(new Tokens.Set(text, current_line, col()));
				break;
			case "let":
				tokens.add(new Tokens.Let(text, current_line, col()));
				break;
			case "apply":
				tokens.add(new Tokens.Apply(text, current_line, col()));
				break;
			default: // not a keyword
				throw new ScanError("Invalid Character");
		}
	}

	/**
	 * Handle the case where we encounter an identifier
	 * @throws ScanError
	 */
	private void handleIdentifier() throws ScanError {
		char c = peek();
		while (Character.isLetter(c) || SYMBOLS.indexOf(c) != -1 || Character.isDigit(c) || MATH.indexOf(c) != -1) { // consume all valid characters
			advance();
			c = peek();
		}
		if (!isEnd()) { // if we are not at the end, then we have an invalid character
			throw new ScanError("Invalid character in ID");
		}
		String text = new String(source, start, index - start); // build the token text
		if (text.length() != 1 && (text.charAt(0) == '+' || text.charAt(0) == '-')) { // if the identifier starts with a + or -, then we have an invalid identifier
			throw new ScanError("Invalid Character after +/-");
		}
		tokens.add(new Tokens.Identifier(text, current_line, col()));
	}

	/**
	 * Handle the case where we encounter a string
	 * @throws ScanError
	 */
	private void handleString() throws ScanError {
		StringBuilder sb = new StringBuilder();
		
		while (peek() != '"' && !isEnd()) { // consume all characters until we reach the end of the string
			if (peek() == '\n') {
				current_line++;
				line_start_char = index + 1;
			}
			if (peek() == '\\') { // handle escape characters
				advance();
				if (isEnd()) { // if we are at the end, then we have an invalid string
					throw new ScanError("Unterminated string");
				}
				switch (advance()) {
					case 'n': // newline
						sb.append('\n');
						break;
					case 't': // tab
						sb.append('\t');
						break;
					case 'r': // carriage return
						sb.append('\r');
						break;
					case '\\': // backslash
						sb.append('\\');
						break;
					case '"': // double quote
						sb.append('"');
						break;
					default:
						throw new ScanError("Invalid Escape Character in String ");
				}
			} else { // everything else
				sb.append(advance()); 
			}
		} 
		if (peek() != '"') { // if we do not have a closing quote, then we have an invalid string
			throw new ScanError("Invalid Whitespace in String");
		}
		advance();

		tokens.add(new Tokens.Str(new String(source, start, index - start), current_line, col(), sb.toString()));
	}
}  
