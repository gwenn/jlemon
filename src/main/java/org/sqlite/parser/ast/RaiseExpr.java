package org.sqlite.parser.ast;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

public class RaiseExpr implements Expr {
	public final ResolveType type;
	public final String err;

	public RaiseExpr(ResolveType type, String err) {
		this.type = requireNonNull(type);
		if (type == ResolveType.Ignore || type == ResolveType.Replace) {
			throw new IllegalArgumentException();
		}
		this.err = err;
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		a.append("RAISE(");
		type.toSql(a);
		if (err != null) {
			a.append(", ");
			singleQuote(a, err);
		}
		a.append(')');
	}
}
