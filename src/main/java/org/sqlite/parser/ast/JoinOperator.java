package org.sqlite.parser.ast;

import java.io.IOException;

import org.sqlite.parser.Token;
import org.sqlite.parser.TokenType;

// Sum Type: comma vs typed join
public class JoinOperator implements ToSql {
	public final boolean comma;
	public final boolean natural;
	public final JoinType joinType;

	public static JoinOperator comma() {
		return new JoinOperator(true, false, null);
	}
	public static JoinOperator typedJoin(boolean natural, JoinType joinType) {
		return new JoinOperator(false, natural, joinType);
	}

	private JoinOperator(boolean comma, boolean natural, JoinType joinType) {
		this.comma = comma;
		this.natural = natural;
		this.joinType = joinType;
	}

	public static JoinOperator from(Token a, Token b, Token c) {
		if (TokenType.TK_COMMA == a.tokenType()) {
			return JoinOperator.comma();
		} else if (b == null && c == null) {
			if (TokenType.TK_JOIN == a.tokenType()) {
				return JoinOperator.typedJoin(false, null);
			} else if ("NATURAL".equalsIgnoreCase(a.text())) {
				return JoinOperator.typedJoin(true, null);
			}
			throw new IllegalArgumentException(String.format("Unsupported Join operator: %s", a));
		} else if ("NATURAL".equalsIgnoreCase(a.text())) {
			return JoinOperator.typedJoin(true, JoinType.from(b, c));
		} else {
			return JoinOperator.typedJoin(false, JoinType.from(b, c));
		}
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		if (comma) {
			a.append(',');
		} else {
			if (natural) {
				a.append(" NATURAL");
			}
			if (joinType != null) {
				a.append(' ');
				joinType.toSql(a);
			}
			a.append(" JOIN");
		}
	}
}
