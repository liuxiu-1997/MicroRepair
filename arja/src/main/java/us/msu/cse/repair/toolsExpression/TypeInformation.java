package us.msu.cse.repair.toolsExpression;

import org.eclipse.jdt.core.dom.*;
import us.msu.cse.repair.core.parser.ModificationPoint;
import us.msu.cse.repair.informationExpression.ExpressionInfo;
import us.msu.cse.repair.informationExpression.LineAndNodeType;
import us.msu.cse.repair.informationExpression.MethClaPacOfExpName;

import java.util.List;

public class TypeInformation {
    public static ExpressionInfo getTypeInformation(Name e, ModificationPoint mp) {
        ASTNode curNode = e;
        ExpressionInfo expressionInfo = null;
        boolean flag = false;
        while ((curNode != null) && (!flag)) {
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
                            expressionInfo = new ExpressionInfo(e, mp.getMethClaPacOfExpName(), mp.getLineAndNodeType(), vb.getType(), strings);
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
                            expressionInfo = new ExpressionInfo(e, mp.getMethClaPacOfExpName(), mp.getLineAndNodeType(), vb.getType(), strings);
                            flag = true;
                            break;
                        }
                    }
                }
            }
            curNode = curNode.getParent();
        }
        return expressionInfo;
    }

    public static ExpressionInfo getTypeInformation(Name e, MethClaPacOfExpName methClaPacOfExpName, LineAndNodeType lineAndNodeType) {
        ASTNode curNode = e;
        ExpressionInfo expressionInfo = null;
        boolean flag = false;
        while ((curNode != null) && (!flag)) {
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
                            expressionInfo = new ExpressionInfo(e, methClaPacOfExpName, lineAndNodeType, vb.getType(), strings);
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
                            expressionInfo = new ExpressionInfo(e, methClaPacOfExpName, lineAndNodeType, vb.getType(), strings);
                            flag = true;
                            break;
                        }
                    }
                }
            }
            curNode = curNode.getParent();
        }
        return expressionInfo;
    }

    public static void TypeInformation(ExpressionInfo expressionInfo) {
        String expName = expressionInfo.getExpression().toString();
        ASTNode curNode = expressionInfo.getExpression();
        boolean flag = false;
        while ((curNode != null) && (!flag)) {
            if (curNode instanceof MethodDeclaration) {
                MethodDeclaration methodDeclaration = (MethodDeclaration) curNode;
                //__________________________在方法体内进行检测____________________________________
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
                        if (strings.equals(expName)) {
                            expressionInfo.setVarType(vb.getType());
                            expressionInfo.setVarTypeStr(vb.getType().toString());
                            expressionInfo.setVarNameStr(strings);
                            flag = true;
                            break;
                        }
                    }
                }
                //—————————————————————————————参数列表内进行检测——————————————————————————————————
                List<SingleVariableDeclaration> parameters = methodDeclaration.parameters();
                for (SingleVariableDeclaration parameter : parameters) {
                    Type parameterType = parameter.getType();
                    String parameterName = parameter.getName().toString();
                    if (parameterName.equals(expName)) {
                        expressionInfo.setVarType(parameterType);
                        expressionInfo.setVarTypeStr(parameterType.toString());
                        expressionInfo.setVarNameStr(parameterName);
                        flag = true;
                        break;
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
                        if (strings.equals(expName)) {
                            expressionInfo.setVarType(vb.getType());
                            expressionInfo.setVarTypeStr(vb.getType().toString());
                            expressionInfo.setVarNameStr(strings);
                            flag = true;
                            break;
                        }
                    }
                }
            } else if (curNode instanceof TypeDeclaration) {
                FieldDeclaration[] fieldDeclaration = ((TypeDeclaration) curNode).getFields();
                for (FieldDeclaration f : fieldDeclaration) {
                    List list = f.fragments();
                    for (Object o : list) {
                        String strings = o.toString();
                        int num = strings.indexOf("=");
                        if (num > 0)
                            strings = strings.substring(0, num);
                        if (strings.equals(expName)) {
                            expressionInfo.setVarType(f.getType());
                            expressionInfo.setVarTypeStr(f.getType().toString());
                            expressionInfo.setVarNameStr(strings);
                            flag = true;
                            break;
                        }
                    }
                }
            }
            curNode = curNode.getParent();
        }
    }
}

