import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import bv.*;

public class GenerPrograms extends Language {
    enum GenType {
        ordinary, fold, tfold, yz;
    }

    HashSet<Op1.OpName> op1s;
    HashSet<Op2.OpName> op2s;
    boolean hasIf0;
    GenType genType; 

    HashMap<GenType, HashMap<Integer, ArrayList<Expression>>> expmap;

    public GenerPrograms (ArrayList<String> operators, boolean hasYZ) {
        op1s = new HashSet<Op1.OpName>();
        op2s = new HashSet<Op2.OpName>();
        for (String s : operators) {
            try {
                op1s.add(Op1.OpName.valueOf(s));
            } catch (IllegalArgumentException e) {
                System.out.println("op1 "+s);
            }
            try {
                op2s.add(Op2.OpName.valueOf(s));
            } catch (IllegalArgumentException e) {
                System.out.println("op2 "+s);
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

    public ArrayList<Program> genAllProg (int size) {
        int cap = 0;
        for (int i = 1; i <= size; i++) {
            cap += genProg(i).size();
        }
        ArrayList<Program> allprogset = new ArrayList<Program>(cap);
        for (int i = 1; i <= size; i++) {
            allprogset.addAll(genProg(i));
        }
        return allprogset;
    }

    public ArrayList<Program> genProg (int size) {
        ArrayList<Program> progset = new ArrayList<Program>(genExp(GenType.ordinary, size - 1).size());
        for (Expression exp : genExp(genType, size - 1)) {
            progset.add(program(exp));
        }
        return progset;
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

    private ArrayList<Expression> genExp (GenType gt, int size) {
        ArrayList<Expression> expset = expmap.get(gt).get(size);
        if (expset != null) {
            return expset;
        }
        //System.out.println("Start genExp(" + gt + ", " + size + ")");
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
                for (Expression exp : genExp(gt, size - 1)) {
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
                            for (Expression exp1 : genExp(gt, i)) {
                                if (!isConst(exp1, 0)) {
                                    for (Expression exp2 : genExp(gt, j)) {
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
                            ArrayList<Expression> expset1 = genExp(gt, i);
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
                        for (Expression exp1 : genExp(GenType.fold, i)) {
                            if (!isConst(exp1, 0)) {
                                for (Expression exp2 : genExp(GenType.ordinary, j)) {
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
                            for (Expression exp1 : genExp(gt, i)) {
                                if (exp1.hasX || exp1.hasYZ) {
                                    for (Expression exp2 : genExp(gt, j)) {
                                        for (Expression exp3 : genExp(gt, k)) {
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
                                for (Expression exp1 : genExp(gts[0], i)) {
                                    if (exp1.hasX || exp1.hasYZ) {
                                        for (Expression exp2 : genExp(gts[1], j)) {
                                            for (Expression exp3 : genExp(gts[2], k)) {
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
                    for (Expression exp1 : genExp(GenType.ordinary, i)) {
                        for (Expression exp2 : genExp(GenType.ordinary, j)) {
                            for (Expression exp3 : genExp(GenType.yz, k)) {
                                expset.add(fold(exp1, exp2, exp3));
                            }
                        }
                    }
                }
            }
        }
        expmap.get(gt).put(size, expset);
        //System.out.println("Stop genExp(" + gt + ", " + size + ")="+expset.size());
        return expset;
    }

    public static void main (String[] args) {
        ArrayList<String> al = new ArrayList<String>();
        al.add("not");
        al.add("and");
        al.add("fold");
        GenerPrograms gen = new GenerPrograms(al, false);
        long start = System.nanoTime();
        ArrayList<Program> sp = gen.genAllProg(10);
        long stop = System.nanoTime();
        //for (Program p : sp) {
        //    System.out.println(p);
        //}
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
