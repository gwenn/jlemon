package org.sqlite.parser.ast;

import java.io.IOException;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static org.sqlite.parser.ast.ToSql.isNotEmpty;

public class LikeExpr implements Expr {
	public final Expr lhs;
	public final boolean not;
	public final LikeOperator op;
	public final Expr rhs;
	public final Expr escape;

	public LikeExpr(Expr lhs, boolean not, LikeOperator op, Expr rhs, Expr escape) {
		this.lhs = requireNonNull(lhs);
		this.not = not;
		this.op = requireNonNull(op);
		this.rhs = requireNonNull(rhs);
		this.escape = escape;
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		lhs.toSql(a);
		if (not) {
			a.append(" NOT");
		}
		a.append(" LIKE ");
		rhs.toSql(a);
		if (escape != null) {
			a.append(" ESCAPE ");
			escape.toSql(a);
		}
	}
}
