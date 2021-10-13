package chap14;
import java.util.List;
import javassist.gluonj.*;
import stone.ast.*;
import chap11.EnvOptimizer;
import chap11.Symbols;
import chap11.EnvOptimizer.AstTreeOptEx;
import chap6.Environment;
import chap6.BasicEvaluator.AstTreeEx;

@Require(EnvOptimizer.class)
@Reviser public class TypedEvaluator {
    @Reviser public static class DefStateEx extends EnvOptimizer.DefStateEx {
        public DefStateEx(List<AstTree> c) { super(c); }
        public TypeTag type() { return (TypeTag)child(2); }
        @Override public BlockState body() { return (BlockState)child(3); }
        @Override public String toString() {
            return "(def " + name() + " " + parameters() + " " + type() + " "
                   + body() + ")";
        }
    }
    @Reviser public static class ParamListEx extends EnvOptimizer.ParamsEx {
        public ParamListEx(List<AstTree> c) { super(c); }
        @Override public String name(int i) {
            return ((AstLeaf)child(i).child(0)).token().getText();
        }
        public TypeTag typeTag(int i) {
            return (TypeTag)child(i).child(1);
        }
    }
    @Reviser public static class VarStateEx extends VarState {
        protected int index;
        public VarStateEx(List<AstTree> c) { super(c); }
        public void lookup(Symbols syms) {
            index = syms.putNew(name());
            ((AstTreeOptEx)initializer()).lookup(syms);
        }
        public Object eval(Environment env) {
            Object value = ((AstTreeEx)initializer()).eval(env);
            ((EnvOptimizer.EnvEx2)env).put(0, index, value);
            return value;
        }
    }
}
