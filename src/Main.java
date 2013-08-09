import bv.*;

public class Main extends Language {

    public static void main(String[] args) {
        Program p = program(x, fold(x, zero, y, z, or(y, z)));
        System.out.println(p+"(0x1122334455667788)="+p.run(0x1122334455667788L));
    }
}
