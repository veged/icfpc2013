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
    public String toString() {
        return Long.toString(c);
    }

}
