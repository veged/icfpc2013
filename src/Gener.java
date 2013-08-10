import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import bv.Const;
import bv.Expression;
import bv.Language;
import bv.Op1;
import bv.Program;

public class Gener extends Language {
    HashMap<Integer, ArrayList<Program>> progmap;
    HashMap<Integer, ArrayList<Expression>> expmap;
    HashMap<Integer, ArrayList<Expression>> expmap_fold;

    public Gener () {
        progmap = new HashMap<Integer, ArrayList<Program>>();
        expmap = new HashMap<Integer, ArrayList<Expression>>();
        expmap_fold = new HashMap<Integer, ArrayList<Expression>>();
    }

    public ArrayList<Program> GenAllProg (int size) {
        ArrayList<Program> allprogset = new ArrayList<Program>();
        for (int i = 1; i <= size; i++) {
            allprogset.addAll(GenProg(i));
        }
        return allprogset;
    }

    public ArrayList<Program> GenAllTFoldProg (int size) {
        ArrayList<Program> allprogset = new ArrayList<Program>();
        for (int i = 1; i <= size; i++) {
            allprogset.addAll(GenTFoldProg(i));
        }
        return allprogset;
    }

    public ArrayList<Program> GenAllIfProg (int size) {
        ArrayList<Program> allprogset = new ArrayList<Program>();
        for (int i = 1; i <= size; i++) {
            allprogset.addAll(GenIfProg(i));
        }
        return allprogset;
    }

    public ArrayList<Program> GenProg (int size) {
        ArrayList<Program> progset = progmap.get(size);
        if (progset != null) {
            return progset;
        }
        progset = new ArrayList<Program>();
        for (Expression exp : GenExp(false, size - 1)) {
            progset.add(program(exp));
        }
        progmap.put(size, progset);
        return progset;
    }

    public ArrayList<Program> GenTFoldProg (int size) {
        ArrayList<Program> progset = new ArrayList<Program>();
        for (Expression exp : GenTFoldExp(size - 1)) {
            progset.add(program(exp));
        }
        return progset;
    }

    public ArrayList<Program> GenIfProg (int size) {
        ArrayList<Program> progset = new ArrayList<Program>();
        for (Expression exp : GenIfExp(size - 1)) {
            progset.add(program(exp));
        }
        return progset;
    }

    public ArrayList<Expression> GenExp (boolean hasYZ, int size) {
        ArrayList<Expression> expset = (hasYZ ? expmap_fold : expmap).get(size);
        if (expset != null) {
            return expset;
        }
        expset = genExp(hasYZ, size);
        (hasYZ ? expmap_fold : expmap).put(size, expset);
        return expset;
    }

    public ArrayList<Expression> GenTFoldExp (int size) {
        ArrayList<Expression> expset = new ArrayList<Expression>();
        if (size >= 5) {
            for (int i = 1; i < size - 2; i++) {
                for (int j = 1; j < size - 2 - i; j++) {
                    int k = size - 2 - i - j;
                    for (Expression exp1 : GenExp(false, i)) {
                        for (Expression exp2 : GenExp(false, j)) {
                            for (Expression exp3 : GenExp(true, k)) {
                                expset.add(fold(exp1, exp2, exp3));
                            }
                        }
                    }
                }
            }
        }
        return expset;
    }

    public ArrayList<Expression> GenIfExp (int size) {
        ArrayList<Expression> expset = new ArrayList<Expression>();
        int max_branch_size = 6;
        for (int i = 1; i <= max_branch_size; i++) {
            for (int j = 1; j <= Math.min(size - 2 - i, max_branch_size); j++) {
                int k = size - 1 - i - j;
                for (Expression exp1 : GenExp(false, i)) {
                    if (exp1.hasX) {
                        for (Expression exp2 : GenExp(false, j)) {
                            for (Expression exp3 : GenExp(false, k)) {
                                if (exp2 != exp3) {
                                    expset.add(if0(exp1, exp2, exp3));
                                }
                            }
                        }
                    }
                }
            }
        }
        return expset;
    }

    private static boolean isOp1 (Expression exp, Op1.OpName name) {
        return exp instanceof Op1 && ((Op1) exp).op == name;
    }

    private static boolean isConst (Expression exp, long c) {
        return exp instanceof Const && ((Const) exp).c == c;
    }

