package chap14;
import stone.ast.AstTree;

public class TypeException extends Exception {
    public TypeException(String msg, AstTree t) {
        super(msg + " " + t.location()); 
    }
}
