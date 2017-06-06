package org.sqlite.parser.ast;

import java.io.IOException;

public class Rollback implements Stmt {
	public final String txName;
	public final String savepointName;

	public Rollback(String txName,
			String savepointName) {
		this.txName = txName;
		this.savepointName = savepointName;
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		a.append("ROLLBACK");
		if (txName != null) {
			a.append(" TRANSACTION ");
			doubleQuote(a, txName);
		}
		if (savepointName != null) {
			a.append(" TO ");
			doubleQuote(a, savepointName);
		}
	}
}