package org.sqlite.parser.ast;

import java.io.IOException;
import java.sql.DatabaseMetaData;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static org.sqlite.parser.ast.LiteralExpr.integer;
import static org.sqlite.parser.ast.ToSql.comma;
import static org.sqlite.parser.ast.ToSql.doubleQuote;
import static org.sqlite.parser.ast.ToSql.isNotEmpty;

public class ForeignKeyClause implements ToSql {
	public final String tblName;
	public final List<IndexedColumn> columns;
	public final List<RefArg> args;

	public ForeignKeyClause(String tblName, List<IndexedColumn> columns, List<RefArg> args) {
		this.tblName = requireNonNull(tblName);
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

	public LiteralExpr getUpdateRule() {
		if (isNotEmpty(args)) {
			for (RefArg arg : args) {
				if (arg instanceof OnUpdateRefArg) {
					RefAct refAct = ((OnUpdateRefArg) arg).refAct;
					return integer(refAct.getRule());
				}
			}
		}
		return integer(DatabaseMetaData.importedKeyNoAction);
	}

	public LiteralExpr getDeleteRule() {
		if (isNotEmpty(args)) {
			for (RefArg arg : args) {
				if (arg instanceof OnDeleteRefArg) {
					RefAct refAct = ((OnDeleteRefArg) arg).refAct;
					return integer(refAct.getRule());
				}
			}
		}
		return integer(DatabaseMetaData.importedKeyNoAction);
	}
}
