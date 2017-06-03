package org.sqlite.parser.ast;

import java.io.IOException;

public enum TriggerEvent implements ToSql {
	Delete,
	Insert,
	Update,
	UpdateOf;

	@Override
	public void toSql(Appendable a) throws IOException {
		if (Delete == this) {
			a.append("DELETE");
		} else if (Insert == this) {
			a.append("INSERT");
		} else if (Update == this) {
			a.append("UPDATE");
		} else if (UpdateOf == this) {
			a.append("UPDATE OF");
		}
	}
}
