package org.sqlite.parser.ast;

import java.io.IOException;

public enum CompoundOperator implements ToSql {
	Union,
	UnionAll,
	Except,
	Intersect;

	@Override
	public void toSql(Appendable a) throws IOException {
		if (Union == this) {
			a.append("UNION");
		} else if (UnionAll == this) {
			a.append("UNION ALL");
		} else if (Except == this) {
			a.append("EXCEPT");
		} else if (Intersect == this) {
			a.append("INTERSECT");
		}
	}
}
