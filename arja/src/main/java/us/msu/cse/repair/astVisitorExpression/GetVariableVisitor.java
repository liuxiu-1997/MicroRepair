package us.msu.cse.repair.astVisitorExpression;

import org.eclipse.jdt.core.dom.*;
import us.msu.cse.repair.informationExpression.ExpressionInfo;

public class GetVariableVisitor extends ASTVisitor {
    private String str;
    private ExpressionInfo expInfo = null;

    public GetVariableVisitor(String name, ExpressionInfo expInfo) {
        this.str = name;
        this.expInfo = expInfo;
    }

    @Override
    public boolean visit(VariableDeclarationStatement node) {
        for (Object obj : node.fragments()) {
            VariableDeclarationFragment v = (VariableDeclarationFragment) obj;
            Type varType = node.getType();
            String varName = v.getName().toString();
            if (varName.equals(str)) {
                AST ast = AST.newAST(AST.JLS8);
                Name name = ast.newName(varName);
                expInfo.setExpression(name);
                expInfo.setVarType(varType);
                expInfo.setVarNameStr(str);
            }
            return true;
        }
        return false;
    }
}
