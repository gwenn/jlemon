package org.sqlite.parser.ast;

import java.io.IOException;

import static java.util.Objects.requireNonNull;
import static org.sqlite.parser.ast.ToSql.doubleQuote;

public class QualifiedName implements ToSql {
	public final String dbName;
	public final String name;

	public static QualifiedName from(String xxx, String yyy) {
		if (yyy == null) {
			return new QualifiedName(null, xxx);
		} else {
			return new QualifiedName(xxx, yyy);
		}
	}

	public QualifiedName(String dbName, String name) {
		this.dbName = dbName;
		this.name = requireNonNull(name);
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		if (dbName != null) {
			doubleQuote(a, dbName);
			a.append('.');
		}
		doubleQuote(a, name);
	}
}