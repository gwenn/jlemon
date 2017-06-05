package org.sqlite.parser.ast;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

public class CollateExpr implements Expr {
	public final Expr expr;
	public final String name;

	public CollateExpr(Expr expr, String name) {
		this.expr = requireNonNull(expr);
		this.name = requireNonNull(name);
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		a.append("COLLATE ");
		doubleQuote(a, name);
	}
}
