package all;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import bv.Op1;
import bv.Op2;
import bv.Op3;
import bv.Program;

public class Operators {
    public static final int NODEBIT = 3;
    public static final int NODEBITS = 7;

    public static final long ZERO = 0;
    public static final long ONE = 1;
    public static final long X = 2;
    public static final long Y = 3;
    public static final long Z = 4;
    public static final long OP1 = 5;

    public static final long NOT = 0;
    public static final long SHL1 = 1;
    public static final long SHR1 = 2;
    public static final long SHR4 = 3;
    public static final long SHR16 = 4;

    public static final long AND = 0;
    public static final long OR = 1;
    public static final long XOR = 2;
    public static final long PLUS = 3;

    public static final long IF0 = 0;
    public static final long FOLD = 1;

    private static class Adder {
        private final int size;
        private final long[] ops;
        private final ArrayListLong permuts;

        public Adder (int size, long[] ops) {
            this.size = size;
            this.ops = ops;
            this.permuts = new ArrayListLong();
        }

        public void add (int[] arr) {
            long res = 0;
            for (int i = 0; i < size; i++) {
                res = (res << NODEBITS) ^ ops[arr[i]];
            }
            permuts.add(res);
        }

        public long[] get () {
            return permuts.toArray();
        }
    }

    private static void getPermutations (Adder add, int size, int ops_count) {
        int[] ops = new int[size];
        Arrays.fill(ops, -1);
        int[] ops_pos = new int[ops_count];
        Arrays.fill(ops_pos, -1);
        int ops_used = 0;
        int i = 0;
        while (true) {
            if (i < 0) {
                break;
            } else if (i == size) {
                add.add(ops);
                i--;
            } else {
                if (ops[i] != -1 && ops_pos[ops[i]] == i) {
                    ops_pos[ops[i]] = -1;
                    ops_used--;
                }
                ops[i]++;
                if (ops[i] == ops_count) {
                    ops[i] = -1;
                    i--;
                } else {
                    if (ops_pos[ops[i]] == -1) {
                        ops_pos[ops[i]] = i;
                        ops_used++;
                    }
                    if ((ops_count - ops_used) < (size - i)) {
                        i++;
                    }
                }
            }
        }
    }

    public static long genAllProgs (long t, String[] operators) {
        return genAllProgs(t, new ArrayList<String>(Arrays.asList(operators)));
    }

    public static long genAllProgs (long t, ArrayList<String> operators) {
        int[] op1s = new int[Op1.OpName.total.ordinal()];
        int[] op2s = new int[Op2.OpName.total.ordinal()];
        int[] op3s = new int[Op3.OpName.total.ordinal()];
        int op1count = 0;
        int op2count = 0;
        int op3count = 0;
        for (String s : operators) {
            try {
                Op1.OpName op = Op1.OpName.valueOf(s);
                op1s[op.ordinal()] = 1;
                op1count++;
            } catch (IllegalArgumentException e) {
                // System.out.println("op1 "+s);
            }
            try {
                Op2.OpName op = Op2.OpName.valueOf(s);
                op2s[op.ordinal()] = 1;
                op2count++;
            } catch (IllegalArgumentException e) {
                // System.out.println("op2 "+s);
            }
            try {
                Op3.OpName op = Op3.OpName.valueOf(s);
                op3s[op.ordinal()] = 1;
                op3count++;
            } catch (IllegalArgumentException e) {
                // System.out.println("op2 "+s);
            }
        }

        int un = Tree.un(t);
        int bi = Tree.bi(t);
        int te = Tree.te(t);

        ArrayList<Op3.OpName[]> tesarr = new ArrayList<Op3.OpName[]>();
        Op3.OpName[] tes = new Op3.OpName[te];
        for (int i = 0; i < tes.length; i++) {
            tes[i] = Op3.OpName.if0;
        }
        if (op3s[Op3.OpName.fold.ordinal()] == 1) {
            for (int i = 0; i < tes.length; i++) {
                Op3.OpName[] tes0 = tes.clone();
                tesarr.add(tes0);
            }
        } else {
            if (op3s[Op3.OpName.tfold.ordinal()] == 1) {
                tes[0] = Op3.OpName.tfold;
            }
            tesarr.add(tes);
        }
        // System.out.println("TE=" + tesarr.size());

        long[] biops = new long[op2count];
        long[] unops = new long[op1count];

        Adder adderBi = new Adder(bi, biops);
        getPermutations(adderBi, bi, op2count);
        long[] biss = adderBi.get();
        // System.out.println("BI(" + bi + ", " + op2count + ")=" + biss.length);
        Adder adderUn = new Adder(un, unops);
        getPermutations(adderUn, un, op1count);
        long[] unss = adderUn.get();
        // System.out.println("UN(" + un + ", " + op1count + ")=" + unss.length);
        return ((long) tesarr.size()) * biss.length * unss.length;
    }

    public static void main (String[] args) {
        long start = System.nanoTime();
        long[] ts = (new Tree.Gener()).genAllProgs(16, new String[] { "fold", "if0", "plus", "shr1", "shr16", "xor" });
        long stop = System.nanoTime();
        System.out.println("Total: " + ts.length + ", time: " + ((stop - start) / 1e9));
        start = System.nanoTime();
        long res = 0;
        for (long t : ts) {
            res += genAllProgs(t, new String[] { "fold", "if0", "plus", "shr1", "shr16", "xor" });
        }
        stop = System.nanoTime();
        System.out.println("Total: " + res + ", time: " + ((stop - start) / 1e9));
        // for (long t : ts) {
        // System.out.println(toString(t));
        // }
        // System.out.println("Total: " + ts.length + ", time: " + ((stop - start) / 1e9));
    }
}
