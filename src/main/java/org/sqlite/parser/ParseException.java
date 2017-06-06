package org.sqlite.parser;

public class ParseException extends RuntimeException {
	public ParseException(String message) {
		super(message);
	}

	public ParseException(String message, Object... args) {
		super(String.format(message, args));
	}
}
