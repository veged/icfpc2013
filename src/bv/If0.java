package bv;

import java.util.ArrayList;

public class If0 extends Expression {
	public final Expression e0;
	public final Expression e1;
	public final Expression e2;

	public If0(Expression e0, Expression e1, Expression e2) {
		super(e0, e1, e2, true);
		this.e0 = e0;
		this.e1 = e1;
		this.e2 = e2;
	}

	@Override
	public long eval() {
		return e0.eval() == 0 ? e1.eval() : e2.eval();
	}

	@Override
	public void reset_values() {
		//if (values != null) {
			values = null;
			e0.reset_values();
			e1.reset_values();
			e2.reset_values();
		//}
	}

	public void update_values(int n) {
		if (values != null) {
			return;
		}
		e0.update_values(n);
		e1.update_values(n);
		e2.update_values(n);
		values = new ArrayList<Long>(n);
		for (int i = 0; i < n; i++) {
			values.add(e0.values.get(i) == 0 ? e1.values.get(i) : e2.values.get(i));
		}
	}

	@Override
	public String toString() {
		return "(if0 " + e0 + " " + e1 + " " + e2 + ")";
	}
}
