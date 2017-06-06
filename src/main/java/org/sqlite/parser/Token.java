package org.sqlite.parser;

public interface Token {
	/** @return The token type */
	int tokenType();
	/** @return Text of the token. */
	String text();
}
