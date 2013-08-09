package bv;

public class Program {
	private Var var;
	private Expression exp;
	static Environment env = new Environment();

	public Program(Var var, Expression exp) {
		this.var = var;
		this.exp = exp;
	}

	public long run(long value) {
		env.set(var, value);
		return exp.eval(env);
	}

	public String toString() {
		return "(lambda (" + var + ") " + exp + ")";
	}
}
