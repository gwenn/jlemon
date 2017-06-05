package org.sqlite.parser.ast;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

public class VariableExpr implements Expr {
	public final String variable;

	public VariableExpr(String variable) {
		this.variable = requireNonNull(variable);
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		a.append(variable);
	}
}
