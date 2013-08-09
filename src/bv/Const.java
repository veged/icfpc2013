package bv;

public class Const implements Expression {
	private long c;

	public Const(long c) {
		this.c = c;
	}

	@Override
	public long eval(Environment env) {
		return c;
	}

	public String toString() {
		return Long.toString(c);
	}
}
