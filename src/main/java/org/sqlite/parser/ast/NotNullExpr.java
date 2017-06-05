package org.sqlite.parser.ast;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

public class NotNullExpr implements Expr {
	public final Expr expr;

	public NotNullExpr(Expr expr) {
		this.expr = requireNonNull(expr);
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		expr.toSql(a);
		a.append("NOT NULL");
	}
}
