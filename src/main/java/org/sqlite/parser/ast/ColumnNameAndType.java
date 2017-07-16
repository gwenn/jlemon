package org.sqlite.parser.ast;

import java.io.IOException;
import java.sql.Types;

import static java.util.Objects.requireNonNull;
import static org.sqlite.parser.ast.ToSql.doubleQuote;

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

	public LiteralExpr getTypeExpr() {
		if (colType == null) {
			return LiteralExpr.EMPTY_STRING;
		}
		return LiteralExpr.string(colType.toSql());
	}

	/**
	 * @return {@link java.sql.Types}.*
	 */
	public int getDataType() {
		if (colType == null) {
			return Types.OTHER;
		}
		return colType.getDataType();
	}
}
