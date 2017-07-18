package org.sqlite.parser.ast;

import java.io.IOException;
import java.util.List;

import org.sqlite.parser.ParseException;

import static java.util.Objects.requireNonNull;
import static org.sqlite.parser.ast.ToSql.comma;
import static org.sqlite.parser.ast.ToSql.isNotEmpty;
import static org.sqlite.parser.ast.ToSql.requireNotEmpty;

public class ForeignKeyTableConstraint extends TableConstraint implements ForeignKeyConstraint {
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
		// "number of columns in foreign key does not match the number of columns in the referenced table"
		if (isNotEmpty(clause.columns) && columns.size() != clause.columns.size()) {
			throw new ParseException(String.format("Inconsistent FOREIGN KEY table constraint with %d column(s) but %d reference(s)", columns.size(), clause.columns.size()));
		}
	}
	@Override
	public ForeignKeyClause getClause() {
		return clause;
	}
	@Override
	public DeferSubclause getDerefClause() {
		return derefClause;
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
