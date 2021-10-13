package chapB;
import java.util.Arrays;
import stone.*;
import stone.ast.*;

public class ExprParser {
    private Lexer lexer;

    public ExprParser(Lexer p) {
        lexer = p;
    }
    public AstTree expression() throws ParseException {
        AstTree left = term();
        while (isToken("+") || isToken("-")) {
            AstLeaf op = new AstLeaf(lexer.read());
            AstTree right = term();
            left = new BinaryExpr(Arrays.asList(left, op, right));
        }
        return left;
    }
    public AstTree term() throws ParseException {
        AstTree left = factor();
        while (isToken("*") || isToken("/")) {
            AstLeaf op = new AstLeaf(lexer.read());
            AstTree right = factor();
            left = new BinaryExpr(Arrays.asList(left, op, right));
        }
        return left;
    }
    public AstTree factor() throws ParseException {
        if (isToken("(")) {
            token("(");
            AstTree e = expression();
            token(")");
            return e;
        }
        else {
            Token t = lexer.read();
            if (t.isNumber()) {
                NumberLiteral n = new NumberLiteral(t);
                return n;
            }
            else
                throw new ParseException(t);
        }
    }
    void token(String name) throws ParseException {
        Token t = lexer.read();
        if (!(t.isIdentifier() && name.equals(t.getText())))
            throw new ParseException(t);
    }
    boolean isToken(String name) throws ParseException {
        Token t = lexer.peek(0);
        return t.isIdentifier() && name.equals(t.getText());
    }

    public static void main(String[] args) throws ParseException {
        Lexer lexer = new Lexer(new CodeDialog());
        ExprParser p = new ExprParser(lexer);
        AstTree t = p.expression();
        System.out.println("=> " + t);
    }
}