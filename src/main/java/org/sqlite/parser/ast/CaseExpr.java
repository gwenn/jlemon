package org.sqlite.parser.ast;

import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;

public class CaseExpr implements Expr {
	public final Expr base;
	public final List<Entry<Expr,Expr>> whenThenPairs;
	public final Expr elseExpr;

	public CaseExpr(Expr base,
			List<Entry<Expr, Expr>> whenThenPairs,
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
		for (Entry<Expr, Expr> whenThenPair : whenThenPairs) {
			a.append(" WHEN ");
			whenThenPair.getKey().toSql(a);
			a.append(" THEN ");
			whenThenPair.getValue().toSql(a);
		}
		if (elseExpr != null) {
			a.append(" ELSE ");
			elseExpr.toSql(a);
		}
	}
}
