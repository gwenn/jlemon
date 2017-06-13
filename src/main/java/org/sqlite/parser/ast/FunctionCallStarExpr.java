package org.sqlite.parser.ast;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

/**
 * function-name(*)
 */
public class FunctionCallStarExpr implements Expr {
	public final String name;

	public FunctionCallStarExpr(String name) {
		this.name = requireNonNull(name);
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		doubleQuote(a, name);
		a.append("(*)");
	}
}
