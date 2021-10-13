package stone.ast;
import java.util.List;

public class TypeTag extends AstList {
    public static final String UNDEF = "<Undef>";
    public TypeTag(List<AstTree> c) { super(c); }
    public String type() {
        if (numChildren() > 0)
            return ((AstLeaf)child(0)).token().getText();
        else
            return UNDEF;
    }
    public String toString() { return ":" + type(); }
}
