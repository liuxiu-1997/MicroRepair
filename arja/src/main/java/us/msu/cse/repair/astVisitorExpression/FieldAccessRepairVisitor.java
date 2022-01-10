package us.msu.cse.repair.astVisitorExpression;

import org.eclipse.jdt.core.dom.*;
import us.msu.cse.repair.AbstractClass.ASTVisitorPlus;
import us.msu.cse.repair.core.parser.ModificationPoint;
import us.msu.cse.repair.formWorkExpression.makeFormWorkStatement;
import us.msu.cse.repair.informationExpression.ExpressionInfo;
import us.msu.cse.repair.toolsExpression.ChangeSimpleName;
import us.msu.cse.repair.toolsExpression.TemplateBoolean;
import us.msu.cse.repair.toolsExpression.TypeInformation;

import java.util.List;

public class FieldAccessRepairVisitor extends ASTVisitorPlus {
    private ModificationPoint mp = null;
    private volatile boolean isRepaired = false;
    private volatile boolean statementFlag = false;
    private List<ExpressionInfo> expressionInfoList = null;
    private Statement s = null;

    public FieldAccessRepairVisitor(ModificationPoint mp) {
        this.mp = mp;
        expressionInfoList = mp.getExpressionInfosIngredients();
    }

    @Override
    public boolean visit(FieldAccess node) {
        if (!mp.isRepair()) {
            ExpressionInfo expressionInfo = TypeInformation.getTypeInformation(node.getName(), mp);
            if (expressionInfo == null)
                expressionInfo = TypeInformation.getSourceVariable(mp.getSourceFilePath(), node.getName().toString());
            if (expressionInfo != null) {
                if (expressionInfo.getVarType() != null) {
                    Type typeAssign = expressionInfo.getVarType();
                    for (ExpressionInfo e : expressionInfoList) {
                        if ((e.getVarType() != null) && (e.getExpression() instanceof Name) && (typeAssign.toString().equals(e.getVarType().toString()))
                                && (!(TemplateBoolean.templateBooleanCheck(mp, e.getExpressionStr() + "field")))) {
                            SimpleName s = (SimpleName) ASTNode.copySubtree(node.getAST(), e.getExpression());
                            node.setName(s);
                            mp.getTemplateBoolean().put(e.getExpressionStr() + "field", true);
                            mp.setRepair(true);
                            return true;
                        }
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
