package org.sqlite.parser.ast;

import java.io.IOException;

import org.sqlite.parser.Token;

import static java.util.Objects.requireNonNull;
import static org.sqlite.parser.ast.ToSql.singleQuote;

public class Pragma implements Stmt {
	public final QualifiedName name;
	public final String value;

	public static Pragma from(Token x, String y, String value) {
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