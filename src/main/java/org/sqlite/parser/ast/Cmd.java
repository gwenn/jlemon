package org.sqlite.parser.ast;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

public class Cmd implements ToSql {
	public final ExplainKind explain;
	public final Stmt stmt;

	public Cmd(ExplainKind explain, Stmt stmt) {
		this.explain = explain;
		this.stmt = requireNonNull(stmt);
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		if (explain != null) {
			explain.toSql(a);
			a.append(' ');
		}
		stmt.toSql(a);
		a.append(';');
	}
}