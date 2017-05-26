package simple;

class NumExpr implements Expr {
    final int value;

    NumExpr(int value) {
        this.value = value;
    }

    @Override
    public String String() {
        return String.valueOf(value);
    }
}