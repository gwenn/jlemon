package org.sqlite.parser.ast;

import java.io.IOException;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static org.sqlite.parser.ast.ToSql.comma;
import static org.sqlite.parser.ast.ToSql.isNotEmpty;

public class Select implements Stmt, TriggerCmd {
	public final With with;
	public final SelectBody body;
	public final List<SortedColumn> orderBy;
	public final Limit limit;

	public static Select from(OneSelect oneSelect) {
		return from(new SelectBody(oneSelect, null));
	}

	public static Select from(SelectBody body) {
		return new Select(null, body, null, null);
	}

	public Select(With with,
			SelectBody body,
			List<SortedColumn> orderBy,
			Limit limit) {
		this.with = with;
		this.body = requireNonNull(body);
		this.orderBy = orderBy;
		this.limit = limit;
		if (body.select.values != null && (isNotEmpty(orderBy) || limit != null)) {
			throw new IllegalArgumentException("ORDER BY or LIMIT clauses cannot be used with VALUES");
		}
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		if (with != null) {
			with.toSql(a);
			a.append(' ');
		}
		body.toSql(a);
		if (isNotEmpty(orderBy)) {
			a.append(" ORDER BY ");
			comma(a, orderBy);
		}
		if (limit != null) {
			a.append(' ');
			limit.toSql(a);
		}
	}
}
