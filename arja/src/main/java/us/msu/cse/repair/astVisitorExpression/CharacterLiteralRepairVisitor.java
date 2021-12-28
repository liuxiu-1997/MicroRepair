package us.msu.cse.repair.astVisitorExpression;

import org.eclipse.jdt.core.dom.*;
import us.msu.cse.repair.AbstractClass.ASTVisitorPlus;
import us.msu.cse.repair.core.parser.ModificationPoint;
import us.msu.cse.repair.informationExpression.ExpressionInfo;
import us.msu.cse.repair.toolsExpression.TemplateBoolean;

import java.util.List;

public class CharacterLiteralRepairVisitor extends ASTVisitorPlus {
    private ModificationPoint mp = null;
    private volatile boolean isRepaired = false;
    private volatile boolean statementFlag = false;
    private List<ExpressionInfo> expressionInfoList = null;
    private Statement s = null;

    public CharacterLiteralRepairVisitor(ModificationPoint mp) {
        this.mp = mp;
        expressionInfoList = mp.getExpressionInfosIngredients();
    }

    @Override
    public boolean visit(CharacterLiteral node) {

        if (!isRepaired) {
            for (ExpressionInfo e : expressionInfoList) {
                if ((e.getExpression() instanceof CharacterLiteral) && (!(TemplateBoolean.templateBooleanCheck(mp, e.getExpressionStr() + "char")))) {
                    CharacterLiteral literal = (CharacterLiteral) e.getExpression();
                    node.setCharValue(literal.charValue());
                    mp.getTemplateBoolean().put(e.getExpressionStr() + "char", true);
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
