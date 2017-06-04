package org.sqlite.parser.ast;

import java.io.IOException;
import java.util.List;

public class ForeignKeyClause implements ToSql {
	public final String tblName;
	public final List<IndexedColumn> columns;
	public final List<RefArg> args;

	public ForeignKeyClause(String tblName, List<IndexedColumn> columns, List<RefArg> args) {
		this.tblName = tblName;
		this.columns = columns;
		this.args = args;
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		doubleQuote(a, tblName);
		if (columns != null && !columns.isEmpty()) {
			a.append('(');
			comma(a, columns);
			a.append(')');
		}
		if (args != null && !args.isEmpty()) {
			for (RefArg arg : args) {
				a.append(' ');
				arg.toSql(a);
			}
		}
	}
}
