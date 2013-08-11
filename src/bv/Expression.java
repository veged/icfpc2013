package bv;

public abstract class Expression {
    public final boolean hasX;
    public final boolean hasYZ;
    public final boolean hasWildcard;

    public Expression(boolean hasX, boolean hasYZ) {
        this.hasX = hasX;
        this.hasYZ = hasYZ;
        hasWildcard = false;
    }

    public Expression(boolean hasX, boolean hasYZ, boolean hasWildcard) {
        this.hasX = hasX;
        this.hasYZ = hasYZ;
        this.hasWildcard = hasWildcard;
    }

    public Expression(Expression e1) {
        this.hasX = e1.hasX;
        this.hasYZ = e1.hasYZ;
        hasWildcard = e1.hasWildcard;
    }

    public Expression(Expression e1, Expression e2) {
        this.hasX = e1.hasX || e2.hasX;
        this.hasYZ = e1.hasYZ || e2.hasYZ;
        this.hasWildcard = e1.hasWildcard || e2.hasWildcard;
    }

    public Expression(Expression e1, Expression e2, Expression e3) {
        this.hasX = e1.hasX || e2.hasX || e3.hasX;
        this.hasYZ = e1.hasYZ || e2.hasYZ || e3.hasYZ;
        this.hasWildcard = e1.hasWildcard || e2.hasWildcard || e3.hasWildcard;
    }

    abstract public long eval();

    public Expression filter(long output) {
        if(eval() == output) return this;
        else return null;
    }

    public Expression any() {
        if (!hasWildcard) return this;
        else return null;
    }

}
