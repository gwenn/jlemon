package org.sqlite.parser.ast;

import java.io.IOException;

import static java.util.Objects.requireNonNull;
import static org.sqlite.parser.ast.ToSql.doubleQuote;

public class QualifiedExpr implements Expr {
	public final String qualifier;
	public final String qualified;

	public QualifiedExpr(String qualifier, String qualified) {
		this.qualifier = requireNonNull(qualifier);
		this.qualified = requireNonNull(qualified);
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		doubleQuote(a, qualifier);
		a.append('.');
		doubleQuote(a, qualified);
	}
}
