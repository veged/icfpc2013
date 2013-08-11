package bv;

public class Var extends Expression {
    public final char n;
    public long value;

    public Var(boolean hasX, boolean hasYZ, char n) {
        super(hasX, hasYZ);
        this.n = n;
    }

    @Override
    public long eval() {
        return value;
    }

    @Override
    public Expression filter(long output) {
        if(eval() == output) return this;
        else return null;
    }

    @Override
    public String toString() {
        return Character.toString(n);
    }
}
