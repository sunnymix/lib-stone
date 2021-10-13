package chap6;
import javassist.gluonj.*;
import stone.Token;
import stone.StoneException;
import stone.ast.*;
import java.util.List;

@Reviser public class BasicEvaluator {
    public static final int TRUE = 1;
    public static final int FALSE = 0;
    @Reviser public static abstract class AstTreeEx extends AstTree {
        public abstract Object eval(Environment env);
    }
    @Reviser public static class AstListEx extends AstList {
        public AstListEx(List<AstTree> c) { super(c); }
        public Object eval(Environment env) {
            throw new StoneException("cannot eval: " + toString(), this);
        }
    }
    @Reviser public static class AstLeafEx extends AstLeaf {
        public AstLeafEx(Token t) { super(t); }
        public Object eval(Environment env) {
            throw new StoneException("cannot eval: " + toString(), this);
        }
    }
    @Reviser public static class NumberEx extends NumberLiteral {
        public NumberEx(Token t) { super(t); }
        public Object eval(Environment e) { return value(); }
    }
    @Reviser public static class StringEx extends StringLiteral {
        public StringEx(Token t) { super(t); }
        public Object eval(Environment e) { return value(); }
    }
    @Reviser public static class NameEx extends Name {
        public NameEx(Token t) { super(t); }
        public Object eval(Environment env) {
            Object value = env.get(name());
            if (value == null)
                throw new StoneException("undefined name: " + name(), this);
            else
                return value;
        }
    }
    @Reviser public static class NegativeEx extends NegativeExpr {
        public NegativeEx(List<AstTree> c) { super(c); }
        public Object eval(Environment env) {
            Object v = ((AstTreeEx)operand()).eval(env);
            if (v instanceof Integer)
                return new Integer(-((Integer)v).intValue());
            else
                throw new StoneException("bad type for -", this);
        }
    }
    @Reviser public static class BinaryEx extends BinaryExpr {
        public BinaryEx(List<AstTree> c) { super(c); }
        public Object eval(Environment env) {
            String op = operator();
            if ("=".equals(op)) {
                Object right = ((AstTreeEx)right()).eval(env);
                return computeAssign(env, right);
            }
            else {
                Object left = ((AstTreeEx)left()).eval(env);
                Object right = ((AstTreeEx)right()).eval(env);
                return computeOp(left, op, right);
            }
        }
        protected Object computeAssign(Environment env, Object rvalue) {
            AstTree l = left();
            if (l instanceof Name) {
                env.put(((Name)l).name(), rvalue);
                return rvalue;
            }
            else
                throw new StoneException("bad assignment", this);
        }
        protected Object computeOp(Object left, String op, Object right) {
            if (left instanceof Integer && right instanceof Integer) {
                return computeNumber((Integer)left, op, (Integer)right);
            }
            else
                if (op.equals("+"))
                    return String.valueOf(left) + String.valueOf(right);
                else if (op.equals("==")) {
                    if (left == null)
                        return right == null ? TRUE : FALSE;
                    else
                        return left.equals(right) ? TRUE : FALSE;
                }
                else
                    throw new StoneException("bad type", this);
        }
        protected Object computeNumber(Integer left, String op, Integer right) {
            int a = left.intValue(); 
            int b = right.intValue();
            if (op.equals("+"))
                return a + b;
            else if (op.equals("-"))
                return a - b;
            else if (op.equals("*"))
                return a * b;
            else if (op.equals("/"))
                return a / b;
            else if (op.equals("%"))
                return a % b;
            else if (op.equals("=="))
                return a == b ? TRUE : FALSE;
            else if (op.equals(">"))
                return a > b ? TRUE : FALSE;
            else if (op.equals("<"))
                return a < b ? TRUE : FALSE;
            else
                throw new StoneException("bad operator", this);
        }
    }
    @Reviser public static class BlockEx extends BlockState {
        public BlockEx(List<AstTree> c) { super(c); }
        public Object eval(Environment env) {
            Object result = 0;
            for (AstTree t: this) {
                if (!(t instanceof NullState))
                    result = ((AstTreeEx)t).eval(env);
            }
            return result;
        }
    }
    @Reviser public static class IfEx extends IfState {
        public IfEx(List<AstTree> c) { super(c); }
        public Object eval(Environment env) {
            Object c = ((AstTreeEx)condition()).eval(env);
            if (c instanceof Integer && ((Integer)c).intValue() != FALSE)
                return ((AstTreeEx)thenBlock()).eval(env);
            else {
                AstTree b = elseBlock();
                if (b == null)
                    return 0;
                else
                    return ((AstTreeEx)b).eval(env);
            }
        }
    }
    @Reviser public static class WhileEx extends WhileState {
        public WhileEx(List<AstTree> c) { super(c); }
        public Object eval(Environment env) {
            Object result = 0;
            for (;;) {
                Object c = ((AstTreeEx)condition()).eval(env);
                if (c instanceof Integer && ((Integer)c).intValue() == FALSE)
                    return result;
                else
                    result = ((AstTreeEx)body()).eval(env);
            }
        }
    }
}
