package org.sqlite.parser.ast;

import java.io.IOException;

public interface ToSql {
	void toSql(Appendable a) throws IOException;
}