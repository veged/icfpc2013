import bv.*;

public class Main {

    public static void main(String[] args) {
        System.out.println("Hello World!");
        Var x = new Var("x");
        Var y = new Var("y");
        Var z = new Var("z");
        Program p = new Program(x, new Fold(x, Const.Zero, y, z, new Op2(Op2.OpName.or, y, z)));
        System.out.println(p+"(0x1122334455667788)="+p.run(0x1122334455667788L));
    }
}
