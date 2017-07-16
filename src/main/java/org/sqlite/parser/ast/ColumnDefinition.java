package org.sqlite.parser.ast;

import java.io.IOException;
import java.sql.DatabaseMetaData;
import java.util.*;
import java.util.Set;

import org.sqlite.parser.ParseException;

import static java.util.Objects.requireNonNull;
import static org.sqlite.parser.ast.ToSql.nullToEmpty;

public class ColumnDefinition implements ToSql {
	public final ColumnNameAndType nameAndType;
	public final List<ColumnConstraint> constraints;

	public ColumnDefinition(ColumnNameAndType nameAndType,
			List<ColumnConstraint> constraints) {
		this.nameAndType = requireNonNull(nameAndType);
		this.constraints = nullToEmpty(constraints);
		if (!this.constraints.isEmpty()) {
			boolean pk = false;
			for (ColumnConstraint constraint : constraints) {
				if (constraint instanceof PrimaryKeyColumnConstraint) {
					if (pk) {
						throw new ParseException("Multiple PRIMARY KEY constraints");
					}
					pk = true;
				}
			}
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

	public Optional<Boolean> isAnAliasForRowId() {
		final boolean integer = Optional.ofNullable(nameAndType.colType)
				.map(type -> type.name.equalsIgnoreCase("INTEGER") && type.size == null)
				.orElse(Boolean.FALSE);
		if (!integer) {
			return Optional.of(Boolean.FALSE);
		}
		return constraints.stream()
				.filter(PrimaryKeyColumnConstraint.class::isInstance)
				.map(c -> ((PrimaryKeyColumnConstraint) c).order)
				.map(order -> order == null || SortOrder.Asc == order)
				.findAny();
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
