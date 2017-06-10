package org.sqlite.parser.ast;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

// Sum Type: Expr vs Star vs TableStar
public class ResultColumn implements ToSql {
	public final Expr expr; // TODO SQLite parser builds expression for Star and TableStar
	public final As as;
	public final String tblName;

	public static ResultColumn expr(Expr expr, As as) {
		return new ResultColumn(requireNonNull(expr), as, null);
	}

	public static ResultColumn star() {
		return new ResultColumn(null, null, null);
	}

	public static ResultColumn tableStar(String tblName) {
		return new ResultColumn(null, null, requireNonNull(tblName));
	}

	private ResultColumn(Expr expr, As as, String tblName) {
		this.expr = expr;
		this.as = as;
		this.tblName = tblName;
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		if (expr != null) {
			expr.toSql(a);
			if (as != null) {
				a.append(' ');
				as.toSql(a);
			}
		} else if (tblName == null) {
			a.append('*');
		} else {
			doubleQuote(a, tblName);
			a.append(".*");
		}
	}
}
