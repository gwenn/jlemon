package org.sqlite.parser.ast;

import java.io.IOException;

import static java.util.Objects.requireNonNull;
import static org.sqlite.parser.ast.ToSql.doubleQuote;

public class CollateColumnConstraint extends ColumnConstraint {
	public final String collationName;

	public CollateColumnConstraint(String name, String collationName) {
		super(name);
		this.collationName = requireNonNull(collationName);
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		constraintName(a);
		a.append("COLLATE ");
		doubleQuote(a, collationName);
	}
}
