package org.sqlite.parser.ast;

import java.io.IOException;

import static org.sqlite.parser.ast.ToSql.doubleQuote;

public class Begin implements Stmt {
	public final TransactionType type;
	public final String name;

	public Begin(TransactionType type, String name) {
		this.type = type;
		this.name = name;
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		a.append("BEGIN");
		if (type != null) {
			a.append(' ');
			type.toSql(a);
		}
		if (name != null) {
			a.append(" TRANSACTION ");
			doubleQuote(a, name);
		}
	}
}