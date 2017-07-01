package org.sqlite.parser.ast;

import java.io.IOException;
import java.util.List;

import static org.sqlite.parser.ast.ToSql.comma;
import static org.sqlite.parser.ast.ToSql.requireNotEmpty;

public class UniqueTableConstraint extends TableConstraint {
	public final List<SortedColumn> columns;
	public final ResolveType conflictClause;

	public UniqueTableConstraint(String name,
			List<SortedColumn> columns,
			ResolveType conflictClause) {
		super(name);
		this.columns = requireNotEmpty(columns);
		this.conflictClause = conflictClause;
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		constraintName(a);
		a.append("UNIQUE");
		a.append('(');
		comma(a, columns);
		a.append(')');
		if (conflictClause != null) {
			a.append(' ');
			conflictClause.toSql(a);
		}
	}
}
