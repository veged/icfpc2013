package bv;

import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

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
        return apply(e1.eval(), e2.eval());
    }

    @Override
    public long weight () {
        return e1.weight() * e2.weight();
    }

    private long apply (long v1, long v2) {
        switch (op) {
            case and:
                return v1 & v2;
            case or:
                return v1 | v2;
            case xor:
                return v1 ^ v2;
            case plus:
                return v1 + v2;
            default:
                return 0;
        }
    }

    @Override
    public Expression filter (long output) {
        if (!hasWildcard)
            return super.filter(output);
        switch (op) {
            case and: {
                ArrayList<Expression> alts = new ArrayList<Expression>();
                ArrayList<Long> v1_ok = new ArrayList<Long>();
                for (long v1 : e1.allValues()) {
                    if ((v1 & output) == output)
                        v1_ok.add(v1);
                }
                for (long v2 : e2.allValues()) {
                    if ((v2 & output) == output) {
                        for (long v1 : v1_ok) {
                            if ((v1 & v2) == output) {
                                Expression e1_ = e1.filter(v1);
                                Expression e2_ = e2.filter(v1);
                                addToArrayList(alts, Language.and(e1_, e2_));
                            }
                        }
                    }
                }
                return Language.alt(alts);
            }
            case or:
                return null; // TODO
            case xor: {
                Set<Long> outs2 = all.SolverMeta.myGenVals.genValues(e2.size);
                ArrayList<Expression> alts = new ArrayList<Expression>();
                if (e1 instanceof Wildcard) {
                    Set<Long> outs1 = all.SolverMeta.myGenVals.genValues(e1.size);
                    for (long v1 : outs1) {
                        long output_ = v1 ^ output;
                        if (outs2.contains(output_)) {
                            Expression e2_ = e2.filter(output_);
                            if (e2_ != null) {
                                addToArrayList(alts, Language.xor(all.SolverMeta.expsBySizeAndOutput.get(e1.size).get(v1), e2_));
                            }
                        }
                    }
                } else {
                    for (Expression e1_ : e1.all()) {
                        long output_ = e1_.eval() ^ output;
                        if (outs2.contains(output_)) {
                            Expression e2_ = e2.filter(output_);
                            if (e2_ != null) {
                                addToArrayList(alts, Language.xor(e1_, e2_));
                            }
                        }
                    }
                }
                return Language.alt(alts);
            }
            case plus:
                return null; // TODO
            default:
                return null;
        }
    }

    public static void addToArrayList (ArrayList<Expression> al, Expression e) {
        if (e != null)
            al.add(e);
    }

    public Set<Long> allValues () {
        if (!hasWildcard) {
            return super.allValues();
        }
        Set<Long> values = new HashSet<Long>();
        for (long v1 : e1.allValues()) {
            for (long v2 : e2.allValues()) {
                values.add(apply(v1, v2));
            }
        }
        return values;
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
