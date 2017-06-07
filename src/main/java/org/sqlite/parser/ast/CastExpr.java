package org.sqlite.parser.ast;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

public class CastExpr implements Expr {
	public final Expr expr;
	public final Type typeName;

	public CastExpr(Expr expr, Type typeName) {
		this.expr = requireNonNull(expr);
		this.typeName = requireNonNull(typeName);
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		a.append("CAST(");
		expr.toSql(a);
		a.append(" AS ");
		typeName.toSql(a);
		a.append(')');
	}
}