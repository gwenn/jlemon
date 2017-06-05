package org.sqlite.parser.ast;

import java.io.IOException;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static org.sqlite.parser.ast.ToSql.isNotEmpty;
import static org.sqlite.parser.ast.ToSql.requireNotEmpty;

public class InListExpr implements Expr {
	public final Expr lhs;
	public final boolean not;
	public final List<Expr> rhs;

	public InListExpr(Expr lhs, boolean not, List<Expr> rhs) {
		this.lhs = requireNonNull(lhs);
		this.not = not;
		this.rhs = rhs;
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		lhs.toSql(a);
		if (not) {
			a.append(" NOT");
		}
		a.append(" IN (");
		if (isNotEmpty(rhs)) {
			comma(a, rhs);
		}
		a.append(")");
	}
}
