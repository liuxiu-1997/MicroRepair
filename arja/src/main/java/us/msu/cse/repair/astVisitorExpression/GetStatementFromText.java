package us.msu.cse.repair.astVisitorExpression;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.Statement;

public class GetStatementFromText extends ASTVisitor {
    Statement statement = null;

    @Override
    public boolean visit(Block node) {
        if (node.statements().size()>0)
            statement = (Statement) node.statements().get(0);
        return super.visit(node);
    }
    public Statement getStatement(){
        return statement;
    }
}
