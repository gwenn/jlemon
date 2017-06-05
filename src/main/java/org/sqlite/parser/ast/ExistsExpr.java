package org.sqlite.parser.ast;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

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
