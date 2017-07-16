package org.sqlite.parser;

import java.sql.DatabaseMetaData;
import java.sql.SQLSyntaxErrorException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.sqlite.parser.ast.CaseExpr;
import org.sqlite.parser.ast.Cmd;
import org.sqlite.parser.ast.ColumnDefinition;
import org.sqlite.parser.ast.ColumnsAndConstraints;
import org.sqlite.parser.ast.CompoundOperator;
import org.sqlite.parser.ast.CompoundSelect;
import org.sqlite.parser.ast.CreateTable;
import org.sqlite.parser.ast.CreateTableBody;
import org.sqlite.parser.ast.Expr;
import org.sqlite.parser.ast.FromClause;
import org.sqlite.parser.ast.IdExpr;
import org.sqlite.parser.ast.LiteralExpr;
import org.sqlite.parser.ast.OneSelect;
import org.sqlite.parser.ast.ResultColumn;
import org.sqlite.parser.ast.Select;
import org.sqlite.parser.ast.SelectBody;
import org.sqlite.parser.ast.SelectTable;
import org.sqlite.parser.ast.SortedColumn;
import org.sqlite.parser.ast.Stmt;
import org.sqlite.parser.ast.WhenThenPair;

import static org.sqlite.parser.ast.As.as;
import static org.sqlite.parser.ast.LiteralExpr.EMPTY_STRING;
import static org.sqlite.parser.ast.LiteralExpr.NULL;
import static org.sqlite.parser.ast.LiteralExpr.integer;
import static org.sqlite.parser.ast.LiteralExpr.string;
import static org.sqlite.parser.ast.ResultColumn.expr;

public class EnhancedPragma {
	/**
	 * Like {@code PRAGMA catalog.table_info(tableName)} but enhanced for {@link java.sql.DatabaseMetaData#getColumns}
	 *
	 * @param catalog   Table catalog
	 * @param tableName Table name
	 * @param sql       Schema for {@code tableName}
	 * @return Dynamic select that generates a {@link java.sql.ResultSet} expected by {@link java.sql.DatabaseMetaData#getColumns}
	 */
	public static Select tableInfo(String catalog, String tableName, String sql) throws SQLSyntaxErrorException {
		Cmd cmd = Parser.parse(sql);
		assert cmd != null;
		Stmt stmt = cmd.stmt;
		assert stmt instanceof CreateTable;
		CreateTable createTable = (CreateTable) stmt;
		assert createTable.tblName.name.equalsIgnoreCase(tableName);
		final CreateTableBody createTableBody = createTable.body;
		assert createTableBody instanceof ColumnsAndConstraints;
		ColumnsAndConstraints columnsAndConstraints = (ColumnsAndConstraints) createTableBody;

		final LiteralExpr cat;
		if (catalog == null) {
			cat = NULL;
		} else {
			cat = string(catalog);
		}
		final LiteralExpr tbl = string(tableName);
		final IdExpr colNullable = new IdExpr("colnullable");
		List<ResultColumn> columns = Arrays.asList(
				expr(cat, as("TABLE_CAT")),
				expr(NULL, as("TABLE_SCHEM")),
				expr(tbl, as("TABLE_NAME")),
				expr(new IdExpr("cn"), as("COLUMN_NAME")),
				expr(new IdExpr("ct"), as("DATA_TYPE")), // SQL type from java.sql.Types
				expr(new IdExpr("tn"), as("TYPE_NAME")),
				expr(integer(10), as("COLUMN_SIZE")), // FIXME precision or display size
				expr(NULL, as("BUFFER_LENGTH")),
				expr(integer(10), as("DECIMAL_DIGITS")), // FIXME scale or null: the number of fractional digits. Null is returned for data types where DECIMAL_DIGITS is not applicable.
				expr(integer(10), as("NUM_PREC_RADIX")), // Radix (typically either 10 or 2)
				expr(colNullable, as("NULLABLE")),
				expr(NULL, as("REMARKS")),
				expr(new IdExpr("cdflt"), as("COLUMN_DEF")),
				expr(NULL, as("SQL_DATA_TYPE")),
				expr(NULL, as("SQL_DATETIME_SUB")),
				expr(integer(10), as("CHAR_OCTET_LENGTH")), // FIXME same as COLUMN_SIZE
				expr(new IdExpr("ordpos"), as("ORDINAL_POSITION")),
				expr(new CaseExpr(colNullable,
						Arrays.asList(
								new WhenThenPair(integer(0), string("NO")),
								new WhenThenPair(integer(1), string("YES"))
						),
						EMPTY_STRING), as("IS_NULLABLE")),
				expr(NULL, as("SCOPE_CATLOG")),
				expr(NULL, as("SCOPE_SCHEMA")),
				expr(NULL, as("SCOPE_TABLE")),
				expr(NULL, as("SOURCE_DATA_TYPE")),
				expr(EMPTY_STRING, as("IS_AUTOINCREMENT")),  // TODO http://sqlite.org/autoinc.html
				expr(EMPTY_STRING, as("IS_GENERATEDCOLUMN")) // TODO rowid or alias
		);
		OneSelect head = null;
		List<List<Expr>> tail = new ArrayList<>(columnsAndConstraints.columns.size() - 1);
		for (int i = 0; i < columnsAndConstraints.columns.size(); i++) {
			ColumnDefinition column = columnsAndConstraints.columns.get(i);
			final String colName = column.nameAndType.colName;
			final LiteralExpr colNameExpr = string(colName);
			final LiteralExpr colType = integer(column.nameAndType.getDataType());
			final LiteralExpr declType = column.nameAndType.getTypeExpr();
			final Integer nullable = column.getNullable()
					.orElse(columnsAndConstraints.isAnAliasForRowId(colName) ? DatabaseMetaData.columnNoNulls : DatabaseMetaData.columnNullable);
			final LiteralExpr columnDefault = column.getDefault();
			final LiteralExpr ordpos = integer(i + 1);
			if (i == 0) {
				head = new OneSelect(null, Arrays.asList(
						expr(colNameExpr, as("cn")),
						expr(colType, as("ct")),
						expr(declType, as("tn")),
						expr(integer(nullable), as("colnullable")),
						expr(columnDefault, as("cdflt")),
						expr(ordpos, as("ordpos"))
				), null, null, null);
			} else {
				tail.add(Arrays.asList(
						colNameExpr,
						colType,
						declType,
						integer(nullable),
						columnDefault,
						ordpos
				));
			}
		}
		final List<CompoundSelect> compounds = Collections.singletonList(
				new CompoundSelect(CompoundOperator.UnionAll, new OneSelect(tail)));
		SelectBody subBody = new SelectBody(head, compounds);
		Select subSelect = new Select(null, subBody, null, null);
		FromClause from = new FromClause(SelectTable.select(subSelect, null), null);
		from.setComplete();
		OneSelect oneSelect = new OneSelect(null, columns, from, null, null);
		SelectBody body = new SelectBody(oneSelect, null);
		List<SortedColumn> orderBy = Arrays.asList(
				new SortedColumn(new IdExpr("TABLE_SCHEM"), null),
				new SortedColumn(new IdExpr("TABLE_NAME"), null),
				new SortedColumn(new IdExpr("ORDINAL_POSITION"), null)
		);
		Select select = new Select(null, body, orderBy, null);
		return select;
	}
}
