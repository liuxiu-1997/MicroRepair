package us.msu.cse.repair.astVisitorExpression;

import org.eclipse.jdt.core.dom.*;
import us.msu.cse.repair.AbstractClass.ASTVisitorPlus;
import us.msu.cse.repair.core.parser.ModificationPoint;
import us.msu.cse.repair.formWorkExpression.makeFormWorkStatement;
import us.msu.cse.repair.informationExpression.ExpressionInfo;
import us.msu.cse.repair.toolsExpression.ChangeSimpleName;
import us.msu.cse.repair.toolsExpression.TemplateBoolean;

import java.util.List;

public class ConstructorInvocationRepairVisitor extends ASTVisitorPlus {
    private ModificationPoint mp = null;
    private volatile boolean isRepaired = false;
    private volatile boolean statementFlag = false;
    private List<ExpressionInfo> expressionInfoList = null;
    private Statement s = null;

    public ConstructorInvocationRepairVisitor(ModificationPoint mp) {
        this.mp = mp;
        expressionInfoList = mp.getExpressionInfosIngredients();
    }

    @Override
    public boolean visit(ConstructorInvocation node) {
        if (node.arguments() != null) {
            if ((!TemplateBoolean.templateBooleanCheck(mp, node.toString() + mp.getStatement().toString() + "cons"))) {
                mp.getTemplateBoolean().put(node.toString() + mp.getStatement().toString() + "cons", true);
                List<Statement> statementList = ChangeSimpleName.getChangedConstructor(mp, node.toString(), node.arguments());
                if (mp.getIngredients() == null) {
                    mp.setIngredients(statementList);
                } else {
                    mp.getIngredients().addAll(statementList);
                }

            }
        }
        return super.visit(node);
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
