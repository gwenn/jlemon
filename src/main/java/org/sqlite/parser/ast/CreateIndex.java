package org.sqlite.parser.ast;

import java.io.IOException;
import java.util.List;

import static java.util.Objects.requireNonNull;

public class CreateIndex implements Stmt {
	public final boolean unique;
	public final boolean ifNotExists;
	public final QualifiedName idxName;
	public final String tblName;
	public final List<SortedColumn> columns;
	public final Expr whereClause;

	public CreateIndex(boolean unique,
			boolean ifNotExists,
			QualifiedName idxName,
			String tblName,
			List<SortedColumn> columns,
			Expr whereClause) {
		this.unique = unique;
		this.ifNotExists = ifNotExists;
		this.idxName = requireNonNull(idxName);
		this.tblName = requireNonNull(tblName);
		this.columns = requireNonNull(columns);
		if (columns.isEmpty()) {
			throw new IllegalArgumentException("No columns");
		}
		this.whereClause = whereClause;
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		a.append("CREATE ");
		if (unique) {
			a.append("UNIQUE ");
		}
		a.append("INDEX ");
		if (ifNotExists) {
			a.append("IF NOT EXISTS ");
		}
		idxName.toSql(a);
		a.append(" ON ");
		doubleQuote(a, tblName);
		a.append('(');
		comma(a, columns);
		a.append(')');
		if (whereClause != null) {
			a.append(" WHERE ");
			whereClause.toSql(a);
		}
	}
}