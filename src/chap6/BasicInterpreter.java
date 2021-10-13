package chap6;
import stone.*;
import stone.ast.AstTree;
import stone.ast.NullState;

public class BasicInterpreter {
    public static void main(String[] args) throws ParseException {
        run(new BasicParser(), new BasicEnv());
    }
    public static void run(BasicParser bp, Environment env)
        throws ParseException
    {
        Lexer lexer = new Lexer(new CodeDialog());
        while (lexer.peek(0) != Token.EOF) {
            AstTree t = bp.parse(lexer);
            if (!(t instanceof NullState)) {
                Object r = ((BasicEvaluator.AstTreeEx)t).eval(env);
                System.out.println("=> " + r);
            }
        }
    }
}
