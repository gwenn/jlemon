package org.sqlite.parser.ast;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

/**
 * bind-parameter
 */
public class VariableExpr implements Expr {
	public final String variable;

	public VariableExpr(String variable) {
		this.variable = requireNonNull(variable);
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		if (variable.isEmpty()) {
			a.append('?');
			return;
		}
		final char c = variable.charAt(0);
		if (c == '$' || c == '@' || c == '#' || c == ':') {
			a.append(variable);
			return;
		}
		a.append('?');
		a.append(variable);
	}
}
