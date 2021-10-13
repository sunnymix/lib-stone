package stone.ast;

import java.util.List;

// OK
public class BinaryExpr extends AstList {
    public BinaryExpr(List<AstTree> c) {
        super(c);
    }

    public AstTree left() {
        return child(0);
    }

    public String operator() {
        return ((AstLeaf) child(1)).token().getText();
    }

    public AstTree right() {
        return child(2);
    }
}
