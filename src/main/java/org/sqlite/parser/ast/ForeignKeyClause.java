package org.sqlite.parser.ast;

import java.io.IOException;
import java.util.List;

import static org.sqlite.parser.ast.ToSql.comma;
import static org.sqlite.parser.ast.ToSql.doubleQuote;
import static org.sqlite.parser.ast.ToSql.isNotEmpty;

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
		if (isNotEmpty(columns)) {
			a.append('(');
			comma(a, columns);
			a.append(')');
		}
		if (isNotEmpty(args)) {
			for (RefArg arg : args) {
				a.append(' ');
				arg.toSql(a);
			}
		}
	}
}
