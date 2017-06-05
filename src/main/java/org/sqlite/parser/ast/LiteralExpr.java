package org.sqlite.parser.ast;

import java.io.IOException;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static org.sqlite.parser.ast.ToSql.isNotEmpty;

public class LiteralExpr implements Expr {
	public final String value;

	public LiteralExpr(String value) {
		this.value = requireNonNull(value);
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		singleQuote(a, value);
	}
}
