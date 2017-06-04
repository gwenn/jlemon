package org.sqlite.parser.ast;

import java.io.IOException;

public class UniqueColumnConstraint extends ColumnConstraint {
	public final ResolveType conflictClause;

	public UniqueColumnConstraint(String name, ResolveType conflictClause) {
		super(name);
		this.conflictClause = conflictClause;
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		constraintName(a);
		if (conflictClause != null) {
			a.append(' ');
			conflictClause.toSql(a);
		}
	}
}
