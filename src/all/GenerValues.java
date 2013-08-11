package all;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GenerValues {
    public final Map<Integer, Set<Long>> valmap;
    private final long value;

    public GenerValues (long value) {
        this.valmap = new HashMap<Integer, Set<Long>>();
        this.value = value;
    }

    public Set<Long> genValues (int size) {
        Set<Long> valset = valmap.get(size);
        if (valset != null) {
            return valset;
        }
        valset = new HashSet<Long>(size == 1 ? 3 : valmap.get(size - 1).size() * 4);
        if (size == 1) {
            valset.add(0L);
            valset.add(1L);
            valset.add(value);
        }
        if (size >= 2) {
            for (long v : genValues(size - 1)) {
                valset.add(~v);
                valset.add(v << 1);
                valset.add(v >>> 1);
                valset.add(v >>> 4);
                valset.add(v >>> 16);
            }
        }
        if (size >= 3) {
            for (int i = 1; i <= (size - 1) / 2; i++) {
                int j = size - 1 - i;
                for (long v : genValues(i)) {
                    for (long w : genValues(j)) {
                        valset.add(v & w);
                        valset.add(v | w);
                        valset.add(v ^ w);
                        valset.add(v + w);
                    }
                }
            }
        }
        valmap.put(size, valset);
        return valset;
    }

    public static void main (String[] args) {
        GenerValues genvals = new GenerValues(1L);
        for (int size = 1; size < 16; size++) {
            long start = System.nanoTime();
            genvals.genValues(size);
            long stop = System.nanoTime();
            System.out.println("size(" + size + ")=" + genvals.genValues(size).size() + ", gener_time=" + ((stop - start) / 1e9));
        }
    }

}
