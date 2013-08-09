package bv;

import java.util.HashMap;

public class Environment {
	private final HashMap<Var, Long> map;

	@SuppressWarnings("unchecked")
	private Environment(Environment env) {
		map = (HashMap<Var, Long>) env.map.clone();
	}

	public Environment() {
		map = new HashMap<Var, Long>(3);
	}

	public void set(Var var, long value) {
		map.put(var, value);
	}

	public long get(Var var) {
		return map.get(var);
	}

	public String toString() {
		return "";
	}

	public Environment clone() {
		return new Environment(this);
	}
}
