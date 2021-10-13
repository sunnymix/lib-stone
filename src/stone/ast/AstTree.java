package stone.ast;

import java.util.Iterator;

// OK
public abstract class AstTree implements Iterable<AstTree> {
    public abstract AstTree child(int i);

    public abstract int numChildren();

    public abstract Iterator<AstTree> children();

    public abstract String location();

    public Iterator<AstTree> iterator() {
        return children();
    }
}
