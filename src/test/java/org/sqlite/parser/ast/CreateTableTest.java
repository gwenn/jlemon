package org.sqlite.parser.ast;

import java.sql.DatabaseMetaData;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.sqlite.parser.ast.As.as;
import static org.sqlite.parser.ast.LiteralExpr.NULL;
import static org.sqlite.parser.ast.LiteralExpr.integer;
import static org.sqlite.parser.ast.ResultColumn.expr;

public class CreateTableTest {
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
