package org.sqlite.parser.ast;

import java.io.IOException;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class AsSelect implements CreateTableBody {
	public final Select select;

	public AsSelect(Select select) {
		this.select = requireNonNull(select);
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		a.append(" AS ");
		select.toSql(a);
	}
}
