package org.sqlite.parser.ast;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

public class JoinedSelectTable implements ToSql {
	public final JoinOperator operator;
	public final SelectTable select;
	public final JoinConstraint constraint;

	public JoinedSelectTable(JoinOperator operator,
			SelectTable select,
			JoinConstraint constraint) {
		this.operator = requireNonNull(operator);
		this.select = requireNonNull(select);
		this.constraint = constraint;
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		operator.toSql(a);
		a.append(' ');
		select.toSql(a);
		if (constraint != null) {
			a.append(' ');
			constraint.toSql(a);
		}
	}
}
