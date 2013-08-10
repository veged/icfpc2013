package bv;

public class Var extends Expression {
	public final char n;
	public long value;

	public Var(boolean hasX, boolean hasYZ, char n) {
		super(hasX, hasYZ, false);
		this.n = n;
	}

	@Override
	public long eval() {
		return value;
	}

	public String toString() {
		return Character.toString(n);
	}
}
