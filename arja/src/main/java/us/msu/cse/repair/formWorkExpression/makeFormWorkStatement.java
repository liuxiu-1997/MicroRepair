package us.msu.cse.repair.formWorkExpression;

import org.eclipse.jdt.core.dom.*;
import us.msu.cse.repair.core.parser.ModificationPoint;

import java.util.ArrayList;
import java.util.List;

public class makeFormWorkStatement {
    public static List<Statement> getStatement(ModificationPoint mp, ASTNode simpleName){
        AST ast = AST.newAST(AST.JLS8);
        NullLiteral nullLiteral = ast.newNullLiteral();
        IfStatement ifStatement = ast.newIfStatement();
        InfixExpression infixExpression = ast.newInfixExpression();
        ReturnStatement returnStatement = ast.newReturnStatement();
        BooleanLiteral booleanLiteral = ast.newBooleanLiteral(false);
        List<Statement> list = new ArrayList<>();
        {
            //1)	if(sum != null)
            InfixExpression infixExpression1 = (InfixExpression) ASTNode.copySubtree(ast,infixExpression);
            Expression simpleName1 = (Expression) ASTNode.copySubtree(ast,simpleName);
            NullLiteral nullLiteral1 = (NullLiteral) ASTNode.copySubtree(ast,nullLiteral);
            IfStatement ifStatement1 = (IfStatement) ASTNode.copySubtree(ast,ifStatement);
            infixExpression1.setLeftOperand(simpleName1);
            infixExpression1.setRightOperand(nullLiteral1);
            infixExpression1.setOperator(InfixExpression.Operator.GREATER_EQUALS);
            ifStatement1.setExpression(infixExpression1);
            list.add(ifStatement1);
            IfStatement ifStatement11 = (IfStatement)ASTNode.copySubtree(ast,ifStatement1);
            ((InfixExpression)ifStatement11.getExpression()).setOperator(InfixExpression.Operator.EQUALS);
            list.add(ifStatement11);

            // 3)	if(sum != null){放着这条语句的父类}
            IfStatement ifStatement2 = (IfStatement)ASTNode.copySubtree(ast,ifStatement1);
            ifStatement2.setThenStatement((Statement) ASTNode.copySubtree(ifStatement2.getAST(),mp.getStatement().getParent()));
            list.add(ifStatement2);
            IfStatement ifStatement22 = (IfStatement)ASTNode.copySubtree(ast,ifStatement1);
            ((InfixExpression)ifStatement22.getExpression()).setOperator(InfixExpression.Operator.EQUALS);
            list.add(ifStatement22);

            // 5)	if(sum != null) return;
            IfStatement ifStatement3 = (IfStatement) ASTNode.copySubtree(ast,ifStatement1);
            ReturnStatement returnStatement3 = (ReturnStatement) ASTNode.copySubtree(ast,returnStatement);
            ifStatement3.setThenStatement(returnStatement3);
            list.add(ifStatement3);
            IfStatement ifStatement33 = (IfStatement)ASTNode.copySubtree(ast,ifStatement1);
            ((InfixExpression)ifStatement33.getExpression()).setOperator(InfixExpression.Operator.EQUALS);
            list.add(ifStatement33);

            //6)	if(sum != null) return true;
            IfStatement ifStatement4 = (IfStatement) ASTNode.copySubtree(ast,ifStatement1);
            BooleanLiteral booleanLiteral4 = ast.newBooleanLiteral(true);
            ReturnStatement returnStatement4 = (ReturnStatement) ASTNode.copySubtree(ast,returnStatement);
            returnStatement4.setExpression(booleanLiteral4);
            ifStatement4.setThenStatement(returnStatement4);
            list.add(ifStatement4);
            IfStatement ifStatement44 = (IfStatement)ASTNode.copySubtree(ast,ifStatement1);
            ((InfixExpression)ifStatement44.getExpression()).setOperator(InfixExpression.Operator.EQUALS);
            list.add(ifStatement44);

            //9)	if(sum != null) return false;
            IfStatement ifStatement5 = (IfStatement) ASTNode.copySubtree(ast,ifStatement1);
            BooleanLiteral booleanLiteral5 = ast.newBooleanLiteral(false);
            ReturnStatement returnStatement5 = (ReturnStatement) ASTNode.copySubtree(ast,returnStatement);
            returnStatement5.setExpression(booleanLiteral5);
            ifStatement5.setThenStatement(returnStatement5);
            list.add(ifStatement5);
            IfStatement ifStatement55 = (IfStatement)ASTNode.copySubtree(ast,ifStatement1);
            ((InfixExpression)ifStatement55.getExpression()).setOperator(InfixExpression.Operator.EQUALS);
            list.add(ifStatement55);

            //7)	if(sum != null) return 0;
            IfStatement ifStatement6 = (IfStatement) ASTNode.copySubtree(ast,ifStatement1);
            NumberLiteral numberLiteral6 = ast.newNumberLiteral("0");
            ReturnStatement returnStatement6 = (ReturnStatement) ASTNode.copySubtree(ast,returnStatement);
            returnStatement6.setExpression(numberLiteral6);
            ifStatement6.setThenStatement(returnStatement6);
            list.add(ifStatement6);
            IfStatement ifStatement66 = (IfStatement)ASTNode.copySubtree(ast,ifStatement1);
            ((InfixExpression)ifStatement66.getExpression()).setOperator(InfixExpression.Operator.EQUALS);
            list.add(ifStatement66);

            //8)	if(sum != null) return 1;
            IfStatement ifStatement7 = (IfStatement) ASTNode.copySubtree(ast,ifStatement1);
            NumberLiteral numberLiteral7 = ast.newNumberLiteral("1");
            ReturnStatement returnStatement7 = (ReturnStatement) ASTNode.copySubtree(ast,returnStatement);
            returnStatement7.setExpression(numberLiteral7);
            ifStatement7.setThenStatement(returnStatement7);
            list.add(ifStatement7);
            IfStatement ifStatement77 = (IfStatement)ASTNode.copySubtree(ast,ifStatement1);
            ((InfixExpression)ifStatement77.getExpression()).setOperator(InfixExpression.Operator.EQUALS);
            list.add(ifStatement77);
        }
        return list;
    }
}
