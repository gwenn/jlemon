package org.sqlite.parser.ast;

import java.io.IOException;
import java.util.List;

import static java.util.Objects.requireNonNull;

public class ColumnDefinition implements ToSql {
	public final ColumnNameAndType nameAndType;
	public final List<ColumnConstraint> constraints;

	public ColumnDefinition(ColumnNameAndType nameAndType,
			List<ColumnConstraint> constraints) {
		this.nameAndType = requireNonNull(nameAndType);
		this.constraints = constraints;
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		nameAndType.toSql(a);
		if (constraints != null) {
			for (ColumnConstraint constraint : constraints) {
				a.append(' ');
				constraint.toSql(a);
			}
		}
	}
}
