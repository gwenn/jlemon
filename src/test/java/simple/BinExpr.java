package simple;

class BinExpr implements Expr {
    private final int op;
    private final Expr lhs;
    private final Expr rhs;

    BinExpr(int op, Expr lhs, Expr rhs) {
        assert op > 0;
        this.op = op;
        assert lhs != null;
        this.lhs = lhs;
        assert rhs != null;
        this.rhs = rhs;
    }

    @Override
    public String String() {
        return String.format("(%s %s %s)", lhs.String(), TokenType.toString(op), rhs.String());
    }
}