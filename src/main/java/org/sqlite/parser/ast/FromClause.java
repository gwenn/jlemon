package org.sqlite.parser.ast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

public class FromClause implements ToSql {
	public final SelectTable select;
	public final List<JoinedSelectTable> joins;
	private transient JoinOperator op;

	public static FromClause from(QualifiedName qualifiedName) {
		return new FromClause(SelectTable.table(qualifiedName, null, null), null);
	}
	public static FromClause from(SelectBody subSelect) {
		return from(Select.from(subSelect));
	}
	public static FromClause from(Select subSelect) {
		return new FromClause(SelectTable.select(subSelect, null), null);
	}

	public static FromClause from(FromClause from, JoinOperator op) {
		from.op = op;
		return from;
	}
	public static FromClause from(FromClause from, SelectTable select, JoinConstraint constraint) {
		if (from == null) {
			return new FromClause(select, new ArrayList<>());
		}
		from.joins.add(new JoinedSelectTable(from.op, select, constraint));
		from.op = null;
		return from;
	}

	public FromClause(SelectTable select, List<JoinedSelectTable> joins) {
		this.select = requireNonNull(select);
		this.joins = joins;
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		select.toSql(a);
		if (joins != null) {
			for (JoinedSelectTable join : joins) {
				a.append(' ');
				join.toSql(a);
			}
		}
	}
}
