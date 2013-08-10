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
		ArrayList<Program> sp = gen.GenAllProg(10);
		//for(Program p : sp) {
		//	System.out.println(p);
		//}
		long stop = System.nanoTime();
		System.out.println("Total: "+sp.size()+", time: "+((stop-start)/1e9));
		ArrayList<Long> sl = new ArrayList<Long>();
		Random rand = new Random();
		sl.add(0L);
		sl.add(1L<<1);sl.add(1L<<2);sl.add(1L<<4);sl.add(1L<<8);sl.add(1L<<16);
		for(int i = 0; i < 1024; i++) {
			sl.add(rand.nextLong());
		}
		start = System.nanoTime();
		System.out.print("FUNCTION \t");
		for(long l : sl) {
			//System.out.print("0x"+Long.toHexString(l)+" ");
		}
		System.out.println();
		for(Program p : sp) {
			//System.out.print(p+"\t");
			//p.values = new ArrayList<Long>(sl.size());
			//for(long l : sl) {
			//	long r = p.run(l);
			//	p.values.add(r);
				//System.out.print("0x"+Long.toHexString(r)+" ");
			//}
			//p.run(sl.size());
			//for(long r : p.values) {
			//	System.out.print("0x"+Long.toHexString(r)+" ");
			//}
			//System.out.println();
		}
		stop = System.nanoTime();
		System.out.println("Total: "+sp.size()+", time: "+((stop-start)/1e9));

		//(new Solver()).solve();
	}
}
