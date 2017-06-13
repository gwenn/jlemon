package org.sqlite.parser.ast;

import java.io.IOException;
import java.util.List;

/**
 * CASE [expr] [WHEN expr THEN expr]* [ELSE expr] END
 */
public class CaseExpr implements Expr {
	public final Expr base;
	public final List<WhenThenPair> whenThenPairs;
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
	}
}
