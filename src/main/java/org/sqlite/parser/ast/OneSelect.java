package org.sqlite.parser.ast;

import java.io.IOException;
import java.util.List;

import static org.sqlite.parser.ast.ToSql.comma;
import static org.sqlite.parser.ast.ToSql.requireNotEmpty;

// Sum Type: Select vs Values
public class OneSelect implements ToSql {
	public final Distinctness distinctness;
	public final List<ResultColumn> columns;
	public final FromClause from;
	public final Expr whereClause;
	public final GroupBy groupBy;
	public final List<Window> windowClause;

	public final List<List<Expr>> values;

	public OneSelect(Distinctness distinctness,
			List<ResultColumn> columns,
			FromClause from,
			Expr whereClause,
			GroupBy groupBy,
			List<Window> windowClause) {
		this.distinctness = distinctness;
		this.columns = requireNotEmpty(columns);
		this.from = from;
		this.whereClause = whereClause;
		this.groupBy = groupBy;
		this.windowClause = windowClause;
		this.values = null;
	}
	public OneSelect(List<List<Expr>> values) {
		this.distinctness = null;
		this.columns = null;
		this.from = null;
		this.whereClause = null;
		this.groupBy = null;
		this.windowClause = null;
		this.values = requireNotEmpty(values);
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		if (values != null) {
			for (int i = 0; i < values.size(); i++) {
				if (i == 0) {
					a.append("VALUES (");
				} else {
					a.append(", (");
				}
				comma(a, values.get(i));
				a.append(")");
			}
			return;
		}
		a.append("SELECT");
		if (distinctness != null) {
			a.append(' ');
			distinctness.toSql(a);
		}
		a.append(' ');
		comma(a, columns);
		if (from != null) {
			a.append(" FROM ");
			from.toSql(a);
		}
		if (whereClause != null) {
			a.append(" WHERE ");
			whereClause.toSql(a);
		}
		if (groupBy != null) {
			a.append(' ');
			groupBy.toSql(a);
		}
		if (windowClause != null) {
			a.append(" WINDOW ");
			comma(a, windowClause);
		}
	}
}
