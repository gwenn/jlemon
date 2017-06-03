package org.sqlite.parser.ast;

import java.io.IOException;

public class DropTable extends Drop {

	public DropTable(boolean ifExists,
			QualifiedName name) {
		super(ifExists, name);
	}

	@Override
	void appendKind(Appendable a) throws IOException {
		a.append("TABLE");
	}
}