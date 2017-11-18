package org.sqlite.parser;

import java.io.Reader;

import static java.lang.Character.isWhitespace;
import static org.sqlite.parser.Identifier.isIdentifierContinue;
import static org.sqlite.parser.Identifier.isIdentifierStart;
import static org.sqlite.parser.TokenType.*;

/**
 * A SQL tokenizer.
 * Adapted from [SQLite tokenizer](http://www.sqlite.org/src/artifact?ci=trunk&filename=src/tokenize.c)
 */
@SuppressWarnings("StatementWithEmptyBody")
class Tokenizer extends Scanner {
	private int lineno;
	private int column;
	private int nextColumn;
	private int nextLineno;

	private int tokenStart;
	private int tokenEnd;

	Tokenizer(Reader r) {
		super(r);
	}

	@Override
	void init(Reader r) {
		super.init(r);
		lineno = 1;
		column = 1;
		nextColumn = 1;
		nextLineno = 1;

		tokenStart = 0;
		tokenEnd = 0;
	}

	short split(char[] data, int start, int end, boolean atEOF) throws ScanException {
		if (atEOF && end == start) {
			return 0;
		}
		char c = data[start];
		tokenStart = start;
		start += 1;
		if (isWhitespace(c)) {
			// eat as much space as possible
			int i;
			for (i = start; i < end && isWhitespace(data[i]); i++) {}
			advance(i, data);
			return 0;
		}
		if (c == '-') {
			if (start < end) {
				if (data[start] == '-') {
					// eat comment
					int i;
					for (i = start + 1; i < end && (c=data[i]) != '\n'; i++) {}
					if (c == '\n' || atEOF) {
						advance(i + (atEOF ? 0 : 1), data);
						return 0;
					} // else ask more data until '\n'
				} else {
					advance(start, data);
					return TK_MINUS;
				}
			} else if (atEOF) {
				advance(start, data);
				return TK_MINUS;
			} // else ask more data
		} else if (c == '(') {
			advance(start, data);
			return TK_LP;
		} else if (c == ')') {
			advance(start, data);
			return TK_RP;
		} else if (c == ';') {
			advance(start, data);
			return TK_SEMI;
		} else if (c == '+') {
			advance(start, data);
			return TK_PLUS;
		} else if (c == '*') {
			advance(start, data);
			return TK_STAR;
		} else if (c == '/') {
			if (start < end) {
				if (data[start] == '*') {
					// eat comment
					int i;
					for (i = start + 1; i < end && (c != '*' || data[i] != '/'); ) {
						c = data[i];
						i++;
					}
					if (i < end && data[i] == '/') {
						advance(i + 1, data);
						return 0;
					} else if (atEOF) {
						throw new ScanException(ErrorCode.UnterminatedBlockComment);
					} // else ask more data until '*/'
				} else {
					advance(start, data);
					return TK_SLASH;
				}
			} else if (atEOF) {
				advance(start, data);
				return TK_SLASH;
			} // else ask more data
		} else if (c == '%') {
			advance(start, data);
			return TK_REM;
		} else if (c == '=') {
			if (start < end) {
				if (data[start] == '=') {
					advance(start + 1, data);
				} else {
					advance(start, data);
				}
				return TK_EQ;
			} else if (atEOF) {
				advance(start, data);
				return TK_EQ;
			} // else ask more data to fuse '==' or not
		} else if (c == '<') {
			if (start < end) {
				if (data[start] == '=') {
					advance(start + 1, data);
					return TK_LE;
				} else if (data[start] == '>') {
					advance(start + 1, data);
					return TK_NE;
				} else if (data[start] == '<') {
					advance(start + 1, data);
					return TK_LSHIFT;
				} else {
					advance(start, data);
					return TK_LT;
				}
			} else if (atEOF) {
				advance(start, data);
				return TK_LT;
			} // else ask more data
		} else if (c == '>') {
			if (start < end) {
				if (data[start] == '=') {
					advance(start + 1, data);
					return TK_GE;
				} else if (data[start] == '>') {
					advance(start + 1, data);
					return TK_RSHIFT;
				} else {
					advance(start, data);
					return TK_GT;
				}
			} else if (atEOF) {
				advance(start, data);
				return TK_GT;
			} // else ask more data
		} else if (c == '!') {
			if (start < end) {
				if (data[start] == '=') {
					advance(start + 1, data);
					return TK_NE;
				} else {
					throw new ScanException(ErrorCode.ExpectedEqualsSign);
				}
			} else if (atEOF) {
				throw new ScanException(ErrorCode.ExpectedEqualsSign);
			} // else ask more data
		} else if (c == '|') {
			if (start < end) {
				if (data[start] == '|') {
					advance(start + 1, data);
					return TK_CONCAT;
				} else {
					advance(start, data);
					return TK_BITOR;
				}
			} else if (atEOF) {
				advance(start, data);
				return TK_BITOR;
			} // else ask more data
		} else if (c == ',') {
			advance(start, data);
			return TK_COMMA;
		} else if (c == '&') {
			advance(start, data);
			return TK_BITAND;
		} else if (c == '~') {
			advance(start, data);
			return TK_BITNOT;
		} else if (c == '`' || c == '\'' || c == '"') {
			int i;
			char pc = 0;
			int escapedQuotes = 0;
			for (i = start; i < end; i++) {
				if (data[i] == c) {
					if (pc == c) { // escaped quote
						pc = 0;
						escapedQuotes++;
						continue;
					}
				} else {
					if (pc == c) {
						break;
					}
				}
				pc = data[i];
			}
			if (i < end || (atEOF && pc == c)) {
				advance(i, data);
				unescapeQuotes(data, start, i - 1, c, escapedQuotes);
				tokenStart = start; // do not include the quote in the token
				tokenEnd = i - 1 - escapedQuotes;
				return c == '\'' ? TK_STRING : TK_ID;
			} else if (atEOF) {
				throw new ScanException(ErrorCode.UnterminatedLiteral);
			} // else ask more data until closing quote
		} else if (c == '.') {
			if (start < end) {
				if (isDigit(data[start])) {
					return fractionalPart(data, start, end, atEOF);
				} else {
					advance(start, data);
					return TK_DOT;
				}
			} else if (atEOF) {
				advance(start, data);
				return TK_DOT;
			} // else ask more data
		} else if (isDigit(c)) {
			return number(data, start, end, atEOF);
		} else if (c == '[') {
			int i;
			for (i = start; i < end && (c=data[i]) != ']'; i++) {}
			if (c == ']') {
				advance(i + 1, data);
				tokenStart = start; // do not include the '['/']' in the token
				tokenEnd = i;
				return TK_ID;
			} else if (atEOF) {
				throw new ScanException(ErrorCode.UnterminatedBracket);
			} // else ask more data until ']'
		} else if (c == '?') {
			int i;
			for (i = start; i < end && isDigit(data[i]); i++) {}
			if (i < end || atEOF) {
				advance(i, data);
				tokenStart = start; // do not include the '?' in the token
				return TK_VARIABLE;
			} // else ask more data
		} else if (c == '$' || c == '@' || c == '#' || c == ':') {
			int i;
			for (i = start; i < end && isIdentifierContinue(data[i]); i++) {}
			if (i < end || atEOF) {
				if (i == start) {
					throw new ScanException(ErrorCode.BadVariableName);
				}
				advance(i, data);
				// '$' is included as part of the name
				return TK_VARIABLE;
			} // else ask more data
		} else if (isIdentifierStart(c)) {
			if (c == 'x' || c == 'X') {
				if (start < end) {
					if (data[start] == '\'') {
						return blobLiteral(data, start, end, atEOF);
					} else {
						return identifierish(data, start, end, atEOF);
					}
				} else if (atEOF) {
					advance(start, data);
					return TK_ID;
				} // else ask more data
			} else {
				return identifierish(data, start, end, atEOF);
			}
		} else {
			throw new ScanException(ErrorCode.UnrecognizedToken);
		}
		return 0; // ask more data
	}

