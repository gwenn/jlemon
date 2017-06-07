package org.sqlite.parser;

import java.util.List;
import org.sqlite.parser.ast.*;

public class Parser {
	ExplainKind explain;
	private Stmt stmt;
	Token constraintName;

	void sqlite3BeginTransaction(TransactionType type) {
		stmt = new Begin(type, null); // TODO trans_opt
	}
	void sqlite3CommitTransaction() {
		stmt = new Commit(null); // TODO trans_opt
	}
	void sqlite3RollbackTransaction() {
		stmt = new Rollback(null, null); // TODO trans_opt
	}

	void sqlite3Savepoint(Token nm) {
		stmt = new Savepoint(nm.text());
	}
	void sqlite3ReleaseSavepoint(Token nm) {
		stmt = new Release(nm.text());
	}
	void sqlite3RollbackSavepoint(Token nm) {
		stmt = new Rollback(null, nm.text()); // TODO trans_opt
	}

	void sqlite3AddDefaultValue(Expr expr) {
		new DefaultColumnConstraint(null, expr); // TODO constraintName
	}
	void sqlite3AddNotNull(boolean nullable, ResolveType onconf) {
		new NotNullColumnConstraint(null, nullable, onconf);
	}
	void sqlite3AddPrimaryKey(SortOrder sortorder, ResolveType onconf, boolean autoinc) {
		new PrimaryKeyColumnConstraint(null,sortorder,onconf,autoinc);
	}
	void sqlite3AddUnique(ResolveType onconf) {
		new UniqueColumnConstraint(null,onconf);
	}
	void sqlite3AddCheckConstraint(Expr expr) {
		new CheckColumnConstraint(null, expr);
	}
	void sqlite3CreateForeignKey(Token nm, List<IndexedColumn> columns, List<RefArg> refargs) {
		new ForeignKeyColumnConstraint(null, new ForeignKeyClause(nm.text(),columns,refargs), null);
	}
	void sqlite3AddCollateType(String ids) {
		new CollateColumnConstraint(null, ids);
	}

	void sqlite3DropTable(boolean ifExists, QualifiedName fullname) {
		stmt = new DropTable(ifExists, fullname);
	}

	void sqlite3CreateView(boolean temporary,
		boolean ifNotExists,
		Token nm,
		String dbnm,
		List<IndexedColumn> columns,
		Select select) {
		QualifiedName viewName = QualifiedName.from(nm.text(), dbnm);
		stmt = new CreateView(temporary, ifNotExists, viewName, columns, select);
	}
	void sqlite3DropView(boolean ifExists, QualifiedName fullname) {
		stmt = new DropView(ifExists, fullname);
	}
}
