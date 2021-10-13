package stone.ast;
import java.util.List;

public class ArrayLiteral extends AstList {
    public ArrayLiteral(List<AstTree> list) { super(list); }
    public int size() { return numChildren(); }
}
