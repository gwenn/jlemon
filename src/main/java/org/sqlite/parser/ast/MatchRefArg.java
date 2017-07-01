package org.sqlite.parser.ast;

import java.io.IOException;

import static java.util.Objects.requireNonNull;
import static org.sqlite.parser.ast.ToSql.doubleQuote;

public class MatchRefArg implements RefArg {
	public final String name;

	public MatchRefArg(String name) {
		this.name = requireNonNull(name);
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		a.append("MATCH ");
		doubleQuote(a, name);
	}
}
