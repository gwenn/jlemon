package simple;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static simple.IToken.*;

public class TestSimple {
    @Test
    public void test() {
        Result r = new Result();
        yyParser p = new yyParser();
        p.Parse(INTEGER, 5, r);
        p.Parse(PLUS, 0, r);
        p.Parse(INTEGER, 10, r);
        p.Parse(TIMES, 0, r);
        p.Parse(INTEGER, 4, r);
        p.Parse(0, 0, r);
        p.ParseFinalize();

        assertEquals("(5 PLUS (10 TIMES 4))", r.expr.String());
    }
}