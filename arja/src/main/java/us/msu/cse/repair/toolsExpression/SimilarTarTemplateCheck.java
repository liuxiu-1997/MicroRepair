package us.msu.cse.repair.toolsExpression;

import org.eclipse.jdt.core.dom.*;

import java.util.ArrayList;
import java.util.List;

public class SimilarTarTemplateCheck {

    public static boolean templateCheck(Expression e,String expType){
        boolean flag=false;
        switch (expType){
            case "CastExpression":
                flag = castExpressionCheck(e);
                break;
            case "ArrayAccess":
                flag = arrayAccessCheck(e);
                break;
            case "FieldAccess":
                flag = fieldAccessCheck(e);
                break;
        }
        return flag;
    }
    public static boolean castExpressionCheck(Expression e){
        ASTNode cur = e;
        while(cur!=null){
            if (cur instanceof IfStatement){
                IfStatement ifStatement = (IfStatement) cur;
                if (ifStatement.getExpression() instanceof InstanceofExpression)
                    return true;
            }
            cur = cur.getParent();
        }
        return false;
    }
    public static boolean arrayAccessCheck(Expression e){
        ASTNode cur = e;
        List<InfixExpression.Operator> list = new ArrayList<>();
        list.add(InfixExpression.Operator.GREATER);
        list.add(InfixExpression.Operator.GREATER_EQUALS);
        list.add(InfixExpression.Operator.LESS);
        list.add(InfixExpression.Operator.LESS_EQUALS);
        while(cur!=null){
            if (cur instanceof IfStatement){
                IfStatement ifStatement = (IfStatement) cur;

                if (ifStatement.getExpression() instanceof InfixExpression)
                    if (list.contains(((InfixExpression)ifStatement.getExpression()).getOperator()))
                        return true;
            }
            cur = cur.getParent();
        }
        return false;
    }
    public static boolean fieldAccessCheck(Expression e){
        ASTNode cur = e;
        List<InfixExpression.Operator> list = new ArrayList<>();
        list.add(InfixExpression.Operator.EQUALS);
        list.add(InfixExpression.Operator.NOT_EQUALS);
        while(cur!=null){
            if (cur instanceof IfStatement){
                IfStatement ifStatement = (IfStatement) cur;

                if (ifStatement.getExpression() instanceof InfixExpression)
                    if (list.contains(((InfixExpression)ifStatement.getExpression()).getOperator()))
                        return true;
            }
            cur = cur.getParent();
        }
        return false;
    }
}
