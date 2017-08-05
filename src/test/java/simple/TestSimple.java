package simple;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static simple.TokenType.DIVIDE;
import static simple.TokenType.INTEGER;
import static simple.TokenType.PLUS;
import static simple.TokenType.TIMES;

public class TestSimple {
	@Test
	public void test() {
		Result r = new Result();
		yyParser p = new yyParser(r);
		p.Parse(INTEGER, 5);
		p.Parse(PLUS, 0);
		p.Parse(INTEGER, 10);
		p.Parse(TIMES, 0);
		p.Parse(INTEGER, 4);
		p.Parse(0, 0);
		assertEquals("(5 PLUS (10 TIMES 4))", r.expr.String());
		r.expr = null;

		p.Parse(INTEGER, 15);
		p.Parse(DIVIDE, 0);
		p.Parse(INTEGER, 5);
		p.Parse(0, 0);
		assertEquals("(15 DIVIDE 5)", r.expr.String());
		r.expr = null;

		p.Parse(INTEGER, 50);
		p.Parse(PLUS, 0);
		p.Parse(INTEGER, 125);
		p.Parse(0, 0);
		assertEquals("(50 PLUS 125)", r.expr.String());
		r.expr = null;

		p.Parse(INTEGER, 50);
		p.Parse(TIMES, 0);
		p.Parse(INTEGER, 125);
		p.Parse(PLUS, 0);
		p.Parse(INTEGER, 125);
		p.Parse(0, 0);
		assertEquals("((50 TIMES 125) PLUS 125)", r.expr.String());
		r.expr = null;
	}
}