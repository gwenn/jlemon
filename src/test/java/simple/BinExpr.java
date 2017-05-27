package simple;

class BinExpr implements Expr {
    final int op;
    final Expr lhs;
    final Expr rhs;

    BinExpr(int op, Expr lhs, Expr rhs) {
        this.op = op;
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    public String String() {
        return String.format("(%s %s %s)", lhs.String(), TokenType.String(op), rhs.String());
    }
}