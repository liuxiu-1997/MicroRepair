package us.msu.cse.repair.toolsExpression;

import org.eclipse.jdt.core.dom.*;
import us.msu.cse.repair.core.parser.ModificationPoint;
import us.msu.cse.repair.informationExpression.ExpressionInfo;
import us.msu.cse.repair.informationExpression.LineAndNodeType;
import us.msu.cse.repair.informationExpression.MethClaPacOfExpName;

import java.util.List;

public class TypeInformation {
    public static ExpressionInfo getTypeInformation(Name e, ModificationPoint mp){
        ASTNode curNode = e;
        ExpressionInfo expressionInfo=null;
        boolean flag=false;
        while ((curNode != null)&&(!flag)) {
            //如果是方法声明，证明这个return 后的变量只能在方法中找；
            //否则，我则去typeDeclaration中找
            if (curNode instanceof MethodDeclaration) {

                MethodDeclaration methodDeclaration = (MethodDeclaration) curNode;
                Block block = methodDeclaration.getBody();
                List stL = block.statements();
                for (Object o : stL) {
                    Statement statement = (Statement) o;
                    if (statement instanceof VariableDeclarationStatement) {
                        VariableDeclarationStatement vb = (VariableDeclarationStatement) statement;
                        List list = vb.fragments();
                        String strings = list.get(0).toString();
                        int num = strings.indexOf("=");
                        if (num > 0)
                            strings = strings.substring(0, num);
                        if (strings.equals(e.toString())) {
                            expressionInfo = new ExpressionInfo(e,mp.getMethClaPacOfExpName() , mp.getLineAndNodeType(), vb.getType(), strings);
                            flag = true;
                            break;

                        }
                    }
                }
            } else if (curNode instanceof Block) {
                Block td = (Block) curNode;
                List stL = td.statements();
                for (Object o : stL) {

                    Statement statement = (Statement) o;
                    if (statement instanceof VariableDeclarationStatement) {
                        VariableDeclarationStatement vb = (VariableDeclarationStatement) statement;
                        List list = vb.fragments();
                        String strings = list.get(0).toString();
                        int num = strings.indexOf("=");
                        if (num > 0)
                            strings = strings.substring(0, num);
                        if (strings.equals(e.toString())) {
                            expressionInfo = new ExpressionInfo(e,mp.getMethClaPacOfExpName() , mp.getLineAndNodeType(), vb.getType(), strings);
                            flag=true;
                            break;
                        }
                    }
                }
            }
            curNode = curNode.getParent();
        }
        return expressionInfo;
    }
    public static ExpressionInfo getTypeInformation(Name e, MethClaPacOfExpName methClaPacOfExpName, LineAndNodeType lineAndNodeType){
        ASTNode curNode = e;
        ExpressionInfo expressionInfo=null;
        boolean flag=false;
        while ((curNode != null)&&(!flag)) {
            //如果是方法声明，证明这个return 后的变量只能在方法中找；
            //否则，我则去typeDeclaration中找
            if (curNode instanceof MethodDeclaration) {

                MethodDeclaration methodDeclaration = (MethodDeclaration) curNode;
                Block block = methodDeclaration.getBody();
                List stL = block.statements();
                for (Object o : stL) {
                    Statement statement = (Statement) o;
                    if (statement instanceof VariableDeclarationStatement) {
                        VariableDeclarationStatement vb = (VariableDeclarationStatement) statement;
                        List list = vb.fragments();
                        String strings = list.get(0).toString();
                        int num = strings.indexOf("=");
                        if (num > 0)
                            strings = strings.substring(0, num);
                        if (strings.equals(e.toString())) {
                            expressionInfo = new ExpressionInfo(e,methClaPacOfExpName , lineAndNodeType, vb.getType(), strings);
                            flag = true;
                            break;

                        }
                    }
                }
            } else if (curNode instanceof Block) {
                Block td = (Block) curNode;
                List stL = td.statements();
                for (Object o : stL) {

                    Statement statement = (Statement) o;
                    if (statement instanceof VariableDeclarationStatement) {
                        VariableDeclarationStatement vb = (VariableDeclarationStatement) statement;
                        List list = vb.fragments();
                        String strings = list.get(0).toString();
                        int num = strings.indexOf("=");
                        if (num > 0)
                            strings = strings.substring(0, num);
                        if (strings.equals(e.toString())) {
                            expressionInfo = new ExpressionInfo(e,methClaPacOfExpName , lineAndNodeType, vb.getType(), strings);
                            flag=true;
                            break;
                        }
                    }
                }
            }
            curNode = curNode.getParent();
        }
        return expressionInfo;
    }

}

