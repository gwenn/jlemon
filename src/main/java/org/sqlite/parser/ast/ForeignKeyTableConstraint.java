package org.sqlite.parser.ast;

import java.io.IOException;
import java.util.List;

import static java.util.Objects.requireNonNull;

import static org.sqlite.parser.ast.ToSql.comma;
import static org.sqlite.parser.ast.ToSql.requireNotEmpty;

public class ForeignKeyTableConstraint extends TableConstraint {
	public final List<IndexedColumn> columns;
	public final ForeignKeyClause clause;
	public final DeferSubclause derefClause;

	public ForeignKeyTableConstraint(String name,
			List<IndexedColumn> columns,
			ForeignKeyClause clause,
			DeferSubclause derefClause) {
		super(name);
		this.columns = requireNotEmpty(columns);
		this.clause = requireNonNull(clause);
		this.derefClause = derefClause;
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		constraintName(a);
		a.append("FOREIGN KEY ");
		a.append('(');
		comma(a, columns);
		a.append(')');
		a.append(" REFERENCES ");
		clause.toSql(a);
		if (derefClause != null) {
			a.append(' ');
			derefClause.toSql(a);
		}
	}
}
