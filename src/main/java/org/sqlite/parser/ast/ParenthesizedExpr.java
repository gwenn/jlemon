package org.sqlite.parser.ast;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static org.sqlite.parser.ast.ToSql.requireNotEmpty;

public class ParenthesizedExpr implements Expr {
	public final List<Expr> exprs;

	public ParenthesizedExpr(Expr expr) {
		this.exprs = Collections.singletonList(requireNonNull(expr));
	}
	public ParenthesizedExpr(List<Expr> exprs) {
		this.exprs = requireNotEmpty(exprs);
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		a.append('(');
		comma(a, exprs);
		a.append(')');
	}
}
