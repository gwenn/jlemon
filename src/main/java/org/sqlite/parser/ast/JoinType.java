package org.sqlite.parser.ast;

import java.io.IOException;

public enum JoinType implements ToSql {
	Left,
	LeftOuter,
	Inner,
	Cross;

	@Override
	public void toSql(Appendable a) throws IOException {
		if (Left == this) {
			a.append("LEFT");
		} else if (LeftOuter == this) {
			a.append("LEFT OUTER");
		} else if (Inner == this) {
			a.append("INNER");
		} else if (Cross == this) {
			a.append("CROSS");
		}
	}
}
