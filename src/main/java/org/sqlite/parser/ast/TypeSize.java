package org.sqlite.parser.ast;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

// Sum Type
public class TypeSize implements ToSql {
	public final String size1;
	public final String size2;

	public static TypeSize maxSize(String max) {
		return new TypeSize(requireNonNull(max), null);
	}
	public static TypeSize couple(String size1, String size2) {
		return new TypeSize(requireNonNull(size1), requireNonNull(size2));
	}

	private TypeSize(String size1,
			String size2) {
		this.size1 = size1;
		this.size2 = size2;
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		a.append(size1); // TODO check content
		if (size2 != null) {
			a.append(", ");
			a.append(size2);
		}
	}
}
