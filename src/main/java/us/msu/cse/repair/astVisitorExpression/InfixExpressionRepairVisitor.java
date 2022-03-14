package us.msu.cse.repair.astVisitorExpression;

import org.eclipse.jdt.core.dom.*;
import us.msu.cse.repair.AbstractClass.ASTVisitorPlus;
import us.msu.cse.repair.core.parser.ModificationPoint;
import us.msu.cse.repair.informationExpression.ExpressionInfo;
import us.msu.cse.repair.toolsExpression.ChangeSimpleName;
import us.msu.cse.repair.toolsExpression.OperatorInformation;
import us.msu.cse.repair.toolsExpression.TemplateBoolean;
import us.msu.cse.repair.toolsExpression.TypeInformation;

import java.util.List;

public class InfixExpressionRepairVisitor extends ASTVisitorPlus {

    private ModificationPoint mp = null;
    private volatile boolean isRepaired = false;
    private volatile boolean statementFlag = false;
    private List<ExpressionInfo> expressionInfoList = null;
    private Statement s = null;
    public InfixExpressionRepairVisitor(ModificationPoint mp) {
        this.mp = mp;
        this.expressionInfoList = mp.getExpressionInfosIngredients();
    }
    @Override
    public boolean visit(InfixExpression node) {
        if ((!mp.isRepair())) {
            InfixExpression.Operator operator = node.getOperator();
            List<InfixExpression.Operator> listTDRPM = OperatorInformation.getTDRPM();// * / % + -
            List<InfixExpression.Operator> listLRR = OperatorInformation.getLRR();// << >> >>>
            List<InfixExpression.Operator> listLGLGEN = OperatorInformation.getLGLGEN(); //< > <= >= == !=
            List<InfixExpression.Operator> listXAOCC = OperatorInformation.getXAOCC(); //^ & | && ||
            if (listTDRPM.contains(operator)) {
                for (InfixExpression.Operator value : listTDRPM) {
                    if ((!value.equals(operator)) && (!TemplateBoolean.templateBooleanCheck(mp, value.toString() + "infix"))) {
                        node.setOperator(value);
                        mp.getTemplateBoolean().put(value.toString() + "infix", true);
                        mp.setRepair(true);
                        return true;
                    }
                }
            } else if (listLRR.contains(operator)) {
                for (InfixExpression.Operator value : listLRR) {
                    if ((!value.equals(operator)) && (!TemplateBoolean.templateBooleanCheck(mp, value.toString() + "infix"))) {
                        node.setOperator(value);
                        mp.getTemplateBoolean().put(value.toString() + "infix", true);
                        mp.setRepair(true);
                        return true;
                    }
                }
            } else if (listLGLGEN.contains(operator)) {
                for (InfixExpression.Operator value : listLGLGEN) {
                    if ((!value.equals(operator)) && (!TemplateBoolean.templateBooleanCheck(mp, value.toString() + "infix"))) {
                        node.setOperator(value);
                        mp.getTemplateBoolean().put(value.toString() + "infix", true);
                        mp.setRepair(true);
                        return true;
                    }
                }
            } else if (listXAOCC.contains(operator)) {
                for (InfixExpression.Operator value : listXAOCC) {
                    if ((!value.equals(operator)) && (!TemplateBoolean.templateBooleanCheck(mp, value.toString() + "infix"))) {
                        node.setOperator(value);
                        mp.getTemplateBoolean().put(value.toString() + "infix", true);
                        mp.setRepair(true);
                        return true;
                    }
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
