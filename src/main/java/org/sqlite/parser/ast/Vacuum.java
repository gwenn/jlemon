package org.sqlite.parser.ast;

import java.io.IOException;

public class Vacuum implements Stmt {
	public final String name;

	public Vacuum(String name) {
		this.name = name;
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		a.append("VACUUM");
		if (name != null) {
			a.append(' ');
			doubleQuote(a, name);
		}
	}
}