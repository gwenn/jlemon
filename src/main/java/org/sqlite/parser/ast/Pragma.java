package org.sqlite.parser.ast;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

public class Pragma implements Stmt {
	public final QualifiedName name;
	public final String value;

	public static Pragma from(String x, String y, String value) {
		return new Pragma(QualifiedName.from(x, y), value);
	}

	public Pragma(QualifiedName name, String value) {
		this.name = requireNonNull(name);
		this.value = value;
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		a.append("PRAGMA ");
		name.toSql(a);
		if (value != null) {
			a.append('(');
			singleQuote(a, value);
			a.append(')');
		}
	}
}