package org.sqlite.parser.ast;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

public class QualifiedName implements ToSql {
	public final String dbName;
	public final String name;

	public QualifiedName(String dbName, String name) {
		this.dbName = dbName;
		this.name = requireNonNull(name);
	}

	public void toSql(Appendable a) throws IOException {
		if (dbName != null) {
			a.append(dbName);
			a.append('.');
		}
		a.append(name);
	}
}