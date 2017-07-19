package org.sqlite.parser.ast;

import java.io.IOException;
import java.sql.DatabaseMetaData;
import java.util.List;
import java.util.Optional;

import org.sqlite.parser.ParseException;

import static java.util.Objects.requireNonNull;
import static org.sqlite.parser.ast.ToSql.nullToEmpty;

public class ColumnDefinition implements ToSql {
	public final ColumnNameAndType nameAndType;
	public final List<ColumnConstraint> constraints;
	public final PrimaryKeyColumnConstraint primaryKeyColumnConstraint;

	public ColumnDefinition(ColumnNameAndType nameAndType,
			List<ColumnConstraint> constraints) {
		this.nameAndType = requireNonNull(nameAndType);
		this.constraints = nullToEmpty(constraints);
		if (!this.constraints.isEmpty()) {
			PrimaryKeyColumnConstraint pk = null;
			for (ColumnConstraint constraint : constraints) {
				if (constraint instanceof PrimaryKeyColumnConstraint) {
					if (pk != null) {
						throw new ParseException("Multiple PRIMARY KEY constraints");
					}
					pk = (PrimaryKeyColumnConstraint) constraint;
				}
			}
			primaryKeyColumnConstraint = pk;
		} else {
			primaryKeyColumnConstraint = null;
		}
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		nameAndType.toSql(a);
		for (ColumnConstraint constraint : constraints) {
			a.append(' ');
			constraint.toSql(a);
		}
	}

	/**
	 * @return {@link DatabaseMetaData#columnNullable} or {@link DatabaseMetaData#columnNoNulls}
	 */
	public Optional<Integer> getNullable() {
		return constraints.stream()
				.filter(NotNullColumnConstraint.class::isInstance)
				.findAny()
				.map(NotNullColumnConstraint.class::cast)
				.map(NotNullColumnConstraint::getNullable);
	}

	public LiteralExpr getDefault() {
		return constraints.stream()
				.filter(DefaultColumnConstraint.class::isInstance)
				.findAny()
				.map(DefaultColumnConstraint.class::cast)
				.map(dcc -> LiteralExpr.string(dcc.expr.toSql()))
				.orElse(LiteralExpr.NULL);
	}
}
