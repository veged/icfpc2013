import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import bv.*;

public class GenerPrograms extends Language {
    enum GenType {
        ordinary, fold, tfold, yz;
    }

    private HashSet<Op1.OpName> op1s;
    private HashSet<Op2.OpName> op2s;
    private boolean hasIf0;
    private GenType genType;

    HashMap<GenType, HashMap<Integer, ArrayList<Expression>>> expmap;

    public GenerPrograms (ArrayList<String> operators) {
        op1s = new HashSet<Op1.OpName>();
        op2s = new HashSet<Op2.OpName>();
        for (String s : operators) {
            try {
                op1s.add(Op1.OpName.valueOf(s));
            } catch (IllegalArgumentException e) {
                // System.out.println("op1 "+s);
            }
            try {
                op2s.add(Op2.OpName.valueOf(s));
            } catch (IllegalArgumentException e) {
                // System.out.println("op2 "+s);
            }
        }
        hasIf0 = operators.contains("if0");

        genType = GenType.ordinary;
        if (operators.contains("fold")) {
            genType = GenType.fold;
        }
        if (operators.contains("tfold")) {
            genType = GenType.tfold;
        }

        expmap = new HashMap<GenType, HashMap<Integer, ArrayList<Expression>>>();
        expmap.put(GenType.ordinary, new HashMap<Integer, ArrayList<Expression>>());
        expmap.put(GenType.fold, new HashMap<Integer, ArrayList<Expression>>());
        expmap.put(GenType.tfold, new HashMap<Integer, ArrayList<Expression>>());
        expmap.put(GenType.yz, new HashMap<Integer, ArrayList<Expression>>());
    }

    public ArrayList<Program> genAllProgs (int size) {
        ArrayList<Program> allprogset = new ArrayList<Program>(genAllExps(genType, size - 1).size());
        for (Expression exp : genAllExps(genType, size - 1)) {
            allprogset.add(program(exp));
        }
        return allprogset;
    }

    public ArrayList<Program> genProgs (int size) {
        ArrayList<Program> progset = new ArrayList<Program>(genExps(genType, size - 1).size());
        for (Expression exp : genExps(genType, size - 1)) {
            progset.add(program(exp));
        }
        return progset;
    }

    private ArrayList<Expression> genAllExps (GenType gt, int size) {
        int cap = 0;
        for (int i = 1; i <= size; i++) {
            cap += genExps(gt, i).size();
        }
        ArrayList<Expression> allexpset = new ArrayList<Expression>(cap);
        for (int i = 1; i <= size; i++) {
            allexpset.addAll(genExps(gt, i));
        }
        return allexpset;
    }

    private static boolean isOp1 (Expression exp, Op1.OpName name) {
        return exp instanceof Op1 && ((Op1) exp).op == name;
    }

    private static boolean isConst (Expression exp, long c) {
        return exp instanceof Const && ((Const) exp).c == c;
    }

    private void addOp1 (ArrayList<Expression> expset, Op1.OpName op, Expression exp) {
        if (op1s.contains(op)) {
            expset.add(op1(op, exp));
        }
    }

    private void addOp2 (ArrayList<Expression> expset, Op2.OpName op, Expression exp1, Expression exp2) {
        if (op2s.contains(op)) {
            expset.add(op2(op, exp1, exp2));
        }
    }

