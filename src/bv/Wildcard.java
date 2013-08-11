package bv;

import java.util.Set;

public class Wildcard extends Expression {
    public static final int metaSize = 9;

    public Wildcard (int size) {
        super(true, false, true, size);
    }

    @Override
    public long eval () {
        return 0L;
    }

    @Override
    public long weight () {
        return all.SolverMeta.myGenProgs.genProgs(size).size();
    }

    @Override
    public Expression filter (long output) {
        if (eval() == output)
            return this;
        else
            return null;
    }

    public Set<Long> allValues () {
        return all.SolverMeta.myGenVals.genValues(size);
    }

    @Override
    public Expression any () {
        // TODO: Hack
        return all.SolverMeta.expsBySizeAndOutput.get(size).values().iterator().next().any();
    }

    @Override
    public String toString () {
        return "(*" + size + ")";
    }
}
