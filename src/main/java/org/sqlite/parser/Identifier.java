package org.sqlite.parser;

public interface Identifier {
	static boolean isIdentifierStart(char c) {
		return (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 'z') || c > 0x7F;
	}
	static boolean isIdentifierContinue(char c) {
		return c == '$' || (c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' ||
				(c >= 'a' && c <= 'z') || c > 0x7F;
	}
}
