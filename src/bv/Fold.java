package bv;

public class Fold extends Expression {
	private Expression e0;
	private Expression e1;
	private Var y;
	private Var z;
	private Expression e2;
	
	public Fold(Expression e0, Expression e1, Var y, Var z, Expression e2) {
		super(e0.hasX || e1.hasX || e2.hasX, e0.hasYZ || e1.hasYZ);
		this.e0 = e0;
		this.e1 = e1;
		this.y = y;
		this.z = z;
		this.e2 = e2;
	}

	@Override
	public long eval(Environment env) {
		long r = e0.eval(env);
		long acc = e1.eval(env);
		Environment env1 = env.clone();
		for(int i=0;i<8;i++) {
			env1.set(y, r & 0xFF);
			env1.set(z, acc);
			acc = e2.eval(env1);
			r = r>>8;
		}
		return acc;
	}

	@Override
	public String toString() {
		return "(fold "+e0+" "+e1+" (lambda ("+y+" "+z+") "+e2+"))";
	}
}
