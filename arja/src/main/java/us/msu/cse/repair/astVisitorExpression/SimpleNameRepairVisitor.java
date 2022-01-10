package us.msu.cse.repair.astVisitorExpression;


import jmetal.util.PseudoRandom;
import org.eclipse.jdt.core.dom.*;
import us.msu.cse.repair.AbstractClass.ASTVisitorPlus;
import us.msu.cse.repair.core.parser.ModificationPoint;
import us.msu.cse.repair.formWorkExpression.makeFormWorkStatement;
import us.msu.cse.repair.informationExpression.ExpressionInfo;
import us.msu.cse.repair.toolsExpression.ChangeSimpleName;
import us.msu.cse.repair.toolsExpression.RuleCheck;
import us.msu.cse.repair.toolsExpression.TemplateBoolean;

import java.awt.*;
import java.security.Permission;
import java.util.List;


/**
 * 设置了一个共享变量isRepaied，目的每次只修改一个位置，将这个语句放入成分空间中。
 */
public class SimpleNameRepairVisitor extends ASTVisitorPlus {
    private ModificationPoint mp = null;
    private volatile boolean isRepaired = false;
    private volatile boolean statementFlag = false;
    private List<ExpressionInfo> expressionInfoList = null;
    private Statement s = null;

    public SimpleNameRepairVisitor(ModificationPoint mp) {
        this.mp = mp;
        expressionInfoList = mp.getExpressionInfosIngredients();
    }

    @Override
    public boolean visit(SimpleName node) {
        //只用于替换里面的SimpleName；筛选掉里面SimpleName为Name和类型
        boolean checkRule1 = RuleCheck.rule1OfSimpleName(mp,node);
        boolean checkRule2 = RuleCheck.rule2OfClassName(mp,node);
        if ((!TemplateBoolean.templateBooleanCheck(mp,node.toString()+"all")) && checkRule1 && checkRule2) {
            mp.getTemplateBoolean().put(node.toString() + "all", true);
            int num = 0;
            if (mp.getIngredients() != null) {
                num = mp.getIngredients().size();
            }
            if ((!TemplateBoolean.templateBooleanCheck(mp, node.toString()  + "simpleRepair"))) {
                List<Statement> statementList = ChangeSimpleName.getChangedSimpleName(mp, node.toString());
                if (mp.getIngredients() == null) {
                    mp.setIngredients(statementList);
                } else {
                    mp.getIngredients().addAll(statementList);
                }
                mp.getTemplateBoolean().put(node.toString()  + "simpleRepair", true);
            }
            if ((!TemplateBoolean.templateBooleanCheck(mp, node.toString()  + "formwork"))&&mp.getVariableName().contains(node.toString())) {
                List<Statement> statementList = makeFormWorkStatement.getStatement(mp, node);
                if (mp.getIngredients() == null) {
                    mp.setIngredients(statementList);
                } else {
                    mp.getIngredients().addAll(statementList);
                }
                mp.getTemplateBoolean().put(node.toString() + "formwork", true);
            }
            if (mp.getIngredients() != null) {
                if (num != mp.getIngredients().size())
                    mp.setRepair(true);
            }
        }
        return true;
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
