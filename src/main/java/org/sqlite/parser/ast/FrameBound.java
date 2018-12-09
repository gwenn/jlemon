package org.sqlite.parser.ast;

import java.io.IOException;

import static org.sqlite.parser.TokenType.TK_CURRENT;
import static org.sqlite.parser.TokenType.TK_FOLLOWING;
import static org.sqlite.parser.TokenType.TK_PRECEDING;

public class FrameBound implements ToSql {
	public static final FrameBound UNBOUNDED_PRECEDING = new FrameBound(TK_PRECEDING, null);
	public static final FrameBound UNBOUNDED_FOLLOWING = new FrameBound(TK_FOLLOWING, null);
	public static final FrameBound CURRENT_ROW = new FrameBound(TK_CURRENT, null);

	public final short eType;
	public final Expr expr;

	public FrameBound(short eType, Expr expr) {
		this.eType = eType;
		assert eType == TK_CURRENT || eType == TK_FOLLOWING || eType == TK_PRECEDING;
		this.expr = expr;
		assert eType != TK_CURRENT || expr == null;
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		if (expr != null) {
			expr.toSql(a);
			a.append(' ');
		} else if (eType != TK_CURRENT) {
			a.append("UNBOUNDED ");
		}
		if (eType == TK_CURRENT) {
			a.append("CURRENT ROW");
		} else if (eType == TK_FOLLOWING) {
			a.append("FOLLOWING");
		} else if (eType == TK_PRECEDING) {
			a.append("PRECEDING");
		} else {
			throw new IllegalStateException();
		}
	}
}
