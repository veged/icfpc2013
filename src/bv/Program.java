package bv;

public class Program {
	public final Expression e;

	public Program(Expression e) {
		this.e = e;
	}

	public long run(long value) {
		Language.x.value = value;
		return e.eval();
	}

	public String toString() {
		return "(lambda (" + Language.x + ") " + e + ")";
	}
}
