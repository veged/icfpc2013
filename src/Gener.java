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

	public ArrayList<Program> GenProg(int depth) {
		ArrayList<Program> progset = progmap.get(depth);
		if (progset != null) {
			return progset;
		}
		progset = new ArrayList<Program>();
		for (Expression exp : GenExp(false, depth - 1)) {
			progset.add(program(x, exp));
		}
		progmap.put(depth, progset);
		return progset;
	}

	public ArrayList<Expression> GenExp(boolean isFold, int depth) {
		ArrayList<Expression> expset = (isFold ? expmap_fold : expmap)
				.get(depth);
		if (expset != null) {
			return expset;
		}
		expset = genExp(isFold, depth);
		(isFold ? expmap_fold : expmap).put(depth, expset);
		return expset;
	}

	private static boolean isOp1(Expression exp, Op1.OpName name) {
		return exp instanceof Op1 && ((Op1) exp).op == name;
	}

	private static boolean isConst(Expression exp, long c) {
		return exp instanceof Const && ((Const) exp).c == c;
	}

	private ArrayList<Expression> genExp(boolean isFold, int depth) {
		// System.out.println("genExp(" + isFold + ", " + depth + ")");
		ArrayList<Expression> expset = new ArrayList<Expression>();
		if (depth == 1) {
			expset.add(zero);
			expset.add(one);
			expset.add(x);
			if (isFold) {
				expset.add(y);
				expset.add(z);
			}
		}
		if (depth >= 2) {
			for (Expression exp : GenExp(isFold, depth - 1)) {
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
						if (!(isOp1(exp, Op1.OpName.shr1) || isOp1(exp,
								Op1.OpName.shr4))) {
							expset.add(shr16(exp));
						}
					}
				}
			}
		}
		if (depth >= 3) {
			for (int i = 1; i < depth - 1; i++) {
				int j = depth - 1 - i;
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
		if (depth >= 4) {
			for (int i = 1; i < depth - 1; i++) {
				for (int j = 1; j < depth - 1 - i; j++) {
					int k = depth - 1 - i - j;
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
		if (depth >= 5) {
			for (int i = 1; i < depth - 2; i++) {
				for (int j = 1; j < depth - 2 - i; j++) {
					int k = depth - 2 - i - j;
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
