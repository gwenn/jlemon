package org.sqlite.parser.ast;

import java.io.IOException;

public class ReIndex implements Stmt {
	public final QualifiedName tblName;

	public ReIndex(QualifiedName tblName) {
		this.tblName = tblName;
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		a.append("REINDEX");
		if (tblName != null) {
			a.append(' ');
			tblName.toSql(a);
		}
	}
}