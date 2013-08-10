package bv;

import java.util.ArrayList;

public class Program {
	public final Expression e;
	public ArrayList<Long> values;

	public Program(Expression e) {
		this.e = e;
	}

	public long run(long value) {
		Language.x.value = value;
		return e.eval();
	}

	public void run(int n) {
		e.update_values(n);
		values = e.values;
	}

	public String toString() {
		return "(lambda (" + Language.x + ") " + e + ")";
	}
}
