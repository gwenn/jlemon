package org.sqlite.parser.ast;

import java.io.IOException;

public enum TransactionType implements ToSql {
	Deferred,
	Immediate,
	Exclusive;

	public void toSql(Appendable a) throws IOException {
		if (Deferred == this) {
			a.append("DEFERRED");
		} else if (Immediate == this) {
			a.append("IMMEDIATE");
		} else if (Exclusive == this) {
			a.append("EXCLUSIVE");
		}
	}
}