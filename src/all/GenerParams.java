package all;

import java.util.ArrayList;
import java.util.HashSet;

import bv.Language;
import bv.Op1;
import bv.Op2;

public class GenerParams extends Language {
    public enum GenType {
        ordinary, fold, tfold, yz
    }

    final HashSet<Op1.OpName> op1s;
    final HashSet<Op2.OpName> op2s;
    final boolean hasIf0;
    final GenType genType;

    public GenerParams (ArrayList<String> operators) {
        op1s = new HashSet<Op1.OpName>();
        op2s = new HashSet<Op2.OpName>();
        for (String s : operators) {
            try {
                op1s.add(Op1.OpName.valueOf(s));
            } catch (IllegalArgumentException e) {
                // System.out.println("op1 "+s);
            }
            try {
                op2s.add(Op2.OpName.valueOf(s));
            } catch (IllegalArgumentException e) {
                // System.out.println("op2 "+s);
            }
        }
        hasIf0 = operators.contains("if0");

        if (operators.contains("fold")) {
            genType = GenType.fold;
        } else if (operators.contains("tfold")) {
            genType = GenType.tfold;
        } else {
            genType = GenType.ordinary;
        }
    }
}
