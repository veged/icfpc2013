package bv;

public class Const implements Expression {
	static public Const Zero = new Const(0);
	static public Const One = new Const(1);

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
