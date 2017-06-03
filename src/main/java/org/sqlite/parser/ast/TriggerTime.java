package org.sqlite.parser.ast;

import java.io.IOException;

public enum TriggerTime implements ToSql {
	Before,
	After,
	InsteadOf;

	@Override
	public void toSql(Appendable a) throws IOException {
		if (Before == this) {
			a.append("BEFORE");
		} else if (After == this) {
			a.append("AFTER");
		} else if (InsteadOf == this) {
			a.append("INSTEAD OF");
		}
	}
}
