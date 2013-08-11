package bv;
import java.util.Set;
import java.util.HashSet;

public class If0 extends Expression {
    public final Expression e0;
    public final Expression e1;
    public final Expression e2;

    public If0 (Expression e0, Expression e1, Expression e2) {
        super(e0, e1, e2);
        this.e0 = e0;
        this.e1 = e1;
        this.e2 = e2;
    }

    @Override
    public long eval () {
        return e0.eval() == 0 ? e1.eval() : e2.eval();
    }

    @Override
    public Expression filter (long output) {
        if (eval() == output)
            return this;
        else
            return null;
    }

    public Set<Long> allValues () {
        Set<Long> values = new HashSet<Long>();
        if (!hasWildcard) {
            values.add(eval());
        } else {
            for (long v0 : e0.allValues()) {
                if (v0 == 0) {
                    for (long v1 : e1.allValues()) {
                        values.add(v1);
                    }
                } else {
                    for (long v2 : e2.allValues()) {
                        values.add(v2);
                    }
                }
            }
        }
        return values;
    }

    @Override
    public Expression any () {
        return Language.if0(e0.any(), e1.any(), e2.any());
    }

    @Override
    public String toString () {
        return "(if0 " + e0 + " " + e1 + " " + e2 + ")";
    }
}
