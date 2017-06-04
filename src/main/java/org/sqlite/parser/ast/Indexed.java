package org.sqlite.parser.ast;

import java.io.IOException;

// Sum Type: Indexed by vs Not Indexed
public class Indexed implements ToSql {
	public final String idxName;

	public Indexed(String idxName) {
		this.idxName = idxName;
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		if (idxName == null) {
			a.append("NOT INDEXED");
		} else {
			a.append("INDEXED BY ");
			doubleQuote(a, idxName);
		}
	}
}
