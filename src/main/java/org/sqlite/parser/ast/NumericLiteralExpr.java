package org.sqlite.parser.ast;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

public class NumericLiteralExpr implements Expr {
	public final String value;

	public NumericLiteralExpr(String value) {
		this.value = requireNonNull(value);
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		a.append(value);
	}
}
