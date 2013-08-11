package bv;
import java.util.Set;
import java.util.HashSet;

public class Op1 extends Expression {
    public enum OpName {
        not, shl1, shr1, shr4, shr16, total
    }

    public final OpName op;
    public final Expression e;

    public Op1 (Op1.OpName op, Expression e) {
        super(e);
        this.op = op;
        this.e = e;
    }

    @Override
    public long eval () {
		return apply(e.eval());
    }

    @Override
    public long weight () {
        return e.weight();
    }

	private long apply(long v) {
        switch (op) {
            case not:
                return ~v;
            case shl1:
                return v << 1;
            case shr1:
                return v >>> 1;
            case shr4:
                return v >>> 4;
            case shr16:
                return v >>> 16;
            default:
                return 0;
        }
    }

    @Override
    public Expression filter (long output) {
        if (!hasWildcard)
            return super.filter(output);
        switch (op) {
            case not:
                Expression e_ = e.filter(~output);
                if (e_ == null)
                    return null;
                else
                    return Language.not(e_);
            case shl1: {
                if ((output & 1L) != 0)
                    return null;

                Expression e1 = e.filter(output >>> 1);
                Expression e2 = e.filter((output >>> 1) | (1L << 63));

                Language.shl1(Language.alt(e1, e2));
            }
            case shr1: {
                if ((output & (1L << 63)) != 0)
                    return null;

                Expression e1 = e.filter(output << 1);
                Expression e2 = e.filter((output << 1) | 1L);

                Language.shr1(Language.alt(e1, e2));
            }
            case shr4:
                return null; // TODO
            case shr16:
                return null; // TODO
            default:
                return null;
        }
    }

    public Set<Long> allValues () {
        if (!hasWildcard) {
            return super.allValues();
        }
		Set<Long> values = new HashSet<Long>();
		for (long v : e.allValues())
			values.add(apply(v));
		return values;
    }

    @Override
    public Expression any () {
        return Language.op1(op, e.any());
    }

    @Override
    public String toString () {
        return "(" + op + " " + e + ")";
    }
}
