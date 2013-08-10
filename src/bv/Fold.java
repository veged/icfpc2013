package bv;

import java.util.ArrayList;

public class Fold extends Expression {
	public final Expression e0;
	public final Expression e1;
	public final Expression e2;

	public Fold(Expression e0, Expression e1, Expression e2) {
		super(e0.hasX || e1.hasX || e2.hasX, e0.hasYZ || e1.hasYZ, e0.hasIf0 || e1.hasIf0 || e2.hasIf0);
		this.e0 = e0;
		this.e1 = e1;
		this.e2 = e2;
	}

	@Override
	public long eval() {
		long r = e0.eval();
		long acc = e1.eval();
		for (int i = 0; i < 8; i++) {
			Language.y.value = r & 0xFF;
			Language.z.value = acc;
			acc = e2.eval();
			r = r >> 8;
		}
		return acc;
	}

	@Override
	public void reset_values() {
		//if (values != null) {
			values = null;
			e0.reset_values();
			e1.reset_values();
			Language.y.reset_values();
			Language.z.reset_values();
			e2.reset_values();
		//}
	}

	public void update_values(int n) {
		if (values != null) {
			return;
		}
		e0.update_values(n);
		e1.update_values(n);
		values = new ArrayList<Long>(n);
		for (int j = 0; j < n; j++) {
			long r = e0.values.get(j);
			long acc = e1.values.get(j);
			Language.x.value = Language.x.values.get(j);
			for (int i = 0; i < 8; i++) {
				Language.y.value = r & 0xFF;
				Language.z.value = acc;
				acc = e2.eval();
				r = r >> 8;
			}
			values.add(acc);
		}
	}

	@Override
	public String toString() {
		return "(fold " + e0 + " " + e1 + " (lambda (" + Language.y + " " + Language.z + ") " + e2 + "))";
	}
}
