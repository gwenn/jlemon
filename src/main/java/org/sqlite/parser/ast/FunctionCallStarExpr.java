package org.sqlite.parser.ast;

import java.io.IOException;

import static java.util.Objects.requireNonNull;
import static org.sqlite.parser.ast.ToSql.doubleQuote;

/**
 * function-name(*)
 */
public class FunctionCallStarExpr implements Expr {
	public final String name;
	public final Window overClause;

	public FunctionCallStarExpr(String name, Window overClause) {
		this.name = requireNonNull(name);
		this.overClause = overClause;
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		doubleQuote(a, name);
		a.append("(*)");
		if (overClause != null) {
			a.append(' ');
			overClause.toSql(a);
		}
	}
}
