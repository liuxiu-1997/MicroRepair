package us.msu.cse.repair;

import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.Statement;
import us.msu.cse.repair.core.parser.ModificationPoint;

import java.util.List;

public class IngredientFilterRule {
    //此类用于所有定义的规则
    public static boolean getIsMatchRule(Statement statement, ModificationPoint mp){
        boolean rule1 = rule1CheckIf(statement,mp);
        if (rule1)
            return true;
        return false;
    }

    private static boolean rule1CheckIf(Statement statement, ModificationPoint mp) {
        List<String> list = mp.getMethodAndTypeNameToFilter();
        if ((list.size()>0)&&(statement instanceof IfStatement)) {
            if ((((IfStatement) statement).getExpression() instanceof InfixExpression)) {
                String str = ((InfixExpression) ((IfStatement) statement).getExpression()).getLeftOperand().toString();
                for (String s : list) {
                    if (((str.charAt(0)>='A')&&(str.charAt(0)<='Z'))||s.equals(str)||(s.contains(str.subSequence(0,str.length())))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
