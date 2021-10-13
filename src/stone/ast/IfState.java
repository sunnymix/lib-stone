package stone.ast;

import java.util.List;

// OK
public class IfState extends AstList {
    public IfState(List<AstTree> c) {
        super(c);
    }

    public AstTree condition() {
        return child(0);
    }

    public AstTree thenBlock() {
        return child(1);
    }

    public AstTree elseBlock() {
        return numChildren() > 2 ? child(2) : null;
    }

    public String toString() {
        return "(if " + condition() + " " + thenBlock()
                + " else " + elseBlock() + ")";
    }
}
