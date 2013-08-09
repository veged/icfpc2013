import java.util.HashSet;

import bv.*;

public class Gener extends Language {
	public static long count = 1;
	
	public static Var GenVar () {
		Var var = new Var("x_"+Long.toString(count));
		count++;
		return var;
		
	}

	public static HashSet<Program> GenProgram (int depth) {
		Var var = GenVar();
		Environment env = new Environment();
		env.set(var, 0);
		HashSet<Expression> senexp = GenExpression(env, depth-1);
		HashSet<Program> setprog = new HashSet<Program>();
		for (Expression exp : senexp) {
			setprog.add(program(var, exp));
		}
		return setprog;
	}

	public static HashSet<Expression> GenExpression(Environment env, int depth) {
		HashSet<Expression> setexp = new HashSet<Expression>();
		if (depth == 1) {
			setexp.add(zero);
			setexp.add(one);
			for (Var v : env.map.keySet()) {
				setexp.add(v);
			}
		}
		if (depth >= 2) {
			HashSet<Expression> setexp1 = GenExpression(env, depth-1);
			for(Expression exp : setexp1) {
				setexp.add(not(exp));
				setexp.add(shl1(exp));
				setexp.add(shr1(exp));
				setexp.add(shr4(exp));
				setexp.add(shr16(exp));
			}
		}
		if (depth >= 3) {
			for (int i = 1; i < depth - 1; i++) {
				int j = depth - 1 - i;
				HashSet<Expression> setexp1 = GenExpression(env, i);
				HashSet<Expression> setexp2 = GenExpression(env, j);
				for (Expression exp1 : setexp1) {
					for (Expression exp2 : setexp2) {
						setexp.add(and(exp1, exp2));
						setexp.add(or(exp1, exp2));
						setexp.add(xor(exp1, exp2));
						setexp.add(plus(exp1, exp2));
					}
				}
			}
		}
		if (depth >= 4) {
			for (int i = 1; i < depth - 1; i++) {
				for (int j = 1; j < depth - 1 - i; j++) {
					int k = depth - 1 - i - j;
					HashSet<Expression> setexp1 = GenExpression(env, i);
					HashSet<Expression> setexp2 = GenExpression(env, j);
					HashSet<Expression> setexp3 = GenExpression(env, k);
					for (Expression exp1 : setexp1) {
						for (Expression exp2 : setexp2) {
							for (Expression exp3 : setexp3) {
								setexp.add(if0(exp1, exp2, exp3));
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
					HashSet<Expression> setexp1 = GenExpression(env, i);
					HashSet<Expression> setexp2 = GenExpression(env, j);
					Environment env1 = env.clone();
					Var var1 = GenVar();
					Var var2 = GenVar();
					env1.set(var1, 0);
					env1.set(var2, 0);
					HashSet<Expression> setexp3 = GenExpression(env1, k);
					for (Expression exp1 : setexp1) {
						for (Expression exp2 : setexp2) {
							for (Expression exp3 : setexp3) {
								setexp.add(fold(exp1, exp2, var1, var2, exp3));
							}
						}
					}
				}
			}
		}
		
		return setexp;		
	}
}
