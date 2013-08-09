import bv.*;
import java.util.HashSet;

public class Main extends Language {

	public static void main(String[] args) {
		//Program p = program(x, fold(x, zero, y, z, or(y, z)));
		//System.out.println(p+"(0x1122334455667788)="+p.run(0x1122334455667788L));
		Gener gen = new Gener();
		long start = System.nanoTime();
		HashSet<Program> sp = gen.GenProg(3);
		//for(Program p : sp) {
		//	System.out.println(p);
		//}
		long stop = System.nanoTime();
		//System.out.println("Total: "+sp.size()+", time: "+((stop-start)/1e9));
		HashSet<Long> sl = new HashSet<Long>();
		sl.add(0L);sl.add(1L<<1);sl.add(1L<<2);sl.add(1L<<4);sl.add(1L<<15);
		start = System.nanoTime();
		System.out.print("FUNCTION \t");
		for(long l : sl) {
			System.out.print("0x"+Long.toHexString(l)+" ");
		}
		System.out.println();
		for(Program p : sp) {
			System.out.print(p+"\t");
			for(long l : sl) {
				System.out.print("0x"+Long.toHexString(p.run(l))+" ");
			}
			System.out.println();
		}
		stop = System.nanoTime();
		//System.out.println("Total: "+sp.size()+", time: "+((stop-start)/1e9));

		(new Solver()).solve();
	}
}
