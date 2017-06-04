package org.sqlite.parser.ast;

import java.io.IOException;

public enum RefAct implements ToSql {
	SetNull,
	SetDefault,
	Cascade,
	Restrict,
	NoAction;

	@Override
	public void toSql(Appendable a) throws IOException {
		if (SetNull == this) {
			a.append("SET NULL");
		} else if (SetDefault == this) {
			a.append("SET DEFAULT");
		} else if (Cascade == this) {
			a.append("CASCADE");
		} else if (Restrict == this) {
			a.append("RESTRICT");
		} else if (NoAction == this) {
			a.append("NO ACTION");
		}
	}
}
