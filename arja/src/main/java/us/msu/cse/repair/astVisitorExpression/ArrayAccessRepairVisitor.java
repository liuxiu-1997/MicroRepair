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

public class ArrayAccessRepairVisitor extends ASTVisitorPlus {
    private ModificationPoint mp = null;
    private volatile boolean isRepaired = false;
    private volatile boolean statementFlag = false;
    private List<ExpressionInfo> expressionInfoList = null;
    private Statement s = null;

    public ArrayAccessRepairVisitor(ModificationPoint mp) {
        this.mp = mp;
        expressionInfoList = mp.getExpressionInfosIngredients();
    }

    @Override
    public boolean visit(ArrayAccess node) {
        if ((!isRepaired)) {
            ExpressionInfo expressionInfo = TypeInformation.getArrayAccessTypeInfo(node);
            if (expressionInfo == null)
                expressionInfo = TypeInformation.getSourceVariable(mp.getSourceFilePath(), node.getArray().toString());
            if (expressionInfo != null) {
                for (ExpressionInfo e : expressionInfoList) {
                    if ((e.getVarType() instanceof ArrayType)&&(expressionInfo.getVarType() instanceof ArrayType )) {
                        ArrayType t1 = (ArrayType) e.getVarType();
                        ArrayType t2 = (ArrayType) expressionInfo.getVarType();
                        String t1S = t1.getElementType().toString();
                        String t2S = t2.getElementType().toString();
                        if ((t1.toString().equals(t2.toString())) && (t1S.equals(t2S)) && (!(TemplateBoolean.templateBooleanCheck(mp, e.getExpressionStr() + "aa")))) {
                            Expression expression = (Expression) ASTNode.copySubtree(node.getAST(), e.getExpression());
                            node.setArray(expression);
                            mp.getTemplateBoolean().put(e.getExpression().toString() + "aa", true);
                            isRepaired = true;
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
