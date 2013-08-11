package bv;

public class Op2 extends Expression {
    public enum OpName {
        and, or, xor, plus
    }

    public final OpName op;
    public final Expression e1;
    public final Expression e2;

    public Op2(Op2.OpName op, Expression e1, Expression e2) {
        super(e1, e2);
        this.op = op;
        this.e1 = e1;
        this.e2 = e2;
    }

    @Override
    public long eval() {
        switch (op) {
            case and:
                return e1.eval() & e2.eval();
            case or:
                return e1.eval() | e2.eval();
            case xor:
                return e1.eval() ^ e2.eval();
            case plus:
                return e1.eval() + e2.eval();
            default:
                return 0;
        }
    }

    @Override
    public Expression filter(long output) {
        if (!hasWildcard) return super.filter(output);
        switch (op) {
            case and:
                return null; // TODO
            case or:
                return null; // TODO
            case xor:
                return null; // TODO
            case plus:
                return null; // TODO
            default:
                return null;
        }
    }

    @Override
    public Expression any() {
        return Language.op2(op, e1.any(), e2.any());
    }

    @Override
    public String toString() {
        return "(" + op + " " + e1 + " " + e2 + ")";
    }
}
