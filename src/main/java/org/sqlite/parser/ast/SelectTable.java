package org.sqlite.parser.ast;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

// Sum Type: table vs table call vs select vs sub select
public class SelectTable implements ToSql {
	public final QualifiedName tblName;
	public final Indexed indexed;
	public final List<Expr> exprs;
	public final Select select;
	public final FromClause from;

	public final As as;

	public static SelectTable table(QualifiedName tblName, As as, Indexed indexed) {
		return new SelectTable(tblName, indexed, null, null, null, as);
	}
	public static SelectTable tableCall(QualifiedName tblName, List<Expr> exprs, As as) {
		return new SelectTable(tblName, null, exprs == null ? Collections.emptyList() : null, null, null, as);
	}
	public static SelectTable select(Select select, As as) {
		return new SelectTable(null, null, null, select, null, as);
	}
	public static SelectTable sub(FromClause from, As as) {
		return new SelectTable(null, null, null, null, from, as);
	}

	private SelectTable(QualifiedName tblName,
			Indexed indexed,
			List<Expr> exprs,
			Select select,
			FromClause from,
			As as) {
		this.tblName = tblName;
		this.indexed = indexed;
		this.exprs = exprs;
		this.select = select;
		this.from = from;
		this.as = as;
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		if (exprs != null) {
			tblName.toSql(a);
			a.append('(');
			comma(a, exprs);
			a.append(')');
		} else if (select != null) {
			a.append('(');
			select.toSql(a);
			a.append(')');
		} else if (from != null) {
			a.append('(');
			from.toSql(a);
			a.append(')');
		} else {
			tblName.toSql(a);
		}
		if (as != null) {
			a.append(' ');
			as.toSql(a);
		}
		if (indexed != null) {
			a.append(' ');
			indexed.toSql(a);
		}
	}
}
