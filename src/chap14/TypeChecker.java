package chap14;
import java.util.List;
import chap7.FuncEvaluator;
import chap11.EnvOptimizer;
import stone.Token;
import static javassist.gluonj.GluonJ.revise;
import stone.ast.*;
import javassist.gluonj.*;

@Require(TypedEvaluator.class)
@Reviser public class TypeChecker {
    @Reviser public static abstract class AstTreeTypeEx extends AstTree {
        public TypeInfo typeCheck(TypeEnv tenv) throws TypeException {
            return null;
        }
    }
    @Reviser public static class NumberEx extends NumberLiteral {
        public NumberEx(Token t) { super(t); }
        public TypeInfo typeCheck(TypeEnv tenv) throws TypeException {
            return TypeInfo.INT;
        }
    }
    @Reviser public static class StringEx extends StringLiteral {
        public StringEx(Token t) { super(t); }
        public TypeInfo typeCheck(TypeEnv tenv) throws TypeException {
            return TypeInfo.STRING;
        }
    }
    @Reviser public static class NameEx2 extends EnvOptimizer.NameEx {
        protected TypeInfo type;
        public NameEx2(Token t) { super(t); }
        public TypeInfo typeCheck(TypeEnv tenv) throws TypeException {
            type = tenv.get(nest, index);
            if (type == null)
                throw new TypeException("undefined name: " + name(), this);
            else
                return type;
        }
        public TypeInfo typeCheckForAssign(TypeEnv tenv, TypeInfo valueType)
            throws TypeException
        {
            type = tenv.get(nest, index);
            if (type == null) {
                type = valueType;
                tenv.put(0, index, valueType);
                return valueType;
            }
            else {
                valueType.assertSubtypeOf(type, tenv, this);
                return type;
            }
        }
    }
    @Reviser public static class NegativeEx extends NegativeExpr {
        public NegativeEx(List<AstTree> c) { super(c); }
        public TypeInfo typeCheck(TypeEnv tenv) throws TypeException {
            TypeInfo t = ((AstTreeTypeEx)operand()).typeCheck(tenv);
            t.assertSubtypeOf(TypeInfo.INT, tenv, this);
            return TypeInfo.INT;
        }
    }
    @Reviser public static class BinaryEx extends BinaryExpr {
        protected TypeInfo leftType, rightType;
        public BinaryEx(List<AstTree> c) { super(c); }
        public TypeInfo typeCheck(TypeEnv tenv) throws TypeException {
            String op = operator();
            if ("=".equals(op))
                return typeCheckForAssign(tenv);
            else {
                leftType = ((AstTreeTypeEx)left()).typeCheck(tenv);
                rightType = ((AstTreeTypeEx)right()).typeCheck(tenv);
                if ("+".equals(op))
                    return leftType.plus(rightType, tenv);
                else if ("==".equals(op))
                    return TypeInfo.INT;
                else {
                    leftType.assertSubtypeOf(TypeInfo.INT, tenv, this);
                    rightType.assertSubtypeOf(TypeInfo.INT, tenv, this);
                    return TypeInfo.INT;
                }
            }
        }
        protected TypeInfo typeCheckForAssign(TypeEnv tenv)
            throws TypeException
        {
            rightType = ((AstTreeTypeEx)right()).typeCheck(tenv);
            AstTree le = left();
            if (le instanceof Name)
                return ((NameEx2)le).typeCheckForAssign(tenv, rightType);
            else
                throw new TypeException("bad assignment", this);
        }
    }
    @Reviser public static class BlockEx extends BlockState {
        TypeInfo type;
        public BlockEx(List<AstTree> c) { super(c); }
        public TypeInfo typeCheck(TypeEnv tenv) throws TypeException {
            type = TypeInfo.INT;
            for (AstTree t: this)
                if (!(t instanceof NullState))
                    type = ((AstTreeTypeEx)t).typeCheck(tenv);
            return type;
        }
    }
    @Reviser public static class IfEx extends IfState {
        public IfEx(List<AstTree> c) { super(c); }
        public TypeInfo typeCheck(TypeEnv tenv) throws TypeException {
            TypeInfo condType = ((AstTreeTypeEx)condition()).typeCheck(tenv);
            condType.assertSubtypeOf(TypeInfo.INT, tenv, this);
            TypeInfo thenType = ((AstTreeTypeEx)thenBlock()).typeCheck(tenv);
            TypeInfo elseType;
            AstTree elseBk = elseBlock();
            if (elseBk == null)
                elseType = TypeInfo.INT; 
            else
                elseType = ((AstTreeTypeEx)elseBk).typeCheck(tenv);
            return thenType.union(elseType, tenv);
        }
    }
    @Reviser public static class WhileEx extends WhileState {
        public WhileEx(List<AstTree> c) { super(c); }
        public TypeInfo typeCheck(TypeEnv tenv) throws TypeException {
            TypeInfo condType = ((AstTreeTypeEx)condition()).typeCheck(tenv);
            condType.assertSubtypeOf(TypeInfo.INT, tenv, this);
            TypeInfo bodyType = ((AstTreeTypeEx)body()).typeCheck(tenv);
            return bodyType.union(TypeInfo.INT, tenv);
        }
    }
    @Reviser public static class DefStateEx2 extends TypedEvaluator.DefStateEx {
        protected TypeInfo.FunctionType funcType;
        protected TypeEnv bodyEnv;
        public DefStateEx2(List<AstTree> c) { super(c); }
        public TypeInfo typeCheck(TypeEnv tenv) throws TypeException {
            TypeInfo[] params = ((ParamListEx2)parameters()).types();
            TypeInfo retType = TypeInfo.get(type());
            funcType = TypeInfo.function(retType, params);
            TypeInfo oldType = tenv.put(0, index, funcType);
            if (oldType != null)
                throw new TypeException("function redefinition: " + name(),
                                        this);
            bodyEnv = new TypeEnv(size, tenv);
            for (int i = 0; i < params.length; i++)
                bodyEnv.put(0, i, params[i]);
            TypeInfo bodyType
                = ((AstTreeTypeEx)revise(body())).typeCheck(bodyEnv);
            bodyType.assertSubtypeOf(retType, tenv, this);
            return funcType;
        }
    }
    @Reviser
    public static class ParamListEx2 extends TypedEvaluator.ParamListEx {
        public ParamListEx2(List<AstTree> c) { super(c); }
        public TypeInfo[] types() throws TypeException {
            int s = size();
            TypeInfo[] result = new TypeInfo[s];
            for (int i = 0; i < s; i++)
                result[i] = TypeInfo.get(typeTag(i));
            return result;
        }
    }
    @Reviser public static class PrimaryEx2 extends FuncEvaluator.PrimaryEx {
        public PrimaryEx2(List<AstTree> c) { super(c); }
        public TypeInfo typeCheck(TypeEnv tenv) throws TypeException {
            return typeCheck(tenv, 0);
        }
        public TypeInfo typeCheck(TypeEnv tenv, int nest) throws TypeException {
            if (hasPostfix(nest)) {
                TypeInfo target = typeCheck(tenv, nest + 1);
                return ((PostfixEx)postfix(nest)).typeCheck(tenv, target); 
            }
            else
                return ((AstTreeTypeEx)operand()).typeCheck(tenv);
        }
    }
    @Reviser public static abstract class PostfixEx extends Postfix {
        public PostfixEx(List<AstTree> c) { super(c); }
        public abstract TypeInfo typeCheck(TypeEnv tenv, TypeInfo target)
            throws TypeException;
    }
    @Reviser public static class ArgumentsEx extends Arguments {
        protected TypeInfo[] argTypes;
        protected TypeInfo.FunctionType funcType;
        public ArgumentsEx(List<AstTree> c) { super(c); }
        public TypeInfo typeCheck(TypeEnv tenv, TypeInfo target)
            throws TypeException
        {
            if (!(target instanceof TypeInfo.FunctionType))
                throw new TypeException("bad function", this);
            funcType = (TypeInfo.FunctionType)target;
            TypeInfo[] params = funcType.parameterTypes;
            if (size() != params.length)
                throw new TypeException("bad number of arguments", this);
            argTypes = new TypeInfo[params.length];
            int num = 0;
            for (AstTree a: this) {
                TypeInfo t = argTypes[num] = ((AstTreeTypeEx)a).typeCheck(tenv);
                t.assertSubtypeOf(params[num++], tenv, this);
            }
            return funcType.returnType;
        }
    }
    @Reviser public static class VarStateEx2 extends TypedEvaluator.VarStateEx {
        protected TypeInfo varType, valueType;
        public VarStateEx2(List<AstTree> c) { super(c); }
        public TypeInfo typeCheck(TypeEnv tenv) throws TypeException {
            if (tenv.get(0, index) != null)
                throw new TypeException("duplicate variable: " + name(), this);
            varType = TypeInfo.get(type());
            tenv.put(0, index, varType);
            valueType = ((AstTreeTypeEx)initializer()).typeCheck(tenv);
            valueType.assertSubtypeOf(varType, tenv, this);
            return varType;
        }
    }
}
