package org.sqlite.parser;

import org.sqlite.parser.ast.*;

public class Context {
	ExplainKind explain;
	private String constraintName;
	Stmt stmt;
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

	/**
	 * This routine is called after a single SQL statement has been parsed.
	 */
	public void sqlite3FinishCoding() {
		done = true;
	}

	public boolean done() {
		return done;
	}
}
