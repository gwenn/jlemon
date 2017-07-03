package org.sqlite.parser.ast;

import java.io.IOException;

import org.sqlite.parser.Token;

public enum JoinType implements ToSql {
	Left,
	LeftOuter,
	Inner,
	Cross;

	public static JoinType from(Token b, Token c) {
		if ("LEFT".equalsIgnoreCase(b.text())) {
			if (c == null) {
				return Left;
			} else if ("OUTER".equalsIgnoreCase(c.text())) {
				return LeftOuter;
			}
		} else if ("INNER".equalsIgnoreCase(b.text())) {
			if (c == null) {
				return Inner;
			}
		} else if ("CROSS".equalsIgnoreCase(b.text())) {
			if (c == null) {
				return Cross;
			}
		}
		throw new IllegalArgumentException(String.format("Unsupported Join operator: %s, %s", b, c));
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		if (Left == this) {
			a.append("LEFT");
		} else if (LeftOuter == this) {
			a.append("LEFT OUTER");
		} else if (Inner == this) {
			a.append("INNER");
		} else if (Cross == this) {
			a.append("CROSS");
		}
	}
}
