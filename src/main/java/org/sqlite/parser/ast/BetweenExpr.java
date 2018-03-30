package org.sqlite.parser.ast;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

/**
 * Represents a {@code BETWEEN} expression.
 * <pre>{@code expr [NOT] BETWEEN expr AND expr}</pre>
 */
public class BetweenExpr implements Expr {
	public final Expr lhs;
	public final boolean not;
	public final Expr start;
	public final Expr end;

	public BetweenExpr(Expr lhs, boolean not, Expr start, Expr end) {
		this.lhs = requireNonNull(lhs);
		this.not = not;
		this.start = requireNonNull(start);
		this.end = requireNonNull(end);
	}
	@Override
	public void toSql(Appendable a) throws IOException {
		lhs.toSql(a);
		if (not) {
			a.append(" NOT");
		}
		a.append(" BETWEEN ");
		start.toSql(a);
		a.append(" AND ");
		end.toSql(a);
	}
}
