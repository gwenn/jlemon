package org.sqlite.parser.ast;

import java.io.IOException;
import java.util.List;

public interface ToSql {
	void toSql(Appendable a) throws IOException;

	default void doubleQuote(Appendable a, String name) throws IOException {
		throw new UnsupportedOperationException("TBD");
	}

	default void comma(Appendable a, List<? extends ToSql> items) throws IOException {
		throw new UnsupportedOperationException("TBD");
	}
}