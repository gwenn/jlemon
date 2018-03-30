package org.sqlite.parser.ast;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

/**
 * Represents a subquery {@code SELECT} expression.
 */
public class SubqueryExpr implements Expr {
	public final Select subquery;

	public SubqueryExpr(Select subquery) {
		this.subquery = requireNonNull(subquery);
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		a.append('(');
		subquery.toSql(a);
		a.append(')');
	}
}