    private ArrayList<Expression> genExps (GenType gt, int size) {
        ArrayList<Expression> expset = expmap.get(gt).get(size);
        if (expset != null) {
            return expset;
        }
        if (size > 5 && genExps(gt, size - 1).size() > 50000000) {
            throw new Error("genExps: Too many results! gt=" + gt + ", size=" + size + "!");
        }
        // System.out.println("Start genExp(" + gt + ", " + size + ")");
        expset = new ArrayList<Expression>();
        if (gt != GenType.tfold) {
            if (size == 1 && gt != GenType.fold) {
                expset.add(zero);
                expset.add(one);
                expset.add(x);
                if (gt == GenType.yz) {
                    expset.add(y);
                    expset.add(z);
                }
            }
            if (size >= 2) {
                for (Expression exp : genExps(gt, size - 1)) {
                    if (!isOp1(exp, Op1.OpName.not)) {
                        addOp1(expset, Op1.OpName.not, exp);
                    }
                    if (!isConst(exp, 0)) {
                        addOp1(expset, Op1.OpName.shl1, exp);
                        if (!isConst(exp, 1)) {
                            addOp1(expset, Op1.OpName.shr1, exp);
                            if (!isOp1(exp, Op1.OpName.shr1)) {
                                addOp1(expset, Op1.OpName.shr4, exp);
                            }
                            if (!(isOp1(exp, Op1.OpName.shr1) || isOp1(exp, Op1.OpName.shr4))) {
                                addOp1(expset, Op1.OpName.shr16, exp);
                            }
                        }
                    }
                }
            }
            if (size >= 3) {
                for (int i = 1; i < size - 1; i++) {
                    int j = size - 1 - i;
                    if (gt != GenType.fold) {
                        if (i < j) {
                            for (Expression exp1 : genExps(gt, i)) {
                                if (!isConst(exp1, 0)) {
                                    for (Expression exp2 : genExps(gt, j)) {
                                        if (!isConst(exp2, 0)) {
                                            if (exp1 != exp2) {
                                                addOp2(expset, Op2.OpName.and, exp1, exp2);
                                                addOp2(expset, Op2.OpName.or, exp1, exp2);
                                                addOp2(expset, Op2.OpName.xor, exp1, exp2);
                                                addOp2(expset, Op2.OpName.plus, exp1, exp2);
                                            }
                                        }
                                    }
                                }
                            }
                        } else if (i == j) {
                            ArrayList<Expression> expset1 = genExps(gt, i);
                            for (int k = 0; k < expset1.size(); k++) {
                                Expression exp1 = expset1.get(k);
                                if (!isConst(exp1, 0)) {
                                    for (int l = 0; l <= k; l++) {
                                        Expression exp2 = expset1.get(l);
                                        if (!isConst(exp2, 0)) {
                                            if (exp1 != exp2) {
                                                addOp2(expset, Op2.OpName.and, exp1, exp2);
                                                addOp2(expset, Op2.OpName.or, exp1, exp2);
                                                addOp2(expset, Op2.OpName.xor, exp1, exp2);
                                                addOp2(expset, Op2.OpName.plus, exp1, exp2);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        for (Expression exp1 : genExps(GenType.fold, i)) {
                            if (!isConst(exp1, 0)) {
                                for (Expression exp2 : genExps(GenType.ordinary, j)) {
                                    if (!isConst(exp2, 0)) {
                                        if (exp1 != exp2) {
                                            addOp2(expset, Op2.OpName.and, exp1, exp2);
                                            addOp2(expset, Op2.OpName.or, exp1, exp2);
                                            addOp2(expset, Op2.OpName.xor, exp1, exp2);
                                            addOp2(expset, Op2.OpName.plus, exp1, exp2);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (size >= 4 && hasIf0) {
                for (int i = 1; i < size - 1; i++) {
                    for (int j = 1; j < size - 1 - i; j++) {
                        int k = size - 1 - i - j;
                        if (gt != GenType.fold) {
                            for (Expression exp1 : genExps(gt, i)) {
                                if (exp1.hasX || exp1.hasYZ) {
                                    for (Expression exp2 : genExps(gt, j)) {
                                        for (Expression exp3 : genExps(gt, k)) {
                                            if (exp2 != exp3) {
                                                expset.add(if0(exp1, exp2, exp3));
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            GenType[] gts = new GenType[3];
                            for (int t = 0; t < 3; t++) {
                                gts[0] = GenType.ordinary;
                                gts[1] = GenType.ordinary;
                                gts[2] = GenType.ordinary;
                                gts[t] = GenType.fold;
                                for (Expression exp1 : genExps(gts[0], i)) {
                                    if (exp1.hasX || exp1.hasYZ) {
                                        for (Expression exp2 : genExps(gts[1], j)) {
                                            for (Expression exp3 : genExps(gts[2], k)) {
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
                }
            }
        }
        if (size >= 5 && (gt == GenType.fold || gt == GenType.tfold)) {
            for (int i = 1; i < size - 2; i++) {
                for (int j = 1; j < size - 2 - i; j++) {
                    int k = size - 2 - i - j;
                    for (Expression exp1 : genExps(GenType.ordinary, i)) {
                        for (Expression exp2 : genExps(GenType.ordinary, j)) {
                            for (Expression exp3 : genExps(GenType.yz, k)) {
                                expset.add(fold(exp1, exp2, exp3));
                            }
                        }
                    }
                }
            }
        }
        expmap.get(gt).put(size, expset);
        // System.out.println("Stop genExp(" + gt + ", " + size + ")="+expset.size());
        return expset;
    }

    public static ArrayList<Program> GenAllProgs (int size, String[] operators) {
        return GenAllProgs(size, new ArrayList<String>(Arrays.asList(operators)));
    }

    public static ArrayList<Program> GenAllProgs (int size, ArrayList<String> operators) {
        return (new GenerPrograms(operators)).genAllProgs(size);
    }

    public static void main (String[] args) {
        long start = System.nanoTime();
        // ArrayList<Program> sp = GenAllProgs(12, new String[] { "fold", "if0", "shl1" });
        ArrayList<Program> sp = GenAllProgs(10, new String[] { "not", "shl1", "shr1", "shr4", "shr16", "and", "or", "xor", "plus", "if0" });
        long stop = System.nanoTime();
        // for (Program p : sp) {
        // System.out.println(p);
        // }
        System.out.println("Total: " + sp.size() + ", time: " + ((stop - start) / 1e9));

        // ArrayList<Long> sl = new ArrayList<Long>();
        // Random rand = new Random();
        // sl.add(0L);
        // sl.add(1L << 1);
        // sl.add(1L << 2);
        // sl.add(1L << 4);
        // sl.add(1L << 8);
        // sl.add(1L << 16);
        // for (int i = 0; i < 1024; i++) {
        // sl.add(rand.nextLong());
        // }
        // start = System.nanoTime();
        // System.out.print("FUNCTION \t");
        // for (long l : sl) {
        // System.out.print("0x" + Long.toHexString(l) + " ");
        // }
        // System.out.println();
        // for (Program p : sp) {
        // System.out.print(p + "\t");
        // for (long l : sl) {
        // System.out.print("0x" + Long.toHexString(p.run(l)) + " ");
        // }
        // System.out.println();
        // }
        // stop = System.nanoTime();
        // System.out.println("Total: " + sp.size() + ", time: " + ((stop - start) / 1e9));
    }
}
