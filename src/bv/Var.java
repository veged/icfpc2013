package bv;

public class Var extends Expression {
	public final String name;
	
	public Var(boolean hasX, boolean hasYZ, String name) {
		super(hasX, hasYZ); 
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
