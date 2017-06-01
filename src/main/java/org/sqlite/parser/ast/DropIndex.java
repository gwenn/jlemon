package org.sqlite.parser.ast;

import java.io.IOException;
import java.util.List;

import static java.util.Objects.requireNonNull;

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