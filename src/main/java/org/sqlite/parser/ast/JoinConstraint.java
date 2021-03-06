package org.sqlite.parser.ast;

import java.io.IOException;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static org.sqlite.parser.ast.ToSql.commaNames;
import static org.sqlite.parser.ast.ToSql.requireNotEmpty;

// Sum Type: on vs using
public class JoinConstraint implements ToSql {
	public final Expr on;
	public final List<String> colNames;

	public static JoinConstraint on(Expr expr) {
		return new JoinConstraint(requireNonNull(expr), null);
	}
	public static JoinConstraint from(Expr expr, List<String> colNames) {
		if (expr == null && colNames == null) {
			return null;
		}
		if (expr != null && colNames != null) {
			throw new IllegalArgumentException(String.format("ON and USING join constraints are exclusive. You can specify both: %s, %s", expr, colNames)); // TODO toSql
		}
		return new JoinConstraint(expr, colNames);
	}

	public static JoinConstraint using(List<String> colNames) {
		return new JoinConstraint(null, requireNotEmpty(colNames));
	}

	private JoinConstraint(Expr on, List<String> colNames) {
		this.on = on;
		this.colNames = colNames;
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		if (on != null) {
			a.append("ON ");
			on.toSql(a);
		} else {
			a.append("USING (");
			commaNames(a, colNames);
			a.append(')');
		}
	}
}
