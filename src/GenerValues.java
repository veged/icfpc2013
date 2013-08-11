import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GenerValues {
    Map<Integer, Set<Long>> valmap;
    long value;

    Set<Long> getSet(int size) {
        Set<Long> valset = valmap.get(size);
        if (valset == null) {
            valset = new HashSet<Long>(size == 1 ? 3 : valmap.get(size - 1).size() * 4);
            valmap.put(size, valset);
        }
        return valset;
    }

    public GenerValues(long value) {
        this.valmap = new HashMap<Integer, Set<Long>>();
        this.value = value;
    }

    public void gen(int size) {
        Set<Long> valset = getSet(size);
        if (size == 1) {
            valset.add(0L);
            valset.add(1L);
            valset.add(value);
        }
        if (size >= 2) {
            for (long v : getSet(size - 1)) {
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
                for (long v : getSet(i)) {
                    for (long w : getSet(j)) {
                        valset.add(v & w);
                        valset.add(v | w);
                        valset.add(v ^ w);
                        valset.add(v + w);
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        GenerValues genvals = new GenerValues(1L);
        for (int size = 1; size < 20; size++) {
            long start = System.nanoTime();
            genvals.gen(size);
            long stop = System.nanoTime();
            System.out.println("size(" + size + ")=" + genvals.getSet(size).size() + ", gener_time=" + ((stop - start) / 1e9));
        }
    }

}
