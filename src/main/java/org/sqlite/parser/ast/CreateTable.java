package org.sqlite.parser.ast;

import java.io.IOException;

import org.sqlite.parser.ParseException;

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
		if (body instanceof ColumnsAndConstraints) {
			boolean pk = false;
			ColumnsAndConstraints ccs = (ColumnsAndConstraints) body;
			for (ColumnDefinition column : ccs.columns) {
				for (ColumnConstraint constraint : column.constraints) {
					if (constraint instanceof PrimaryKeyColumnConstraint) {
						if (pk) {
							throw new ParseException(String.format("table \"%s\" has more than one primary key", tblName));
						}
						pk = true;
					}
				}
			}
			for (TableConstraint constraint : ccs.constraints) {
				if (constraint instanceof PrimaryKeyTableConstraint) {
					if (pk) {
						throw new ParseException(String.format("table \"%s\" has more than one primary key", tblName));
					}
					pk = true;
				}
			}
		}
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