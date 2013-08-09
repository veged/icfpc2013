import java.util.ArrayList;
import java.util.HashMap;

import bv.*;

public class Gener extends Language {
	ArrayList<Var> varset;
	HashMap<Integer, ArrayList<Program>> progmap;
	HashMap<Integer, ArrayList<Expression>> expmap;
	HashMap<Integer, ArrayList<Expression>> expmap_fold;

	public Gener() {
		varset = new ArrayList<Var>();
		varset.add(x);
		varset.add(y);
		varset.add(z);
		progmap = new HashMap<Integer, ArrayList<Program>>();
		expmap = new HashMap<Integer, ArrayList<Expression>>();
		expmap_fold = new HashMap<Integer, ArrayList<Expression>>();
	}

	public ArrayList<Program> GenAllProg(int size) {
		ArrayList<Program> allprogset = new ArrayList<Program>();
		for (int i = 1; i <= size; i++) {
			allprogset.addAll(GenProg(i));
		}
		return allprogset;
	}

	public ArrayList<Program> GenProg(int size) {
		ArrayList<Program> progset = progmap.get(size);
		if (progset != null) {
			return progset;
		}
		progset = new ArrayList<Program>();
		for (Expression exp : GenExp(false, size - 1)) {
			progset.add(program(x, exp));
		}
		progmap.put(size, progset);
		return progset;
	}

	public ArrayList<Expression> GenExp(boolean isFold, int size) {
		ArrayList<Expression> expset = (isFold ? expmap_fold : expmap).get(size);
		if (expset != null) {
			return expset;
		}
		expset = genExp(isFold, size);
		(isFold ? expmap_fold : expmap).put(size, expset);
		return expset;
	}

	private static boolean isOp1(Expression exp, Op1.OpName name) {
		return exp instanceof Op1 && ((Op1) exp).op == name;
	}

	private static boolean isConst(Expression exp, long c) {
		return exp instanceof Const && ((Const) exp).c == c;
	}

	private ArrayList<Expression> genExp(boolean isFold, int size) {
		ArrayList<Expression> expset = new ArrayList<Expression>();
		if (size == 1) {
			expset.add(zero);
			expset.add(one);
			expset.add(x);
			if (isFold) {
				expset.add(y);
				expset.add(z);
			}
		}
		if (size >= 2) {
			for (Expression exp : GenExp(isFold, size - 1)) {
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
					for (Expression exp1 : GenExp(isFold, i)) {
						if (!isConst(exp1, 0)) {
							for (Expression exp2 : GenExp(isFold, j)) {
								if (!isConst(exp2, 0)) {
									if (exp1 != exp2) {
										expset.add(and(exp1, exp2));
										expset.add(or(exp1, exp2));
										expset.add(xor(exp1, exp2));
									}
									expset.add(plus(exp1, exp2));
								}
							}
						}
					}
				} else if (i == j) {
					ArrayList<Expression> expset1 = GenExp(isFold, i);
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
									}
									expset.add(plus(exp1, exp2));
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
					for (Expression exp1 : GenExp(isFold, i)) {
						if (exp1.hasX || exp1.hasYZ) {
							for (Expression exp2 : GenExp(isFold, j)) {
								for (Expression exp3 : GenExp(isFold, k)) {
									expset.add(if0(exp1, exp2, exp3));
								}
							}
						}
					}
				}
			}
		}
		if (size >= 5) {
			for (int i = 1; i < size - 2; i++) {
				for (int j = 1; j < size - 2 - i; j++) {
					int k = size - 2 - i - j;
					for (Expression exp1 : GenExp(isFold, i)) {
						for (Expression exp2 : GenExp(isFold, j)) {
							for (Expression exp3 : GenExp(true, k)) {
								expset.add(fold(exp1, exp2, y, z, exp3));
							}
						}
					}
				}
			}
		}
		return expset;
	}
}
