package org.sqlite.parser.ast;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

/**
 * unary_operator expr
 *
 * a unary negation expression.
 * a unary positive-sign expression.
 * a unary bitwise negation expression.
 * a unary logical negation expression.
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
		if (UnaryOperator.Not == op || expr instanceof UnaryExpr) {
			a.append(' ');
		}
		expr.toSql(a);
	}
}
