package bv;

public class Language {
	public static final Expression zero = new Const(0);
	public static final Expression one = new Const(1);
	public static final Var x = new Var(true, false, 'x');
	public static final Var y = new Var(false, true, 'y');
	public static final Var z = new Var(false, true, 'z');

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

	public static Expression if0(Expression e0, Expression e1, Expression e2) {
		return new If0(e0, e1, e2);
	}

	public static Expression fold(Expression e0, Expression e1, Expression e2) {
		return new Fold(e0, e1, e2);
	}

	public static Program program(Expression e) {
		return new Program(e);
	}
}
