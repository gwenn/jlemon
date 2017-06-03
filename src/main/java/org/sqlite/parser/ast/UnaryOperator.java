package org.sqlite.parser.ast;

import java.io.IOException;

public enum UnaryOperator implements ToSql {
	// bitwise negation (~)
	BitwiseNot,
	// negative-sign
	Negative,
	// "NOT"
	Not,
	// positive-sign
	Positive;

	@Override
	public void toSql(Appendable a) throws IOException {
		if (BitwiseNot == this) {
			a.append("~");
		} else if (Negative == this) {
			a.append("-");
		} else if (Not == this) {
			a.append("NOT");
		} else if (Positive == this) {
			a.append("+");
		}
	}
}
