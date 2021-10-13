package stone.ast;

import java.util.Iterator;
import java.util.List;

// OK
public class AstList extends AstTree {
    protected List<AstTree> children;

    public AstList(List<AstTree> list) {
        children = list;
    }

    public AstTree child(int i) {
        return children.get(i);
    }

    public int numChildren() {
        return children.size();
    }

    public Iterator<AstTree> children() {
        return children.iterator();
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append('(');
        String sep = "";
        for (AstTree t : children) {
            builder.append(sep);
            sep = " ";
            builder.append(t.toString());
        }
        return builder.append(')').toString();
    }

    public String location() {
        for (AstTree t : children) {
            String s = t.location();
            if (s != null)
                return s;
        }
        return null;
    }
}
