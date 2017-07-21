package org.sqlite.parser;

import java.sql.SQLException;

public interface SchemaProvider {
	/**
	 * Find the database containing this table.
	 * @param dbName May be null to search in all databases.
	 * @param tableName Table name
	 * @return {@code "temp"} or {@code "main"} or attached database name
	 */
	String getDbName(String dbName, String tableName) throws SQLException;

	/**
	 * @param dbName May be null to search in all databases.
	 * @param tableName Table name
	 * @return SQL used to create the specified table
	 */
	String getSchema(String dbName, String tableName) throws SQLException;

	/**
	 * Find all tables in the specified database.
	 * @param dbName Database name.
	 * @return all tables name
	 */
	Iterable<String> getTables(String dbName) throws SQLException;
}
