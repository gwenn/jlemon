package org.sqlite.parser.ast;

import java.io.IOException;

import org.sqlite.parser.Token;

import static java.util.Objects.requireNonNull;
import static org.sqlite.parser.ast.ToSql.singleQuote;

/**
 * literal-value
 */
public class LiteralExpr implements Expr {
	public static final LiteralExpr NULL = new LiteralExpr("NULL", LiteralType.Keyword);
	public static final LiteralExpr EMPTY_STRING = new LiteralExpr("", LiteralType.String);
	public final String value;
	public final LiteralType type;

	public static LiteralExpr from(Token t) {
		LiteralType type = LiteralType.from(t.tokenType());
		return new LiteralExpr(t.text(), type);
	}

	public static LiteralExpr integer(int value) {
		return new LiteralExpr(String.valueOf(value), LiteralType.Integer);
	}
	public static LiteralExpr string(String value) {
		if (value == null) {
			return NULL;
		} if (value.isEmpty()) {
			return EMPTY_STRING;
		}
		return new LiteralExpr(value, LiteralType.String);
	}
	public LiteralExpr(String value, LiteralType type) {
		this.value = requireNonNull(value);
		this.type = requireNonNull(type);
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		if (LiteralType.String == type) {
			singleQuote(a, value);
		} else if (LiteralType.Float == type) {
			a.append(value);
		} else if (LiteralType.Integer == type) {
			a.append(value);
		} else if (LiteralType.Keyword == type) {
			a.append(value);
		} else if (LiteralType.Blob == type) {
			a.append('X');
			singleQuote(a, value);
		}
	}
}