	/**
	 * Decimal or Hexadecimal Integer or Real
	 * data[start-1] is a digit
	 */
	private short number(char[] data, int start, int end, boolean atEOF) throws ScanException {
		if (data[start - 1] == '0') {
			if (start < end) {
				if (data[start] == 'x' || data[start] == 'X') {
					return hexInteger(data, start + 1, end, atEOF);
				}
			} else if (atEOF) {
				advance(start, data);
				return TK_INTEGER;
			} else {
				return 0; // ask more data
			}
		}

		int i;
		for (i = start; i < end && isDigit(data[i]); i++) {}
		if (i < end) {
			if (data[i] == '.') {
				return fractionalPart(data, i + 1, end, atEOF);
			} else if (data[i] == 'e' || data[i] == 'E') {
				return exponentialPart(data, i + 1, end, atEOF);
			} else if (isIdentifierStart(data[i])) {
				throw new ScanException(ErrorCode.BadNumber);
			}
			advance(i, data);
			return TK_INTEGER;
		} else if (atEOF) {
			advance(i, data);
			return TK_INTEGER;
		} else {
			return 0; // ask more data
		}
	}
	/**
	 * data[start-2] is a zero
	 * data[start-1] is a 'x' or a 'X'
	 */
	private short hexInteger(char[] data, int start, int end, boolean atEOF) throws ScanException {
		int i;
		for (i = start; i < end && isHexaDigit(data[i]); i++) {}
		if (i < end) {
			// Must not be empty (Ox is invalid)
			if (i == start) {
				throw new ScanException(ErrorCode.MalformedHexInteger);
			}
			if (isIdentifierStart(data[i])) {
				throw new ScanException(ErrorCode.MalformedHexInteger);
			}
			advance(i, data);
			return TK_INTEGER;
		} else if (atEOF) {
			// Must not be empty (Ox is invalid)
			if (i == start) {
				throw new ScanException(ErrorCode.MalformedHexInteger);
			}
			advance(i, data);
			return TK_INTEGER;
		} else {
			return 0; // ask more data
		}
	}
	/**
	 * Real
	 * (data[start-1] is a dot,
	 * data[start] is a digit)
	 * or
	 * (data[start-2] is a digit,
	 * data[start-1] is a dot)
	 */
	private short fractionalPart(char[] data, int start, int end, boolean atEOF) throws ScanException {
		int i;
		for (i = start; i < end && isDigit(data[i]); i++) {}
		if (i < end) {
			if (data[i] == 'e' || data[i] == 'E') {
				return exponentialPart(data, i + 1, end, atEOF);
			} else if (isIdentifierStart(data[i])) {
				throw new ScanException(ErrorCode.BadNumber);
			}
			advance(i, data);
			return TK_FLOAT;
		} else if (atEOF) {
			advance(i, data);
			return TK_FLOAT;
		} else {
			return 0; // ask more data
		}
	}
	/**
	 * Real
	 * data[start-2] is a digit,
	 * data[start-1] is a 'e' or a 'E'
	 */
	private short exponentialPart(char[] data, int start, int end, boolean atEOF) throws ScanException {
		if (start < end) {
			if (data[start] == '+' || data[start] == '-') {
				start++;
			}
			int i;
			for (i = start; i < end && isDigit(data[i]); i++) {}
			if (i < end || atEOF) {
				// Must not be empty
				if (i == start) {
					throw new ScanException(ErrorCode.BadNumber);
				}
				if (isIdentifierStart(data[i])) {
					throw new ScanException(ErrorCode.BadNumber);
				}
				advance(i, data);
				return TK_FLOAT;
			}
		} else if (atEOF) {
			throw new ScanException(ErrorCode.BadNumber);
		}
		return 0; // ask more data
	}

