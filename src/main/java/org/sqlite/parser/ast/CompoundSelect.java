package org.sqlite.parser.ast;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

public class CompoundSelect implements ToSql {
	public final CompoundOperator operator;
	public final OneSelect select;

	public CompoundSelect(CompoundOperator operator, OneSelect select) {
		this.operator = requireNonNull(operator);
		this.select = requireNonNull(select);
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		operator.toSql(a);
		a.append(' ');
		select.toSql(a);
	}
}
