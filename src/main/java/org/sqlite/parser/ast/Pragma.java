package org.sqlite.parser.ast;

import java.io.IOException;

import org.sqlite.parser.Token;

import static java.util.Objects.requireNonNull;

public class Pragma implements Stmt {
	public final QualifiedName name;
	public final Expr value;

	public static Pragma from(Token x, String y, Expr value) {
		return new Pragma(QualifiedName.from(x, y), value);
	}

	public Pragma(QualifiedName name, Expr value) {
		this.name = requireNonNull(name);
		this.value = value;
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		a.append("PRAGMA ");
		name.toSql(a);
		if (value != null) {
			a.append('(');
			value.toSql(a);
			a.append(')');
		}
	}
}