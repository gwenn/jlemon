package org.sqlite.parser.ast;

import java.io.IOException;
import java.util.List;

import static java.util.Collections.singletonList;
import static java.util.Objects.requireNonNull;
import static org.sqlite.parser.ast.ToSql.comma;
import static org.sqlite.parser.ast.ToSql.requireNotEmpty;

/**
 * Represents a parenthesized subexpression.
 */
public class ParenthesizedExpr implements Expr {
	public final List<Expr> exprs;

	public ParenthesizedExpr(Expr expr) {
		this.exprs = singletonList(requireNonNull(expr));
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
