package bv;

public class Program {
    public final Expression e;
    public final long size;

    public Program (Expression e) {
        this.e = e;
        this.size = e.size + 1;
    }

    public long run (long value) {
        Language.x.value = value;
        return e.eval();
    }

    public String toString () {
        return "(lambda (" + Language.x + ") " + e + ")";
    }
}
