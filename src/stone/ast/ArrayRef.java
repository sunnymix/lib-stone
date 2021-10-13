package stone.ast;
import java.util.List;

public class ArrayRef extends Postfix {
    public ArrayRef(List<AstTree> c) { super(c); }
    public AstTree index() { return child(0); }
    public String toString() { return "[" + index() + "]"; }
}
