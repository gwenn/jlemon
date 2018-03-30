package org.sqlite.parser.ast;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

/**
 * An instance of this structure holds information about the
 * LIMIT clause of a SELECT statement.
 */
public class Limit implements ToSql {
	public final Expr expr;
	public final Expr offset;

	public Limit(Expr expr, Expr offset) {
		this.expr = requireNonNull(expr);
		this.offset = offset;
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		a.append("LIMIT ");
		expr.toSql(a);
		if (offset != null) {
			a.append(" OFFSET ");
			offset.toSql(a);
		}
	}
}
