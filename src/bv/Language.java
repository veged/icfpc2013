package bv;

public class Language {
	public static Expression zero = new Const(0);
	public static Expression one = new Const(1);
	public static Var x = new Var("x");
	public static Var y = new Var("y");
	public static Var z = new Var("z");

	public static Expression not(Expression e) {
		return new Op1(Op1.OpName.not, e);
	}
	public static Expression shl1(Expression e) {
		return new Op1(Op1.OpName.shl1, e);
	}
	public static Expression shr1(Expression e) {
		return new Op1(Op1.OpName.shr1, e);
	}
	public static Expression shr4(Expression e) {
		return new Op1(Op1.OpName.shr4, e);
	}
	public static Expression shr16(Expression e) {
		return new Op1(Op1.OpName.shr16, e);
	}

	public static Expression and(Expression e1, Expression e2) {
		return new Op2(Op2.OpName.and, e1, e2);
	}
	public static Expression or(Expression e1, Expression e2) {
		return new Op2(Op2.OpName.or, e1, e2);
	}
	public static Expression xor(Expression e1, Expression e2) {
		return new Op2(Op2.OpName.xor, e1, e2);
	}
	public static Expression plus(Expression e1, Expression e2) {
		return new Op2(Op2.OpName.plus, e1, e2);
	}

	public static Expression fold(Expression e0, Expression e1, Var x, Var y, Expression e2) {
		return new Fold(e0, e1, x, y, e2);
	}

	public static Program program(Var x, Expression e) {
		return new Program(x, e);
	}
}
