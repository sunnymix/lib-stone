package chap10;
import java.util.List;
import javassist.gluonj.*;
import stone.ArrayParser;
import stone.StoneException;
import stone.ast.*;
import chap6.Environment;
import chap6.BasicEvaluator;
import chap6.BasicEvaluator.AstTreeEx;
import chap7.FuncEvaluator;
import chap7.FuncEvaluator.PrimaryEx;

@Require({FuncEvaluator.class, ArrayParser.class})
@Reviser public class ArrayEvaluator {
    @Reviser public static class ArrayLitEx extends ArrayLiteral {
        public ArrayLitEx(List<AstTree> list) { super(list); }
        public Object eval(Environment env) {
            int s = numChildren();
            Object[] res = new Object[s];
            int i = 0;
            for (AstTree t: this)
                res[i++] = ((AstTreeEx)t).eval(env);
            return res;
        }
    }
    @Reviser public static class ArrayRefEx extends ArrayRef {
        public ArrayRefEx(List<AstTree> c) { super(c); }
        public Object eval(Environment env, Object value) {
            if (value instanceof Object[]) {
                Object index = ((AstTreeEx)index()).eval(env);
                if (index instanceof Integer)
                    return ((Object[])value)[(Integer)index];
            }

            throw new StoneException("bad array access", this);
        }
    }
    @Reviser public static class AssignEx extends BasicEvaluator.BinaryEx {
        public AssignEx(List<AstTree> c) { super(c); }
        @Override
        protected Object computeAssign(Environment env, Object rvalue) {
            AstTree le = left();
            if (le instanceof PrimaryExpr) {
                PrimaryEx p = (PrimaryEx)le;
                if (p.hasPostfix(0) && p.postfix(0) instanceof ArrayRef) {
                    Object a = ((PrimaryEx)le).evalSubExpr(env, 1);
                    if (a instanceof Object[]) {
                        ArrayRef aref = (ArrayRef)p.postfix(0);
                        Object index = ((AstTreeEx)aref.index()).eval(env);
                        if (index instanceof Integer) {
                            ((Object[])a)[(Integer)index] = rvalue;
                            return rvalue;
                        }
                    }
                    throw new StoneException("bad array access", this);
                }
            }
            return super.computeAssign(env, rvalue);
        }
    }       
}
