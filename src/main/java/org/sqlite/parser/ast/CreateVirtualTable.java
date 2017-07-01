package org.sqlite.parser.ast;

import java.io.IOException;

import static java.util.Objects.requireNonNull;
import static org.sqlite.parser.ast.ToSql.doubleQuote;

public class CreateVirtualTable implements Stmt {
	public final boolean ifNotExists;
	public final QualifiedName tblName;
	public final String moduleName;
	public String args;

	public CreateVirtualTable(boolean ifNotExists,
			QualifiedName tblName,
			String moduleName) {
		this.ifNotExists = ifNotExists;
		this.tblName = requireNonNull(tblName);
		this.moduleName = requireNonNull(moduleName);
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		a.append("CREATE VIRTUAL TABLE ");
		if (ifNotExists) {
			a.append("IF NOT EXISTS ");
		}
		tblName.toSql(a);
		a.append("USING ");
		doubleQuote(a, moduleName);
		a.append('(');
		if (args != null && !args.isEmpty()) {
			a.append(args);
		}
		a.append(')');
	}
}