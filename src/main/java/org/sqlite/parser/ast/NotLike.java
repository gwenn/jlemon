package org.sqlite.parser.ast;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

public class NotLike implements ToSql {
	public static final NotLike LIKE = new NotLike(false, LikeOperator.Like);
	public final boolean not;
	public final LikeOperator op;

	public NotLike(boolean not, LikeOperator op) {
		this.not = not;
		this.op = requireNonNull(op);
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		if (not) {
			a.append("NOT ");
		}
		op.toSql(a);
	}
}
