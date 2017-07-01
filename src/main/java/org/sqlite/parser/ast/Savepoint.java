package org.sqlite.parser.ast;

import java.io.IOException;

import static java.util.Objects.requireNonNull;
import static org.sqlite.parser.ast.ToSql.doubleQuote;

public class Savepoint implements Stmt {
	public final String name;

	public Savepoint(String name) {
		this.name = requireNonNull(name);
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		a.append("SAVEPOINT ");
		doubleQuote(a, name);
	}
}