package us.msu.cse.repair.astVisitorExpression;

import org.eclipse.jdt.core.dom.*;
import us.msu.cse.repair.informationExpression.ExpressionInfo;

import java.util.List;

public class GetVariableVisitor extends ASTVisitor {
    private String str;
    private ExpressionInfo expInfo = null;

    public GetVariableVisitor(String name, ExpressionInfo expInfo) {
        this.str = name;
        this.expInfo = expInfo;
    }

    @Override
    public boolean visit(VariableDeclarationStatement node) {
        VariableDeclarationStatement vb = (VariableDeclarationStatement) node;
        List listVar = vb.fragments();
        String strings = listVar.get(0).toString();
        int num = strings.indexOf("=");
        if (num > 0)
            strings = strings.substring(0, num);
        if (strings.equals(str)) {
            AST ast = AST.newAST(AST.JLS8);
            Name name = ast.newName(strings);
            expInfo.setExpression(name);
            expInfo.setVarType(vb.getType());
            expInfo.setVarNameStr(str);
        }
        return true;
    }
}
