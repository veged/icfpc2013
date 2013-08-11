package all;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import bv.Op1;
import bv.Op2;

public class GenerValues extends GenerParams {
    private final HashMap<Integer, HashSet<Long>> valmap;
    private final long value;

    public GenerValues (ArrayList<String> operators, long value) {
        super(operators);
        this.valmap = new HashMap<Integer, HashSet<Long>>();
        this.value = value;
    }

    public HashSet<Long> genAllValues (int size) {
        HashSet<Long> allvalueset = new HashSet<Long>();
        for (int i = 1; i <= size; i++) {
            allvalueset.addAll(genValues(size));
        }
        return allvalueset;
    }

    public HashSet<Long> genValues (int size) {
        HashSet<Long> valset = valmap.get(size);
        if (valset != null) {
            return valset;
        }
        valset = new HashSet<Long>();
        if (size == 1) {
            valset.add(0L);
            valset.add(1L);
            valset.add(value);
        }
        if (size >= 2) {
            for (long v : genValues(size - 1)) {
                if (op1s.contains(Op1.OpName.not)) {
                    valset.add(~v);
                }
                if (op1s.contains(Op1.OpName.shl1)) {
                    valset.add(v << 1);
                }
                if (op1s.contains(Op1.OpName.shr1)) {
                    valset.add(v >>> 1);
                }
                if (op1s.contains(Op1.OpName.shr4)) {
                    valset.add(v >>> 4);
                }
                if (op1s.contains(Op1.OpName.shr16)) {
                    valset.add(v >>> 16);
                }
            }
        }
        if (size >= 3) {
            for (int i = 1; i <= (size - 1) / 2; i++) {
                int j = size - 1 - i;
                for (long v : genValues(i)) {
                    for (long w : genValues(j)) {
                        if (op2s.contains(Op2.OpName.and)) {
                            valset.add(v & w);
                        }
                        if (op2s.contains(Op2.OpName.and)) {
                            valset.add(v | w);
                        }
                        if (op2s.contains(Op2.OpName.and)) {
                            valset.add(v ^ w);
                        }
                        if (op2s.contains(Op2.OpName.and)) {
                            valset.add(v + w);
                        }
                    }
                }
            }
        }
        valmap.put(size, valset);
        return valset;
    }

    public static void main (String[] args) {
        GenerValues genvals = new GenerValues(new ArrayList<String>(Arrays.asList(new String[] { "not", "shl1", "shr1", "shr4", "shr16", "and", "or", "xor", "plus", "if0" })), 1L);
        for (int size = 1; size < 16; size++) {
            long start = System.nanoTime();
            genvals.genValues(size);
            long stop = System.nanoTime();
            System.out.println("size(" + size + ")=" + genvals.genValues(size).size() + ", gener_time=" + ((stop - start) / 1e9));
        }
    }

}
