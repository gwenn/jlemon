package org.sqlite.parser;

import com.google.errorprone.annotations.FormatMethod;
import com.google.errorprone.annotations.FormatString;

@SuppressWarnings("serial")
public class ParseException extends RuntimeException {
	public ParseException(String message) {
		super(message);
	}

	@FormatMethod
	public ParseException(@FormatString String message, Object... args) {
		super(String.format(message, args));
	}
}
