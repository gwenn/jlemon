package org.sqlite.parser.ast;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

public class DoublyQualifiedExpr implements Expr {
	public final String dbName;
	public final String tblName;
	public final String colName;

	public DoublyQualifiedExpr(String dbName, String tblName, String colName) {
		this.dbName = requireNonNull(dbName);
		this.tblName = requireNonNull(tblName);
		this.colName = requireNonNull(colName);
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		doubleQuote(a, dbName);
		a.append('.');
		doubleQuote(a, tblName);
		a.append('.');
		doubleQuote(a, colName);
	}
}
