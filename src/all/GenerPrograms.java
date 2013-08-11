package all;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import bv.Const;
import bv.Expression;
import bv.Op1;
import bv.Op2;
import bv.Program;
import bv.Wildcard;

public class GenerPrograms extends GenerParams {
    private final HashMap<GenType, HashMap<Integer, ArrayList<Expression>>> expmap;
    private final HashMap<Integer, ArrayList<Expression>> metaExpmap;

    public GenerPrograms (ArrayList<String> operators) {
        super(operators);
        expmap = new HashMap<GenType, HashMap<Integer, ArrayList<Expression>>>();
        expmap.put(GenType.ordinary, new HashMap<Integer, ArrayList<Expression>>());
        expmap.put(GenType.fold, new HashMap<Integer, ArrayList<Expression>>());
        expmap.put(GenType.tfold, new HashMap<Integer, ArrayList<Expression>>());
        expmap.put(GenType.yz, new HashMap<Integer, ArrayList<Expression>>());
        metaExpmap = new HashMap<Integer, ArrayList<Expression>>();
    }

    public ArrayList<Program> genAllProgs (int size) {
        ArrayList<Program> allprogset = new ArrayList<Program>();
        for (Expression exp : genAllExps(genType, size - 1)) {
            allprogset.add(program(exp));
        }
        return allprogset;
    }

    public ArrayList<Program> genProgs (int size) {
        ArrayList<Program> progset = new ArrayList<Program>();
        for (Expression exp : genExps(genType, size - 1)) {
            progset.add(program(exp));
        }
        return progset;
    }

    private ArrayList<Expression> genAllExps (GenType gt, int size) {
        ArrayList<Expression> allexpset = new ArrayList<Expression>();
        for (int i = 1; i <= size; i++) {
            allexpset.addAll(genExps(gt, i));
        }
        return allexpset;
    }

    private static boolean isOp1 (Expression exp, Op1.OpName name) {
        return exp instanceof Op1 && ((Op1) exp).op == name;
    }

    private static boolean isOp2 (Expression exp, Op2.OpName name) {
        return exp instanceof Op2 && ((Op2) exp).op == name;
    }

    private static boolean isConst (Expression exp, long c) {
        return exp instanceof Const && ((Const) exp).c == c;
    }

    static class StopException extends Exception {
        private static final long serialVersionUID = -3795885717498941894L;

        public StopException () {
            super("Stop");
        }
    }

    private void addToList (ArrayList<Expression> expset, Expression exp) throws StopException {
        if (expset.size() < 100000000) {
            expset.add(exp);
        } else {
            System.out.println("TOO MANY EXPRESSION! --- STOP!");
            throw new StopException();
        }
    }

    private void addOp1 (ArrayList<Expression> expset, Op1.OpName op, Expression exp) throws StopException {
        if (op1s.contains(op)) {
            addToList(expset, op1(op, exp));
        }
    }

    private void addOp2 (ArrayList<Expression> expset, Op2.OpName op, Expression exp1, Expression exp2) throws StopException {
        if (op2s.contains(op) && !isOp2(exp1, op)) {
            addToList(expset, op2(op, exp1, exp2));
        }
    }

    private int maxsize = 256;

    public ArrayList<Expression> genExps (GenType gt, int size) {
        ArrayList<Expression> expset = expmap.get(gt).get(size);
        if (expset != null) {
            return expset;
        }
        // System.out.println("Start genExp(" + gt + ", " + size + ")");
        expset = new ArrayList<Expression>();
        expmap.get(gt).put(size, expset);
        if (size > maxsize) {
            return expset;
        }
        try {
            if (gt != GenType.tfold) {
                if (size == 1 && gt != GenType.fold) {
                    addToList(expset, zero);
                    addToList(expset, one);
                    addToList(expset, x);
                    if (gt == GenType.yz) {
                        addToList(expset, y);
                        addToList(expset, z);
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
                                                    addToList(expset, if0(exp1, exp2, exp3));
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
                                                        addToList(expset, if0(exp1, exp2, exp3));
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
                                    addToList(expset, fold(exp1, exp2, exp3));
                                }
                            }
                        }
                    }
                }
            }
        } catch (StopException e) {
            maxsize = size;
        }
        // System.out.println("Stop genExp(" + gt + ", " + size + ")="+expset.size());
        return expset;
    }

    public ArrayList<Expression> genMetaExps (int size) {
        ArrayList<Expression> expset = new ArrayList<Expression>();
        if (size <= Wildcard.metaSize) {
            expset.add(new Wildcard(size));
        } else {
            if (metaExpmap.containsKey(size))
                return metaExpmap.get(size);
            metaExpmap.put(size, expset);

            try {
                for (Expression exp : genMetaExps(size - 1)) {
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

                for (int i = 1; i < size - 1; i++) {
                    int j = size - 1 - i;
                    if (i < j) {
                        for (Expression exp1 : genMetaExps(i)) {
                            if (!isConst(exp1, 0)) {
                                for (Expression exp2 : genMetaExps(j)) {
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
                        ArrayList<Expression> expset1 = genMetaExps(i);
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
                }

                if (hasIf0) {
                    for (int i = 1; i < size - 1; i++) {
                        for (int j = 1; j < size - 1 - i; j++) {
                            int k = size - 1 - i - j;
                            for (Expression exp1 : genMetaExps(i)) {
                                for (Expression exp2 : genMetaExps(j)) {
                                    for (Expression exp3 : genMetaExps(k)) {
                                        if (exp2 != exp3) {
                                            expset.add(if0(exp1, exp2, exp3));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (StopException e) {

            }
        }

        return expset;
    }

    public static ArrayList<Program> GenAllProgs (int size, String[] operators) {
        return GenAllProgs(size, new ArrayList<String>(Arrays.asList(operators)));
    }

    public static ArrayList<Program> GenAllProgs (int size, ArrayList<String> operators) {
        return (new GenerPrograms(operators)).genAllProgs(size);
    }

    public static ArrayList<Expression> GenExps (int size, ArrayList<String> operators) {
        return (new GenerPrograms(operators)).genExps(GenType.ordinary, size);
    }

    public static ArrayList<Expression> GenMetaExps (int size, ArrayList<String> operators) {
        return (new GenerPrograms(operators)).genMetaExps(size);
    }

    public static void main (String[] args) {
        long start = System.nanoTime();
        // ArrayList<Program> sp = GenAllProgs(12, new String[] { "fold", "if0", "shl1" });
        ArrayList<Program> sp = GenAllProgs(10, new String[] { "not", "shl1", "shr1", "shr4", "shr16", "and", "or", "xor", "plus", "if0" });
        // ArrayList<Program> sp = GenAllProgs(14, new String[] { "fold", "not", "plus", "shl1", "shr1", "xor" });
        // ArrayList<Expression> exps = GenMetaExps(16, new ArrayList<String>(Arrays.asList(new String[] { "not", "shl1", "shr1", "shr4", "shr16", "and", "or", "xor", "plus", "if0"
        // })));
        System.out.println(sp.size());

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
