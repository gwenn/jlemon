package org.sqlite.parser.ast;

import java.io.IOException;

import org.sqlite.parser.Token;

import static org.sqlite.parser.ast.ToSql.doubleQuote;

public class RenameColumn implements ToSql {
	public final String oldName;
	public final String newName;

	public RenameColumn(String oldName, String newName) {
		this.oldName = oldName;
		this.newName = newName;
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		a.append("RENAME ");
		doubleQuote(a, oldName);
		a.append(" TO ");
		doubleQuote(a, newName);
	}
}
