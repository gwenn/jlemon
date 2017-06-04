package org.sqlite.parser.ast;

import java.io.IOException;

// Sum Type
public abstract class ColumnConstraint implements ToSql {
	public final String name;

	ColumnConstraint(String name) {
		this.name = name;
	}

	void constraintName(Appendable a) throws IOException {
		if (name != null) {
			a.append("CONSTRAINT ");
			doubleQuote(a, name);
			a.append(' ');
		}
	}
}
