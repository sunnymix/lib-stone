package stone.ast;

import java.util.List;

// OK
public class PrimaryExpr extends AstList {
    public PrimaryExpr(List<AstTree> c) {
        super(c);
    }
    
    public static AstTree create(List<AstTree> c) {
        return c.size() == 1 ? c.get(0) : new PrimaryExpr(c);
    }
}
