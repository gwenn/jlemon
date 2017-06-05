package org.sqlite.parser.ast;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

public class BinaryExpr implements Expr {
	public final Expr lhs;
	public final Operator op;
	public final Expr rhs;

	public BinaryExpr(Expr lhs, Operator op, Expr rhs) {
		this.lhs = requireNonNull(lhs);
		this.op = requireNonNull(op);
		this.rhs = requireNonNull(rhs);
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		lhs.toSql(a);
		a.append(' ');
		op.toSql(a);
		a.append(' ');
		rhs.toSql(a);
	}
}
