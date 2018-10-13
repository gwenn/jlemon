package org.sqlite.parser.ast;

import java.io.IOException;
import java.util.List;

import static org.sqlite.parser.ast.ToSql.comma;

/**
 * An instance of this object holds the argument of the ON CONFLICT
 * clause of an UPSERT.
 */
public class Upsert implements ToSql {
	public final List<SortedColumn> targets; /* Optional description of conflicting index */
	public final Expr targetWhere; /* WHERE clause for partial index targets */
	public final List<Set> sets; /* The SET clause from an ON CONFLICT UPDATE */
	public final Expr where; /* WHERE clause for the ON CONFLICT UPDATE */

	public Upsert(List<SortedColumn> targets, Expr targetWhere, List<Set> sets, Expr where) {
		this.targets = targets;
		this.targetWhere = targetWhere;
		this.sets = sets;
		this.where = where;
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		a.append(" ON CONFLICT");
		if (targets != null) {
			a.append(" (");
			comma(a, targets);
			a.append(')');
			if (targetWhere != null) {
				a.append(" WHERE ");
				targetWhere.toSql(a);
			}
		}
		if (sets != null) {
			a.append(" DO UPDATE SET ");
			comma(a, sets);
			if (where != null) {
				a.append(" WHERE ");
				where.toSql(a);
			}
		} else {
			a.append(" DO NOTHING");
		}
	}
}
