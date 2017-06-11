package org.sqlite.parser.ast;

import java.io.IOException;

import org.sqlite.parser.TokenType;

import static java.util.Objects.requireNonNull;

public class IsNullExpr implements Expr {
	public final Expr expr;
	public final boolean not;

	public static IsNullExpr from(Expr expr, int tt) {
		if (TokenType.TK_ISNULL == tt) {
			return new IsNullExpr(expr, false);
		} else if (TokenType.TK_NOTNULL == tt) {
			return new IsNullExpr(expr, true);
		}
		throw new IllegalArgumentException(); // TODO
	}

	public IsNullExpr(Expr expr, boolean not) {
		this.expr = requireNonNull(expr);
		this.not = not;
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		expr.toSql(a);
		if (not) {
			a.append(" NOTNULL");
		} else {
			a.append(" ISNULL");
		}
	}
}
