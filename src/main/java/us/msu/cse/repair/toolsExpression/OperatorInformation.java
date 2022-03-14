package us.msu.cse.repair.toolsExpression;

import org.eclipse.jdt.core.dom.InfixExpression;

import java.util.ArrayList;
import java.util.List;

public   class OperatorInformation {
    public static List<InfixExpression.Operator> getTDRPM(){
        List<InfixExpression.Operator> list = new ArrayList<>();
        list.add(InfixExpression.Operator.TIMES);
        list.add(InfixExpression.Operator.DIVIDE);
        list.add(InfixExpression.Operator.REMAINDER);
        list.add(InfixExpression.Operator.PLUS);
        list.add(InfixExpression.Operator.MINUS);
        return list;
    }
    public static List<InfixExpression.Operator> getLRR(){
        List<InfixExpression.Operator> list = new ArrayList<>();
        list.add(InfixExpression.Operator.LEFT_SHIFT);
        list.add(InfixExpression.Operator.RIGHT_SHIFT_SIGNED);
        list.add(InfixExpression.Operator.RIGHT_SHIFT_UNSIGNED);
        return list;
    }
    public static List<InfixExpression.Operator> getLGLGEN(){
        List<InfixExpression.Operator> list = new ArrayList<>();
        list.add(InfixExpression.Operator.LESS);
        list.add(InfixExpression.Operator.GREATER);
        list.add(InfixExpression.Operator.LESS_EQUALS);
        list.add(InfixExpression.Operator.GREATER_EQUALS);
        list.add(InfixExpression.Operator.EQUALS);
        list.add(InfixExpression.Operator.NOT_EQUALS);
        return list;
    }
    public static List<InfixExpression.Operator> getXAOCC(){
        List<InfixExpression.Operator> list = new ArrayList<>();
        list.add(InfixExpression.Operator.XOR);
        list.add(InfixExpression.Operator.AND);
        list.add(InfixExpression.Operator.OR);
        list.add(InfixExpression.Operator.CONDITIONAL_OR);
        list.add(InfixExpression.Operator.CONDITIONAL_AND);
        return list;
    }
}
