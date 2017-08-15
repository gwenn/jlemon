package org.sqlite.parser;

public class Token {
	private final short type;
	private String text;

	public Token(short type, String text) {
		this.type = type;
		this.text = text;
	}

	/** @return The token type */
	public short tokenType() {
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
