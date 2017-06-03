package org.sqlite.parser.ast;

import java.io.IOException;

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
	Multiply,
	Modulus,
	NotEquals, // != or <>
	Or,
	RightShift,
	Substract;

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
			a.append("=>");
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
