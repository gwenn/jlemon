package org.sqlite.parser.ast;

import java.io.IOException;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static org.sqlite.parser.ast.ToSql.comma;
import static org.sqlite.parser.ast.ToSql.requireNotEmpty;

public class Update implements Stmt {
	public final With with;
	public final ResolveType orConflict;
	public final QualifiedName tblName;
	public final Indexed indexed;
	public final List<Set> sets;
	public final Expr whereClause;
	public final List<SortedColumn> orderBy;
	public final Limit limit;

	public Update(With with,
			ResolveType orConflict,
			QualifiedName tblName,
			Indexed indexed,
			List<Set> sets,
			Expr whereClause,
			List<SortedColumn> orderBy,
			Limit limit) {
		this.with = with;
		this.orConflict = orConflict;
		this.tblName = requireNonNull(tblName);
		this.indexed = indexed;
		// TODO "too many columns in ""set list"
		this.sets = requireNotEmpty(sets);
		this.whereClause = whereClause;
		this.orderBy = orderBy;
		this.limit = limit;
	}
	@Override
	public void toSql(Appendable a) throws IOException {
		if (with != null) {
			with.toSql(a);
			a.append(' ');
		}
		a.append("UPDATE ");
		if (orConflict != null) {
			a.append("OR ");
			orConflict.toSql(a);
			a.append(' ');
		}
		tblName.toSql(a);
		if (indexed != null) {
			a.append(' ');
			indexed.toSql(a);
		}
		a.append(" SET ");
		comma(a, sets);
		if (whereClause != null) {
			a.append(" WHERE ");
			whereClause.toSql(a);
		}
		if (ToSql.isNotEmpty(orderBy)) {
			a.append(" ORDER BY ");
			comma(a, orderBy);
		}
		if (limit != null) {
			a.append(' ');
			limit.toSql(a);
		}
	}
}
