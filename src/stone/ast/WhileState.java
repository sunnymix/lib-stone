package stone.ast;

import java.util.List;

// OK
public class WhileState extends AstList {
    public WhileState(List<AstTree> c) {
        super(c);
    }

    public AstTree condition() {
        return child(0);
    }

    public AstTree body() {
        return child(1);
    }

    public String toString() {
        return "(while " + condition() + " " + body() + ")";
    }
}
