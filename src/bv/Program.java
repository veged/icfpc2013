package bv;

public class Program {
	private static final Environment env = new Environment();

	public final Var var;
	public final Expression exp;

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
