package stone.ast;

import stone.Token;

// OK
public class Name extends AstLeaf {
    public Name(Token t) {
        super(t);
    }

    public String name() {
        return token().getText();
    }
}
