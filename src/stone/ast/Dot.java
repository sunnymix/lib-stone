package stone.ast;

import java.util.List;

public class Dot extends Postfix {
    public Dot(List<AstTree> c) {
        super(c);
    }

    public String name() {
        return ((AstLeaf) child(0)).token().getText();
    }

    public String toString() {
        return "." + name();
    }
}
