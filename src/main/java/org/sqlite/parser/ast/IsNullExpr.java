package org.sqlite.parser.ast;

import java.io.IOException;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static org.sqlite.parser.ast.ToSql.isNotEmpty;

public class IsNullExpr implements Expr {
	public final Expr expr;

	public IsNullExpr(Expr expr) {
		this.expr = requireNonNull(expr);
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		expr.toSql(a);
		a.append("ISNULL");
	}
}
