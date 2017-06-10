package org.sqlite.parser.ast;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static org.sqlite.parser.ast.ToSql.requireNotEmpty;

public class Set implements ToSql {
	public final List<String> colNames;
	public final Expr expr;

	public Set(String colName, Expr expr) {
		this.colNames = Collections.singletonList(requireNonNull(colName));
		this.expr = requireNonNull(expr);
	}
	public Set(List<String> colNames, Expr expr) {
		this.colNames = requireNotEmpty(colNames);
		this.expr = requireNonNull(expr);
	}
	@Override
	public void toSql(Appendable a) throws IOException {
		if (colNames.size() == 1) {
			doubleQuote(a, colNames.get(0));
		} else {
			a.append('(');
			commaNames(a, colNames);
			a.append(')');
		}
		a.append(" = ");
		expr.toSql(a);
	}
}
