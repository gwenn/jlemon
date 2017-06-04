package org.sqlite.parser.ast;

import java.io.IOException;

public class NotNullColumnConstraint extends ColumnConstraint {
	public final boolean nullable;
	public final ResolveType conflictClause;

	public NotNullColumnConstraint(String name, boolean nullable, ResolveType conflictClause) {
		super(name);
		this.nullable = nullable;
		this.conflictClause = conflictClause;
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		constraintName(a);
		if (!nullable) {
			a.append("NOT ");
		}
		a.append("NULL");
		if (conflictClause != null) {
			a.append(' ');
			conflictClause.toSql(a);
		}
	}
}
