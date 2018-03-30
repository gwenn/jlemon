package org.sqlite.parser.ast;

import java.io.IOException;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static org.sqlite.parser.ast.ToSql.comma;

/**
 * Represents an @IN@ expression with the right-hand side being a table name, optionally qualified by a database name.
 * <pre>{@code expr [NOT] IN (schema-name.table-name)
 * expr [NOT] IN (schema-name.table-function(expr[, expr]*))}</pre>
 */
public class InTableExpr implements Expr {
	public final Expr lhs;
	public final boolean not;
	public final QualifiedName rhs;
	public final List<Expr> args;

	public InTableExpr(Expr lhs, boolean not, QualifiedName rhs, List<Expr> args) {
		this.lhs = requireNonNull(lhs);
		this.not = not;
		this.rhs = requireNonNull(rhs);
		this.args = args;
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		lhs.toSql(a);
		if (not) {
			a.append(" NOT");
		}
		a.append(" IN ");
		rhs.toSql(a);
		if (args != null) {
			a.append('(');
			comma(a, args);
			a.append(')');
		}
	}
}
