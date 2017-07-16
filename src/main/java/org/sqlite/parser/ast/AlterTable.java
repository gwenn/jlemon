package org.sqlite.parser.ast;

import java.io.IOException;

import static java.util.Objects.requireNonNull;
import static org.sqlite.parser.ast.ToSql.doubleQuote;

// Sum Type: rename vs add column
public class AlterTable implements Stmt {
	public final QualifiedName tblName;
	public final String renameTo;
	public final ColumnDefinition colDefinition;

	public AlterTable(QualifiedName tblName,
			String renameTo) {
		this.tblName = requireNonNull(tblName);
		// TODO check renameTo != tblName
		this.renameTo = requireNonNull(renameTo);
		this.colDefinition = null;
	}
	public AlterTable(QualifiedName tblName,
			ColumnDefinition colDefinition) {
		this.tblName = requireNonNull(tblName);
		this.renameTo = null;
		this.colDefinition = requireNonNull(colDefinition);
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