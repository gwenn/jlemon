package org.sqlite.parser.ast;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

public class ColumnDefinition implements ToSql {
	public final String colName;

	public ColumnDefinition(String colName) {
		this.colName = requireNonNull(colName);
	}

	@Override
	public void toSql(Appendable a) throws IOException {
	}
}
