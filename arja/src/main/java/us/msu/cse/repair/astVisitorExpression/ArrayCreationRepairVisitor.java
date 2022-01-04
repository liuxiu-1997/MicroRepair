package us.msu.cse.repair.astVisitorExpression;

import org.eclipse.jdt.core.dom.*;
import us.msu.cse.repair.AbstractClass.ASTVisitorPlus;
import us.msu.cse.repair.core.parser.ModificationPoint;
import us.msu.cse.repair.formWorkExpression.makeFormWorkStatement;
import us.msu.cse.repair.informationExpression.ExpressionInfo;
import us.msu.cse.repair.toolsExpression.ChangeSimpleName;
import us.msu.cse.repair.toolsExpression.TemplateBoolean;

import java.util.List;

public class ArrayCreationRepairVisitor extends ASTVisitorPlus {
    private ModificationPoint mp = null;
    private volatile boolean isRepaired = false;
    private volatile boolean statementFlag = false;
    private List<ExpressionInfo> expressionInfoList = null;
    private Statement s = null;

    public ArrayCreationRepairVisitor(ModificationPoint mp) {
        this.mp = mp;
        expressionInfoList = mp.getExpressionInfosIngredients();
    }


    @Override
    public boolean visit(ArrayCreation node) {
        if ((!isRepaired)) {
            for (ExpressionInfo e : expressionInfoList) {
                if ((e.getVarType() instanceof ArrayType) && (e.getExpression() instanceof ArrayCreation)) {
                    ArrayType t1 = (ArrayType) e.getVarType();
                    ArrayType t2 = node.getType();
                    String t1S = t1.getElementType().toString();
                    String t2S = t2.getElementType().toString();
                    if ((t1.toString().equals(t2.toString())) && (t1S.equals(t2S)) && (!TemplateBoolean.templateBooleanCheck(mp, e.getExpressionStr() + "ac"))) {
                        ArrayCreation access = (ArrayCreation) e.getExpression();
                        if ((access.getInitializer() != null) && (access.dimensions().toString().equals(node.dimensions().toString()))) {
                            ArrayInitializer expression = (ArrayInitializer) ASTNode.copySubtree(node.getAST(), access.getInitializer());
                            node.setInitializer(expression);
                            isRepaired = true;
                            mp.getTemplateBoolean().put(e.getExpressionStr() + "ac", true);
                            return true;
                        }
                    }
                }
                if (!TemplateBoolean.templateBooleanCheck(mp, node.toString() + "simpleRepair")) {
                    List<Statement> statementList = ChangeSimpleName.getChangedSimpleName(mp, node.toString());
                    if (mp.getIngredients() == null) {
                        mp.setIngredients(statementList);
                    } else {
                        mp.getIngredients().addAll(statementList);
                    }
                    mp.getTemplateBoolean().put(node.toString() + "simpleRepair", true);
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
