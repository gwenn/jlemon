package org.sqlite.parser.ast;

import java.io.IOException;

public class Commit implements Stmt {
	public final String name;

	public Commit(String name) {
		this.name = name;
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		a.append("COMMIT");
		if (name != null) {
			a.append(" TRANSACTION ");
			doubleQuote(a, name);
		}
	}
}