package org.sqlite.parser.ast;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

public class ColumnNameAndType implements ToSql {
	public final String colName;
	public final Type colType;

	public ColumnNameAndType(String colName, Type colType) {
		this.colName = requireNonNull(colName);
		this.colType = colType;
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		doubleQuote(a, colName);
		if (colType != null) {
			a.append(' ');
			colType.toSql(a);
		}
	}
}
