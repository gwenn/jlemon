package org.sqlite.parser.ast;

import java.io.IOException;
import java.util.List;

import static org.sqlite.parser.Identifier.isIdentifierStart;
import static org.sqlite.parser.Identifier.isIdentifierContinue;

public interface ToSql {
	void toSql(Appendable a) throws IOException;

	default void doubleQuote(Appendable a, String name) throws IOException {
		if (name.isEmpty()) {
			a.append("\"\"");
			return;
		}
		boolean isIdentifier = true;
		for (int i = 0; i < name.length(); i++) {
			if (i == 0) {
				isIdentifier = isIdentifier && isIdentifierStart(name.charAt(i));
			} else {
				isIdentifier = isIdentifier && isIdentifierContinue(name.charAt(i));
			}
		}
		if (isIdentifier) {
			a.append(name); // TODO identifier must be quoted when they match a keyword...
			return;
		}
		a.append('"');
		for (int i = 0; i < name.length(); i++) {
			char c = name.charAt(i);
			if (c == '"') {
				a.append(c);
			}
			a.append(c);
		}
		a.append('"');
	}

	default void singleQuote(Appendable a, String value) throws IOException {
		a.append('\'');
		for (int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);
			if (c == '\'') {
				a.append(c);
			}
			a.append(c);
		}
		a.append('\'');
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