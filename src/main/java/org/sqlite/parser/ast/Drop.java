package org.sqlite.parser.ast;

import java.io.IOException;
import java.util.List;

import static java.util.Objects.requireNonNull;

abstract class Drop implements Stmt {
	public final boolean ifExists;
	public final QualifiedName name;

	Drop(boolean ifExists,
			QualifiedName name) {
		this.ifExists = ifExists;
		this.name = requireNonNull(name);
	}

	abstract void appendKind(Appendable a) throws IOException;

	@Override
	public void toSql(Appendable a) throws IOException {
		a.append("DROP ");
		appendKind(a);
		a.append(' ');
		if (ifExists) {
			a.append("IF EXISTS ");
		}
		name.toSql(a);
	}
}