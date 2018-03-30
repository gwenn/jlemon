package org.sqlite.parser.ast;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

/**
 * Represents a subquery {@code SELECT} expression with the {@code EXISTS} qualifier.
 * <pre>{@code [NOT] EXISTS (select-stmt)}</pre>
 */
public class ExistsExpr implements Expr {
	public final Select subquery;

	public ExistsExpr(Select subquery) {
		this.subquery = requireNonNull(subquery);
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		a.append("EXISTS (");
		subquery.toSql(a);
		a.append(')');
	}
}
