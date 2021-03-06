package org.sqlite.parser.ast;

import org.sqlite.parser.TokenType;

public enum LiteralType {
	Blob, // Represents a literal blob (binary large object) expression.
	Float, // Represents a literal floating-point expression.
	Integer, // Represents a literal integer expression.
	Keyword, // NULL, CURRENT_DATE, ...
	String; // Represents a literal string expression.

	public static LiteralType from(short tt) {
		if (TokenType.TK_STRING == tt) {
			return String;
		} else if (TokenType.TK_BLOB == tt) {
			return Blob;
		} else if (TokenType.TK_FLOAT == tt) {
			return Float;
		} else if (TokenType.TK_INTEGER == tt) {
			return Integer;
		} else if (TokenType.TK_NULL == tt) {
			return Keyword;
		} else if (TokenType.TK_CTIME_KW == tt) {
			return Keyword;
		} else if (TokenType.TK_ON == tt) {
			return Keyword;
		} else if (TokenType.TK_DELETE == tt) {
			return Keyword;
		} else if (TokenType.TK_DEFAULT == tt) {
			return Keyword;
		}
		throw new IllegalArgumentException(java.lang.String.format("Unsupported Literal type: %s", TokenType.toString(tt)));
	}
}
