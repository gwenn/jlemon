package org.sqlite.parser;

import java.util.List;
import org.sqlite.parser.ast.*;

public class Parser {
	ExplainKind explain;
	private Stmt stmt;
	Token constraintName;

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

	void sqlite3ErrorMsg(String message, Object... args) {
		throw new ParseException(message, args);
	}
}
