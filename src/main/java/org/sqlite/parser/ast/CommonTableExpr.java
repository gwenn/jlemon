package org.sqlite.parser.ast;

import java.io.IOException;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static org.sqlite.parser.ast.ToSql.comma;
import static org.sqlite.parser.ast.ToSql.doubleQuote;
import static org.sqlite.parser.ast.ToSql.isNotEmpty;

public class CommonTableExpr implements ToSql {
	public final String tblName;
	public final List<IndexedColumn> columns;
	public final Select select;

	public CommonTableExpr(String tblName, List<IndexedColumn> columns, Select select) {
		this.tblName = requireNonNull(tblName);
		this.columns = columns;
		this.select = requireNonNull(select);
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		doubleQuote(a, tblName);
		if (isNotEmpty(columns)) {
			a.append(" (");
			comma(a, columns);
			a.append(')');
		}
		a.append(" AS (");
		select.toSql(a);
		a.append(')');
	}
}
