package org.sqlite.parser.ast;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

public class Detach implements Stmt {
	public final Expr expr;

	public Detach(Expr expr) {
		this.expr = requireNonNull(expr);
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		a.append("DETACH ");
		expr.toSql(a);
	}
}