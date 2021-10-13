package stone.ast;

import java.util.List;

public class ClassState extends AstList {
    public ClassState(List<AstTree> c) {
        super(c);
    }

    public String name() {
        return ((AstLeaf) child(0)).token().getText();
    }

    public String superClass() {
        if (numChildren() < 3)
            return null;
        else
            return ((AstLeaf) child(1)).token().getText();
    }

    public ClassBody body() {
        return (ClassBody) child(numChildren() - 1);
    }

    public String toString() {
        String parent = superClass();
        if (parent == null)
            parent = "*";
        return "(class " + name() + " " + parent + " " + body() + ")";
    }
}
