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
		// TODO "unknown or unsupported join type: %T %T%s%T"
		// TODO "RIGHT and FULL OUTER JOINs are not currently supported"
		if (TokenType.TK_COMMA == a.tokenType()) {
			return JoinOperator.comma();
		} else if (b == null && c == null) {
			if (TokenType.TK_JOIN == a.tokenType()) {
				return JoinOperator.typedJoin(false, null);
			} else if ("NATURAL".equalsIgnoreCase(a.text())) {
				return JoinOperator.typedJoin(true, null);
			}
			return JoinOperator.typedJoin(false, JoinType.from(a, null));
		} else if ("NATURAL".equalsIgnoreCase(a.text())) {
			return JoinOperator.typedJoin(true, JoinType.from(b, c));
		} else {
			assert c == null;
			return JoinOperator.typedJoin(false, JoinType.from(a, b));
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
