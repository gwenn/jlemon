package org.sqlite.parser;

import java.sql.SQLException;
import java.util.List;

import org.sqlite.parser.ast.QualifiedName;

public interface SchemaProvider {
	/**
	 * Find all tables matching the specified pattern.
	 *
	 * @param dbName May be null to search in all databases, empty to search in {@code "temp"} and {@code "main"}.
	 * @param tableNamePattern LIKE pattern. May be null or empty to retrieve all tables.
	 */
	List<QualifiedName> getExactTableNames(String dbName, String tableNamePattern) throws SQLException;

	/**
	 * Find the database containing this table.
	 *
	 * @param dbName    May be null to search in all databases, empty to search in {@code "temp"} and {@code "main"}.
	 * @param tableName Table name
	 * @return {@code "temp"} or {@code "main"} or attached database name.
	 * If there is no table matching {@code tableName}, the returned value is undefined.
	 */
	String getDbName(String dbName, String tableName) throws SQLException;

	/**
	 * @param dbName    {@code null} means {@code "main"}.
	 * @param tableName Table name
	 * @return SQL used to create the specified table
	 */
	String getSchema(String dbName, String tableName) throws SQLException;

	/**
	 * PRAGMA database_list
	 * @param dbName May be null to search in all databases, empty to search in {@code "temp"} and {@code "main"}.
	 */
	List<String> getDbNames(String dbName) throws SQLException;
}
