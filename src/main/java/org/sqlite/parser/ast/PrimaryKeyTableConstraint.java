package org.sqlite.parser.ast;

import java.io.IOException;
import java.util.List;
import java.util.function.BiFunction;

import static org.sqlite.parser.ast.ToSql.comma;
import static org.sqlite.parser.ast.ToSql.requireNotEmpty;

public class PrimaryKeyTableConstraint extends TableConstraint implements PrimaryKeyConstraint {
	public final List<SortedColumn> columns;
	public final boolean autoIncrement;
	public final ResolveType conflictClause;

	public PrimaryKeyTableConstraint(String name,
			List<SortedColumn> columns,
			boolean autoIncrement,
			ResolveType conflictClause) {
		super(name);
		this.columns = requireNotEmpty(columns);
		for (SortedColumn column : columns) {
			if (!(column.name instanceof IdExpr)) {
				throw new IllegalArgumentException();
			}
		}
		this.conflictClause = conflictClause;
		this.autoIncrement = autoIncrement;
	}

	@Override
	public int getNumberOfColumns() {
		return columns.size();
	}

	@Override
	public String getColumnName(int index) {
		return ((IdExpr) columns.get(index).name).name;
	}

	@Override
	public String getPrimaryKeyName() {
		return name;
	}
	@Override
	public boolean allMatch(BiFunction<String, SortOrder, Boolean> columnChecker) {
		return columns.stream()
				.map(sc -> columnChecker.apply(((IdExpr) sc.name).name, sc.order))
				.allMatch(Boolean.TRUE::equals);
	}

	@Override
	public boolean isAutoIncrement() {
		return autoIncrement;
	}
	@Override
	public ResolveType getConflictClause() {
		return conflictClause;
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		constraintName(a);
		a.append("PRIMARY KEY");
		a.append('(');
		comma(a, columns);
		if (autoIncrement) {
			a.append(" AUTOINCREMENT");
		}
		a.append(')');
		if (conflictClause != null) {
			a.append(" ON CONFLICT ");
			conflictClause.toSql(a);
		}
	}
}
