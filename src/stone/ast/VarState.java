package stone.ast;

import java.util.List;

public class VarState extends AstList {
    public VarState(List<AstTree> c) {
        super(c);
    }

    public String name() {
        return ((AstLeaf) child(0)).token().getText();
    }

    public TypeTag type() {
        return (TypeTag) child(1);
    }

    public AstTree initializer() {
        return child(2);
    }

    public String toString() {
        return "(var " + name() + " " + type() + " " + initializer() + ")";
    }
}
