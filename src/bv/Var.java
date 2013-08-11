package bv;

public class Var extends Expression {
    public final char n;
    public long value;

    public Var (boolean hasX, boolean hasYZ, char n) {
        super(hasX, hasYZ, false, 1);
        this.n = n;
    }

    @Override
    public long eval () {
        return value;
    }

    @Override
    public long weight () {
        return 1;
    }

    @Override
    public String toString () {
        return Character.toString(n);
    }
}
