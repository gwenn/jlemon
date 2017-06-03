package org.sqlite.parser.ast;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

public class Release implements Stmt {
	public final String name;

	public Release(String name) {
		this.name = requireNonNull(name);
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		a.append("RELEASE ");
		doubleQuote(a, name);
	}
}