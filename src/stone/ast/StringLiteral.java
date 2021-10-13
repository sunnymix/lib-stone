package stone.ast;

import stone.Token;

// OK (Str)
public class StringLiteral extends AstLeaf {
    public StringLiteral(Token t) {
        super(t);
    }

    public String value() {
        return token().getText();
    }
}
