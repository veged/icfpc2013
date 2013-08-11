package bv;

public class Wildcard extends Expression {
    public Wildcard (int size) {
        super(true, false, true, size);
    }

    @Override
    public long eval () {
        return 0L;
    }

    @Override
    public Expression filter (long output) {
        if (eval() == output)
            return this;
        else
            return null;
    }

    @Override
    public Expression any () {
        return null;
    }

    @Override
    public String toString () {
        return "(*" + size + ")";
    }
}
