package org.sqlite.parser;

import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.sqlite.parser.ast.QualifiedName;
import org.sqlite.parser.ast.Select;

public class EnhancedPragmaTest {

	private static final String CONTRACT_SCHEMA = "CREATE TABLE contract (\n" +
			"  id INTEGER PRIMARY KEY NOT NULL,\n" +
			"  _number TEXT NOT NULL,\n" +
			"  title TEXT NOT NULL,\n" +
			"  amendment TEXT,\n" +
			"  billing_address TEXT NOT NULL, -- seems useless to decompose it\n" +
			"  member_id INTEGER NOT NULL,\n" +
			"  client_id INTEGER NOT NULL,\n" +
			"  average_daily_rate INTEGER NOT NULL,\n" +
			"  saturday_comp_rate INTEGER, -- TODO (Type, Rate) dedicated table?\n" +
			"  sunday_comp_rate INTEGER,\n" +
			"  holiday_comp_rate INTEGER,\n" +
			"  night_comp_rate INTEGER,\n" +
			"  saturday_on_call_rate INTEGER,\n" +
			"  sunday_on_call_rate INTEGER,\n" +
			"  holiday_on_call_rate INTEGER,\n" +
			"  night_on_call_rate INTEGER,\n" +
			"  start_date INTEGER NOT NULL,\n" +
			"  end_date INTEGER,\n" +
			"  version INTEGER NOT NULL,\n" +
			"  FOREIGN KEY (member_id) REFERENCES member(id),\n" +
			"  FOREIGN KEY (client_id) REFERENCES client(id)\n" +
			");";

	private static final String CLIENT_SCHEMA = "CREATE TABLE client (\n" +
			"  id INTEGER PRIMARY KEY NOT NULL,\n" +
			"  name TEXT NOT NULL UNIQUE,\n" +
			"  accounting_code TEXT NOT NULL UNIQUE,\n" +
			"  version INTEGER NOT NULL\n" +
			");";

	private static final String MEMBER_SCHEMA = "CREATE TABLE member (\n" +
			"  id INTEGER PRIMARY KEY NOT NULL,\n" +
			"  first_name TEXT NOT NULL,\n" +
			"  last_name TEXT NOT NULL,\n" +
			"  tri_graph TEXT NOT NULL UNIQUE CHECK (length(tri_graph) < 4),\n" +
			"  email TEXT NOT NULL,\n" +
			"  login TEXT NOT NULL UNIQUE,\n" +
			"  start_date INTEGER NOT NULL,\n" +
			"  end_date INTEGER,\n" +
			"  intern BOOLEAN NOT NULL DEFAULT 0 CHECK (intern IN (0, 1)),\n" +
			"  admin BOOLEAN NOT NULL DEFAULT 0 CHECK (admin IN (0, 1)),\n" +
			"  accountant BOOLEAN NOT NULL DEFAULT 0 CHECK (accountant IN (0, 1)),\n" +
			"  version INTEGER NOT NULL,\n" +
			"  CHECK (end_date IS NULL OR start_date <= end_date)\n" +
			");";

	@Test
	public void table_info() throws Exception {
		Map<String, String> schemaByTableName = Collections.singletonMap("contract", CONTRACT_SCHEMA);
		Select select = EnhancedPragma.tableInfo(null, "contract", null, new DummySchemaProvider(schemaByTableName));
		final String tableInfo = select.toSql();
		Parser.parse(tableInfo);
	}

	@Test
	public void foreign_key_list() throws Exception {
		Map<String, String> schemaByTableName = new HashMap<>();
		schemaByTableName.put("client", CLIENT_SCHEMA);
		schemaByTableName.put("member", MEMBER_SCHEMA);
		schemaByTableName.put("contract", CONTRACT_SCHEMA);
		Select select = EnhancedPragma.getImportedKeys(null, "contract", new DummySchemaProvider(schemaByTableName));
		final String importedKeys = select.toSql();
		Parser.parse(importedKeys);
	}

	@Test
	public void no_foreign_key_list() throws Exception {
		Map<String, String> schemaByTableName = Collections.singletonMap("client", CLIENT_SCHEMA);
		Select select = EnhancedPragma.getImportedKeys(null, "client", new DummySchemaProvider(schemaByTableName));
		final String importedKeys = select.toSql();
		Parser.parse(importedKeys);
	}

	@Test
	public void foreign_key_list_with_implicit_reference_to_primary_key() throws Exception {
		Map<String, String> schemaByTableName = new HashMap<>();
		schemaByTableName.put("client", CLIENT_SCHEMA);
		schemaByTableName.put("member", MEMBER_SCHEMA);
		final String sql = "CREATE TABLE contract (\n" +
				"  id INTEGER PRIMARY KEY NOT NULL,\n" +
				"  _number TEXT NOT NULL,\n" +
				"  title TEXT NOT NULL,\n" +
				"  amendment TEXT,\n" +
				"  billing_address TEXT NOT NULL, -- seems useless to decompose it\n" +
				"  member_id INTEGER NOT NULL,\n" +
				"  client_id INTEGER NOT NULL,\n" +
				"  average_daily_rate INTEGER NOT NULL,\n" +
				"  saturday_comp_rate INTEGER, -- TODO (Type, Rate) dedicated table?\n" +
				"  sunday_comp_rate INTEGER,\n" +
				"  holiday_comp_rate INTEGER,\n" +
				"  night_comp_rate INTEGER,\n" +
				"  saturday_on_call_rate INTEGER,\n" +
				"  sunday_on_call_rate INTEGER,\n" +
				"  holiday_on_call_rate INTEGER,\n" +
				"  night_on_call_rate INTEGER,\n" +
				"  start_date INTEGER NOT NULL,\n" +
				"  end_date INTEGER,\n" +
				"  version INTEGER NOT NULL,\n" +
				"  FOREIGN KEY (member_id) REFERENCES member,\n" +
				"  FOREIGN KEY (client_id) REFERENCES client\n" +
				");";
		schemaByTableName.put("contract", sql);
		Select select = EnhancedPragma.getImportedKeys(null, "contract", new DummySchemaProvider(schemaByTableName));
		final String importedKeys = select.toSql();
		Parser.parse(importedKeys);
	}

	private static class DummySchemaProvider implements SchemaProvider {
		private final Map<String, String> schemaByTableName;

		private DummySchemaProvider(Map<String, String> schemaByTableName) {
			this.schemaByTableName = schemaByTableName;
		}
		@Override
		public String getDbName(String dbName, String tableName) {
			if (dbName == null || dbName.isEmpty()) {
				return "main";
			}
			return dbName;
		}
		@Override
		public String getSchema(String dbName, String tableName) {
			return schemaByTableName.get(tableName);
		}

		@Override
		public List<QualifiedName> findTables(String dbName, String tableNamePattern) throws SQLException {
			return Collections.singletonList(new QualifiedName(getDbName(dbName, tableNamePattern), tableNamePattern));
		}
		@Override
		public List<String> getDbNames(String dbName) throws SQLException {
			throw new UnsupportedOperationException();
		}
	}
}