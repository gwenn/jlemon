package org.sqlite.parser.ast;

import java.io.IOException;

public enum Distinctness implements ToSql {
	Distinct,
	All;

	@Override
	public void toSql(Appendable a) throws IOException {
		if (Distinct == this) {
			a.append("DISTINCT");
		} else if (All == this) {
			a.append("ALL");
		}
	}
}
