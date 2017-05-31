package org.sqlite.parser.ast;

import java.io.IOException;

public class Analyze implements Stmt {
	public final QualifiedName objName;

	public Analyze(QualifiedName objName) {
		this.objName = objName;
	}

	public void toSql(Appendable a) throws IOException {
		a.append("ANALYZE");
		if (objName != null) {
			a.append(' ');
			objName.toSql(a);
		}
	}
}