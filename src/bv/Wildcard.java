package bv;

public class Wildcard extends Expression {
    public int size = 1;

    public Wildcard(int size) {
        super(true, false, true);
        this.size = size;
    }

    @Override
    public long eval() {
        return 0L;
    }

    @Override
    public Expression any() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String toString() {
        return "(*" + size + ")";
    }
}
