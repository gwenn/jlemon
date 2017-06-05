package org.sqlite.parser;

public interface Token {
	/** @return The token type returned by {@link #split} function */
	int tokenType();
	/** @return Text of the token. */
	String text();
}
