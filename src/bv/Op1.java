package bv;

import java.util.ArrayList;

public class Op1 extends Expression {
	public enum OpName {
		not, shl1, shr1, shr4, shr16
	}

	public final OpName op;
	public final Expression e;

	public Op1(Op1.OpName op, Expression e) {
		super(e);
		this.op = op;
		this.e = e;
	}

	@Override
	public long eval() {
		switch (op) {
		case not:
			return ~e.eval();
		case shl1:
			return e.eval() << 1;
		case shr1:
			return e.eval() >>> 1;
		case shr4:
			return e.eval() >>> 4;
		case shr16:
			return e.eval() >>> 16;
		default:
			return 0;
		}
	}

	@Override
	public void reset_values() {
		//if (values != null) {
			values = null;
			e.reset_values();
		//}
	}

	@Override
	public void update_values(int n) {
		if (values != null) {
			return;
		}
		e.update_values(n);
		values = new ArrayList<Long>(n);
		switch (op) {
		case not:
			for (int i = 0; i < n; i++) { values.add(~e.values.get(i)); } break;
		case shl1:
			for (int i = 0; i < n; i++) { values.add(e.values.get(i)<<1); } break;
		case shr1:
			for (int i = 0; i < n; i++) { values.add(e.values.get(i)>>>1); } break;
		case shr4:
			for (int i = 0; i < n; i++) { values.add(e.values.get(i)>>>4); } break;
		case shr16:
			for (int i = 0; i < n; i++) { values.add(e.values.get(i)>>>16); } break;
		}
	}

	public String toString() {
		return "(" + op + " " + e + ")";
	}
}
