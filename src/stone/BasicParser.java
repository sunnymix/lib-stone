package stone;

import stone.Parser.Operators;
import stone.ast.*;

import java.util.HashSet;

import static stone.Parser.rule;

public class BasicParser {
    // OK
    HashSet<String> reserved = new HashSet<String>();

    // OK
    Operators operators = new Operators();

    // OK
    Parser expr0 = rule();

    // OK
    Parser primary = rule(PrimaryExpr.class).or(
            rule().sep("(").ast(expr0).sep(")"),
            rule().number(NumberLiteral.class),
            rule().identifier(Name.class, reserved),
            rule().string(StringLiteral.class)
    );

    // OK
    Parser factor = rule().or(
            rule(NegativeExpr.class).sep("-").ast(primary),
            primary
    );

    // OK
    Parser expr = expr0.expression(BinaryExpr.class, factor, operators);

    // OK
    Parser statement0 = rule();

    // OK
    Parser block = rule(BlockState.class)
            .sep("{").option(statement0)
            .repeat(rule().sep(";", Token.EOL).option(statement0))
            .sep("}");

    // OK
    Parser simple = rule(PrimaryExpr.class).ast(expr);

    // OK
    Parser statement = statement0.or(
            rule(IfState.class).sep("if").ast(expr).ast(block).option(rule().sep("else").ast(block)),
            rule(WhileState.class).sep("while").ast(expr).ast(block),
            simple
    );

    // OK
    Parser program = rule().or(
            statement,
            rule(NullState.class)
    ).sep(";", Token.EOL);

    // OK
    public BasicParser() {
        reserved.add(";");
        reserved.add("}");
        reserved.add(Token.EOL);

        operators.add("=", 1, Operators.RIGHT);
        operators.add("==", 2, Operators.LEFT);
        operators.add(">", 2, Operators.LEFT);
        operators.add("<", 2, Operators.LEFT);
        operators.add("+", 3, Operators.LEFT);
        operators.add("-", 3, Operators.LEFT);
        operators.add("*", 4, Operators.LEFT);
        operators.add("/", 4, Operators.LEFT);
        operators.add("%", 4, Operators.LEFT);
    }

    public AstTree parse(Lexer lexer) throws ParseException {
        return program.parse(lexer);
    }
}
