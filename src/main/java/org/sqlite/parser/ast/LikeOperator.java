package org.sqlite.parser.ast;

import java.io.IOException;

public enum LikeOperator implements ToSql {
	Glob,
	Like,
	Match,
	Regexp;

	@Override
	public void toSql(Appendable a) throws IOException {
		if (Glob == this) {
			a.append("GLOB");
		} else if (Like == this) {
			a.append("LIKE");
		} else if (Match == this) {
			a.append("MATCH");
		} else if (Regexp == this) {
			a.append("REGEXP");
		}
	}
}
