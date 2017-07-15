package org.sqlite.parser.ast;

import java.sql.DatabaseMetaData;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.sqlite.parser.ast.As.as;
import static org.sqlite.parser.ast.LiteralExpr.EMPTY_STRING;
import static org.sqlite.parser.ast.LiteralExpr.NULL;
import static org.sqlite.parser.ast.LiteralExpr.integer;
import static org.sqlite.parser.ast.LiteralExpr.string;
import static org.sqlite.parser.ast.ResultColumn.expr;

public class CreateTableTest {
	public void getColumns(String catalog, String tableName, String sql) {
		final Expr cat;
		if (catalog == null) {
			cat = NULL;
		} else {
			cat = string(catalog);
		}
		final Expr tbl = string(tableName);
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
		/*
		If a table has a single column primary key and the declared type of that column is "INTEGER" and the table is not a WITHOUT ROWID table,
		then the column is known as an INTEGER PRIMARY KEY.

		Unless the column is an INTEGER PRIMARY KEY or the table is a WITHOUT ROWID table or the column is declared NOT NULL, SQLite allows NULL values in a PRIMARY KEY column.

		With one exception noted below, if a rowid table has a primary key that consists of a single column and the declared type of that column is "INTEGER" in any mixture of upper and lower case,
		then the column becomes an alias for the rowid.
		Such a column is usually referred to as an "integer primary key".
		A PRIMARY KEY column only becomes an integer primary key if the declared type name is exactly "INTEGER".
		Other integer type names like "INT" or "BIGINT" or "SHORT INTEGER" or "UNSIGNED INTEGER" causes the primary key column to behave as an ordinary table column with integer affinity and a unique index, not as an alias for the rowid.

		If the declaration of a column with declared type "INTEGER" includes an "PRIMARY KEY DESC" clause,
		it does not become an alias for the rowid and is not classified as an integer primary key.
		 */
		OneSelect head = null/*new OneSelect(null, Arrays.asList(
				expr(cn, as("cn")),
				expr(ct, as("ct")),
				expr(tn, as("tn")),
				expr(colnullable, as("colnullable")),
				expr(cdflt, as("cdflt")),
				expr(ordpos, as("ordpos"))
		), null, null, null)*/;
		List<OneSelect> tail = Arrays.asList(

		);
		final List<CompoundSelect> compounds = tail.stream()
				.map(os -> new CompoundSelect(CompoundOperator.UnionAll, os))
				.collect(Collectors.toList());
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
	}

	//@Test
	public void getImportedKeys() {
		List<ResultColumn> columns = Arrays.asList(
				expr(new IdExpr("foreignCatalog"), as("PKTABLE_CAT")),
				expr(NULL, as("PKTABLE_SCHEM")),
				expr(new IdExpr("pt"), as("PKTABLE_NAME")),
				expr(new IdExpr("pc"), as("PKCOLUMN_NAME")),
				expr(new IdExpr("foreignCatalog"), as("FKTABLE_CAT")),
				expr(NULL, as("FKTABLE_SCHEM")),
				expr(new IdExpr("foreignTable"), as("FKTABLE_NAME")),
				expr(new IdExpr("fc"), as("FKCOLUMN_NAME")),
				expr(new IdExpr("seq"), as("KEY_SEQ")),
				expr(integer(DatabaseMetaData.importedKeyNoAction), as("UPDATE_RULE")), // FIXME on_update (6) SET NULL (importedKeySetNull), SET DEFAULT (importedKeySetDefault), CASCADE (importedKeyCascade), RESTRICT (importedKeyRestrict), NO ACTION (importedKeyNoAction)
				expr(integer(DatabaseMetaData.importedKeyNoAction), as("DELETE_RULE")), // FIXME on_delete (7)
				expr(new IdExpr("id"), as("FK_NAME")),
				expr(NULL, as("PK_NAME")), // FIXME
				expr(integer(DatabaseMetaData.importedKeyNotDeferrable), as("DEFERRABILITY")) // FIXME
		);
		OneSelect head = null;
		List<OneSelect> tail = Arrays.asList(

		);
		final List<CompoundSelect> compounds = tail.stream()
				.map(os -> new CompoundSelect(CompoundOperator.UnionAll, os))
				.collect(Collectors.toList());
		SelectBody subBody = new SelectBody(head, compounds);
		Select subSelect = new Select(null, subBody, null, null);
		FromClause from = new FromClause(SelectTable.select(subSelect, null), null);
		from.setComplete();
		OneSelect oneSelect = new OneSelect(null, columns, from, null, null);
		SelectBody body = new SelectBody(oneSelect, null);
		List<SortedColumn> orderBy = Arrays.asList(
				new SortedColumn(new IdExpr("PKTABLE_CAT"), null),
				new SortedColumn(new IdExpr("PKTABLE_SCHEM"), null),
				new SortedColumn(new IdExpr("PKTABLE_NAME"), null),
				new SortedColumn(new IdExpr("KEY_SEQ"), null)
		);
		Select select = new Select(null, body, orderBy, null);
	}
}
