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

	@Override
	public void reset_values() {
		values = null;
	}

	@Override
	public void update_values(int n) {
		if (values != null) {
			return;
		}
	}

	public String toString() {
		return ""+n;
	}
}
