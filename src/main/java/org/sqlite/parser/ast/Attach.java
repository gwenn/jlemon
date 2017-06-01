package org.sqlite.parser.ast;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

public class Attach implements Stmt {
	public final Expr expr;
	public final Expr dbName;
	public final Expr key;

	public Attach(Expr expr, Expr dbName, Expr key) {
		this.expr = requireNonNull(expr);
		this.dbName = requireNonNull(dbName);
		this.key = key;
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		a.append("ATTACH ");
		expr.toSql(a);
		a.append(" AS ");
		dbName.toSql(a);
		if (key != null) {
			a.append(" KEY ");
			key.toSql(a);
		}
	}
}