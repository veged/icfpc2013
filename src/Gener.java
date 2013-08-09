import java.util.HashMap;
import java.util.HashSet;

import bv.*;

public class Gener extends Language {
	HashSet<Var> varset;
	HashMap<Integer, HashSet<Expression>> expmap;
	HashMap<Integer, HashSet<Expression>> expmap_fold;
	
	public Gener () {
		varset = new HashSet<Var>();
		varset.add(x);
		varset.add(y);
		varset.add(z);
		expmap = new HashMap<Integer, HashSet<Expression>>();
		expmap_fold = new HashMap<Integer, HashSet<Expression>>();
	}
	
	public HashSet<Expression> GenExp (int depth) {
		HashSet<Expression> expset = expmap.get(depth);
		if (expset != null) {
			return expset;
		}
		genExp(depth);
		return expmap.get(depth);
	}

	public HashSet<Expression> GenExpFold (int depth) {
		HashSet<Expression> expset_fold = expmap_fold.get(depth);
		if (expset_fold != null) {
			return expset_fold;
		}
		genExp(depth);
		return expmap_fold.get(depth);
	}
	
	private void genExp (int depth) {
		HashSet<Expression> expset = new HashSet<Expression>();

		if (depth == 1) {
			expset.add(zero);
			expset.add(one);
			for (Var v : varset) {
				expset.add(v);
			}
		}
		if (depth >= 2) {
			for(Expression exp : GenExpFold(depth-1)) {
				expset.add(not(exp));
				expset.add(shl1(exp));
				expset.add(shr1(exp));
				expset.add(shr4(exp));
				expset.add(shr16(exp));
			}
		}
		if (depth >= 3) {
			for (int i = 1; i < depth - 1; i++) {
				int j = depth - 1 - i;
				for (Expression exp1 : GenExpFold(i)) {
					for (Expression exp2 : GenExpFold(j)) {
						expset.add(and(exp1, exp2));
						expset.add(or(exp1, exp2));
						expset.add(xor(exp1, exp2));
						expset.add(plus(exp1, exp2));
					}
				}
			}
		}
		if (depth >= 4) {
			for (int i = 1; i < depth - 1; i++) {
				for (int j = 1; j < depth - 1 - i; j++) {
					int k = depth - 1 - i - j;
					for (Expression exp1 : GenExpFold(i)) {
						for (Expression exp2 : GenExpFold(j)) {
							for (Expression exp3 : GenExpFold(k)) {
								expset.add(if0(exp1, exp2, exp3));
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
					for (Expression exp1 : GenExpFold(i)) {
						for (Expression exp2 : GenExpFold(j)) {
							for (Expression exp3 : GenExpFold(k)) {
								expset.add(fold(exp1, exp2, y, z, exp3));
							}
						}
					}
				}
			}
		}
		expmap_fold.put(depth, expset);
		HashSet<Expression> expset1 = new HashSet<Expression>();
		for(Expression exp : expset) {
			if (exp.hasYZ == false) {
				expset1.add(exp);
			} else {
				//System.out.println(exp.hasX+" "+exp.hasYZ+" "+exp);
			}
		}
		expmap.put(depth, expset1);
	}
}
