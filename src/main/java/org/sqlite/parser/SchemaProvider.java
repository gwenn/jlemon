package org.sqlite.parser;

import java.sql.SQLException;

public interface SchemaProvider {
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

	// TODO getCatalogs: PRAGMA database_list
}
