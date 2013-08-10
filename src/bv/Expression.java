package bv;

public abstract class Expression {
	public final boolean hasX;
	public final boolean hasYZ;
	public final boolean hasIf0;

	public Expression(boolean hasX, boolean hasYZ, boolean hasIf0) {
		this.hasX = hasX;
		this.hasYZ = hasYZ;
		this.hasIf0 = hasIf0;
	}

	public Expression(Expression e1) {
		this.hasX = e1.hasX;
		this.hasYZ = e1.hasYZ;
		this.hasIf0 = e1.hasIf0;
	}

	public Expression(Expression e1, Expression e2) {
		this.hasX = e1.hasX || e2.hasX;
		this.hasYZ = e1.hasYZ || e2.hasYZ;
		this.hasIf0 = e1.hasIf0 || e2.hasIf0;
	}

	public Expression(Expression e1, Expression e2, Expression e3) {
		this.hasX = e1.hasX || e2.hasX || e3.hasX;
		this.hasYZ = e1.hasYZ || e2.hasYZ || e3.hasYZ;
		this.hasIf0 = e1.hasIf0 || e2.hasIf0 || e3.hasIf0;
	}

	public Expression(Expression e1, Expression e2, Expression e3, boolean hasIf0) {
		this.hasX = e1.hasX || e2.hasX || e3.hasX;
		this.hasYZ = e1.hasYZ || e2.hasYZ || e3.hasYZ;
		this.hasIf0 = hasIf0;
	}

	abstract public long eval();
}
