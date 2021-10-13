package chap13;
import java.util.List;
import stone.StoneException;
import stone.Token;
import chap11.EnvOptimizer;
import chap6.Environment;
import chap6.BasicEvaluator.AstTreeEx;
import chap7.FuncEvaluator;
import javassist.gluonj.*;
import static chap13.Opcode.*;
import static javassist.gluonj.GluonJ.revise;
import stone.ast.*;

@Require(EnvOptimizer.class)
@Reviser public class VmEvaluator {
    @Reviser public static interface EnvEx3 extends EnvOptimizer.EnvEx2 {
        StoneVM stoneVM();
        Code code();
    }
    @Reviser public static abstract class AstTreeVmEx extends AstTree {
        public void compile(Code c) {}
    }
    @Reviser public static class AstListEx extends AstList {
        public AstListEx(List<AstTree> c) { super(c); }
        public void compile(Code c) {
            for (AstTree t: this)
                ((AstTreeVmEx)t).compile(c);
        }
    }
    @Reviser public static class DefStateVmEx extends EnvOptimizer.DefStateEx {
        public DefStateVmEx(List<AstTree> c) { super(c); }
        @Override public Object eval(Environment env) {
            String funcName = name();
            EnvEx3 vmenv = (EnvEx3)env;
            Code code = vmenv.code();
            int entry = code.position();
            compile(code);
            ((EnvEx3)env).putNew(funcName, new VmFunction(parameters(), body(),
                                                          env, entry));
            return funcName;
        }
        public void compile(Code c) {
            c.nextReg = 0;
            c.frameSize = size + StoneVM.SAVE_AREA_SIZE;
            c.add(SAVE);
            c.add(encodeOffset(size));
            ((AstTreeVmEx)revise(body())).compile(c);
            c.add(MOVE);
            c.add(encodeRegister(c.nextReg - 1));
            c.add(encodeOffset(0));
            c.add(RESTORE);
            c.add(encodeOffset(size));
            c.add(RETURN);
        }
    }
    @Reviser public static class ParamsEx2 extends EnvOptimizer.ParamsEx {
        public ParamsEx2(List<AstTree> c) { super(c); }
        @Override public void eval(Environment env, int index, Object value) {
            StoneVM vm = ((EnvEx3)env).stoneVM();
            vm.stack()[offsets[index]] = value;
        }
    }
    @Reviser public static class NumberEx extends NumberLiteral {
        public NumberEx(Token t) { super(t); }
        public void compile(Code c) {
            int v = value();
            if (Byte.MIN_VALUE <= v && v <= Byte.MAX_VALUE) {
                c.add(BCONST);
                c.add((byte)v);
            }
            else {
                c.add(ICONST);
                c.add(v);
            }
            c.add(encodeRegister(c.nextReg++));
        }
    }
    @Reviser public static class StringEx extends StringLiteral {
        public StringEx(Token t) { super(t); }
        public void compile(Code c) {
            int i = c.record(value());
            c.add(SCONST);
            c.add(encodeShortOffset(i));
            c.add(encodeRegister(c.nextReg++));
        }
    }
    @Reviser public static class NameEx2 extends EnvOptimizer.NameEx {
        public NameEx2(Token t) { super(t); }
        public void compile(Code c) {
            if (nest > 0) {
                c.add(GMOVE);
                c.add(encodeShortOffset(index));
                c.add(encodeRegister(c.nextReg++));
            }
            else {
                c.add(MOVE);
                c.add(encodeOffset(index));
                c.add(encodeRegister(c.nextReg++));
            }
        }
        public void compileAssign(Code c) {
            if (nest > 0) {
                c.add(GMOVE);
                c.add(encodeRegister(c.nextReg - 1));
                c.add(encodeShortOffset(index));
            }
            else {
                c.add(MOVE);
                c.add(encodeRegister(c.nextReg - 1));
                c.add(encodeOffset(index));
            }
        }
    }
    @Reviser public static class NegativeEx extends NegativeExpr {
        public NegativeEx(List<AstTree> c) { super(c); }
        public void compile(Code c) {
            ((AstTreeVmEx)operand()).compile(c);
            c.add(NEG);
            c.add(encodeRegister(c.nextReg - 1));   
        }
    }
    @Reviser public static class BinaryEx extends BinaryExpr {
        public BinaryEx(List<AstTree> c) { super(c); }
        public void compile(Code c) {
            String op = operator();
            if (op.equals("=")) {
                AstTree l = left();
                if (l instanceof Name) {
                    ((AstTreeVmEx)right()).compile(c);
                    ((NameEx2)l).compileAssign(c);
                }
                else
                    throw new StoneException("bad assignment", this);
            }
            else {
                ((AstTreeVmEx)left()).compile(c);
                ((AstTreeVmEx)right()).compile(c);
                c.add(getOpcode(op));
                c.add(encodeRegister(c.nextReg - 2));
                c.add(encodeRegister(c.nextReg - 1));
                c.nextReg--;
            }
        }
        protected byte getOpcode(String op) {
            if (op.equals("+"))
                return ADD;
            else if (op.equals("-"))
                return SUB;
            else if (op.equals("*"))
                return MUL;
            else if (op.equals("/"))
                return DIV;
            else if (op.equals("%"))
                return REM;
            else if (op.equals("=="))
                return EQUAL;
            else if (op.equals(">"))
                return MORE;
            else if (op.equals("<"))
                return LESS;
            else
                throw new StoneException("bad operator", this);
        }
    }
    @Reviser public static class PrimaryVmEx extends FuncEvaluator.PrimaryEx {
        public PrimaryVmEx(List<AstTree> c) { super(c); }
        public void compile(Code c) {
            compileSubExpr(c, 0);
        }
        public void compileSubExpr(Code c, int nest) {
            if (hasPostfix(nest)) {
                compileSubExpr(c, nest + 1);
                ((AstTreeVmEx)revise(postfix(nest))).compile(c);
            }
            else
                ((AstTreeVmEx)operand()).compile(c);
        }
    }
    @Reviser public static class ArgumentsEx extends Arguments {
        public ArgumentsEx(List<AstTree> c) { super(c); }
        public void compile(Code c) {
            int newOffset = c.frameSize;
            int numOfArgs = 0;
            for (AstTree a: this) {
                ((AstTreeVmEx)a).compile(c);
                c.add(MOVE);
                c.add(encodeRegister(--c.nextReg));
                c.add(encodeOffset(newOffset++));
                numOfArgs++;
            }
            c.add(CALL);
            c.add(encodeRegister(--c.nextReg));
            c.add(encodeOffset(numOfArgs));
            c.add(MOVE);
            c.add(encodeOffset(c.frameSize));
            c.add(encodeRegister(c.nextReg++));
        }
        public Object eval(Environment env, Object value) {
            if (!(value instanceof VmFunction))
                throw new StoneException("bad function", this);
            VmFunction func = (VmFunction)value;
            ParameterList params = func.parameters();
            if (size() != params.size())
                throw new StoneException("bad number of arguments", this);
            int num = 0;
            for (AstTree a: this)
                ((ParamsEx2)params).eval(env, num++, ((AstTreeEx)a).eval(env));
            StoneVM svm = ((EnvEx3)env).stoneVM();
            svm.run(func.entry());
            return svm.stack()[0];
        }
    }
    @Reviser public static class BlockEx extends BlockState {
        public BlockEx(List<AstTree> c) { super(c); }
        public void compile(Code c) {
            if (this.numChildren() > 0) {
                int initReg = c.nextReg;
                for (AstTree a: this) {
                    c.nextReg = initReg;
                    ((AstTreeVmEx)a).compile(c);
                }
            }
            else {
                c.add(BCONST);
                c.add((byte)0);
                c.add(encodeRegister(c.nextReg++));
            }
        }
    }
    @Reviser public static class IfEx extends IfState {
        public IfEx(List<AstTree> c) { super(c); }
        public void compile(Code c) {
            ((AstTreeVmEx)condition()).compile(c);
            int pos = c.position();
            c.add(IFZERO);
            c.add(encodeRegister(--c.nextReg));
            c.add(encodeShortOffset(0));
            int oldReg = c.nextReg;
            ((AstTreeVmEx)thenBlock()).compile(c);
            int pos2 = c.position();
            c.add(GOTO);
            c.add(encodeShortOffset(0));
            c.set(encodeShortOffset(c.position() - pos), pos + 2);
            AstTree b = elseBlock();
            c.nextReg = oldReg;
            if (b != null)
                ((AstTreeVmEx)b).compile(c);
            else {
                c.add(BCONST);
                c.add((byte)0);
                c.add(encodeRegister(c.nextReg++));
            }
            c.set(encodeShortOffset(c.position() - pos2), pos2 + 1);
        }
    }
    @Reviser public static class WhileEx extends WhileState {
        public WhileEx(List<AstTree> c) { super(c); }
        public void compile(Code c) {
            int oldReg = c.nextReg;
            c.add(BCONST);
            c.add((byte)0);
            c.add(encodeRegister(c.nextReg++));
            int pos = c.position();
            ((AstTreeVmEx)condition()).compile(c);
            int pos2 = c.position();
            c.add(IFZERO);
            c.add(encodeRegister(--c.nextReg));
            c.add(encodeShortOffset(0));
            c.nextReg = oldReg;
            ((AstTreeVmEx)body()).compile(c);
            int pos3= c.position();
            c.add(GOTO);
            c.add(encodeShortOffset(pos - pos3));
            c.set(encodeShortOffset(c.position() - pos2), pos2 + 2);
        }
    }
}
