package bv;

public class Var implements Expression {
	private String name;
	
	public Var(String name) {
		this.name = name;
	}
	
	@Override
	public long eval(Environment env) {
		return env.get(this);
	}

	public String toString() {
		return name;
	}
}
