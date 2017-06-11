package org.sqlite.parser.ast;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

public class OnInsertRefArg implements RefArg {
	public final RefAct refAct;

	public OnInsertRefArg(RefAct refAct) {
		this.refAct = requireNonNull(refAct);
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		a.append("ON INSERT ");
		refAct.toSql(a);
	}
}
