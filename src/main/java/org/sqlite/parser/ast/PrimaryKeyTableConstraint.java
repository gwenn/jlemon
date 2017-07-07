package org.sqlite.parser.ast;

import java.io.IOException;
import java.util.List;

import static org.sqlite.parser.ast.ToSql.comma;
import static org.sqlite.parser.ast.ToSql.requireNotEmpty;

public class PrimaryKeyTableConstraint extends TableConstraint {
	public final List<SortedColumn> columns;
	public final boolean autoIncrement;
	public final ResolveType conflictClause;

	public PrimaryKeyTableConstraint(String name,
			List<SortedColumn> columns,
			boolean autoIncrement,
			ResolveType conflictClause) {
		super(name);
		this.columns = requireNotEmpty(columns);
		this.conflictClause = conflictClause;
		this.autoIncrement = autoIncrement;
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		constraintName(a);
		a.append("PRIMARY KEY");
		a.append('(');
		comma(a, columns);
		if (autoIncrement) {
			a.append(" AUTOINCREMENT");
		}
		a.append(')');
		if (conflictClause != null) {
			a.append(" ON CONFLICT ");
			conflictClause.toSql(a);
		}
	}
}
