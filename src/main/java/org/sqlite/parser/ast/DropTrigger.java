package org.sqlite.parser.ast;

import java.io.IOException;

public class DropTrigger extends Drop {

	public DropTrigger(boolean ifExists,
			QualifiedName name) {
		super(ifExists, name);
	}

	@Override
	void appendKind(Appendable a) throws IOException {
		a.append("TRIGGER");
	}
}