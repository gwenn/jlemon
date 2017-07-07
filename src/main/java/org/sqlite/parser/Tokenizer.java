package org.sqlite.parser;

import java.io.Reader;
import java.util.Map;
import java.util.TreeMap;

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
	private final static Map<String, Integer> KEYWORDS = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

	static {
		KEYWORDS.put("ABORT", TK_ABORT);
		KEYWORDS.put("ACTION", TK_ACTION);
		KEYWORDS.put("ADD", TK_ADD);
		KEYWORDS.put("AFTER", TK_AFTER);
		KEYWORDS.put("ALL", TK_ALL);
		KEYWORDS.put("ALTER", TK_ALTER);
		KEYWORDS.put("ANALYZE", TK_ANALYZE);
		KEYWORDS.put("AND", TK_AND);
		KEYWORDS.put("AS", TK_AS);
		KEYWORDS.put("ASC", TK_ASC);
		KEYWORDS.put("ATTACH", TK_ATTACH);
		KEYWORDS.put("AUTOINCREMENT", TK_AUTOINCR);
		KEYWORDS.put("BEFORE", TK_BEFORE);
		KEYWORDS.put("BEGIN", TK_BEGIN);
		KEYWORDS.put("BETWEEN", TK_BETWEEN);
		KEYWORDS.put("BY", TK_BY);
		KEYWORDS.put("CASCADE", TK_CASCADE);
		KEYWORDS.put("CASE", TK_CASE);
		KEYWORDS.put("CAST", TK_CAST);
		KEYWORDS.put("CHECK", TK_CHECK);
		KEYWORDS.put("COLLATE", TK_COLLATE);
		KEYWORDS.put("COLUMN", TK_COLUMNKW);
		KEYWORDS.put("COMMIT", TK_COMMIT);
		KEYWORDS.put("CONFLICT", TK_CONFLICT);
		KEYWORDS.put("CONSTRAINT", TK_CONSTRAINT);
		KEYWORDS.put("CREATE", TK_CREATE);
		KEYWORDS.put("CROSS", TK_JOIN_KW);
		KEYWORDS.put("CURRENT_DATE", TK_CTIME_KW);
		KEYWORDS.put("CURRENT_TIME", TK_CTIME_KW);
		KEYWORDS.put("CURRENT_TIMESTAMP", TK_CTIME_KW);
		KEYWORDS.put("DATABASE", TK_DATABASE);
		KEYWORDS.put("DEFAULT", TK_DEFAULT);
		KEYWORDS.put("DEFERRABLE", TK_DEFERRABLE);
		KEYWORDS.put("DEFERRED", TK_DEFERRED);
		KEYWORDS.put("DELETE", TK_DELETE);
		KEYWORDS.put("DESC", TK_DESC);
		KEYWORDS.put("DETACH", TK_DETACH);
		KEYWORDS.put("DISTINCT", TK_DISTINCT);
		KEYWORDS.put("DROP", TK_DROP);
		KEYWORDS.put("EACH", TK_EACH);
		KEYWORDS.put("ELSE", TK_ELSE);
		KEYWORDS.put("END", TK_END);
		KEYWORDS.put("ESCAPE", TK_ESCAPE);
		KEYWORDS.put("EXCEPT", TK_EXCEPT);
		KEYWORDS.put("EXCLUSIVE", TK_EXCLUSIVE);
		KEYWORDS.put("EXISTS", TK_EXISTS);
		KEYWORDS.put("EXPLAIN", TK_EXPLAIN);
		KEYWORDS.put("FAIL", TK_FAIL);
		KEYWORDS.put("FOR", TK_FOR);
		KEYWORDS.put("FOREIGN", TK_FOREIGN);
		KEYWORDS.put("FROM", TK_FROM);
		KEYWORDS.put("FULL", TK_JOIN_KW);
		KEYWORDS.put("GLOB", TK_LIKE_KW);
		KEYWORDS.put("GROUP", TK_GROUP);
		KEYWORDS.put("HAVING", TK_HAVING);
		KEYWORDS.put("IF", TK_IF);
		KEYWORDS.put("IGNORE", TK_IGNORE);
		KEYWORDS.put("IMMEDIATE", TK_IMMEDIATE);
		KEYWORDS.put("IN", TK_IN);
		KEYWORDS.put("INDEX", TK_INDEX);
		KEYWORDS.put("INDEXED", TK_INDEXED);
		KEYWORDS.put("INITIALLY", TK_INITIALLY);
		KEYWORDS.put("INNER", TK_JOIN_KW);
		KEYWORDS.put("INSERT", TK_INSERT);
		KEYWORDS.put("INSTEAD", TK_INSTEAD);
		KEYWORDS.put("INTERSECT", TK_INTERSECT);
		KEYWORDS.put("INTO", TK_INTO);
		KEYWORDS.put("IS", TK_IS);
		KEYWORDS.put("ISNULL", TK_ISNULL);
		KEYWORDS.put("JOIN", TK_JOIN);
		KEYWORDS.put("KEY", TK_KEY);
		KEYWORDS.put("LEFT", TK_JOIN_KW);
		KEYWORDS.put("LIKE", TK_LIKE_KW);
		KEYWORDS.put("LIMIT", TK_LIMIT);
		KEYWORDS.put("MATCH", TK_MATCH);
		KEYWORDS.put("NATURAL", TK_JOIN_KW);
		KEYWORDS.put("NO", TK_NO);
		KEYWORDS.put("NOT", TK_NOT);
		KEYWORDS.put("NOTNULL", TK_NOTNULL);
		KEYWORDS.put("NULL", TK_NULL);
		KEYWORDS.put("OF", TK_OF);
		KEYWORDS.put("OFFSET", TK_OFFSET);
		KEYWORDS.put("ON", TK_ON);
		KEYWORDS.put("OR", TK_OR);
		KEYWORDS.put("ORDER", TK_ORDER);
		KEYWORDS.put("OUTER", TK_JOIN_KW);
		KEYWORDS.put("PLAN", TK_PLAN);
		KEYWORDS.put("PRAGMA", TK_PRAGMA);
		KEYWORDS.put("PRIMARY", TK_PRIMARY);
		KEYWORDS.put("QUERY", TK_QUERY);
		KEYWORDS.put("RAISE", TK_RAISE);
		KEYWORDS.put("RECURSIVE", TK_RECURSIVE);
		KEYWORDS.put("REFERENCES", TK_REFERENCES);
		KEYWORDS.put("REGEXP", TK_LIKE_KW);
		KEYWORDS.put("REINDEX", TK_REINDEX);
		KEYWORDS.put("RELEASE", TK_RELEASE);
		KEYWORDS.put("RENAME", TK_RENAME);
		KEYWORDS.put("REPLACE", TK_REPLACE);
		KEYWORDS.put("RESTRICT", TK_RESTRICT);
		KEYWORDS.put("RIGHT", TK_JOIN_KW);
		KEYWORDS.put("ROLLBACK", TK_ROLLBACK);
		KEYWORDS.put("ROW", TK_ROW);
		KEYWORDS.put("SAVEPOINT", TK_SAVEPOINT);
		KEYWORDS.put("SELECT", TK_SELECT);
		KEYWORDS.put("SET", TK_SET);
		KEYWORDS.put("TABLE", TK_TABLE);
		KEYWORDS.put("TEMP", TK_TEMP);
		KEYWORDS.put("TEMPORARY", TK_TEMP);
		KEYWORDS.put("THEN", TK_THEN);
		KEYWORDS.put("TO", TK_TO);
		KEYWORDS.put("TRANSACTION", TK_TRANSACTION);
		KEYWORDS.put("TRIGGER", TK_TRIGGER);
		KEYWORDS.put("UNION", TK_UNION);
		KEYWORDS.put("UNIQUE", TK_UNIQUE);
		KEYWORDS.put("UPDATE", TK_UPDATE);
		KEYWORDS.put("USING", TK_USING);
		KEYWORDS.put("VACUUM", TK_VACUUM);
		KEYWORDS.put("VALUES", TK_VALUES);
		KEYWORDS.put("VIEW", TK_VIEW);
		KEYWORDS.put("VIRTUAL", TK_VIRTUAL);
		KEYWORDS.put("WHEN", TK_WHEN);
		KEYWORDS.put("WHERE", TK_WHERE);
		KEYWORDS.put("WITH", TK_WITH);
		KEYWORDS.put("WITHOUT", TK_WITHOUT);
	}

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
	}

	int split(char[] data, int start, int end, boolean atEOF) throws ScanException {
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
						advance(i+(atEOF ? 0 : 1), data);
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
		} else if (c == '(')  {
			advance(start, data);
			return TK_LP;
		} else if (c == ')')  {
			advance(start, data);
			return TK_RP;
		} else if (c == ';')  {
			advance(start, data);
			return TK_SEMI;
		} else if (c == '+')  {
			advance(start, data);
			return TK_PLUS;
		} else if (c == '*')  {
			advance(start, data);
			return TK_STAR;
		} else if (c == '/')  {
			if (start < end) {
				if (data[start] == '*') {
					// eat comment
					int i;
					for (i = start + 1; i < end && (c != '*' || data[i] != '/');) {
						c = data[i];
						i++;
					}
					if (i < end && data[i] == '/') {
						advance(i+1, data);
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
					advance(start+1, data);
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
					advance(start+1, data);
					return TK_LE;
				} else if (data[start] == '>') {
					advance(start+1, data);
					return TK_NE;
				} else if (data[start] == '<') {
					advance(start+1, data);
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
					advance(start+1, data);
					return TK_GE;
				} else if (data[start] == '>') {
					advance(start+1, data);
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
					advance(start+1, data);
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
					advance(start+1, data);
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
			for (i = start; i < end; i++) {
				if (data[i] == c) {
					if (pc == c) { // escaped quote
						pc = 0;
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
				tokenStart=start; // do not include the quote in the token
				tokenEnd=i-1;
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
				advance(i+1, data);
				tokenStart=start; // do not include the '['/']' in the token
				tokenEnd=i;
				return TK_ID;
			} else if (atEOF) {
				throw new ScanException(ErrorCode.UnterminatedBracket);
			} // else ask more data until ']'
		} else if (c == '?') {
			int i;
			for (i = start; i < end && isDigit(data[i]); i++) {}
			if (i < end || atEOF) {
				advance(i, data);
				tokenStart=start; // do not include the '?' in the token
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
	private int number(char[] data, int start, int end, boolean atEOF) throws ScanException {
		if (data[start-1] == '0') {
			if (start < end) {
				if (data[start] == 'x' || data[start] == 'X') {
					return hexInteger(data, start+1, end, atEOF);
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
				return fractionalPart(data, i+1, end, atEOF);
			} else if (data[i] == 'e' || data[i] == 'E') {
				return exponentialPart(data, i+1, end, atEOF);
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
	private int hexInteger(char[] data, int start, int end, boolean atEOF) throws ScanException {
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
	private int fractionalPart(char[] data, int start, int end, boolean atEOF) throws ScanException {
		int i;
		for (i = start; i < end && isDigit(data[i]); i++) {}
		if (i < end) {
			if (data[i] == 'e' || data[i] == 'E') {
				return exponentialPart(data, i+1, end, atEOF);
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
	private int exponentialPart(char[] data, int start, int end, boolean atEOF) throws ScanException {
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
	private int blobLiteral(char[] data, int start, int end, boolean atEOF) throws ScanException {
		int i, n = 0;
		for (i = start + 1; i < end && isHexaDigit(data[i]); i++) {
			n++;
		}
		if (i < end) {
			if (data[i] != '\'' || n % 2 != 0) {
				throw new ScanException(ErrorCode.MalformedBlobLiteral);
			}
			advance(i+1, data);
			tokenStart=start+1;
			tokenEnd=i;
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
	private int identifierish(char[] data, int start, int end, boolean atEOF) throws ScanException {
		int i;
		for (i = start; i < end && isIdentifierContinue(data[i]); i++) {}
		if (i < end || atEOF) {
			advance(i, data);
			// search for a keyword first; if none are found, this is an Id
			Integer keyword = KEYWORDS.get(new String(data, start-1, i-(start-1)));
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

	void advance(int n, char[] data) throws ScanException {
		super.advance(n);
		tokenEnd=n;
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
}