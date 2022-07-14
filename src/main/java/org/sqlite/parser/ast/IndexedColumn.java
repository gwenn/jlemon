package org.sqlite.parser.ast;

import java.io.IOException;

import static java.util.Objects.requireNonNull;
import static org.sqlite.parser.ast.ToSql.doubleQuote;

public class IndexedColumn implements ToSql {
	public final String colName;
	public final String collationName;
	public final SortOrder order;

	public IndexedColumn(String colName, String collationName, SortOrder order) {
		this.colName = requireNonNull(colName);
		this.collationName = collationName;
		this.order = order;
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		doubleQuote(a, colName);
		if (collationName != null) {
			a.append(" COLLATE ");
			doubleQuote(a, collationName);
		}
		if (order != null) {
			a.append(' ');
			order.toSql(a);
		}
	}
}
