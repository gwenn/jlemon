package org.sqlite.parser.ast;

import java.io.IOException;
import java.util.List;

/**
 * Represents a {@code CASE} expression.
 * <pre>{@code CASE [expr] [WHEN expr THEN expr]* [ELSE expr] END}</pre>
 */
public class CaseExpr implements Expr {
	// The AST node corresponding to the optional first subexpression in a {@code CASE} expression.
	public final Expr base;
	public final List<WhenThenPair> whenThenPairs;
	// The AST node corresponding to the optional {@code ELSE} subclause in a {@code CASE} expression.
	public final Expr elseExpr;

	public CaseExpr(Expr base,
			List<WhenThenPair> whenThenPairs,
			Expr elseExpr) {
		this.base = base;
		this.whenThenPairs = ToSql.requireNotEmpty(whenThenPairs);
		this.elseExpr = elseExpr;
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		a.append("CASE");
		if (base != null) {
			a.append(' ');
			base.toSql(a);
		}
		for (WhenThenPair whenThenPair : whenThenPairs) {
			a.append(' ');
			whenThenPair.toSql(a);
		}
		if (elseExpr != null) {
			a.append(" ELSE ");
			elseExpr.toSql(a);
		}
		a.append(" END");
	}
}
