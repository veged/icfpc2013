package bv;

public class Op1 extends Expression {
    public enum OpName {
        not, shl1, shr1, shr4, shr16
    }

    public final OpName op;
    public final Expression e;

    public Op1 (Op1.OpName op, Expression e) {
        super(e);
        this.op = op;
        this.e = e;
    }

    @Override
    public long eval () {
        switch (op) {
            case not:
                return ~e.eval();
            case shl1:
                return e.eval() << 1;
            case shr1:
                return e.eval() >>> 1;
            case shr4:
                return e.eval() >>> 4;
            case shr16:
                return e.eval() >>> 16;
            default:
                return 0;
        }
    }

    public String toString () {
        return "(" + op + " " + e + ")";
    }
}
