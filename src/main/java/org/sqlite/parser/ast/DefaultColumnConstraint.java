package org.sqlite.parser.ast;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

public class DefaultColumnConstraint extends ColumnConstraint {
	public final Expr expr;

	public DefaultColumnConstraint(String name, Expr expr) {
		super(name);
		this.expr = requireNonNull(expr);
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		constraintName(a);
		a.append("DEFAULT ");
		expr.toSql(a);
	}
}
