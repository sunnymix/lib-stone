package stone.ast;

import stone.Token;

import java.util.ArrayList;
import java.util.Iterator;

// OK
public class AstLeaf extends AstTree {
    private static ArrayList<AstTree> empty = new ArrayList<AstTree>();
    protected Token token;

    public AstLeaf(Token t) {
        token = t;
    }

    public AstTree child(int i) {
        throw new IndexOutOfBoundsException();
    }

    public int numChildren() {
        return 0;
    }

    public Iterator<AstTree> children() {
        return empty.iterator();
    }

    public String toString() {
        return token.getText();
    }

    public String location() {
        return "at line " + token.getLineNumber();
    }

    public Token token() {
        return token;
    }
}
