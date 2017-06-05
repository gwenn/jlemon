package org.sqlite.parser.ast;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

public class OnUpdateRefArg implements RefArg {
	public final RefAct refAct;

	public OnUpdateRefArg(RefAct refAct) {
		this.refAct = requireNonNull(refAct);
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		a.append("ON UPDATE ");
		refAct.toSql(a);
	}
}
