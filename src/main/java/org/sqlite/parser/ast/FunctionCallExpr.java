package org.sqlite.parser.ast;

import java.io.IOException;
import java.util.List;

import static org.sqlite.parser.ast.ToSql.isNotEmpty;

public class FunctionCallExpr implements Expr {
	public final String name;
	public final Distinctness distinctness;
	public final List<Expr> args;

	public FunctionCallExpr(String name,
			Distinctness distinctness,
			List<Expr> args) {
		this.name = name;
		this.distinctness = distinctness;
		this.args = args;
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		doubleQuote(a, name);
		a.append('(');
		if (distinctness != null) {
			distinctness.toSql(a);
			a.append(' ');
		}
		if (isNotEmpty(args)) {
			comma(a, args);
		}
		a.append(')');
	}
}
