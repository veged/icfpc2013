package bv;

import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

public abstract class Expression {
    public final boolean hasX;
    public final boolean hasYZ;
    public final boolean hasWildcard;
    public final int size;

    public Expression (boolean hasX, boolean hasYZ, boolean hasWildcard, int size) {
        this.hasX = hasX;
        this.hasYZ = hasYZ;
        this.hasWildcard = hasWildcard;
        this.size = size;
    }

    // UNARY
    public Expression (Expression e1) {
        this.hasX = e1.hasX;
        this.hasYZ = e1.hasYZ;
        this.hasWildcard = e1.hasWildcard;
        this.size = 1 + e1.size;
    }

    // BINARY
    public Expression (Expression e1, Expression e2) {
        this.hasX = e1.hasX || e2.hasX;
        this.hasYZ = e1.hasYZ || e2.hasYZ;
        this.hasWildcard = e1.hasWildcard || e2.hasWildcard;
        this.size = 1 + e1.size + e2.size;
    }

    // IF
    public Expression (Expression e1, Expression e2, Expression e3) {
        this.hasX = e1.hasX || e2.hasX || e3.hasX;
        this.hasYZ = e1.hasYZ || e2.hasYZ || e3.hasYZ;
        this.hasWildcard = e1.hasWildcard || e2.hasWildcard || e3.hasWildcard;
        this.size = 1 + e1.size + e2.size + e3.size;
    }

    public ArrayList<Expression> all () {
        throw new Error("Called abstract Expression.all!");
    }

    public Set<Long> allValues () {
        if (!hasWildcard) {
            Set<Long> s = new HashSet<Long>();
            s.add(eval());
            return s;
        } else
            throw new Error("Called abstract Expression.allValues!");
    }

    abstract public long eval ();

    public Expression filter (long output) {
        if (eval() == output)
            return this;
        else
            return null;
    }

    public Expression any () {
        if (!hasWildcard)
            return this;
        else
            return null;
    }

}
