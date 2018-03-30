package org.sqlite.parser.ast;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

/**
 * The AST node corresponding to each {@code WHEN}-{@code THEN} pair of subexpressions in a {@code CASE} expression.
 */
public class WhenThenPair implements ToSql {
	public final Expr when;
	public final Expr then;

	public WhenThenPair(Expr when, Expr then) {
		this.when = requireNonNull(when);
		this.then = requireNonNull(then);
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		a.append("WHEN ");
		when.toSql(a);
		a.append(" THEN ");
		then.toSql(a);
	}
}
