package org.sqlite.parser.ast;

import java.io.IOException;

import org.sqlite.parser.TokenType;

public enum CompoundOperator implements ToSql {
	Union,
	UnionAll,
	Except,
	Intersect;

	public static CompoundOperator from(short tt) {
		if (TokenType.TK_UNION == tt) {
			return Union;
		} else if (TokenType.TK_EXCEPT == tt) {
			return Except;
		} else if (TokenType.TK_INTERSECT == tt) {
			return Intersect;
		}
		throw new IllegalArgumentException(String.format("Unsupported Compound operator: %s", TokenType.toString(tt)));
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		if (Union == this) {
			a.append("UNION");
		} else if (UnionAll == this) {
			a.append("UNION ALL");
		} else if (Except == this) {
			a.append("EXCEPT");
		} else if (Intersect == this) {
			a.append("INTERSECT");
		}
	}
}
