package bv;

import java.util.ArrayList;
import java.util.Set;

public class Op2 extends Expression {
    public enum OpName {
        and, or, xor, plus, total
    }

    public final OpName op;
    public final Expression e1;
    public final Expression e2;

    public Op2 (Op2.OpName op, Expression e1, Expression e2) {
        super(e1, e2);
        this.op = op;
        this.e1 = e1;
        this.e2 = e2;
    }

    @Override
    public long eval () {
        switch (op) {
            case and:
                return e1.eval() & e2.eval();
            case or:
                return e1.eval() | e2.eval();
            case xor:
                return e1.eval() ^ e2.eval();
            case plus:
                return e1.eval() + e2.eval();
            default:
                return 0;
        }
    }

    @Override
    public Expression filter (long output) {
        if (!hasWildcard)
            return super.filter(output);
        switch (op) {
            case and:
                return null; // TODO
            case or:
                return null; // TODO
            case xor:
                Set<Long> outs = all.Solver.outValues.get(e2.size);
                ArrayList<Expression> alts = new ArrayList<Expression>();
                for (Expression e1_ : e1.all()) {
                    long output_ = e1_.eval() ^ output;
                    if (outs.contains(output_)) {
                        Expression e2_ = e2.filter(output_);
                        if (e2_ != null) {
                            alts.add(Language.xor(e1_, e2_));
                        }
                    }
                }
                return Language.alt(alts);
            case plus:
                return null; // TODO
            default:
                return null;
        }
    }

    @Override
    public Expression any () {
        return Language.op2(op, e1.any(), e2.any());
    }

    @Override
    public String toString () {
        return "(" + op + " " + e1 + " " + e2 + ")";
    }
}
