package org.sqlite.parser.ast;

import java.io.IOException;

import org.sqlite.parser.ParseException;

import static java.util.Objects.requireNonNull;
import static org.sqlite.parser.ast.ToSql.isNotEmpty;

public class ForeignKeyColumnConstraint extends ColumnConstraint implements ForeignKeyConstraint {
	public final ForeignKeyClause clause;
	public DeferSubclause derefClause;

	public ForeignKeyColumnConstraint(String name, ForeignKeyClause clause, DeferSubclause derefClause) {
		super(name);
		this.clause = requireNonNull(clause);
		this.derefClause = derefClause;
		// "foreign key on %s should reference only one column of table %T"
		if (isNotEmpty(clause.columns) && clause.columns.size() != 1) {
			throw new ParseException(String.format("Inconsistent FOREIGN KEY column constraint with %d reference(s)", clause.columns.size()));
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
