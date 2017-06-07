package org.sqlite.parser.ast;

import java.io.IOException;

public enum InitDeferredPred implements ToSql {
	InitiallyDeferred,
	InitiallyImmediate; // default

	@Override
	public void toSql(Appendable a) throws IOException {
		if (InitiallyDeferred == this) {
			a.append("INITIALLY DEFERRED");
		} else if (InitiallyImmediate == this) {
			a.append("INITIALLY IMMEDIATE");
		}
	}
}