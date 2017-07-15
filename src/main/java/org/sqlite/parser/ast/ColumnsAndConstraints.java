package org.sqlite.parser.ast;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.sqlite.parser.ast.ToSql.comma;
import static org.sqlite.parser.ast.ToSql.isNotEmpty;
import static org.sqlite.parser.ast.ToSql.requireNotEmpty;

public class ColumnsAndConstraints implements CreateTableBody {
	public final List<ColumnDefinition> columns;
	public final List<TableConstraint> constraints;
	public final boolean without;

	public ColumnsAndConstraints(List<ColumnDefinition> columns, List<TableConstraint> constraints, boolean without) {
		this.columns = requireNotEmpty(columns);
		this.constraints = constraints;
		this.without = without;
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		a.append('(');
		comma(a, columns);
		if (isNotEmpty(constraints)) {
			a.append(", ");
			comma(a, constraints);
		}
		a.append(')');
		if (without) {
			a.append(" WITHOUT ROWID");
		}
	}

	public boolean isAutoIncrement(String columnName) {
		return findPrimaryKeyColumnConstraint(columnName)
				.map(pkcc -> pkcc.autoIncrement)
				.findFirst().orElse(
						findPrimaryKeyTableConstraint(columnName)
								.map(pktc -> pktc.autoIncrement)
								.orElse(false));
	}

	private Stream<PrimaryKeyColumnConstraint> findPrimaryKeyColumnConstraint(String columnName) {
		return findColumnDefinition(columnName)
				.flatMap(col -> col.constraints.stream())
				.filter(PrimaryKeyColumnConstraint.class::isInstance)
				.map(PrimaryKeyColumnConstraint.class::cast);
	}

	private Stream<ColumnDefinition> findColumnDefinition(String columnName) {
		return columns.stream()
				.filter(col -> col.nameAndType.colName.equalsIgnoreCase(columnName));
	}
	private Optional<PrimaryKeyTableConstraint> findPrimaryKeyTableConstraint(String columnName) {
		return constraints.stream()
				.filter(PrimaryKeyTableConstraint.class::isInstance)
				.map(PrimaryKeyTableConstraint.class::cast)
				.filter(tc -> tc.isOnlyOn(columnName))
				.findFirst();
	}

	public boolean isGeneratedColumn(String columnName) {
		return isAnAliasForRowId(columnName);
	}
	public boolean isAnAliasForRowId(String columnName) {
		if (without) {
			return false;
		}
		return findColumnDefinition(columnName)
				.findFirst()
				.map(cd -> cd.isAnAliasForRowId())
				.orElse(findPrimaryKeyTableConstraint(columnName)
						.map(pktc -> pktc.columns.get(0).order)
						.map(order -> order == null || SortOrder.Asc == order))
				.orElse(Boolean.FALSE);
	}
}
