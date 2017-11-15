package org.sqlite.parser.ast;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

// Sum Type
public class TypeSize implements ToSql {
	public final Expr size1;
	public final Expr size2;

	public static TypeSize maxSize(Expr max) {
		return new TypeSize(requireNonNull(max), null);
	}
	public static TypeSize couple(Expr size1, Expr size2) {
		return new TypeSize(requireNonNull(size1), requireNonNull(size2));
	}

	private TypeSize(Expr size1,
			Expr size2) {
		this.size1 = size1;
		this.size2 = size2;
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		size1.toSql(a);
		if (size2 != null) {
			a.append(", ");
			size2.toSql(a);
		}
	}

	public Expr getSize() {
		return size1;
	}

	public Expr getDecimalDigits() {
		if (size2 == null) {
			return LiteralExpr.NULL;
		}
		return size2;
	}
}
