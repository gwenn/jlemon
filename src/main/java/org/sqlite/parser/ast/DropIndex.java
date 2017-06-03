package org.sqlite.parser.ast;

import java.io.IOException;

public class DropIndex extends Drop {

	public DropIndex(boolean ifExists,
			QualifiedName name) {
		super(ifExists, name);
	}

	@Override
	void appendKind(Appendable a) throws IOException {
		a.append("INDEX");
	}
}