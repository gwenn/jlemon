package org.sqlite.parser.ast;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

/**
 * unary_operator expr
 */
public class UnaryExpr implements Expr {
	public final UnaryOperator op;
	public final Expr expr;

	public UnaryExpr(UnaryOperator op, Expr expr) {
		this.op = op;
		this.expr = requireNonNull(expr);
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		op.toSql(a);
		a.append(' ');
		expr.toSql(a);
	}
}
