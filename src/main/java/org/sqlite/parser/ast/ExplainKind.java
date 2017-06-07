package org.sqlite.parser.ast;

import java.io.IOException;

public enum ExplainKind implements ToSql {
	Explain,
	QueryPlan;

	@Override
	public void toSql(Appendable a) throws IOException {
		if (Explain == this) {
			a.append("EXPLAIN");
		} else if (QueryPlan == this) {
			a.append("EXPLAIN QUERY PLAN");
		}
	}
}
