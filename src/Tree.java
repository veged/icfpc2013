import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import bv.Op1;
import bv.Op2;

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
        return 1 + u + 2 * b + 3 * t;
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

        public void addAll (long[] ls) {
            array = Arrays.copyOf(array, array.length + ls.length);
            System.arraycopy(ls, 0, array, size, ls.length);
            size += ls.length;
        }

        public long[] toArray () {
            return Arrays.copyOf(array, size);
        }
    }

    private static class Gener {
        private final HashMap<Integer, long[]> hash;

        private int key (int un, int bi, int te) {
            return (te << 16) | (bi << 8) | un;
        }

        public Gener () {
            hash = new HashMap<Integer, long[]>();
        }

        // / if te < 0; then ternary operator on top
        public long[] gen (int un, int bi, int te) {
            long[] res0 = hash.get(key(un, bi, te));
            if (res0 != null) {
                return res0;
            }
            ArrayListLong res = new ArrayListLong();
            if (un == 0 && bi == 0 && te == 0) {
                res.add(nullary());
            }
            if (un > 0 && te >= 0) {
                int un0 = un - 1;
                int s = size(un0, bi, te);
                for (long t : gen(un0, bi, te)) {
                    res.add(unary(t, s));
                }
            }
            if (bi > 0 && te >= 0) {
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
            if (te != 0) {
                if (te < 0) {
                    te = -te;
                }
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

        public long[] genAllProgs (int size, String[] operators) {
            return genAllProgs(size, new ArrayList<String>(Arrays.asList(operators)));
        }

        public long[] genAllProgs (int size, ArrayList<String> operators) {
            int op1count = 0;
            int op2count = 0;
            for (String s : operators) {
                try {
                    Op1.OpName.valueOf(s);
                    op1count++;
                } catch (IllegalArgumentException e) {
                    // System.out.println("op1 "+s);
                }
                try {
                    Op2.OpName.valueOf(s);
                    op2count++;
                } catch (IllegalArgumentException e) {
                    // System.out.println("op2 "+s);
                }
            }
            int op3count = 0;
            if (operators.contains("if0")) {
                op3count++;
            }
            if (operators.contains("fold")) {
                op3count++;
            }
            if (operators.contains("tfold")) {
                op3count++;
            }
            boolean hasIf0 = operators.contains("if0");
            boolean hasFold = operators.contains("fold");
            boolean hasTFold = operators.contains("tfold");
            if (hasFold || hasTFold) {
                size--;
            }
            size--;

            System.out.println(size + " " + op1count + " " + op2count + " " + op3count);
            ArrayListLong res = new ArrayListLong();
            for (int un = op1count; un <= size; un++) {
                for (int bi = op2count; bi <= size; bi++) {
                    for (int te = op3count; te <= (hasIf0 ? size : op3count); te++) {
                        if (size(un, bi, te) == size) {
                            if (hasTFold) {
                                res.addAll(gen(un, bi, -te));
                            } else {
                                res.addAll(gen(un, bi, te));
                            }
                        }
                    }
                }
            }
            return res.toArray();
        }
    }

    public static void main (String[] args) {
        long start = System.nanoTime();
        // long[] ts = gen(0, 3, 1);
        // long[] ts = (new Gener()).genAllProgs(18, new String[] { "not", "shl1", "shr1", "shr4", "shr16", "and", "or",
        // "xor", "plus", "if0" });
        // long[] ts = (new Gener()).genAllProgs(17, new String[] { "if0","not","plus","shr16","shr4" });
        long[] ts = (new Gener()).genAllProgs(20, new String[] { "and", "not", "or", "plus", "shl1", "shr16", "fold", "xor" });
        long stop = System.nanoTime();
        System.out.println("Total: " + ts.length + ", time: " + ((stop - start) / 1e9));
        // for (long t : ts) {
        // System.out.println(toString(t));
        // }
        // System.out.println("Total: " + ts.length + ", time: " + ((stop - start) / 1e9));
    }
}
