package org.sqlite.parser.ast;

import java.io.IOException;
import java.util.List;

public interface ToSql {
	void toSql(Appendable a) throws IOException;

	default void doubleQuote(Appendable a, String name) throws IOException {
		throw new UnsupportedOperationException("TBD");
	}
	default void singleQuote(Appendable a, String name) throws IOException {
		throw new UnsupportedOperationException("TBD");
	}

	default void comma(Appendable a, List<? extends ToSql> items) throws IOException {
		for (int i = 0; i < items.size(); i++) {
			if (i != 0) {
				a.append(", ");
			}
			items.get(i).toSql(a);
		}
	}

	default void commaNames(Appendable a, List<String> names) throws IOException {
		for (int i = 0; i < names.size(); i++) {
			if (i != 0) {
				a.append(", ");
			}
			doubleQuote(a, names.get(i));
		}
	}
}