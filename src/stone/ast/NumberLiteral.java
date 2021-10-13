package stone.ast;

import stone.Token;

// OK (Num)
public class NumberLiteral extends AstLeaf {
    public NumberLiteral(Token t) {
        super(t);
    }

    public int value() {
        return token().getNumber();
    }
}
