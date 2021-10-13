package stone.ast;
import java.util.List;

public class Arguments extends Postfix {
    public Arguments(List<AstTree> c) { super(c); }
    public int size() { return numChildren(); }
}
