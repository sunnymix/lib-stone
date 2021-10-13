package stone.ast;
import java.util.List;

public class ParameterList extends AstList {
    public ParameterList(List<AstTree> c) { super(c); }
    public String name(int i) { return ((AstLeaf)child(i)).token().getText(); }
    public int size() { return numChildren(); }
}
