package bv;

public class If0 extends Expression {
	private Expression e0;
	private Expression e1;
	private Expression e2;
	
	public If0(Expression e0,  Expression e1, Expression e2) {
		super(e0, e1, e2);
		this.e0 = e0;
		this.e1 = e1;
		this.e2 = e2;
	}

	@Override
	public long eval(Environment env) {
		return e0.eval(env) == 0 ? e1.eval(env) : e2.eval(env); 
	}

	@Override
	public String toString() {
		return "(if0 "+e0+" "+e1+" "+e2+")";
	}
}
