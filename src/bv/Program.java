package bv;

import java.util.ArrayList;

public class Program {
	public final Var x;
	public final Expression e;
	public ArrayList<Long> values;

	public Program(Var x, Expression e) {
		this.x = x;
		if (x != Language.x) {
			throw new Error("Argument of program must be x!");
		}
		this.e = e;
	}

	public long run(long value) {
		x.value = value;
		return e.eval();
	}

	public void run(int n) {
		e.update_values(n);
		values = e.values;
	}

	public String toString() {
		return "(lambda (" + x + ") " + e + ")";
	}
}
