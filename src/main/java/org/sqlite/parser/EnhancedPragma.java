package org.sqlite.parser;

import java.sql.DatabaseMetaData;
import java.sql.SQLSyntaxErrorException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.sqlite.parser.ast.CaseExpr;
import org.sqlite.parser.ast.Cmd;
import org.sqlite.parser.ast.ColumnConstraint;
import org.sqlite.parser.ast.ColumnDefinition;
import org.sqlite.parser.ast.ColumnsAndConstraints;
import org.sqlite.parser.ast.CompoundOperator;
import org.sqlite.parser.ast.CompoundSelect;
import org.sqlite.parser.ast.CreateTable;
import org.sqlite.parser.ast.CreateTableBody;
import org.sqlite.parser.ast.Expr;
import org.sqlite.parser.ast.ForeignKeyColumnConstraint;
import org.sqlite.parser.ast.ForeignKeyTableConstraint;
import org.sqlite.parser.ast.FromClause;
import org.sqlite.parser.ast.IdExpr;
import org.sqlite.parser.ast.IndexedColumn;
import org.sqlite.parser.ast.Limit;
import org.sqlite.parser.ast.LiteralExpr;
import org.sqlite.parser.ast.OneSelect;
import org.sqlite.parser.ast.PrimaryKeyConstraint;
import org.sqlite.parser.ast.ResultColumn;
import org.sqlite.parser.ast.Select;
import org.sqlite.parser.ast.SelectBody;
import org.sqlite.parser.ast.SelectTable;
import org.sqlite.parser.ast.SortedColumn;
import org.sqlite.parser.ast.Stmt;
import org.sqlite.parser.ast.TableConstraint;
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
	 * @return Dynamic select that generates a {@link java.sql.ResultSet} for {@link java.sql.DatabaseMetaData#getColumns}
	 */
	public static Select tableInfo(String catalog, String tableName, SchemaProvider schemaProvider) throws SQLSyntaxErrorException {
		final IdExpr colNullable = new IdExpr("colnullable");
		List<ResultColumn> columns = Arrays.asList(
				expr(new IdExpr("cat"), as("TABLE_CAT")),
				expr(NULL, as("TABLE_SCHEM")),
				expr(new IdExpr("tbl"), as("TABLE_NAME")),
				expr(new IdExpr("cn"), as("COLUMN_NAME")),
				expr(new IdExpr("ct"), as("DATA_TYPE")), // SQL type from java.sql.Types
				expr(new IdExpr("tn"), as("TYPE_NAME")),
				expr(new IdExpr("cs"), as("COLUMN_SIZE")),
				expr(NULL, as("BUFFER_LENGTH")),
				expr(new IdExpr("decimalDigits"), as("DECIMAL_DIGITS")),
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
				expr(new IdExpr("autoinc"), as("IS_AUTOINCREMENT")),
				expr(new IdExpr("generated"), as("IS_GENERATEDCOLUMN"))
		);
		catalog = schemaProvider.getDbName(catalog, tableName);
		final LiteralExpr cat = string(catalog);
		final LiteralExpr tbl = string(tableName);
		OneSelect head = null;
		String sql = schemaProvider.getSchema(catalog, tableName);
		ColumnsAndConstraints columnsAndConstraints = getColumnsAndConstraints(tableName, sql);
		List<List<Expr>> tail = new ArrayList<>(columnsAndConstraints.columns.size() - 1);
		for (int i = 0; i < columnsAndConstraints.columns.size(); i++) {
			ColumnDefinition column = columnsAndConstraints.columns.get(i);
			final String colName = column.nameAndType.colName;
			final LiteralExpr colNameExpr = string(colName);
			final boolean rowIdAlias = columnsAndConstraints.isGeneratedColumn(colName);
			final LiteralExpr colType = integer(rowIdAlias ? Types.ROWID : column.nameAndType.getDataType());
			final LiteralExpr declType = column.nameAndType.getTypeExpr();
			LiteralExpr colSize = column.nameAndType.getSize();
			LiteralExpr decimalDigits = column.nameAndType.getDecimalDigits();
			final Integer nullable = column.getNullable()
					.orElse(columnsAndConstraints.isAnAliasForRowId(colName) ? DatabaseMetaData.columnNoNulls : DatabaseMetaData.columnNullable);
			final LiteralExpr columnDefault = column.getDefault();
			final LiteralExpr ordpos = integer(i + 1);
			final LiteralExpr autoinc = string(columnsAndConstraints.isAutoIncrement(colName) ? "YES" : rowIdAlias ? "" : "NO");
			final LiteralExpr generated = string(rowIdAlias ? "YES" : "NO");
			if (i == 0) {
				head = new OneSelect(null, Arrays.asList(
						expr(cat, as("cat")),
						expr(tbl, as("tbl")),
						expr(colNameExpr, as("cn")),
						expr(colType, as("ct")),
						expr(declType, as("tn")),
						expr(colSize, as("cs")),
						expr(decimalDigits, as("decimalDigits")),
						expr(integer(nullable), as("colnullable")),
						expr(columnDefault, as("cdflt")),
						expr(ordpos, as("ordpos")),
						expr(autoinc, as("autoinc")),
						expr(generated, as("generated"))
				), null, null, null);
			} else {
				tail.add(Arrays.asList(
						cat,
						tbl,
						colNameExpr,
						colType,
						declType,
						colSize,
						decimalDigits,
						integer(nullable),
						columnDefault,
						ordpos,
						autoinc,
						generated
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

	/**
	 * Like {@code PRAGMA catalog.foreign_key_list(tableName)} but enhanced for {@link java.sql.DatabaseMetaData#getCrossReference}
	 *
	 * @param parentCatalog  Parent table catalog
	 * @param parentTable    Name of the parent table where primary/unique key(s) are declared.
	 * @param foreignCatalog Child table catalog
	 * @param foreignTable   Name of the parent table where foreign key(s) are declared.
	 * @param schemaProvider Given one parent table's name (that a foreign key constraint refers to), returns its schema.
	 * @return Dynamic select that generates a {@link java.sql.ResultSet} for {@link java.sql.DatabaseMetaData#getColumns}
	 */
	public static Select getCrossReference(String parentCatalog, String parentTable,
			String foreignCatalog, String foreignTable, SchemaProvider schemaProvider) throws SQLSyntaxErrorException {
		return foreign_key_list(parentCatalog, parentTable, foreignCatalog, foreignTable, schemaProvider, true);
	}

	/**
	 * Like {@code PRAGMA catalog.foreign_key_list(tableName)} but enhanced for {@link java.sql.DatabaseMetaData#getImportedKeys}
	 *
	 * @param catalog        Tables catalog
	 * @param tableName      Name of the table where foreign key(s) are declared.
	 * @param schemaProvider Given one parent table's name (that a foreign key constraint refers to), returns its schema.
	 * @return Dynamic select that generates a {@link java.sql.ResultSet} for {@link java.sql.DatabaseMetaData#getColumns}
	 */
	public static Select getImportedKeys(String catalog, String tableName, SchemaProvider schemaProvider) throws SQLSyntaxErrorException {
		return foreign_key_list(null, null, catalog, tableName, schemaProvider, false);
	}

	private static Select foreign_key_list(String parentCatalog, String parentTable,
			String foreignCatalog, String foreignTable,
			SchemaProvider schemaProvider, boolean cross) throws SQLSyntaxErrorException {
		foreignCatalog = schemaProvider.getDbName(foreignCatalog, foreignTable);
		String sql = schemaProvider.getSchema(foreignCatalog, foreignTable);
		ColumnsAndConstraints columnsAndConstraints = getColumnsAndConstraints(foreignTable, sql);

		final LiteralExpr cat = string(foreignCatalog); // a temporary table can have reference(s) to a main table.
		final LiteralExpr tbl = string(foreignTable);
		List<ResultColumn> columns = Arrays.asList(
				expr(new IdExpr("pcat"), as("PKTABLE_CAT")), // parent catalog
				expr(NULL, as("PKTABLE_SCHEM")),
				expr(new IdExpr("pt"), as("PKTABLE_NAME")), // parent table
				expr(new IdExpr("pc"), as("PKCOLUMN_NAME")), // parent table column
				expr(cat, as("FKTABLE_CAT")),
				expr(NULL, as("FKTABLE_SCHEM")),
				expr(tbl, as("FKTABLE_NAME")),
				expr(new IdExpr("fc"), as("FKCOLUMN_NAME")),
				expr(new IdExpr("seq"), as("KEY_SEQ")),
				expr(new IdExpr("updateRule"), as("UPDATE_RULE")),
				expr(new IdExpr("deleteRule"), as("DELETE_RULE")),
				expr(new IdExpr("fkName"), as("FK_NAME")),
				expr(new IdExpr("pkName"), as("PK_NAME")), // PRIMARY KEY or UNIQUE constraint name
				expr(new IdExpr("deferrability"), as("DEFERRABILITY"))
		);
		OneSelect head = null;
		List<List<Expr>> tail = new ArrayList<>();
		int count = 0;
		for (ColumnDefinition column : columnsAndConstraints.columns) {
			for (ColumnConstraint constraint : column.constraints) {
				if (!(constraint instanceof ForeignKeyColumnConstraint)) {
					continue;
				}
				ForeignKeyColumnConstraint fcc = (ForeignKeyColumnConstraint) constraint;
				final String parentTableName = fcc.clause.tblName;
				final String dbName = schemaProvider.getDbName(foreignCatalog, parentTableName);
				if (cross && !match(parentCatalog, parentTable, dbName, parentTableName)) {
					continue;
				}
				count++;
				LiteralExpr pcat = string(dbName);
				LiteralExpr pt = string(parentTableName);
				String colName = column.nameAndType.colName;
				LiteralExpr fc = string(colName);
				LiteralExpr seq = integer(1);
				LiteralExpr updateRule = fcc.clause.getUpdateRule();
				LiteralExpr deleteRule = fcc.clause.getDeleteRule();
				LiteralExpr fkName = string(fcc.name == null ? generateForeignKeyName(foreignTable, parentTableName, count) : fcc.name);
				PrimaryKeyConstraint primaryKeyConstraint = getPrimaryKeyConstraint(foreignTable, schemaProvider, foreignCatalog, parentTableName, 1);
				LiteralExpr pkName = string(primaryKeyConstraint.getPrimaryKeyName());
				LiteralExpr deferrability = integer(fcc.getDeferrability());
				List<IndexedColumn> pics = fcc.clause.columns;
				LiteralExpr pc;
				if (pics == null || pics.isEmpty()) {
					pc = string(primaryKeyConstraint.getColumnName(0));
				} else {
					assert pics.size() == 1;
					IndexedColumn pic = pics.get(0);
					pc = string(pic.colName);
				}
				head = append(head, tail, pcat, pt, pc, fc, seq, updateRule, deleteRule, fkName, pkName, deferrability);
			}
		}
		for (TableConstraint constraint : columnsAndConstraints.constraints) {
			if (!(constraint instanceof ForeignKeyTableConstraint)) {
				continue;
			}
			ForeignKeyTableConstraint ftc = (ForeignKeyTableConstraint) constraint;
			final String parentTableName = ftc.clause.tblName;
			final String dbName = schemaProvider.getDbName(foreignCatalog, parentTableName);
			if (cross && !match(parentCatalog, parentTable, dbName, parentTableName)) {
				continue;
			}
			count++;
			LiteralExpr updateRule = ftc.clause.getUpdateRule();
			LiteralExpr deleteRule = ftc.clause.getDeleteRule();
			LiteralExpr pcat = string(dbName);
			LiteralExpr pt = string(parentTableName);
			LiteralExpr fkName = string(ftc.name == null ? generateForeignKeyName(foreignTable, parentTableName, count) : ftc.name);
			List<IndexedColumn> fics = ftc.columns;
			PrimaryKeyConstraint primaryKeyConstraint = getPrimaryKeyConstraint(foreignTable, schemaProvider, foreignCatalog, parentTableName, fics.size());
			LiteralExpr pkName = string(primaryKeyConstraint.getPrimaryKeyName());
			LiteralExpr deferrability = integer(ftc.getDeferrability());
			List<IndexedColumn> pics = ftc.clause.columns;
			for (int i = 0; i < fics.size(); i++) {
				IndexedColumn fic = fics.get(i);
				LiteralExpr fc = string(fic.colName);
				LiteralExpr seq = integer(i + 1);
				LiteralExpr pc;
				if (pics == null || pics.isEmpty()) {
					// implicit primary key
					pc = string(primaryKeyConstraint.getColumnName(i));
				} else {
					IndexedColumn pic = pics.get(i);
					pc = string(pic.colName);
				}
				head = append(head, tail, pcat, pt, pc, fc, seq, updateRule, deleteRule, fkName, pkName, deferrability);
			}
		}
		SelectBody subBody;
		Limit limit = null;
		// Handle the case where there is no FK
		if (head == null) {
			head = append(head, tail, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
			limit = new Limit(integer(0), null);
		}
		List<CompoundSelect> compounds;
		if (tail.isEmpty()) {
			compounds = null;
		} else {
			compounds = Collections.singletonList(
					new CompoundSelect(CompoundOperator.UnionAll, new OneSelect(tail)));
		}
		subBody = new SelectBody(head, compounds);
		Select subSelect = new Select(null, subBody, null, limit);
		FromClause from = new FromClause(SelectTable.select(subSelect, null), null);
		from.setComplete();
		OneSelect oneSelect = new OneSelect(null, columns, from, null, null);
		SelectBody body = new SelectBody(oneSelect, null);
		List<SortedColumn> orderBy;
		if (cross) {
			orderBy = Arrays.asList(
					new SortedColumn(new IdExpr("FKTABLE_CAT"), null),
					new SortedColumn(new IdExpr("FKTABLE_SCHEM"), null),
					new SortedColumn(new IdExpr("FKTABLE_NAME"), null),
					new SortedColumn(new IdExpr("KEY_SEQ"), null)
			);
		} else {
			orderBy = Arrays.asList(
					new SortedColumn(new IdExpr("PKTABLE_CAT"), null),
					new SortedColumn(new IdExpr("PKTABLE_SCHEM"), null),
					new SortedColumn(new IdExpr("PKTABLE_NAME"), null),
					new SortedColumn(new IdExpr("KEY_SEQ"), null)
			);
		}
		Select select = new Select(null, body, orderBy, null);
		return select;
	}

	private static boolean match(String parentCatalog, String parentTable, String dbName, String parentTableName) {
		return parentTableName.equalsIgnoreCase(parentTable) &&
				(parentCatalog == null || parentCatalog.equalsIgnoreCase(dbName));
	}

	private static String generateForeignKeyName(String tableName, String parentTableName, int count) {
		return tableName + '_' + parentTableName + '_' + count;
	}

	private static PrimaryKeyConstraint getPrimaryKeyConstraint(String tableName, SchemaProvider schemaProvider,
			String dbName, String parentTableName, int expectedNumberOfColumns) throws SQLSyntaxErrorException {
		PrimaryKeyConstraint primaryKeyConstraint;
		final ColumnsAndConstraints parentBody = getColumnsAndConstraints(parentTableName, schemaProvider.getSchema(dbName, parentTableName));
		if (parentBody.primaryKeyConstraint == null) {
			throw new SQLSyntaxErrorException(String.format("No PRIMARY KEY declared in %s referenced by %s", parentTableName, tableName));
		}
		primaryKeyConstraint = parentBody.primaryKeyConstraint;
		if (primaryKeyConstraint.getNumberOfColumns() != expectedNumberOfColumns) {
			throw new SQLSyntaxErrorException(String.format("number of columns in foreign key of %s does not match the number of columns in the referenced table %s", parentTableName, tableName));
		}
		return primaryKeyConstraint;
	}

	private static OneSelect append(OneSelect head, List<List<Expr>> tail, LiteralExpr pcat, LiteralExpr pt, LiteralExpr pc,
			LiteralExpr fc, LiteralExpr seq, LiteralExpr updateRule, LiteralExpr deleteRule,
			LiteralExpr fkName, LiteralExpr pkName, LiteralExpr deferrability) {
		if (head == null) {
			head = new OneSelect(null, Arrays.asList(
					expr(pcat, as("pcat")),
					expr(pt, as("pt")),
					expr(pc, as("pc")),
					expr(fc, as("fc")),
					expr(seq, as("seq")),
					expr(updateRule, as("updateRule")),
					expr(deleteRule, as("deleteRule")),
					expr(fkName, as("fkName")),
					expr(pkName, as("pkName")),
					expr(deferrability, as("deferrability"))
			), null, null, null);
		} else {
			tail.add(Arrays.asList(
					pcat,
					pt,
					pc,
					fc,
					seq,
					updateRule,
					deleteRule,
					fkName,
					pkName,
					deferrability
			));
		}
		return head;
	}

	private static ColumnsAndConstraints getColumnsAndConstraints(String tableName, String sql) throws SQLSyntaxErrorException {
		Cmd cmd = Parser.parse(sql);
		assert cmd != null;
		Stmt stmt = cmd.stmt;
		assert stmt instanceof CreateTable;
		CreateTable createTable = (CreateTable) stmt;
		assert createTable.tblName.name.equalsIgnoreCase(tableName);
		final CreateTableBody createTableBody = createTable.body;
		assert createTableBody instanceof ColumnsAndConstraints;
		return (ColumnsAndConstraints) createTableBody;
	}
}
