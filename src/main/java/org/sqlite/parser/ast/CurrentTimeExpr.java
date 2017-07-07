package org.sqlite.parser.ast;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

public class CurrentTimeExpr implements Expr {
	public final String currentTimeKeyword;

	public CurrentTimeExpr(String currentTimeKeyword) {
		this.currentTimeKeyword = requireNonNull(currentTimeKeyword);
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		a.append(currentTimeKeyword);
	}
}
