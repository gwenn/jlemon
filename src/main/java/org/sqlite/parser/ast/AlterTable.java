package org.sqlite.parser.ast;

import java.io.IOException;

import static java.util.Objects.requireNonNull;
import static org.sqlite.parser.ast.ToSql.doubleQuote;

// Sum Type: rename vs add column vs rename column
public class AlterTable implements Stmt {
	public final QualifiedName tblName;
	public final String renameTo;
	public final ColumnDefinition colDefinition;
	public final RenameColumn renameColumn;

	public AlterTable(QualifiedName tblName,
			String renameTo) {
		this.tblName = requireNonNull(tblName);
		// TODO check renameTo != tblName
		this.renameTo = requireNonNull(renameTo);
		this.colDefinition = null;
		this.renameColumn = null;
	}
	public AlterTable(QualifiedName tblName,
			ColumnDefinition colDefinition) {
		this.tblName = requireNonNull(tblName);
		this.renameTo = null;
		this.colDefinition = requireNonNull(colDefinition);
		this.renameColumn = null;
		// TODO "Cannot add a PRIMARY KEY column"
		// "Cannot add a UNIQUE column"
		// "Cannot add a REFERENCES column with non-NULL default value"
		// "Cannot add a NOT NULL column with default value NULL"
		// "Cannot add a column with non-constant default"
	}
	public AlterTable(QualifiedName tblName, RenameColumn renameColumn) {
		this.tblName = requireNonNull(tblName);
		this.renameTo = null;
		this.colDefinition = null;
		this.renameColumn = requireNonNull(renameColumn);
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		a.append("ALTER TABLE ");
		tblName.toSql(a);
		a.append(' ');
		if (renameTo != null) {
			a.append("RENAME TO ");
			doubleQuote(a, renameTo);
		} else if (colDefinition != null) {
			a.append("ADD COLUMN ");
			colDefinition.toSql(a);
		} else {
			renameColumn.toSql(a);
		}
	}
}