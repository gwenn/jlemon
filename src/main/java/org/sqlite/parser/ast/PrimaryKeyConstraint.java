package org.sqlite.parser.ast;

import java.util.function.BiFunction;

/**
 * We need the column name(s) so only {@link ColumnDefinition} and {@link PrimaryKeyTableConstraint} implement
 * this interface (not {@link PrimaryKeyColumnConstraint}).
 */
public interface PrimaryKeyConstraint {
	int getNumberOfColumns();
	String getColumnName(int index);
	String getPrimaryKeyName();
	boolean allMatch(BiFunction<String, SortOrder, Boolean> columnChecker);
	ResolveType getConflictClause();
	boolean isAutoIncrement();
}
