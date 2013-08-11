import java.util.Arrays;
import java.util.HashMap;

public class Tree {
    public static final int NODEBIT = 2;
    public static final int NODEBITS = 3;
    public static final long NULLARY = 0;
    public static final long UNARY = 1;
    public static final long BINARY = 2;
    public static final long TERNARY = 3;

    public static long nullary () {
        return NULLARY;
    }

    public static long unary (long t, int s) {
        return (t << NODEBIT) | UNARY;
    }

    public static long binary (long t1, int s1, long t2, int s2) {
        return (((t1 << (s2 * NODEBIT)) | t2) << NODEBIT) | BINARY;
    }

    public static long ternary (long t1, int s1, long t2, int s2, long t3, int s3) {
        return (((((t1 << (s2 * NODEBIT)) | t2) << (s3 * NODEBIT)) | t3) << NODEBIT) | TERNARY;
    }

    public static String toString (long t) {
        StringBuffer sb = new StringBuffer();
        toStringBuffer(sb, t);
        return sb.toString();
    }

    private static long toStringBuffer (StringBuffer sb, long t) {
        switch (((int) t & NODEBITS)) {
            case (int) NULLARY:
                t = t >>> NODEBIT;
                sb.append(NULLARY);
                break;
            case (int) UNARY:
                t = t >>> NODEBIT;
                sb.append("(" + UNARY + " ");
                t = toStringBuffer(sb, t);
                sb.append(")");
                break;
            case (int) BINARY:
                t = t >>> NODEBIT;
                sb.append("(" + BINARY + " ");
                t = toStringBuffer(sb, t);
                sb.append(" ");
                t = toStringBuffer(sb, t);
                sb.append(")");
                break;
            case (int) TERNARY:
                t = t >>> NODEBIT;
                sb.append("(" + TERNARY + " ");
                t = toStringBuffer(sb, t);
                sb.append(" ");
                t = toStringBuffer(sb, t);
                sb.append(" ");
                t = toStringBuffer(sb, t);
                sb.append(")");
                break;
        }
        return t;
    }

    public static int size (int u, int b, int t) {
        return 1 + u + b + t;
    }

    public static long[] gen (int un, int bi, int te) {
        Gener gener = new Gener();
        return gener.gen(un, bi, te);
    }

    private static class ArrayListLong {
        int size;
        long[] array;

        public ArrayListLong () {
            size = 0;
            array = new long[16];
        }

        public void add (long l) {
            if (size == array.length) {
                array = Arrays.copyOf(array, size * 2);
            }
            array[size] = l;
            size++;
        }

        public long[] toArray () {
            return Arrays.copyOf(array, size);
        }
    }

    private static class Gener {
        private final HashMap<Integer, long[]> hash;

        private int key (int un, int bi, int te) {
            return (un << 16) | (bi << 8) | te;
        }

        public Gener () {
            hash = new HashMap<Integer, long[]>();
        }

        public long[] gen (int un, int bi, int te) {
            long[] res0 = hash.get(key(un, bi, te));
            if (res0 != null) {
                return res0;
            }
            ArrayListLong res = new ArrayListLong();
            if (un == 0 && bi == 0 && te == 0) {
                res.add(nullary());
            }
            if (un > 0) {
                int un0 = un - 1;
                int s = size(un0, bi, te);
                for (long t : gen(un0, bi, te)) {
                    res.add(unary(t, s));
                }
            }
            if (bi > 0) {
                int bi0 = bi - 1;
                for (int un1 = 0; un1 <= un; un1++) {
                    int un2 = un - un1;
                    for (int bi1 = 0; bi1 <= bi0; bi1++) {
                        int bi2 = bi0 - bi1;
                        for (int te1 = 0; te1 <= te; te1++) {
                            int te2 = te - te1;
                            int s1 = size(un1, bi1, te1);
                            int s2 = size(un2, bi2, te2);
                            for (long t1 : gen(un1, bi1, te1)) {
                                for (long t2 : gen(un2, bi2, te2)) {
                                    res.add(binary(t2, s2, t1, s1));
                                }
                            }
                        }
                    }
                }
            }
            if (te > 0) {
                int te0 = te - 1;
                for (int un1 = 0; un1 <= un; un1++) {
                    for (int un2 = 0; un2 <= un - un1; un2++) {
                        int un3 = un - un1 - un2;
                        for (int bi1 = 0; bi1 <= bi; bi1++) {
                            for (int bi2 = 0; bi2 <= bi - bi1; bi2++) {
                                int bi3 = bi - bi1 - bi2;
                                for (int te1 = 0; te1 <= te0; te1++) {
                                    for (int te2 = 0; te2 <= te0 - te1; te2++) {
                                        int te3 = te0 - te1 - te2;
                                        int s1 = size(un1, bi1, te1);
                                        int s2 = size(un2, bi2, te2);
                                        int s3 = size(un3, bi3, te3);
                                        for (long t1 : gen(un1, bi1, te1)) {
                                            for (long t2 : gen(un2, bi2, te2)) {
                                                for (long t3 : gen(un3, bi3, te3)) {
                                                    res.add(ternary(t3, s3, t2, s2, t1, s1));
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            res0 = res.toArray();
            hash.put(key(un, bi, te), res0);
            return res0;
        }
    }

    public static void main (String[] args) {
        long start = System.nanoTime();
        long[] ts = gen(4, 3, 3);
        long stop = System.nanoTime();
        System.out.println("Total: " + ts.length + ", time: " + ((stop - start) / 1e9));
        // for (long t : ts) {
        // System.out.println(toString(t));
        // }
        // System.out.println("Total: " + ts.length + ", time: " + ((stop - start) / 1e9));
    }
}
