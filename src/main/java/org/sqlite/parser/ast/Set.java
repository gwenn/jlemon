package org.sqlite.parser.ast;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

public class Set implements ToSql {
	public final String colName;
	public final Expr expr;

	public Set(String colName, Expr expr) {
		this.colName = requireNonNull(colName);
		this.expr = requireNonNull(expr);
	}
	@Override
	public void toSql(Appendable a) throws IOException {
		doubleQuote(a, colName);
		a.append(" = ");
		expr.toSql(a);
	}
}
