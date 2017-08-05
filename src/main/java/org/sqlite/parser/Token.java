package org.sqlite.parser;

public class Token {
	private final int type;
	private String text;

	public Token(int type, String text) {
		this.type = type;
		this.text = text;
	}

	/** @return The token type */
	public int tokenType() {
		return type;
	}
	/** @return Text of the token. */
	public String text() {
		return text;
	}

	public void append(Token t) {
		text = text + ' ' + t.text;
	}

	@Override
	public String toString() {
		return "Token{" +
				"type=" + type +
				", text='" + text + '\'' +
				'}';
	}
}
