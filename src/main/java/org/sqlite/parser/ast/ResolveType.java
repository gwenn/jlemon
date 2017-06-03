package org.sqlite.parser.ast;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

public enum ResolveType implements ToSql {
	Rollback,
	Abort,
	Fail,
	Ignore,
	Replace;

	@Override
	public void toSql(Appendable a) throws IOException {
		if (Rollback == this) {
			a.append("ROLLBACK");
		} else if (Abort == this) {
			a.append("ABORT");
		} else if (Fail == this) {
			a.append("FAIL");
		} else if (Ignore == this) {
			a.append("IGNORE");
		} else if (Replace == this) {
			a.append("REPLACE");
		}
	}
}
