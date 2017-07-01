package org.sqlite.parser.ast;

import java.io.IOException;

import static java.util.Objects.requireNonNull;
import static org.sqlite.parser.ast.ToSql.doubleQuote;

public class As implements ToSql {
	public final boolean elided;
	public final String name;

	public static As as(String name) {
		return new As(false, name);
	}
	public static As elided(String name) {
		return new As(true, name);
	}

	private As(boolean elided, String name) {
		this.elided = elided;
		this.name = requireNonNull(name);
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		if (!elided) {
			a.append("AS ");
		}
		doubleQuote(a, name);
	}
}