	/**
	 * data[start-1] is a 'x' or 'X'
	 * data[start] is a '\''
	 */
	private short blobLiteral(char[] data, int start, int end, boolean atEOF) throws ScanException {
		int i, n = 0;
		for (i = start + 1; i < end && isHexaDigit(data[i]); i++) {
			n++;
		}
		if (i < end) {
			if (data[i] != '\'' || n % 2 != 0) {
				throw new ScanException(ErrorCode.MalformedBlobLiteral);
			}
			advance(i + 1, data);
			tokenStart = start + 1;
			tokenEnd = i;
			return TK_BLOB;
		} else if (atEOF) {
			throw new ScanException(ErrorCode.MalformedBlobLiteral);
		} else {
			return 0; // ask more data
		}
	}

	/**
	 * start-1 is the start of the identifier/keyword.
	 */
	private short identifierish(char[] data, int start, int end, boolean atEOF) throws ScanException {
		int i;
		for (i = start; i < end && isIdentifierContinue(data[i]); i++) {}
		if (i < end || atEOF) {
			advance(i, data);
			// search for a keyword first; if none are found, this is an Id
			Short keyword = Keyword.tokenType(new String(data, start - 1, i - (start - 1)));
			if (keyword == null) {
				return TK_ID;
			}
			return keyword;
		} else {
			return 0; // ask more data
		}
	}

	/** @return Text of the token. */
	public String text() {
		return subSequence(tokenStart, tokenEnd);
	}

	/**
	 * Returns current line number
	 */
	int lineno() {
		return lineno;
	}

	/**
	 * Returns current column.
	 */
	int column() {
		return column;
	}

	private void advance(int n, char[] data) throws ScanException {
		super.advance(n);
		tokenEnd = n;
		lineno = nextLineno;
		column = nextColumn;
		for (int i = tokenStart; i < tokenEnd; i++) {
			if (data[i] == '\n') {
				nextLineno++;
				nextColumn = 1;
			}
			nextColumn++;
		}
	}

	private static boolean isDigit(char c) {
		return c >= '0' && c <= '9';
	}

	private static boolean isHexaDigit(char c) {
		return (c >= '0' && c <= '9') || (c >= 'A' && c <= 'F') || (c >= 'a' && c <= 'f');
	}

	private static void unescapeQuotes(char[] data, int start, int end, char quote, int count) {
		if (count == 0) {
			return;
		}
		for (int i = start, j = start; i < end; i++, j++) {
			data[j] = data[i];
			if (data[i] == quote) {
				i++;
			}
		}
	}
}