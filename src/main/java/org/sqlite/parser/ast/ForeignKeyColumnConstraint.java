package org.sqlite.parser.ast;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

public class ForeignKeyColumnConstraint extends ColumnConstraint {
	public final ForeignKeyClause clause;
	public DeferSubclause derefClause;

	public ForeignKeyColumnConstraint(String name, ForeignKeyClause clause, DeferSubclause derefClause) {
		super(name);
		this.clause = requireNonNull(clause);
		this.derefClause = derefClause;
	}

	public void setDerefClause(DeferSubclause derefClause) {
		this.derefClause = derefClause;
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		constraintName(a);
		a.append("REFERENCES ");
		clause.toSql(a);
		if (derefClause != null) {
			a.append(' ');
			derefClause.toSql(a);
		}
	}
}
