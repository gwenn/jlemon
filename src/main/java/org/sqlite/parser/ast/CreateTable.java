package org.sqlite.parser.ast;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

public class CreateTable implements Stmt {
	public final boolean temporary;
	public final boolean ifNotExists;
	public final QualifiedName tblName;
	public final CreateTableBody body;

	public CreateTable(boolean temporary,
			boolean ifNotExists,
			QualifiedName tblName,
			CreateTableBody body) {
		this.temporary = temporary;
		this.ifNotExists = ifNotExists;
		this.tblName = requireNonNull(tblName);
		this.body = requireNonNull(body);
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		a.append("CREATE ");
		if (temporary) {
			a.append("TEMP ");
		}
		a.append("TABLE ");
		if (ifNotExists) {
			a.append("IF NOT EXISTS ");
		}
		tblName.toSql(a);
		body.toSql(a);
	}
}