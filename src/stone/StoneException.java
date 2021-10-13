package stone;
import stone.ast.AstTree;

public class StoneException extends RuntimeException {
    public StoneException(String m) { super(m); }
    public StoneException(String m, AstTree t) {
        super(m + " " + t.location());
    }
}
