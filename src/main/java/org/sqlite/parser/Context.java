package org.sqlite.parser;

import org.sqlite.parser.ast.*;

class Context {
	ExplainKind explain;
	private String constraintName;
	Stmt stmt;
	private boolean done;

	void sqlite3ErrorMsg(String message, Object... args) {
		throw new ParseException(message, args);
	}

	String constraintName() {
		final String constraintName = this.constraintName;
		this.constraintName = null;
		return constraintName;
	}
	void constraintName(String constraintName) {
		this.constraintName = constraintName;
	}

	/**
	 * This routine is called after a single SQL statement has been parsed.
	 */
	void sqlite3FinishCoding() {
		done = true;
	}

	boolean done() {
		return done;
	}
}
