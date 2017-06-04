package org.sqlite.parser.ast;

import java.io.IOException;

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
