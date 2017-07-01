package org.sqlite.parser.ast;

import java.io.IOException;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static org.sqlite.parser.ast.ToSql.comma;
import static org.sqlite.parser.ast.ToSql.isNotEmpty;

public class Delete implements Stmt {
	public final With with;
	public final QualifiedName tblName;
	public final Indexed indexed;
	public final Expr whereClause;
	public final List<SortedColumn> orderBy;
	public final Limit limit;

	public Delete(With with,
			QualifiedName tblName,
			Indexed indexed,
			Expr whereClause,
			List<SortedColumn> orderBy,
			Limit limit) {
		this.with = with;
		this.tblName = requireNonNull(tblName);
		this.indexed = indexed;
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
		a.append("DELETE FROM ");
		tblName.toSql(a);
		if (indexed != null) {
			a.append(' ');
			indexed.toSql(a);
		}
		if (whereClause != null) {
			a.append(" WHERE ");
			whereClause.toSql(a);
		}
		if (isNotEmpty(orderBy)) {
			a.append(" ORDER BY ");
			comma(a, orderBy);
		}
		if (limit != null) {
			a.append(' ');
			limit.toSql(a);
		}
	}
}
