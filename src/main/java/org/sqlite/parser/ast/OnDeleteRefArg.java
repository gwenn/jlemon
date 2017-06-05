package org.sqlite.parser.ast;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

public class OnDeleteRefArg implements RefArg {
	public final RefAct refAct;

	public OnDeleteRefArg(RefAct refAct) {
		this.refAct = requireNonNull(refAct);
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		a.append("ON DELETE ");
		refAct.toSql(a);
	}
}
