package org.sqlite.parser.ast;

import java.io.IOException;

import org.sqlite.parser.TokenType;

import static java.util.Objects.requireNonNull;

/**
 * Represents an {@code ISNULL}/{@code IS NULL} or a {@code NOTNULL} expression.
 */
public class IsNullExpr implements Expr {
	public final Expr expr;
	public final NullOperator op;

	public static IsNullExpr from(Expr expr, short tt) {
		if (TokenType.TK_ISNULL == tt) {
			return new IsNullExpr(expr, NullOperator.IsNull);
		} else if (TokenType.TK_NOTNULL == tt) {
			return new IsNullExpr(expr, NullOperator.NotNull);
		}
		throw new IllegalArgumentException(String.format("Unsupported null expression: %s", TokenType.toString(tt)));
	}

	public IsNullExpr(Expr expr, NullOperator op) {
		this.expr = requireNonNull(expr);
		this.op = requireNonNull(op);
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		expr.toSql(a);
		a.append(' ');
		op.toSql(a);
	}
}
