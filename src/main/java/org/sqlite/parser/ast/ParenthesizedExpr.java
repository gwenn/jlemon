package org.sqlite.parser.ast;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

public class ParenthesizedExpr implements Expr {
	public final Expr expr;

	public ParenthesizedExpr(Expr expr) {
		this.expr = requireNonNull(expr);
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		a.append('(');
		expr.toSql(a);
		a.append(')');
	}
}
