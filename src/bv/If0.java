package bv;

public class If0 extends Expression {
    public final Expression e0;
    public final Expression e1;
    public final Expression e2;

    public If0(Expression e0, Expression e1, Expression e2) {
        super(e0, e1, e2);
        this.e0 = e0;
        this.e1 = e1;
        this.e2 = e2;
    }

    @Override
    public long eval() {
        return e0.eval() == 0 ? e1.eval() : e2.eval();
    }

    @Override
    public Expression filter(long output) {
        if(eval() == output) return this;
        else return null;
    }

    @Override
    public String toString() {
        return "(if0 " + e0 + " " + e1 + " " + e2 + ")";
    }
}
