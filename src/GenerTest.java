import bv.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class GenerTest extends Language {

	public static void main(String[] args) {
		//Program p = program(x, fold(x, zero, y, z, or(y, z)));
		//System.out.println(p+"(0x1122334455667788)="+p.run(0x1122334455667788L));
		Gener gen = new Gener();
		long start = System.nanoTime();
		ArrayList<Program> sp = gen.GenProg(9);
		//for(Program p : sp) {
		//	System.out.println(p);
		//}
		long stop = System.nanoTime();
		System.out.println("Total: "+sp.size()+", time: "+((stop-start)/1e9));
		HashSet<Long> sl = new HashSet<Long>();
		Random rand = new Random();
		for(int i = 0; i < 256; i++) {
			sl.add(rand.nextLong());
		}
		start = System.nanoTime();
		System.out.print("FUNCTION \t");
		for(long l : sl) {
			System.out.print("0x"+Long.toHexString(l)+" ");
		}
		System.out.println();
		for(Program p : sp) {
			//System.out.print(p+"\t");
			for(long l : sl) {
				long r = p.run(l);
				//System.out.print("0x"+Long.toHexString(r)+" ");
			}
			//System.out.println();
		}
		stop = System.nanoTime();
		System.out.println("Total: "+sp.size()+", time: "+((stop-start)/1e9));

		//(new Solver()).solve();
	}
}
