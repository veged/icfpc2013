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
    public String toString() {
        return "(ALT size: " + alts.size() + ")";
    }
}
