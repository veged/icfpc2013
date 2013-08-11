package bv;

import java.util.ArrayList;

public class Alt extends Expression {
    public ArrayList<Expression> alts;

    public Alt(ArrayList<Expression> alts) {
        super(true, false, true);
        this.alts = alts;
    }

    @Override
    public long eval() {
        return 0L;
    }

    @Override
    public Expression filter(long output) {
        if(eval() == output) return this;
        else return null;
    }

    @Override
    public ArrayList<Expression> all() {
        return alts;
    }

    @Override
    public Expression any() {
        return alts.get(0).any();
    }

    @Override
    public String toString() {
        return "(ALT size: " + alts.size() + ")";
    }
}
