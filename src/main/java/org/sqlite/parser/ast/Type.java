package org.sqlite.parser.ast;

import java.io.IOException;
import java.sql.Types;

import static java.util.Objects.requireNonNull;
import static org.sqlite.parser.ast.ToSql.doubleQuote;

public class Type implements ToSql {
	public final String name;
	public final TypeSize size;

	public Type(String name,
			TypeSize size) {
		this.name = requireNonNull(name);
		this.size = size;
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		doubleQuote(a, name);
		if (size != null) {
			a.append('(');
			size.toSql(a);
			a.append(')');
		}
	}

	/**
	 * @return {@link java.sql.Types}.*
	 */
	public int getDataType() {
		if (name.isEmpty()) {
			return Types.OTHER;
		}
		String declType = name.toUpperCase();
		if (declType.contains("INT")) {
			return Types.INTEGER;
		} else if (declType.contains("TEXT") || declType.contains("CHAR") || declType.contains("CLOB")) {
			return Types.VARCHAR;
		} else if (declType.contains("BLOB")) {
			return Types.BLOB;
		} else if (declType.contains("REAL") || declType.contains("FLOA") || declType.contains("DOUB")) {
			return Types.REAL;
		} else {
			return Types.NUMERIC;
		}
	}
}
