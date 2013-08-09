package bv;

import java.util.ArrayList;

public class Const extends Expression {
	public final long c;

	public Const(long c) {
		super(false, false, false);
		this.c = c;
	}

	@Override
	public long eval() {
		return c;
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
		values = new ArrayList<Long>(n);
		for (int i = 0; i < n; i++) {
			values.add(c);
		}
	}

	public String toString() {
		return Long.toString(c);
	}
}
