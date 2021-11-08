package us.msu.cse.repair.toolsExpression;

import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;


public class OperatorFilterPreAndIn {
    //true可以作为成分
    //false返回失败
    public static boolean ExpressionFilterReturn(Expression expression){
        if (expression instanceof PrefixExpression){
            PrefixExpression prefixExpression = (PrefixExpression)expression;
            if (prefixExpression.getOperator().equals(PrefixExpression.Operator.NOT))
                return true;

        }else if (expression instanceof InfixExpression) {
            InfixExpression infixExpression = (InfixExpression) expression;
            InfixExpression.Operator operator = infixExpression.getOperator();
            if (operator.equals(InfixExpression.Operator.LESS) | operator.equals(InfixExpression.Operator.LESS_EQUALS) | operator.equals(InfixExpression.Operator.GREATER) |
                    operator.equals(InfixExpression.Operator.GREATER_EQUALS) | operator.equals(InfixExpression.Operator.EQUALS) | operator.equals(InfixExpression.Operator.NOT_EQUALS)
                    | operator.equals(InfixExpression.Operator.AND) | operator.equals(InfixExpression.Operator.CONDITIONAL_AND) | operator.equals(InfixExpression.Operator.CONDITIONAL_OR) |
                    operator.equals(InfixExpression.Operator.CONDITIONAL_OR)) {
                return true;
            }
        }
        return false;


    }
}
