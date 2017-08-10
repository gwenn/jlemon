package org.sqlite.parser.ast;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.sqlite.parser.Keyword;

import static org.sqlite.parser.Identifier.isIdentifierContinue;
import static org.sqlite.parser.Identifier.isIdentifierStart;

public interface ToSql {
	void toSql(Appendable a) throws IOException;

	default String toSql() {
		StringBuilder builder = new StringBuilder();
		try {
			toSql(builder);
			return builder.toString();
		} catch (IOException e) {
			throw new AssertionError("No IOException expected with StringBuilder", e);
		}
	}

	static void doubleQuote(Appendable a, String name) throws IOException {
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
			// identifier must be quoted when they match a keyword...
			if (Keyword.isKeyword(name)) {
				a.append('`');
				a.append(name);
				a.append('`');
				return;
			}
			a.append(name);
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

	static void singleQuote(Appendable a, String value) throws IOException {
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

	static void comma(Appendable a, List<? extends ToSql> items) throws IOException {
		for (int i = 0; i < items.size(); i++) {
			if (i != 0) {
				a.append(", ");
			}
			items.get(i).toSql(a);
		}
	}

	static void commaNames(Appendable a, List<String> names) throws IOException {
		for (int i = 0; i < names.size(); i++) {
			if (i != 0) {
				a.append(", ");
			}
			doubleQuote(a, names.get(i));
		}
	}

	static <T> List<T> requireNotEmpty(List<T> list) {
		if (list == null) {
			throw new NullPointerException();
		}
		if (list.isEmpty()) {
			throw new IllegalArgumentException("empty list");
		}
		return list;
	}
	static <T> List<T> nullToEmpty(List<T> list) {
		if (list == null) {
			return Collections.emptyList();
		}
		return list;
	}

	static <T> boolean isNotEmpty(List<T> list) {
		return list != null && !list.isEmpty();
	}

	static <T> boolean isEmpty(List<T> list) {
		return list == null || list.isEmpty();
	}
}