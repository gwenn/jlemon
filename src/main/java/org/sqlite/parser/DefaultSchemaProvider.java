package org.sqlite.parser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.sqlite.parser.ast.BinaryExpr;
import org.sqlite.parser.ast.Expr;
import org.sqlite.parser.ast.FromClause;
import org.sqlite.parser.ast.IdExpr;
import org.sqlite.parser.ast.LikeExpr;
import org.sqlite.parser.ast.LiteralExpr;
import org.sqlite.parser.ast.NotLike;
import org.sqlite.parser.ast.OneSelect;
import org.sqlite.parser.ast.Operator;
import org.sqlite.parser.ast.QualifiedName;
import org.sqlite.parser.ast.ResultColumn;
import org.sqlite.parser.ast.Select;
import org.sqlite.parser.ast.VariableExpr;

import static java.util.Collections.singletonList;
import static java.util.Objects.requireNonNull;
import static org.sqlite.parser.ast.ResultColumn.expr;

public class DefaultSchemaProvider implements SchemaProvider {
	private final Connection conn;

	public DefaultSchemaProvider(Connection conn) {
		this.conn = requireNonNull(conn);
	}

	@Override
	public String getDbName(String dbName, String tableName) throws SQLException {
		if ("sqlite_temp_master".equalsIgnoreCase(tableName)) {
			return "temp";
		} else if ("sqlite_master".equalsIgnoreCase(tableName)) {
			if (dbName == null || dbName.isEmpty()) {
				return "main";
			} else {
				return dbName;
			}
		}

		final List<String> dbNames = database_list(dbName);
		if (dbNames.size() == 1) {
			return dbNames.get(0);
		}
		for (String cat : dbNames) {
			Select select = sqlite_master(dbName, "name");
			try (PreparedStatement ps = conn.prepareStatement(select.toSql())) {
				ps.setString(1, tableName);
				try (ResultSet rs = ps.executeQuery()) {
					if (rs.next()) {
						return cat;
					}
				}
			}
		}
		return "temp"; // to avoid invalid qualified table name "".tbl
	}

	private List<String> database_list(String dbName) throws SQLException {
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
			dbNames = Arrays.asList("temp", "main"); // "temp" first
		} else {
			dbNames = Collections.singletonList(dbName);
		}
		return dbNames;
	}

	@Override
	public String getSchema(String dbName, String tableName) throws SQLException {
		Select select = sqlite_master(dbName, "sql");
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

	private Select sqlite_master(String dbName, String column) {
		QualifiedName qualifiedName;
		if ("temp".equalsIgnoreCase(dbName)) {
			qualifiedName = new QualifiedName(null, "sqlite_temp_master");
		} else {
			qualifiedName = new QualifiedName(dbName, "sqlite_master");
		}
		List<ResultColumn> columns = singletonList(expr(new IdExpr(column), null));
		FromClause from = FromClause.from(qualifiedName);
		Expr typeEpr = new BinaryExpr(new IdExpr("type"), Operator.Equals, LiteralExpr.string("table")); // TODO Validate: no view
		Expr nameExpr = LikeExpr.like(new IdExpr("name"), new VariableExpr(""));
		Expr whereClause = new BinaryExpr(typeEpr, Operator.And, nameExpr);
		return Select.from(new OneSelect(null, columns, from, whereClause, null));
	}
}
