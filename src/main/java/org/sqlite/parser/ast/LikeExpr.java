package org.sqlite.parser.ast;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

public class LikeExpr implements Expr {
	public final Expr lhs;
	public final NotLike op;
	public final Expr rhs;
	public final Expr escape;

	public LikeExpr(Expr lhs, NotLike op, Expr rhs, Expr escape) {
		this.lhs = requireNonNull(lhs);
		this.op = requireNonNull(op);
		this.rhs = requireNonNull(rhs);
		this.escape = escape;
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		lhs.toSql(a);
		a.append(' ');
		op.toSql(a);
		a.append(' ');
		rhs.toSql(a);
		if (escape != null) {
			a.append(" ESCAPE ");
			escape.toSql(a);
		}
	}
}
