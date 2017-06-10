package org.sqlite.parser.ast;

import java.io.IOException;

import org.sqlite.parser.TokenType;

public enum TransactionType implements ToSql {
	Deferred, // default
	Immediate,
	Exclusive;

	public static TransactionType from(int tt) {
		if (TokenType.TK_DEFERRED == tt) {
			return Deferred;
		} else if (TokenType.TK_IMMEDIATE == tt) {
			return Immediate;
		} else if (TokenType.TK_EXCLUSIVE == tt) {
			return Exclusive;
		}
		throw new IllegalArgumentException(String.format("%d", tt));
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		if (Deferred == this) {
			a.append("DEFERRED");
		} else if (Immediate == this) {
			a.append("IMMEDIATE");
		} else if (Exclusive == this) {
			a.append("EXCLUSIVE");
		}
	}
}