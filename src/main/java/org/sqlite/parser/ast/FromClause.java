package org.sqlite.parser.ast;

import java.io.IOException;
import java.util.List;

import static java.util.Objects.requireNonNull;

public class FromClause implements ToSql {
	public final SelectTable select;
	public final List<JoinedSelectTable> joins;

	public FromClause(SelectTable select, List<JoinedSelectTable> joins) {
		this.select = requireNonNull(select);
		this.joins = joins;
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		a.append("FROM ");
		select.toSql(a);
		if (joins != null) {
			for (JoinedSelectTable join : joins) {
				a.append(' ');
				join.toSql(a);
			}
		}
	}
}
