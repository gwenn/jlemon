package org.sqlite.parser.ast;

import java.io.IOException;

public enum NullOperator implements ToSql {
	IsNull,
	NotNull,
	Not_Null;

	@Override
	public void toSql(Appendable a) throws IOException {
		if (IsNull == this) {
			a.append("ISNULL");
		} else if (NotNull == this) {
			a.append("NOTNULL");
		} else if (Not_Null == this) {
			a.append("NOT NULL");
		}
	}
}
