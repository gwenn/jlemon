package org.sqlite.parser.ast;

import java.io.IOException;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static org.sqlite.parser.ast.ToSql.isNotEmpty;

public class InTableExpr implements Expr {
	public final Expr lhs;
	public final boolean not;
	public final QualifiedName rhs;

	public InTableExpr(Expr lhs, boolean not, QualifiedName rhs) {
		this.lhs = requireNonNull(lhs);
		this.not = not;
		this.rhs = requireNonNull(rhs);
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		lhs.toSql(a);
		if (not) {
			a.append(" NOT");
		}
		a.append(" IN ");
		rhs.toSql(a);
	}
}
