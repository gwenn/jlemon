package org.sqlite.parser.ast;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

public class AlterTable implements Stmt {
	public final QualifiedName tblName;
	public final String renameTo;
	public final ColumnDefinition colDefinition;

	public AlterTable(QualifiedName tblName,
			String renameTo,
			ColumnDefinition colDefinition) {
		this.tblName = requireNonNull(tblName);
		this.renameTo = renameTo;
		this.colDefinition = colDefinition;
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		a.append("ALTER TABLE ");
		tblName.toSql(a);
		if (renameTo != null) {
			a.append(" RENAME TO ");
			doubleQuote(a, renameTo);
		} else {
			a.append(" ADD COLUMN ");
			colDefinition.toSql(a);
		}
	}
}