package org.sqlite.parser;

import org.sqlite.parser.ast.*;

public class Context {
	ExplainKind explain;
	private String constraintName;
	private boolean done;

	void sqlite3ErrorMsg(String message, Object... args) {
		throw new ParseException(message, args);
	}

	public String constraintName() {
		final String constraintName = this.constraintName;
		this.constraintName = null;
		return constraintName;
	}
	public void constraintName(String constraintName) {
		this.constraintName = constraintName;
	}

	public void sqlite3FinishCoding() {
		done = true;
	}

	public boolean done() {
		return done;
	}
}