    private ArrayList<Expression> genExp (boolean hasYZ, int size) {
        ArrayList<Expression> expset = new ArrayList<Expression>();
        if (size == 1) {
            expset.add(zero);
            expset.add(one);
            expset.add(x);
            if (hasYZ) {
                expset.add(y);
                expset.add(z);
            }
        }
        if (size >= 2) {
            for (Expression exp : GenExp(hasYZ, size - 1)) {
                if (!isOp1(exp, Op1.OpName.not)) {
                    expset.add(not(exp));
                }
                if (!isConst(exp, 0)) {
                    expset.add(shl1(exp));
                    if (!isConst(exp, 1)) {
                        expset.add(shr1(exp));
                        if (!isOp1(exp, Op1.OpName.shr1)) {
                            expset.add(shr4(exp));
                        }
                        if (!(isOp1(exp, Op1.OpName.shr1) || isOp1(exp, Op1.OpName.shr4))) {
                            expset.add(shr16(exp));
                        }
                    }
                }
            }
        }
        if (size >= 3) {
            for (int i = 1; i < size - 1; i++) {
                int j = size - 1 - i;
                if (i < j) {
                    for (Expression exp1 : GenExp(hasYZ, i)) {
                        if (!isConst(exp1, 0)) {
                            for (Expression exp2 : GenExp(hasYZ, j)) {
                                if (!isConst(exp2, 0)) {
                                    if (exp1 != exp2) {
                                        expset.add(and(exp1, exp2));
                                        expset.add(or(exp1, exp2));
                                        expset.add(xor(exp1, exp2));
                                        expset.add(plus(exp1, exp2));
                                    }
                                }
                            }
                        }
                    }
                } else if (i == j) {
                    ArrayList<Expression> expset1 = GenExp(hasYZ, i);
                    for (int k = 0; k < expset1.size(); k++) {
                        Expression exp1 = expset1.get(k);
                        if (!isConst(exp1, 0)) {
                            for (int l = 0; l <= k; l++) {
                                Expression exp2 = expset1.get(l);
                                if (!isConst(exp2, 0)) {
                                    if (exp1 != exp2) {
                                        expset.add(and(exp1, exp2));
                                        expset.add(or(exp1, exp2));
                                        expset.add(xor(exp1, exp2));
                                        expset.add(plus(exp1, exp2));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (size >= 4) {
            for (int i = 1; i < size - 1; i++) {
                for (int j = 1; j < size - 1 - i; j++) {
                    int k = size - 1 - i - j;
                    for (Expression exp1 : GenExp(hasYZ, i)) {
                        if (exp1.hasX || exp1.hasYZ) {
                            for (Expression exp2 : GenExp(hasYZ, j)) {
                                for (Expression exp3 : GenExp(hasYZ, k)) {
                                    if (exp2 != exp3) {
                                        expset.add(if0(exp1, exp2, exp3));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (size >= 35) {
            for (int i = 1; i < size - 2; i++) {
                for (int j = 1; j < size - 2 - i; j++) {
                    int k = size - 2 - i - j;
                    for (Expression exp1 : GenExp(hasYZ, i)) {
                        for (Expression exp2 : GenExp(hasYZ, j)) {
                            for (Expression exp3 : GenExp(true, k)) {
                                expset.add(fold(exp1, exp2, exp3));
                            }
                        }
                    }
                }
            }
        }
        return expset;
    }

    public static void main (String[] args) {
        Gener gen = new Gener();
        long start = System.nanoTime();
        ArrayList<Program> sp = gen.GenAllProg(10);
        // for(Program p : sp) {
        // System.out.println(p);
        // }
        long stop = System.nanoTime();
        System.out.println("Total: " + sp.size() + ", time: " + ((stop - start) / 1e9));

        ArrayList<Long> sl = new ArrayList<Long>();
        Random rand = new Random();
        // sl.add(0L);
        // sl.add(1L << 1);
        // sl.add(1L << 2);
        // sl.add(1L << 4);
        // sl.add(1L << 8);
        // sl.add(1L << 16);
        for (int i = 0; i < 10; i++) {
            sl.add(rand.nextLong());
        }
        start = System.nanoTime();
        // System.out.print("FUNCTION \t");
        // for (long l : sl) {
        // System.out.print("0x" + Long.toHexString(l) + " ");
        // }
        // System.out.println();
        for (Program p : sp) {
            // System.out.print(p + "\t");
            for (long l : sl) {
                long r = p.run(l);
                // System.out.print("0x" + Long.toHexString(r) + " ");
            }
            // System.out.println();
        }
        stop = System.nanoTime();
        System.out.println("Total: " + sp.size() + ", time: " + ((stop - start) / 1e9));
    }
}
