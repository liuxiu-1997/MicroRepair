package us.msu.cse.repair.toolsExpression;

import org.eclipse.jdt.core.dom.*;
import us.msu.cse.repair.astVisitorExpression.GetVariableVisitor;
import us.msu.cse.repair.core.parser.ModificationPoint;
import us.msu.cse.repair.informationExpression.ExpressionInfo;
import us.msu.cse.repair.informationExpression.LineAndNodeType;
import us.msu.cse.repair.informationExpression.MethClaPacOfExpName;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class TypeInformation {
    public static ExpressionInfo getTypeInformation(Name e, ModificationPoint mp) {
        ASTNode curNode = e;
        while (curNode != null) {
            //如果是方法声明，证明这个return 后的变量只能在方法中找；
            //否则，我则去typeDeclaration中找
            if (curNode instanceof MethodDeclaration) {
                MethodDeclaration methodDeclaration = (MethodDeclaration) curNode;
                List list = methodDeclaration.parameters();
                for (Object o : list) {
                    SingleVariableDeclaration ll = (SingleVariableDeclaration) o;
                    if (ll.getName().toString().equals(e.toString())) {
                        Type type = ll.getType();
                        return new ExpressionInfo(e, mp.getMethClaPacOfExpName(), mp.getLineAndNodeType(), type, e.toString());
                    }
                }
                //————————————————方法声明———2.方法的函数体———————————————————————————————————————————————
                Block block = methodDeclaration.getBody();
                List stL = block.statements();
                for (Object o : stL) {
                    Statement statement = (Statement) o;
                    if (statement instanceof VariableDeclarationStatement) {
                        VariableDeclarationStatement vb = (VariableDeclarationStatement) statement;
                        String strings = o.toString();
                        int num = strings.indexOf("=");
                        if (num > 0)
                            strings = strings.substring(0, num);
                        if (strings.equals(e.toString())) {
                            return new ExpressionInfo(e, mp.getMethClaPacOfExpName(), mp.getLineAndNodeType(), vb.getType(), strings);
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
                            return new ExpressionInfo(e, mp.getMethClaPacOfExpName(), mp.getLineAndNodeType(), vb.getType(), strings);
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
                        if (strings.equals(e.toString())) {
                            return new ExpressionInfo(e, mp.getMethClaPacOfExpName(), mp.getLineAndNodeType(), f.getType(), strings);
                        }
                    }
                }
            }else if (curNode instanceof VariableDeclarationStatement){
                VariableDeclarationStatement vb = (VariableDeclarationStatement) curNode;
                List list = vb.fragments();
                String strings = list.get(0).toString();
                int num = strings.indexOf("=");
                if (num > 0)
                    strings = strings.substring(0, num);
                if (strings.equals(e.toString())) {
                    return new ExpressionInfo(e, mp.getMethClaPacOfExpName(), mp.getLineAndNodeType(), vb.getType(), strings);
                }
            }
            curNode = curNode.getParent();
        }
        return null;
    }

    public static ExpressionInfo getTypeInformation(Name e, MethClaPacOfExpName methClaPacOfExpName, LineAndNodeType lineAndNodeType) {
        ASTNode curNode = e;
        while (curNode != null) {
            //如果是方法声明，证明这个return 后的变量只能在方法中找；
            //否则，我则去typeDeclaration中找
            if (curNode instanceof MethodDeclaration) {
                //—————————————————方法声明————1.形参列表中找————————————————————————————————————————————
                MethodDeclaration methodDeclaration = (MethodDeclaration) curNode;
                List list = methodDeclaration.parameters();
                for (Object o : list) {
                    SingleVariableDeclaration ll = (SingleVariableDeclaration) o;
                    if (ll.getName().toString().equals(e.toString())) {
                        Type type = ll.getType();
                        return new ExpressionInfo(e, methClaPacOfExpName, lineAndNodeType, type, e.toString());
                    }
                }
                //————————————————方法声明———2.方法的函数体———————————————————————————————————————————————
                Block block = methodDeclaration.getBody();
                List stL = block.statements();
                for (Object o : stL) {
                    Statement statement = (Statement) o;
                    if (statement instanceof VariableDeclarationStatement) {
                        VariableDeclarationStatement vb = (VariableDeclarationStatement) statement;
                        String strings = o.toString();
                        int num = strings.indexOf("=");
                        if (num > 0)
                            strings = strings.substring(0, num);
                        if (strings.equals(e.toString())) {
                            return new ExpressionInfo(e,methClaPacOfExpName, lineAndNodeType, vb.getType(), strings);
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
                            return new ExpressionInfo(e,methClaPacOfExpName, lineAndNodeType, vb.getType(), strings);
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
                        if (strings.equals(e.toString())) {
                            return new ExpressionInfo(e,methClaPacOfExpName, lineAndNodeType, f.getType(), strings);
                        }
                    }
                }
            }else if (curNode instanceof VariableDeclarationStatement){
                VariableDeclarationStatement vb = (VariableDeclarationStatement) curNode;
                List list = vb.fragments();
                String strings = list.get(0).toString();
                int num = strings.indexOf("=");
                if (num > 0)
                    strings = strings.substring(0, num);
                if (strings.equals(e.toString())) {
                    return new ExpressionInfo(e, methClaPacOfExpName, lineAndNodeType, vb.getType(), strings);
                }
            }
            curNode = curNode.getParent();
        }
        return null;
    }

    //获取变量的类型;
    public static void getTypeInformation(ExpressionInfo expressionInfo) {
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
            }else if (curNode instanceof VariableDeclarationStatement){
                VariableDeclarationStatement vb = (VariableDeclarationStatement) curNode;
                List list = vb.fragments();
                String strings = list.get(0).toString();
                int num = strings.indexOf("=");
                if (num > 0)
                    strings = strings.substring(0, num);
                if (strings.equals(expName)) {
                    expressionInfo.setVarType(vb.getType());
                    expressionInfo.setVarTypeStr(vb.getType().toString());
                    expressionInfo.setVarNameStr(strings);
                    break;
                }
            }
            curNode = curNode.getParent();
        }
    }

    //这个是获取数组的类型——结构较为完美，我也已经实验，可作为以后参考
    public static ExpressionInfo getArrayAccessTypeInfo(ArrayAccess node) {
        /**
         * 说明：
         * 这里是获取数组调用的信息，最后返回一个ExpressionInfo的信息;
         * 例：node == a[1];
         * 那么Type类型为ArrayType类型______例：int[],float[];
         * 具体使用还需要ArrayType中的getElementType()的看里面的元素是否匹配
         */
        ASTNode cur = node.getArray();
        while (cur != null) {
            //总体分为三种：方法声明——类型声明（全局变量）——语句块
            if (cur instanceof MethodDeclaration) {
                //————————————————方法声明———1.方法的参数列表———————————————————————————————————————————————
                MethodDeclaration methodDeclaration = (MethodDeclaration) cur;
                List list = methodDeclaration.parameters();
                for (Object o : list) {
                    SingleVariableDeclaration ll = (SingleVariableDeclaration) o;
                    if ((ll.getType() instanceof ArrayType) && (ll.getName().toString().equals(node.getArray().toString()))) {
                        ArrayType arrayType = (ArrayType) ll.getType();
                        return new ExpressionInfo(node, arrayType, ll.getName().toString());
                    }
                }
                //————————————————方法声明———2.方法的函数体———————————————————————————————————————————————
                Block block = methodDeclaration.getBody();
                List stL = block.statements();
                for (Object o : stL) {
                    Statement statement = (Statement) o;
                    if (statement instanceof VariableDeclarationStatement) {
                        VariableDeclarationStatement vb = (VariableDeclarationStatement) statement;
                        if (vb.getType() instanceof ArrayType) {
                            ArrayType arrayType = (ArrayType) vb.getType();
                            String strings = o.toString();
                            int num = strings.indexOf("=");
                            if (num > 0)
                                strings = strings.substring(0, num);
                            if (strings.equals(node.getArray().toString())) {
                                return new ExpressionInfo(node, arrayType, strings);
                            }
                        }
                    }
                }
            } else if (cur instanceof TypeDeclaration) {

                //————————————————类型声明———主要是获取全局变量———————————————————————————————————————————————
                FieldDeclaration[] fieldDeclaration = ((TypeDeclaration) cur).getFields();
                for (FieldDeclaration f : fieldDeclaration) {
                    if (f.getType() instanceof ArrayType) {
                        ArrayType arrayType = (ArrayType) f.getType();
                        List list = f.fragments();
                        for (Object o : list) {
                            String strings = o.toString();
                            int num = strings.indexOf("=");
                            if (num > 0)
                                strings = strings.substring(0, num);
                            if (strings.equals(node.getArray().toString())) {
                                return new ExpressionInfo(node, arrayType, strings);
                            }
                        }
                    }
                }
            } else if (cur instanceof Block) {
                //————————————————语句块———防止遗漏———————————————————————————————————————————————
                Block block = (Block) cur;
                List stL = block.statements();
                for (Object o : stL) {
                    Statement statement = (Statement) o;
                    if (statement instanceof VariableDeclarationStatement) {
                        VariableDeclarationStatement vb = (VariableDeclarationStatement) statement;
                        List listVar = vb.fragments();
                        if (vb.getType() instanceof ArrayType) {
                            ArrayType arrayType = (ArrayType) vb.getType();
                            String strings = listVar.get(0).toString();
                            int num = strings.indexOf("=");
                            if (num > 0)
                                strings = strings.substring(0, num);
                            if (strings.equals(node.getArray().toString())) {
                                return new ExpressionInfo(node, arrayType, strings);
                            }
                        }
                    }
                }
            }else if (cur instanceof VariableDeclarationStatement){
                VariableDeclarationStatement vb = (VariableDeclarationStatement) cur;
                List list = vb.fragments();
                String strings = list.get(0).toString();
                int num = strings.indexOf("=");
                if (num > 0)
                    strings = strings.substring(0, num);
                if (strings.equals(node.getArray().toString())) {
                    return new ExpressionInfo(node, vb.getType(), strings);
                }
            }
            cur = cur.getParent();
        }
        return null;
    }
    //尽量不用，因为他的成本代价比较高
    public static ExpressionInfo getSourceVariable(String sourceFilePath,String nameStr){
        File file=new File(sourceFilePath);
        ExpressionInfo expressionInfo  = new ExpressionInfo();
        try {
            FileInputStream in=new FileInputStream(file);
            // size  为字串的长度 ，这里一次性读完
            int size=in.available();
            byte[] buffer=new byte[size];
            in.read(buffer);
            in.close();
            String str=new String(buffer,"GB2312");

            ASTParser astParser = ASTParser.newParser(AST.JLS8);
            astParser.setKind(ASTParser.K_COMPILATION_UNIT);
            astParser.setResolveBindings(true);
            astParser.setStatementsRecovery(true);
            astParser.setSource(str.toCharArray());
            CompilationUnit compilationUnit = (CompilationUnit)astParser.createAST(null);

            GetVariableVisitor getVariableVisitor = new GetVariableVisitor(nameStr,expressionInfo);
            compilationUnit.accept(getVariableVisitor);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (expressionInfo.getExpression()!=null){
            return expressionInfo;
        } else
            return null;
    }
}

