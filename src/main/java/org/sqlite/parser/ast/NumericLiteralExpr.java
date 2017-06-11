package org.sqlite.parser.ast;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

public class NumericLiteralExpr implements Expr {
	public final boolean integer;
	public final String value;

	public NumericLiteralExpr(String value, boolean integer) {
		this.value = requireNonNull(value);
		this.integer = integer;
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		a.append(value);
	}
}
