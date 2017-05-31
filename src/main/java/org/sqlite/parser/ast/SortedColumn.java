package org.sqlite.parser.ast;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

public class SortedColumn implements ToSql {
	public final Expr name;
	public final SortOrder order;

	public SortedColumn(Expr name, SortOrder order) {
		this.name = requireNonNull(name);
		this.order = order;
	}

	public void toSql(Appendable a) throws IOException {
		name.toSql(a);
		if (order != null) {
			a.append(' ');
			order.toSql(a);
		}
	}
}