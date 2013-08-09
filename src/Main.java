import java.util.HashSet;

import bv.*;

public class Main extends Language {

    public static void main(String[] args) {
        Program p = program(x, fold(x, zero, y, z, or(y, z)));
        System.out.println(p+"(0x1122334455667788)="+p.run(0x1122334455667788L));
        long start = System.nanoTime();
        HashSet<Program> sp = Gener.GenProgram(8);
        //for(Program p1 : sp) {
        //    System.out.println(p1);
        //}
        long stop = System.nanoTime();
        System.out.println("Total: "+sp.size()+", time: "+((stop-start)/1e9));
        start = System.nanoTime();
        for(Program p1 : sp) {
            p1.run(5);
        }
        stop = System.nanoTime();
        System.out.println("Time: "+((stop-start)/1e9));
    }
}
