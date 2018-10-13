package org.sqlite.parser.ast;

import java.io.IOException;
import java.util.List;

import org.sqlite.parser.Token;

import static org.sqlite.parser.TokenType.TK_OVER;
import static org.sqlite.parser.TokenType.TK_RANGE;
import static org.sqlite.parser.TokenType.TK_ROWS;
import static org.sqlite.parser.ast.ToSql.comma;
import static org.sqlite.parser.ast.ToSql.doubleQuote;

// Sum type: window clause vs over clause
public class Window implements ToSql {
	private short rangeOrRows;
	private FrameBound start;
	private FrameBound end;

	public String name;

	public boolean over;
	public Expr filter;

	public List<Expr> partition;
	public List<SortedColumn> orderBy;

	public Window() {
	}

	public Window(String name, Expr filter) {
		this.name = name;
		this.filter = filter;
		this.over = true;
	}

	public Window(short rangeOrRows, FrameBound frameBound) {
		this(rangeOrRows, frameBound, null);
	}
	public Window(short rangeOrRows, FrameBound start, FrameBound end) {
		this.rangeOrRows = rangeOrRows;
		this.start = start;
		this.end = end;
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		if (over) {
			if (filter != null) {
				a.append("FILTER");
				a.append('(');
				a.append("WHERE ");
				filter.toSql(a);
				a.append(')');
				a.append(' ');
			}
			a.append("OVER ");
			if (name != null) {
				doubleQuote(a, name);
				return;
			}
		} else {
			doubleQuote(a, name);
			a.append(" AS ");
		}
		a.append('(');
		if (partition != null) {
			a.append("PARTITION BY ");
			comma(a, partition);
			a.append(' ');
		}
		if (orderBy != null) {
			a.append("ORDER BY ");
			comma(a, orderBy);
			a.append(' ');
		}
		if (rangeOrRows == 0) {
		} else if (rangeOrRows == TK_RANGE) {
			a.append("RANGE ");
		} else if (rangeOrRows == TK_ROWS) {
			a.append("ROWS ");
		}
		if (end != null) {
			a.append("BETWEEN ");
		}
		if (start != null) {
			start.toSql(a);
		}
		if (end != null) {
			a.append(" AND ");
			end.toSql(a);
		}
		a.append(')');
	}
}
