package org.sqlite.parser.ast;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

public class Type implements ToSql {
	public final String name;
	public final TypeSize size;

	public Type(String name,
			TypeSize size) {
		this.name = requireNonNull(name);
		this.size = size;
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		doubleQuote(a, name);
		if (size != null) {
			a.append('(');
			size.toSql(a);
			a.append(')');
		}
	}
}
