package org.sqlite.parser.ast;

import java.io.IOException;

public class PrimaryKeyColumnConstraint extends ColumnConstraint {
	public final SortOrder order;
	public final ResolveType conflictClause;
	public final boolean autoIncrement;

	public PrimaryKeyColumnConstraint(String name, SortOrder order, ResolveType conflictClause, boolean autoIncrement) {
		super(name);
		this.order = order;
		this.conflictClause = conflictClause;
		this.autoIncrement = autoIncrement;
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		constraintName(a);
		a.append("PRIMARY KEY");
		if (order != null) {
			a.append(' ');
			order.toSql(a);
		}
		if (conflictClause != null) {
			a.append(" ON CONFLICT ");
			conflictClause.toSql(a);
		}
		if (autoIncrement) {
			a.append(" AUTOINCREMENT");
		}
	}
}
