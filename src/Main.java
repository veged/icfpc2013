import java.util.HashSet;

import bv.*;

public class Main extends Language {

    public static void main(String[] args) {
        Program p = program(x, fold(x, zero, y, z, or(y, z)));
        System.out.println(p+"(0x1122334455667788)="+p.run(0x1122334455667788L));
        Gener gen = new Gener();
        Environment env = new Environment();
        env.set(x, 0);
        long start = System.nanoTime();
        HashSet<Expression> sp = gen.GenExp(7);
        for(Expression e : sp) {
            //System.out.println(e);
        }
        long stop = System.nanoTime();
        System.out.println("Total: "+sp.size()+", time: "+((stop-start)/1e9));
        start = System.nanoTime();
        sp = gen.GenExpFold(7);
        for(Expression e : sp) {
            //System.out.println(e);
        }
        stop = System.nanoTime();
        System.out.println("Total: "+sp.size()+", time: "+((stop-start)/1e9));
    }
}
