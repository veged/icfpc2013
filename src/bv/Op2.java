package bv;

import java.util.ArrayList;

public class Op2 extends Expression {
	public enum OpName {
		and, or, xor, plus
	}

	public final OpName op;
	public final Expression e1;
	public final Expression e2;

	public Op2(Op2.OpName op, Expression e1, Expression e2) {
		super(e1, e2);
		this.op = op;
		this.e1 = e1;
		this.e2 = e2;
	}

	@Override
	public long eval() {
		switch (op) {
		case and:
			return e1.eval() & e2.eval();
		case or:
			return e1.eval() | e2.eval();
		case xor:
			return e1.eval() ^ e2.eval();
		case plus:
			return e1.eval() + e2.eval();
		default:
			return 0;
		}
	}

	@Override
	public void reset_values() {
		//if (values != null) {
			values = null;
			e1.reset_values();
			e2.reset_values();
		//}
	}

	@Override
	public void update_values(int n) {
		if (values != null) {
			return;
		}
		e1.update_values(n);
		e2.update_values(n);
		values = new ArrayList<Long>(n);
		switch (op) {
		case and:
			for (int i = 0; i < n; i++) {
				values.add(e1.values.get(i) & e2.values.get(i));
			}
			break;
		case or:
			for (int i = 0; i < n; i++) {
				values.add(e1.values.get(i) | e2.values.get(i));
			}
			break;
		case xor:
			for (int i = 0; i < n; i++) {
				values.add(e1.values.get(i) ^ e2.values.get(i));
			}
			break;
		case plus:
			for (int i = 0; i < n; i++) {
				values.add(e1.values.get(i) + e2.values.get(i));
			}
			break;
		}
	}

	@Override
	public String toString() {
		return "(" + op + " " + e1 + " " + e2 + ")";
	}
}
