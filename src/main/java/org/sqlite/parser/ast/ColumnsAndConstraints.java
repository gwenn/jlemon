package org.sqlite.parser.ast;

import java.io.IOException;
import java.util.List;

import static org.sqlite.parser.ast.ToSql.requireNotEmpty;

public class ColumnsAndConstraints implements CreateTableBody {
	public final List<ColumnDefinition> columns;
	public final List<TableConstraint> constraints;
	public final String without;

	public ColumnsAndConstraints(List<ColumnDefinition> columns, List<TableConstraint> constraints, String without) {
		this.columns = requireNotEmpty(columns);
		this.constraints = constraints;
		this.without = without;
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		a.append('(');
		comma(a, columns);
		if (constraints != null && !constraints.isEmpty()) {
			a.append(", ");
			comma(a, constraints);
		}
		a.append(')');
		if (without != null) {
			a.append(" WITHOUT ");
			doubleQuote(a, without);
		}
	}
}
