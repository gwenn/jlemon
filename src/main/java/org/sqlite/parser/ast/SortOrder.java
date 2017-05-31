package org.sqlite.parser.ast;

import java.io.IOException;

public enum SortOrder implements ToSql {
	Asc,
	Desc;

	public void toSql(Appendable a) throws IOException {
		if (Asc == this) {
			a.append("ASC");
		} else if (Desc == this) {
			a.append("DESC");
		}
	}
}