package stone.ast;

import java.util.List;

// OK
public class NegativeExpr extends AstList {
    public NegativeExpr(List<AstTree> c) {
        super(c);
    }

    public AstTree operand() {
        return child(0);
    }

    public String toString() {
        return "-" + operand();
    }
}
