package bv;

public class Fold implements Expression {
	private Expression e0;
	private Expression e1;
	private Var x;
	private Var y;
	private Expression e2;
	
	public Fold(Expression e0, Expression e1, Var x, Var y, Expression e2) {
		this.e0 = e0;
		this.e1 = e1;
		this.x = x;
		this.y = y;
		this.e2 = e2;
	}

	@Override
	public long eval(Environment env) {
		long r = e0.eval(env);
		long acc = e1.eval(env);
		Environment env1 = env.clone();
		for(int i=0;i<8;i++) {
			env1.set(x, r & 0xFF);
			env1.set(y, acc);
			acc = e2.eval(env1);
			r = r>>8;
		}
		return acc;
	}

	@Override
	public String toString() {
		return "(fold "+e0+" "+e1+" (lambda ("+x+" "+y+") "+e2+"))";
	}
}
