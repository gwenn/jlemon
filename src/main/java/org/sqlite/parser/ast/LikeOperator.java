package org.sqlite.parser.ast;

import java.io.IOException;

import org.sqlite.parser.Token;
import org.sqlite.parser.TokenType;

public enum LikeOperator implements ToSql {
	Glob,
	Like,
	Match,
	Regexp;

	public static LikeOperator from(Token token) {
		if (TokenType.TK_MATCH == token.tokenType()) {
			return Match;
		} else if (TokenType.TK_LIKE_KW != token.tokenType()) {
			throw new IllegalArgumentException();
		}
		if ("GLOB".equalsIgnoreCase(token.text())) {
			return Glob;
		} else if ("LIKE".equalsIgnoreCase(token.text())) {
			return Like;
		} else if ("REGEXP".equalsIgnoreCase(token.text())) {
			return Regexp;
		}
		throw new IllegalArgumentException();
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		if (Glob == this) {
			a.append("GLOB");
		} else if (Like == this) {
			a.append("LIKE");
		} else if (Match == this) {
			a.append("MATCH");
		} else if (Regexp == this) {
			a.append("REGEXP");
		}
	}
}
