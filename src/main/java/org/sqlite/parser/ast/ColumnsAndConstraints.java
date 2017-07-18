package org.sqlite.parser.ast;

import java.io.IOException;
import java.util.List;

import org.sqlite.parser.ParseException;

import static org.sqlite.parser.ast.ToSql.comma;
import static org.sqlite.parser.ast.ToSql.isNotEmpty;
import static org.sqlite.parser.ast.ToSql.nullToEmpty;
import static org.sqlite.parser.ast.ToSql.requireNotEmpty;

public class ColumnsAndConstraints implements CreateTableBody {
	public final List<ColumnDefinition> columns;
	public final List<TableConstraint> constraints;
	public final boolean without;
	public final PrimaryKeyConstraint primaryKeyConstraint;

	public ColumnsAndConstraints(List<ColumnDefinition> columns, List<TableConstraint> constraints, boolean without) {
		this.columns = requireNotEmpty(columns);
		this.constraints = nullToEmpty(constraints);
		this.without = without;
		// "table \"%s\" has more than one primary key"
		PrimaryKeyConstraint pk = null;
		for (ColumnDefinition column : columns) {
			if (column.primaryKeyColumnConstraint != null) {
				if (pk != null) {
					throw new ParseException("More than one primary key");
				}
				pk = column;
			}
		}
		// TODO "conflicting ON CONFLICT clauses specified"
		// TODO "unknown column \"%s\" in foreign key definition"
		for (TableConstraint constraint : this.constraints) {
			if (constraint instanceof PrimaryKeyTableConstraint) {
				if (pk != null) {
					throw new ParseException("More than one primary key");
				}
				pk = (PrimaryKeyConstraint) constraint;
			}
		}
		primaryKeyConstraint = pk;
		// "PRIMARY KEY missing on table %s"
		if (without && pk == null) {
			throw new ParseException("PRIMARY KEY missing");
		}
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

	/**
	 * <a href="http://sqlite.org/autoinc.html">SQLite Autoincrement</a>
	 */
	public boolean isAutoIncrement(String columnName) {
		if (primaryKeyConstraint == null) {
			return false;
		}
		return primaryKeyConstraint.allMatch((pkColName, order) -> pkColName.equalsIgnoreCase(columnName))
				&& (primaryKeyConstraint.isAutoIncrement() || isAnAliasForRowId(columnName));
	}

	public boolean isGeneratedColumn(String columnName) {
		return isAnAliasForRowId(columnName);
	}

	/**
	 * <a href="http://sqlite.org/lang_createtable.html#rowid">ROWIDs and the INTEGER PRIMARY KEY</a>
	 */
	public boolean isAnAliasForRowId(String columnName) {
		if (without || primaryKeyConstraint == null) {
			return false;
		}
		if (!primaryKeyConstraint.allMatch((pkColName, order) -> pkColName.equalsIgnoreCase(columnName) && order == null || SortOrder.Asc == order)) {
			return false;
		}
		Type colType;
		if (primaryKeyConstraint instanceof ColumnDefinition) {
			colType = ((ColumnDefinition) primaryKeyConstraint).nameAndType.colType;
		} else {
			colType = columns.stream()
					.filter(c -> columnName.equalsIgnoreCase(c.nameAndType.colName))
					.map(c -> c.nameAndType.colType)
					.findAny()
					.orElseThrow(AssertionError::new);
		}
		return colType != null && colType.name.equalsIgnoreCase("INTEGER") && colType.size == null;
	}

}
