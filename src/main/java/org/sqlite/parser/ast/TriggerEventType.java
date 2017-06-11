package org.sqlite.parser.ast;

import java.io.IOException;

import org.sqlite.parser.TokenType;

public enum TriggerEventType implements ToSql {
	Delete,
	Insert,
	Update,
	UpdateOf;

	public static TriggerEventType from(int tt) {
		if (TokenType.TK_DELETE == tt) {
			return Delete;
		} else if (TokenType.TK_INSERT == tt) {
			return Insert;
		} else if (TokenType.TK_INSERT == tt) {
			return Insert;
		} else if (TokenType.TK_UPDATE == tt) {
			return Update;
		}
		throw new IllegalArgumentException(); // TODO
	}

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
