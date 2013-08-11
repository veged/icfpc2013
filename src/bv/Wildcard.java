package bv;

public class Wildcard extends Expression {
    public int size = 1;

    public Wildcard(int size) {
        super(true, false);
        this.size = size;
    }

    @Override
    public long eval() {
        return 0L;
    }

    @Override
    public String toString() {
        return "(*" + size + ")";
    }
}
