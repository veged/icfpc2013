package bv;

public class Const extends Expression {
    public final long c;

    public Const(long c) {
        super(false, false);
        this.c = c;
    }

    @Override
    public long eval() {
        return c;
    }

    @Override
    public Expression any() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String toString() {
        return Long.toString(c);
    }


}
