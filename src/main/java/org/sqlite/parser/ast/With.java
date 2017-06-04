package org.sqlite.parser.ast;

import java.io.IOException;
import java.util.List;

import static org.sqlite.parser.ast.ToSql.requireNotEmpty;

public class With implements ToSql {
	public final boolean recursive;
	public final List<CommonTableExpr> ctes;

	public With(boolean recursive, List<CommonTableExpr> ctes) {
		this.recursive = recursive;
		this.ctes = requireNotEmpty(ctes);
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		a.append("WITH ");
		if (recursive) {
			a.append("RECURSIVE ");
		}
		comma(a, ctes);
	}
}
