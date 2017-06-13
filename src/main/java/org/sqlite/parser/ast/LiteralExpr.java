package org.sqlite.parser.ast;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

/**
 * literal-value
 */
public class LiteralExpr implements Expr { // FIXME BLOB versus String
	public final String value;

	public LiteralExpr(String value) {
		this.value = requireNonNull(value);
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		singleQuote(a, value);
	}
}
