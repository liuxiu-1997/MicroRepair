package us.msu.cse.repair.astVisitorExpression;

import org.eclipse.jdt.core.dom.*;

import java.util.List;

public class GetStatementFromText extends ASTVisitor {
    volatile Statement statement = null;

    @Override
    public void endVisit(Block node) {
        ASTNode cur = node;
        while (!(cur.getParent() instanceof TypeDeclaration)) {
            cur = cur.getParent();
        }
        Initializer initializer = (Initializer) cur;
        List list = initializer.getBody().statements();
        if (list.size() >= 1) {
            statement = (Statement) list.get(0);
        }
    }

    public Statement getStatement() {
        return statement;
    }
}
