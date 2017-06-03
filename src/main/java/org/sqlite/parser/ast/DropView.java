package org.sqlite.parser.ast;

import java.io.IOException;

public class DropView extends Drop {

	public DropView(boolean ifExists,
			QualifiedName name) {
		super(ifExists, name);
	}

	@Override
	void appendKind(Appendable a) throws IOException {
		a.append("VIEW");
	}
}