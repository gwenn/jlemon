package org.sqlite.parser.ast;

import java.io.IOException;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static org.sqlite.parser.ast.ToSql.isNotEmpty;

public class SelectBody implements ToSql {
	public final OneSelect select;
	public final List<CompoundSelect> compounds;

	public SelectBody(OneSelect select, List<CompoundSelect> compounds) {
		this.select = requireNonNull(select);
		this.compounds = compounds;
		// TODO "too many terms in compound SELECT"
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		select.toSql(a);
		if (isNotEmpty(compounds)) {
			for (CompoundSelect compound : compounds) {
				a.append(' ');
				compound.toSql(a);
			}
		}
	}
}
