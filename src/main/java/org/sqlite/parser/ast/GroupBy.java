package org.sqlite.parser.ast;

import java.io.IOException;
import java.util.List;

import static org.sqlite.parser.ast.ToSql.requireNotEmpty;

public class GroupBy implements ToSql {
	public final List<Expr> exprs;
	public final Expr having;

	public GroupBy(List<Expr> exprs, Expr having) {
		this.exprs = requireNotEmpty(exprs);
		this.having = having;
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		a.append("GROUP BY ");
		comma(a, exprs);
		if (having != null) {
			a.append(" HAVING ");
			having.toSql(a);
		}
	}
}
