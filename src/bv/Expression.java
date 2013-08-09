package bv;

public abstract class Expression {
	public final boolean hasX;
	public final boolean hasYZ;
	
	public Expression (boolean hasX, boolean hasYZ) {
		this.hasX = hasX;
		this.hasYZ = hasYZ;
	}
	
	public Expression (Expression e1) {
		this.hasX = e1.hasX;
		this.hasYZ = e1.hasYZ;
	}
	
	public Expression (Expression e1, Expression e2) {
		this.hasX = e1.hasX || e2.hasX;
		this.hasYZ = e1.hasYZ || e2.hasYZ;
	}
	
	public Expression (Expression e1, Expression e2, Expression e3) {
		this.hasX = e1.hasX || e2.hasX || e3.hasX;
		this.hasYZ = e1.hasYZ || e2.hasYZ || e3.hasYZ;
	}

	abstract long eval(Environment env);
}
