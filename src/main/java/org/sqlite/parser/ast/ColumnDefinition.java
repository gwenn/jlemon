package org.sqlite.parser.ast;

import java.io.IOException;
import java.util.List;

import static java.util.Objects.requireNonNull;

public class ColumnDefinition implements ToSql {
	public final String colName;
	public final Type colType;
	public final List<ColumnConstraint> constraints;

	public ColumnDefinition(String colName,
			Type colType,
			List<ColumnConstraint> constraints) {
		this.colName = requireNonNull(colName);
		this.colType = colType;
		this.constraints = constraints;
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		doubleQuote(a, colName);
		if (colType != null) {
			a.append(' ');
			colType.toSql(a);
		}
		if (constraints != null) {
			for (ColumnConstraint constraint : constraints) {
				a.append(' ');
				constraint.toSql(a);
			}
		}
	}
}
