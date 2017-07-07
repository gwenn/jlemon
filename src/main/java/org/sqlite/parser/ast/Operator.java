package org.sqlite.parser.ast;

import java.io.IOException;

import org.sqlite.parser.TokenType;

public enum Operator implements ToSql {
	Add,
	And,
	BitwiseAnd,
	BitwiseOr,
	Concat, // String concatenation (||)
	Equals, // = or ==
	Divide,
	Greater,
	GreaterEquals,
	Is,
	IsNot,
	LeftShift,
	Less,
	LessEquals,
	Modulus,
	Multiply,
	NotEquals, // != or <>
	Or,
	RightShift,
	Substract;

	public static Operator from(int tt) {
		if (TokenType.TK_PLUS == tt) {
			return Add;
		} else if (TokenType.TK_AND == tt) {
			return And;
		} else if (TokenType.TK_BITAND == tt) {
			return BitwiseAnd;
		} else if (TokenType.TK_BITOR == tt) {
			return BitwiseOr;
		} else if (TokenType.TK_CONCAT == tt) {
			return Concat;
		} else if (TokenType.TK_EQ == tt) {
			return Equals;
		} else if (TokenType.TK_SLASH == tt) {
			return Divide;
		} else if (TokenType.TK_GT == tt) {
			return Greater;
		} else if (TokenType.TK_GE == tt) {
			return GreaterEquals;
		} else if (TokenType.TK_IS == tt) {
			return Is;
		} else if (TokenType.TK_LSHIFT == tt) {
			return LeftShift;
		} else if (TokenType.TK_LT == tt) {
			return Less;
		} else if (TokenType.TK_LE == tt) {
			return LessEquals;
		} else if (TokenType.TK_REM == tt) {
			return Modulus;
		} else if (TokenType.TK_STAR == tt) {
			return Multiply;
		} else if (TokenType.TK_NE == tt) {
			return NotEquals;
		} else if (TokenType.TK_OR == tt) {
			return Or;
		} else if (TokenType.TK_RSHIFT == tt) {
			return RightShift;
		} else if (TokenType.TK_MINUS == tt) {
			return Substract;
		}
		throw new IllegalArgumentException(String.format("Unsupported Operator: %s", TokenType.toString(tt)));
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		if (Add == this) {
			a.append("+");
		} else if (And == this) {
			a.append("AND");
		} else if (BitwiseAnd == this) {
			a.append("&");
		} else if (BitwiseOr == this) {
			a.append("|");
		} else if (Concat == this) {
			a.append("||");
		} else if (Equals == this) {
			a.append("=");
		} else if (Divide == this) {
			a.append("/");
		} else if (Greater == this) {
			a.append(">");
		} else if (GreaterEquals == this) {
			a.append(">=");
		} else if (Is == this) {
			a.append("IS");
		} else if (IsNot == this) {
			a.append("IS NOT");
		} else if (LeftShift == this) {
			a.append("<<");
		} else if (Less == this) {
			a.append("<");
		} else if (LessEquals == this) {
			a.append("<=");
		} else if (Modulus == this) {
			a.append("%");
		} else if (Multiply == this) {
			a.append("*");
		} else if (NotEquals == this) {
			a.append("<>");
		} else if (Or == this) {
			a.append("OR");
		} else if (RightShift == this) {
			a.append(">>");
		} else if (Substract == this) {
			a.append("-");
		}
	}
}
