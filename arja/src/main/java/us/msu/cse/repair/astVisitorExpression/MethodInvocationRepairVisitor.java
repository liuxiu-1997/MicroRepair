package us.msu.cse.repair.astVisitorExpression;

import org.eclipse.jdt.core.dom.*;
import us.msu.cse.repair.AbstractClass.ASTVisitorPlus;
import us.msu.cse.repair.core.parser.ModificationPoint;
import us.msu.cse.repair.formWorkExpression.makeFormWorkStatement;
import us.msu.cse.repair.informationExpression.ExpressionInfo;
import us.msu.cse.repair.toolsExpression.ChangeSimpleName;
import us.msu.cse.repair.toolsExpression.TemplateBoolean;

import java.util.List;

public class MethodInvocationRepairVisitor extends ASTVisitorPlus {
    private ModificationPoint mp = null;
    private volatile boolean isRepaired = false;
    private volatile boolean statementFlag = false;
    private List<ExpressionInfo> expressionInfoList = null;
    private Statement s = null;

    public MethodInvocationRepairVisitor(ModificationPoint mp) {
        this.mp = mp;
        expressionInfoList = mp.getExpressionInfosIngredients();
    }

    @Override
    public boolean visit(MethodInvocation node) {
        if (!isRepaired) {

            for (ExpressionInfo e : expressionInfoList) {
                if (!TemplateBoolean.templateBooleanCheck(mp, mp.getStatement().toString() + e + "MethodInvocation") && (e.getExpression() instanceof Name)) {
                    Expression eMid = (Expression) ASTNode.copySubtree(node.getAST(), e.getExpression());
                    node.setExpression(eMid);
                    mp.getTemplateBoolean().put(mp.getStatement().toString() + e + "MethodInvocation", true);
                    isRepaired = true;
                    return true;
                }
            }
        }
        return false;
    }
    @Override
    public void endVisit(Block node) {
        if (!statementFlag) {
            ASTNode cur = node;
            while (!(cur.getParent() instanceof TypeDeclaration)) {
                cur = cur.getParent();
            }
            Initializer initializer = (Initializer) cur;
            s = (Statement) initializer.getBody().statements().get(0);
            statementFlag = true;
        }
    }

    public Statement getStatement() {
        return s;
    }

    public boolean isRepaired() {
        return isRepaired;
    }

    public void setRepaired(boolean flag) {
        isRepaired = flag;
    }
}
