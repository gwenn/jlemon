package org.sqlite.parser.ast;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

public class AlterTable implements Stmt {
	public final QualifiedName tblName;

	public AlterTable(QualifiedName tblName) {
		this.tblName = requireNonNull(tblName);
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		a.append("ALTER TABLE ");
		tblName.toSql(a);
		a.append(' ');
		// TODO body
	}
}