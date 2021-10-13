package chap7;
import stone.ast.BlockState;
import stone.ast.ParameterList;
import chap6.Environment;

public class Function {
    protected ParameterList parameters;
    protected BlockState body;
    protected Environment env;
    public Function(ParameterList parameters, BlockState body, Environment env) {
        this.parameters = parameters;
        this.body = body;
        this.env = env;
    }
    public ParameterList parameters() { return parameters; }
    public BlockState body() { return body; }
    public Environment makeEnv() { return new NestedEnv(env); }
    @Override public String toString() { return "<fun:" + hashCode() + ">"; }
}
