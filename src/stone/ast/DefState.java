package stone.ast;

import java.util.List;

public class DefState extends AstList {
    public DefState(List<AstTree> c) {
        super(c);
    }

    public String name() {
        return ((AstLeaf) child(0)).token().getText();
    }

    public ParameterList parameters() {
        return (ParameterList) child(1);
    }

    public BlockState body() {
        return (BlockState) child(2);
    }

    public String toString() {
        return "(def " + name() + " " + parameters() + " " + body() + ")";
    }
}
