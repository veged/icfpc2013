package bv;

public class Op2 extends Expression {
	public enum OpName {
		and, or, xor, plus
	}
	
	private OpName op;
	private Expression e1;
	private Expression e2;
	
	public Op2(Op2.OpName op, Expression e1, Expression e2) {
		super(e1, e2);
		this.op = op;
		this.e1 = e1;
		this.e2 = e2;
	}

	@Override
	public long eval(Environment env) {
		switch (op) {
		case and: return e1.eval(env) & e2.eval(env);
		case or: return e1.eval(env) | e2.eval(env);
		case xor: return e1.eval(env) ^ e2.eval(env);
		case plus: return e1.eval(env) + e2.eval(env);
		default: return 0;
		}
	}

	@Override
	public String toString() {
		return "("+op+" "+e1+" "+e2+")";
	}
}
