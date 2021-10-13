package stone.ast;
import java.util.List;

public class Fun extends AstList {
    public Fun(List<AstTree> c) { super(c); }
    public ParameterList parameters() { return (ParameterList)child(0); }
    public BlockState body() { return (BlockState)child(1); }
    public String toString() {
        return "(fun " + parameters() + " " + body() + ")";
    }
}
