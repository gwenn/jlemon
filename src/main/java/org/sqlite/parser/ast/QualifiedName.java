package org.sqlite.parser.ast;

import java.io.IOException;

import org.sqlite.parser.Token;

import static java.util.Objects.requireNonNull;
import static org.sqlite.parser.ast.ToSql.doubleQuote;

public class QualifiedName implements ToSql {
	public final String dbName;
	public final String name;
	public String alias;

	public static QualifiedName from(Token xxx, Token yyy, Token alias) {
		QualifiedName qn;
		if (xxx == null) {
			qn = new QualifiedName(null, yyy.text());
		} else {
			qn = new QualifiedName(xxx.text(), yyy.text());
		}
		if (alias != null) {
			qn.alias = alias.text();
		}
		return qn;
	}

	public static QualifiedName from(Token xxx, String yyy) {
		if (yyy == null) {
			return new QualifiedName(null, xxx.text());
		} else {
			return new QualifiedName(xxx.text(), yyy);
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
		if (alias != null) {
			a.append(" AS ");
			doubleQuote(a, alias);
		}
	}
}