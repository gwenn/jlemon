package org.sqlite.parser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.sqlite.parser.ast.BinaryExpr;
import org.sqlite.parser.ast.CompoundOperator;
import org.sqlite.parser.ast.CompoundSelect;
import org.sqlite.parser.ast.Expr;
import org.sqlite.parser.ast.FromClause;
import org.sqlite.parser.ast.IdExpr;
import org.sqlite.parser.ast.InListExpr;
import org.sqlite.parser.ast.LiteralExpr;
import org.sqlite.parser.ast.OneSelect;
import org.sqlite.parser.ast.Operator;
import org.sqlite.parser.ast.QualifiedName;
import org.sqlite.parser.ast.ResultColumn;
import org.sqlite.parser.ast.Select;
import org.sqlite.parser.ast.SelectBody;
import org.sqlite.parser.ast.VariableExpr;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.Objects.requireNonNull;
import static org.sqlite.parser.ast.FunctionCallExpr.lower;
import static org.sqlite.parser.ast.LikeExpr.like;
import static org.sqlite.parser.ast.LiteralExpr.string;
import static org.sqlite.parser.ast.ResultColumn.expr;

public class DefaultSchemaProvider implements SchemaProvider {
	private final Connection conn;

	public DefaultSchemaProvider(Connection conn) {
		this.conn = requireNonNull(conn);
	}

	@Override
	public List<QualifiedName> findTables(String dbName, String tableNamePattern) throws SQLException {
		return findTables(dbName, tableNamePattern, true);
	}
	public List<QualifiedName> findTables(String dbName, String tableNamePattern, boolean all) throws SQLException {
		if ("sqlite_temp_master".equalsIgnoreCase(tableNamePattern)) {
			return singletonList(new QualifiedName("temp", tableNamePattern));
		} else if ("sqlite_master".equalsIgnoreCase(tableNamePattern)) {
			if (dbName == null || dbName.isEmpty()) {
				return singletonList(new QualifiedName("main", tableNamePattern));
			} else {
				return singletonList(new QualifiedName(dbName, tableNamePattern));
			}
		}
		List<String> dbNames = getDbNames(dbName);
		tableNamePattern = tableNamePattern == null ? "%" : tableNamePattern;
		List<QualifiedName> tbls;
		if (all) {
			tbls = new ArrayList<>();
		} else {
			tbls = Collections.emptyList();
		}
		for (String catalog : dbNames) {
			Select select = sqlite_master(catalog, "name", all);
			try (PreparedStatement ps = conn.prepareStatement(select.toSql())) {
				// determine exact table name
				ps.setString(1, tableNamePattern);
				try (ResultSet rs = ps.executeQuery()) {
					while (rs.next()) {
						final QualifiedName qualifiedName = new QualifiedName(catalog, rs.getString(1));
						if (all) {
							tbls.add(qualifiedName);
						} else {
							return singletonList(qualifiedName);
						}
					}
				}
			}
		}
		return tbls;
	}

	@Override
	public String getDbName(String dbName, String tableName) throws SQLException {
		if (tableName == null) {
			return "temp"; // to avoid invalid qualified table name "".tbl
		}
		final List<QualifiedName> tableNames = findTables(dbName, tableName, false);
		if (tableNames.isEmpty()) {
			return "temp"; // to avoid invalid qualified table name "".tbl
		}
		return tableNames.get(0).dbName;
	}

	@Override
	public List<String> getDbNames(String dbName) throws SQLException {
		final List<String> dbNames;
		if (dbName == null) {
			dbNames = new ArrayList<>(2);
			try (PreparedStatement database_list = conn.prepareStatement("PRAGMA database_list");
					 ResultSet rs = database_list.executeQuery()) {
				// 1:seq|2:name|3:file
				while (rs.next()) {
					final String name = rs.getString(2);
					if ("temp".equalsIgnoreCase(name)) {
						dbNames.add(0, name); // "temp" first
					} else {
						dbNames.add(name);
					}
				}
			}
		} else if (dbName.isEmpty()) {
			dbNames = asList("temp", "main"); // "temp" first
		} else {
			dbNames = singletonList(dbName);
		}
		return dbNames;
	}

	@Override
	public String getSchema(String dbName, String tableName) throws SQLException {
		if ("sqlite_temp_master".equalsIgnoreCase(tableName)) {
			return "CREATE TEMP TABLE sqlite_temp_master (\n" +
					"  type text,\n" +
					"  name text,\n" +
					"  tbl_name text,\n" +
					"  rootpage integer,\n" +
					"  sql text\n" +
					")";
		} else if ("sqlite_master".equalsIgnoreCase(tableName)) {
			return "CREATE TABLE sqlite_master (\n" +
					"  type text,\n" +
					"  name text,\n" +
					"  tbl_name text,\n" +
					"  rootpage integer,\n" +
					"  sql text\n" +
					")";
		}
		Select select = sqlite_master(dbName, "sql", false);
		try (PreparedStatement ps = conn.prepareStatement(select.toSql())) {
			ps.setString(1, tableName);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return rs.getString(1);
				}
			}
		}
		throw new SQLException("No such table: " + dbName + "." + tableName);
	}

	private static Select sqlite_master(String dbName, String column, boolean like) {
		String sqlite_master;
		QualifiedName qualifiedName;
		if ("temp".equalsIgnoreCase(dbName)) {
			sqlite_master = "sqlite_temp_master";
			qualifiedName = new QualifiedName(null, sqlite_master);
		} else {
			sqlite_master = "sqlite_master";
			qualifiedName = new QualifiedName(dbName, sqlite_master);
		}
		List<ResultColumn> columns = singletonList(expr(new IdExpr(column), null));
		FromClause from = FromClause.from(qualifiedName);
		Expr typeEpr = new InListExpr(new IdExpr("type"), false, asList(string("table"), string("view")));
		Expr nameExpr;
		if (like) {
			nameExpr = like(new IdExpr("name"), new VariableExpr("1"));
		} else {
			nameExpr = new BinaryExpr(lower(new IdExpr("name")), Operator.Equals, lower(new VariableExpr("1")));
		}
		Expr whereClause = new BinaryExpr(typeEpr, Operator.And, nameExpr);
		// SELECT <column> FROM sqlite_master WHERE type IN ('table','view') AND name LIKE ?1
		final OneSelect oneSelect = new OneSelect(null, columns, from, whereClause, null, null);
		if (!"name".equals(column)) {
			return Select.from(oneSelect);
		}
		final LiteralExpr masterExpr = string(sqlite_master);
		Expr masterClause;
		if (like) {
			masterClause = like(masterExpr, new VariableExpr("1"));
		} else {
			masterClause = new BinaryExpr(masterExpr, Operator.Equals, lower(new VariableExpr("1")));
		}
		// UNION SELECT 'sqlite_master' WHERE 'sqlite_master' LIKE ?1
		CompoundSelect union = new CompoundSelect(CompoundOperator.Union,
				new OneSelect(null, singletonList(expr(masterExpr, null)), null, masterClause, null, null));
		return Select.from(new SelectBody(oneSelect, singletonList(union)));
	}
}
