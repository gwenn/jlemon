package org.sqlite.parser.ast;

import java.io.IOException;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static org.sqlite.parser.ast.ToSql.comma;
import static org.sqlite.parser.ast.ToSql.doubleQuote;
import static org.sqlite.parser.ast.ToSql.requireNotEmpty;

public class UpdateTriggerCmd implements TriggerCmd {
	public final ResolveType orConflict;
	public final String tblName;
	public final List<Set> sets;
	public final Expr whereClause;

	public UpdateTriggerCmd(ResolveType orConflict,
			String tblName,
			List<Set> sets,
			Expr whereClause) {
		this.orConflict = orConflict;
		this.tblName = requireNonNull(tblName);
		this.sets = requireNotEmpty(sets);
		this.whereClause = whereClause;
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		a.append("UPDATE ");
		if (orConflict != null) {
			orConflict.toSql(a);
			a.append(' ');
		}
		doubleQuote(a, tblName);
		a.append(" SET ");
		comma(a, sets);
		if (whereClause != null) {
			a.append(" WHERE ");
			whereClause.toSql(a);
		}
	}
}
