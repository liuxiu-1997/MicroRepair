package us.msu.cse.repair.toolsExpression;

import org.eclipse.jdt.core.dom.*;
import us.msu.cse.repair.astVisitorExpression.FieldAccessRepairVisitor;
import us.msu.cse.repair.core.parser.ModificationPoint;
import us.msu.cse.repair.informationExpression.ExpressionInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RuleCheck {
    //SimpleName不能替换Type
    public static boolean rule1OfSimpleName(ModificationPoint mp, SimpleName simpleName) {
        if (mp.getTypeName() != null) {
            for (Type type : mp.getTypeName()) {
                if (type.toString().equals(simpleName.toString())) {
                    return false;
                }
            }
        }
        return true;
    }

    //SimpleName不能为定义的中介类Test1
    public static boolean rule2OfClassName(ModificationPoint mp, SimpleName simpleName) {
        if (simpleName.toString().equals("Test1")) {
            return false;
        }
        return true;
    }

    public static boolean rule2OfIfRepair(ModificationPoint mp, Expression expressionOfIf, ExpressionInfo expressionIngre) {
        boolean flag1 = false;
        Expression expressionOfIngre = expressionIngre.getExpression();
        if (((expressionOfIf instanceof SimpleName) && (mp.getVariableName().contains(expressionOfIf.toString())))
                || (expressionOfIf instanceof QualifiedName)) {
            if ((expressionOfIngre instanceof SimpleName) && (mp.getVariableName().contains(expressionOfIngre.toString()))) {
                flag1 = true;
            }
            if (expressionOfIngre instanceof FieldAccess) {
                flag1 = true;
            }
            if (expressionOfIngre instanceof QualifiedName) {
                flag1 = true;
            }
        } else if (expressionOfIf instanceof MethodInvocation) {
            if (expressionOfIngre instanceof MethodInvocation) {
                flag1 = true;
            }
        }
        return flag1;
    }

    //主要用于simpleName的范围检查，是否在规定的范围内
    public static boolean rule2OfSimpleName(ModificationPoint mp, SimpleName simpleName) {
        //只针对 simpleName 进行过滤
        /* 原因：simpleName使用最为频繁
         * 过滤规则：
         *    1.if 当simpleName为全局变量时，使用；
         *    2.else if 修改点语句 在一个方法中时，看这个simpleName是否出现——————————————可通过对方法包装，用AST去找遍历，看是否存在；
         *    3.else if 在一个Block（且其行数需大于8）中，则用2的方法
         *
         */
        if (mp.getGlobalVariableName().contains(simpleName.toString()) || mp.getMethodName().contains(simpleName.toString()))
            return true;
        else {
            ASTNode cur = mp.getStatement();
            ASTNode checkASTNode = null;
            //第二种方法，检测是否时MethodDeclaration
            while ((cur != null) && (!(cur instanceof MethodDeclaration))) {
                cur = cur.getParent();
            }
            if (cur != null) {
                checkASTNode = cur;
            } else {
                //第三种方法，也是出一个ASTNode
                cur = mp.getStatement();
                CompilationUnit compilationUnit = (CompilationUnit) simpleName.getRoot();
                int lineNode = compilationUnit.getLineNumber(simpleName.getStartPosition());
                while (cur != null) {
                    CompilationUnit compilationUnitIn = (CompilationUnit) cur.getRoot();
                    int lineCur = compilationUnitIn.getLineNumber(cur.getStartPosition());
                    if (cur instanceof Block) {
                        checkASTNode = cur;
                    }
                    if ((lineNode - lineCur) > 8) {
                        checkASTNode = cur;
                        break;
                    }
                    cur = cur.getParent();
                }
            }
            if (checkASTNode != null) {

                SimpleNameAndStringAndNumberLiteralCheckASTVisitor simplenameAndStringAndNumberLiteralCheckASTVisitor = new SimpleNameAndStringAndNumberLiteralCheckASTVisitor(simpleName.toString());
                checkASTNode.accept(simplenameAndStringAndNumberLiteralCheckASTVisitor);
                return simplenameAndStringAndNumberLiteralCheckASTVisitor.getIsIncluded();
            }
        }
        return false;
    }

    //主要用于ArrayAccess的范围检查，是否在规定的范围内
    public static boolean rule1OfArrayAccess(ModificationPoint mp, ArrayAccess access) {
        ASTNode cur = mp.getStatement();
        ASTNode checkASTNode = null;
        String arrayName = access.getArray().toString();
        char[] chars = access.getIndex().toString().toCharArray();
        boolean flagCheck = false;
        if (mp.getGlobalVariableName().contains(arrayName)) {
            for (char c : chars) {
                if (((c >= 'A') && (c <= 'Z')) || ((c >= 'a') && (c <= 'z'))) {
                    flagCheck = true;
                    break;
                }
            }
            if (!flagCheck)
                return true;
        } else {
            //第二种方法，检测是否时MethodDeclaration
            while ((cur != null) && (!(cur instanceof MethodDeclaration))) {
                cur = cur.getParent();
            }
            if (cur != null) {
                checkASTNode = cur;
            } else {
                //第三种方法，也是出一个ASTNode
                cur = mp.getStatement();
                CompilationUnit compilationUnit = (CompilationUnit) access.getRoot();
                int lineNode = compilationUnit.getLineNumber(access.getStartPosition());
                while (cur != null) {
                    CompilationUnit compilationUnitIn = (CompilationUnit) cur.getRoot();
                    int lineCur = compilationUnitIn.getLineNumber(cur.getStartPosition());
                    if (cur instanceof Block) {
                        checkASTNode = cur;
                    }
                    if ((lineNode - lineCur) > 8) {
                        checkASTNode = cur;
                        break;
                    }
                    cur = cur.getParent();
                }
            }
            if (checkASTNode != null) {

                SimpleNameAndStringAndNumberLiteralCheckASTVisitor simplenameAndStringAndNumberLiteralCheckASTVisitor = new SimpleNameAndStringAndNumberLiteralCheckASTVisitor(access.getArray().toString());
                checkASTNode.accept(simplenameAndStringAndNumberLiteralCheckASTVisitor);
                return simplenameAndStringAndNumberLiteralCheckASTVisitor.getIsIncluded();
            }
        }
        return false;
    }

    //主要用于numberLiteral的范围检查，是否在规定的范围内
    public static boolean rule1OfNumberliteral(ModificationPoint mp, NumberLiteral numberLiteral) {
        ASTNode cur = mp.getStatement();
        ASTNode checkASTNode = null;
        //第二种方法，检测是否时MethodDeclaration
        while ((cur != null) && (!(cur instanceof MethodDeclaration))) {
            cur = cur.getParent();
        }
        if (cur != null) {
            checkASTNode = cur;
        } else {
            //第三种方法，也是出一个ASTNode
            cur = mp.getStatement();
            CompilationUnit compilationUnit = (CompilationUnit) numberLiteral.getRoot();
            int lineNode = compilationUnit.getLineNumber(numberLiteral.getStartPosition());
            while (cur != null) {
                CompilationUnit compilationUnitIn = (CompilationUnit) cur.getRoot();
                int lineCur = compilationUnitIn.getLineNumber(cur.getStartPosition());
                if (cur instanceof Block) {
                    checkASTNode = cur;
                }
                if ((lineNode - lineCur) > 8) {
                    checkASTNode = cur;
                    break;
                }
                cur = cur.getParent();
            }
        }
        if (checkASTNode != null) {

            SimpleNameAndStringAndNumberLiteralCheckASTVisitor simplenameAndStringAndNumberLiteralCheckASTVisitor = new SimpleNameAndStringAndNumberLiteralCheckASTVisitor(numberLiteral.toString());
            checkASTNode.accept(simplenameAndStringAndNumberLiteralCheckASTVisitor);
            return simplenameAndStringAndNumberLiteralCheckASTVisitor.getIsIncluded();
        }
        return false;
    }

    //主要用于stringLiteral的范围检查，是否在规定的范围内
    public static boolean rule1OfStringliteral(ModificationPoint mp, StringLiteral stringLiteral) {
        ASTNode cur = mp.getStatement();
        ASTNode checkASTNode = null;
        //第二种方法，检测是否时MethodDeclaration
        while ((cur != null) && (!(cur instanceof MethodDeclaration))) {
            cur = cur.getParent();
        }
        if (cur != null) {
            checkASTNode = cur;
        } else {
            //第三种方法，也是出一个ASTNode
            cur = mp.getStatement();
            CompilationUnit compilationUnit = (CompilationUnit) stringLiteral.getRoot();
            int lineNode = compilationUnit.getLineNumber(stringLiteral.getStartPosition());
            while (cur != null) {
                CompilationUnit compilationUnitIn = (CompilationUnit) cur.getRoot();
                int lineCur = compilationUnitIn.getLineNumber(cur.getStartPosition());
                if (cur instanceof Block) {
                    checkASTNode = cur;
                }
                if ((lineNode - lineCur) > 8) {
                    checkASTNode = cur;
                    break;
                }
                cur = cur.getParent();
            }
        }
        if (checkASTNode != null) {

            SimpleNameAndStringAndNumberLiteralCheckASTVisitor simplenameAndStringAndNumberLiteralCheckASTVisitor = new SimpleNameAndStringAndNumberLiteralCheckASTVisitor(stringLiteral.getLiteralValue());
            checkASTNode.accept(simplenameAndStringAndNumberLiteralCheckASTVisitor);
            return simplenameAndStringAndNumberLiteralCheckASTVisitor.getIsIncluded();
        }
        return false;
    }

    public static boolean rule1OfMethodInvocation(ModificationPoint mp, MethodInvocation methodInvocation) {
        //

        //1.如果node参数为空则返回真；
        //2.如果不为真，则检查参数。
        /*
         * ---2.1检查是否存在simpleName , 若不存在则直接返回 真
         * ---------- 首先检查是否是全局变量。若是则直接要
         * ---------- 否则，按之前检查SimpleName的方法过滤
         */

        MethodInvocationNum methodInvocationNum = new MethodInvocationNum(methodInvocation.getName().toString());
        CompilationUnit compilationUnit = (CompilationUnit) mp.getStatement().getRoot();
        compilationUnit.accept(methodInvocationNum);
        if (methodInvocationNum.getNum() <= 2)
            return false;

        List methodArguments = methodInvocation.arguments();
        boolean flagIn = false;
        if (methodArguments.size() > 0) {
            for (Object o : methodArguments) {
                ASTNode astNode = (ASTNode) o;
                if ((astNode instanceof SimpleName)) {
                    flagIn = rule2OfSimpleName(mp, (SimpleName) astNode);
                    if (!flagIn)
                        break;
                } else if (astNode instanceof FieldAccess) {
                    flagIn = rule2OfFieldAccess(mp, (FieldAccess) astNode);
                    if (!flagIn)
                        break;
                } else if (astNode instanceof InfixExpression) {
                    InfixExpressionCheck infixExpressionCheck = new InfixExpressionCheck(astNode.toString());
                    mp.getStatement().accept(infixExpressionCheck);
                    flagIn = infixExpressionCheck.getFlag();
                    if (!flagIn)
                        break;
                }
            }
            return flagIn;
        } else
            return true;
    }

    public static boolean rule2OfFieldAccess(ModificationPoint mp, FieldAccess fieldAccess) {


        String[] arrayOfQualified = fieldAccess.toString().split("\\.");
        for (String s : arrayOfQualified) {
            if (mp.getGlobalVariableName().contains(s))
                return true;
        }
        if (mp.getGlobalVariableName().contains(fieldAccess.toString()))
            return true;
        else {
            ASTNode cur = mp.getStatement();
            ASTNode checkASTNode = null;
            //第二种方法，检测是否时MethodDeclaration
            while ((cur != null) && (!(cur instanceof MethodDeclaration))) {
                cur = cur.getParent();
            }
            if (cur != null) {
                checkASTNode = cur;
            } else {
                //第三种方法，也是出一个ASTNode
                cur = mp.getStatement();
                CompilationUnit compilationUnit = (CompilationUnit) fieldAccess.getRoot();
                int lineNode = compilationUnit.getLineNumber(fieldAccess.getStartPosition());
                while (cur != null) {
                    CompilationUnit compilationUnitIn = (CompilationUnit) cur.getRoot();
                    int lineCur = compilationUnitIn.getLineNumber(cur.getStartPosition());
                    if (cur instanceof Block) {
                        checkASTNode = cur;
                    }
                    if ((lineNode - lineCur) > 8) {
                        checkASTNode = cur;
                        break;
                    }
                    cur = cur.getParent();
                }
            }
            if (checkASTNode != null) {

                FieldAccessCheckASTVisitor fieldAccessCheckASTVisitor = new FieldAccessCheckASTVisitor(fieldAccess.toString());
                checkASTNode.accept(fieldAccessCheckASTVisitor);
                return fieldAccessCheckASTVisitor.getIsIncluded();
            }
        }
        return false;
    }

    public static boolean rule1OfQualifiedName(ModificationPoint mp, QualifiedName qualifiedName) {
        List<String> list = mp.getGlobalVariableName();
        if (list.contains(qualifiedName.toString()))
            return true;
        else {
            String[] arrayOfQualified = qualifiedName.toString().split("\\.");
            for (String s : arrayOfQualified) {
                if (list.contains(s))
                    return true;
            }
        }
        return false;
    }

    public static boolean rule1OfMethodName(ModificationPoint mp, String staMethodName, String ingreMehodName) {
        List<String> methodName = mp.getMethodName();
        if (methodName.contains(staMethodName) && methodName.contains(ingreMehodName)) {
            List<Type> staReturnType ;
            List<Type> ingreReturnType;

            MethodNameFind methodNameFind = new MethodNameFind(staMethodName);
            CompilationUnit compilationUnit = (CompilationUnit) mp.getStatement().getRoot();
            compilationUnit.accept(methodNameFind);
            staReturnType = methodNameFind.getReturnTypeCheck();

            methodNameFind.setMethodName(ingreMehodName);
            compilationUnit.accept(methodNameFind);
            ingreReturnType = methodNameFind.getReturnTypeCheck();
            if ((ingreReturnType!=null) && (staReturnType!=null)){
                for (Type staType:staReturnType){
                    for (Type ingreType:ingreReturnType) {
                        boolean equals = staType.toString().equals(ingreType.toString());
                        if ((staType instanceof PrimitiveType)
                                &&(ingreType instanceof PrimitiveType)
                                && equals){
                            return true;
                        }else if ((staType instanceof ArrayType)
                                &&(ingreType instanceof ArrayType)
                                && equals){
                            return true;
                        }else if ((staType instanceof SimpleType)
                                &&(ingreType instanceof SimpleType)) {
                            return true;
                        }else if (equals)
                            return true;
                    }
                }
                return false;
            }else
                return true;
        } else
            return true;
    }

    //当simpleName名与函数调用互换时需要满足的要求
    public static boolean rule1OfNameAndMethodInvocation(ModificationPoint mp, String name, String method) {


        CompilationUnit compilationUnit = (CompilationUnit) mp.getStatement().getRoot();
        //找变量名的类型,为数组，可能多多个定义（全局、方法内）

        List<String> arrayNameList =  new ArrayList<>();
        if (name.split("\\.").length>1){
            String[] arrayName = name.split("\\.");
            arrayNameList.addAll(Arrays.asList(arrayName));
        }
        arrayNameList.add(name);
        arrayNameList.add(name);
        List<Type> nameTypeList = new ArrayList<>();
        for (String sMid:arrayNameList) {
            SimpleNameTypeFind simpleNameTypeFind = new SimpleNameTypeFind(sMid);
            compilationUnit.accept(simpleNameTypeFind);
            nameTypeList.addAll(simpleNameTypeFind.getTypelist());
        }

        //方法找返回类型
        MethodNameFind methodNameFind = new MethodNameFind(method);
        compilationUnit.accept(methodNameFind);
        List<Type> methodTypeList = methodNameFind.getReturnTypeCheck();

        if (methodTypeList.size()>0){
            for (Type typeMethod:methodTypeList){
                for (Type typeName:methodTypeList) {
                    boolean equals = typeMethod.toString().equals(typeName.toString());
                    if ((typeMethod instanceof PrimitiveType)
                            &&(typeName instanceof PrimitiveType)
                            && equals){
                        return true;
                    }else if ((typeMethod instanceof ArrayType)
                            &&(typeName instanceof ArrayType)
                            && equals){
                        return true;
                    }else if ((typeMethod instanceof SimpleType)
                            &&(typeName instanceof SimpleType)) {
                        return true;
                    }else if (equals)
                        return true;
                }
            }
            return false;
        }else
            return true;
    }

    public static boolean changeNameRuleAll(ModificationPoint mp ,String simpleName,Expression expression){
        String expressionStr = expression.toString();
        boolean f1 = (mp.getVariableName().contains(simpleName) && mp.getVariableName().contains(expressionStr));
        boolean f2 = ((expression instanceof ArrayAccess)
                      || (expression instanceof QualifiedName)
                      ||(expression instanceof FieldAccess));
        boolean f3 = RuleCheck.rule1OfMethodName(mp,simpleName,expressionStr);
        boolean f4 = RuleCheck.rule1OfNameAndMethodInvocation(mp,simpleName,expressionStr);
        if (expression instanceof MethodInvocation)
            return f4;
        else if (mp.getMethodName().contains(simpleName) && mp.getMethodName().contains(expressionStr))
            return f3;
        else
            return (f1 || f2 );
    }




    public static boolean rule1OfReturnStatement(ModificationPoint mp, Expression statementExpression, Expression ingredientExpression) {

        boolean f1 = (statementExpression.getNodeType() == ingredientExpression.getNodeType());
        boolean f2 = (ingredientExpression instanceof FieldAccess);
        boolean f3 = (ingredientExpression instanceof MethodInvocation);
        boolean f4 = ((ingredientExpression instanceof StringLiteral) || (ingredientExpression instanceof NumberLiteral));
        boolean f5 = (ingredientExpression instanceof ArrayAccess);
        boolean f6 = ((ingredientExpression instanceof SimpleName) && (!mp.getMethodName().contains(ingredientExpression.toString())));
        boolean f7 = ((ingredientExpression instanceof SimpleName) && (mp.getMethodName().contains(ingredientExpression.toString()))
                && (statementExpression instanceof SimpleName) && (mp.getMethodName().contains(statementExpression.toString())));
        boolean f8 = (ingredientExpression instanceof NullLiteral);
        return (f1 || f2 || f3 || f4 || f5 || f6 || f7 || f8);
    }


}

class SimpleNameAndStringAndNumberLiteralCheckASTVisitor extends ASTVisitor {
    private volatile boolean isIncluded = false;
    private String temp = "";

    public SimpleNameAndStringAndNumberLiteralCheckASTVisitor(String temp) {
        this.temp = temp;
    }

    @Override
    public boolean visit(FieldAccess node) {
        if (node.toString().equals(temp))
            isIncluded = true;
        return super.visit(node);
    }

    @Override
    public boolean visit(SimpleName node) {
        if (node.toString().equals(temp))
            isIncluded = true;
        return super.visit(node);
    }

    @Override
    public boolean visit(ArrayAccess node) {
        if (node.getArray().toString().equals(temp))
            isIncluded = true;
        return super.visit(node);
    }

    @Override
    public boolean visit(NumberLiteral node) {
        if (node.toString().equals(temp))
            isIncluded = true;
        return super.visit(node);
    }

    @Override
    public boolean visit(StringLiteral node) {
        if (node.getLiteralValue().equals(temp))
            isIncluded = true;
        return super.visit(node);
    }

    public boolean getIsIncluded() {
        return isIncluded;
    }
}


class FieldAccessCheckASTVisitor extends ASTVisitor {
    private volatile boolean isIncluded = false;
    private String temp = "";

    public FieldAccessCheckASTVisitor(String temp) {
        this.temp = temp;
    }

    @Override
    public boolean visit(FieldAccess node) {
        if (node.toString().equals(temp))
            isIncluded = true;
        return super.visit(node);
    }

    public boolean getIsIncluded() {
        return isIncluded;
    }
}

class MethodInvocationNum extends ASTVisitor {
    String methodName = "";
    int num = 0;

    public MethodInvocationNum(String methodName) {
        this.methodName = methodName;
    }

    @Override
    public boolean visit(MethodInvocation node) {
        if (node.getName().toString().equals(methodName))
            num++;
        return super.visit(node);
    }

    public int getNum() {
        return num;
    }
}

class InfixExpressionCheck extends ASTVisitor {
    String infixStr = "";
    boolean flag = false;

    public InfixExpressionCheck(String infixStr) {
        this.infixStr = infixStr;
    }

    @Override
    public boolean visit(InfixExpression node) {
        if ((infixStr.indexOf(node.getLeftOperand().toString()) > 0) ||
                (infixStr.indexOf(node.getRightOperand().toString()) > 0)) {
            flag = true;
        }
        return super.visit(node);
    }

    public boolean getFlag() {
        return flag;
    }
}

class MethodNameFind extends ASTVisitor {
    private volatile List<Type> returnTypeCheck = new ArrayList<>();
    private String methodName = "";

    public MethodNameFind(String methodName) {
        this.methodName = methodName;
    }

    @Override
    public boolean visit(MethodDeclaration node) {
        if (node.getName().toString().equals(methodName)) {
            if ((node.getReturnType2() != null) && (!returnTypeCheck.contains(node.getReturnType2())))
                returnTypeCheck.add(node.getReturnType2());
        }
        return super.visit(node);
    }

    public List<Type> getReturnTypeCheck() {
        return returnTypeCheck;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
        returnTypeCheck = new ArrayList<>();
    }
}

class SimpleNameTypeFind extends ASTVisitor {
    private volatile List<Type> typelist = new ArrayList<>();
    private String tmp = "";

    public SimpleNameTypeFind(String tmp) {
        this.tmp = tmp;
    }

    @Override
    public boolean visit(FieldDeclaration node) {

        for (Object obj : node.fragments()) {
            VariableDeclarationFragment v = (VariableDeclarationFragment) obj;
            String varName = v.getName().toString();
            if (varName.equals(tmp) && (node.getType() != null) && (!typelist.contains(node.getType())))
                typelist.add(node.getType());
        }
        return true;
    }

    @Override
    public boolean visit(MethodDeclaration node) {
        List<SingleVariableDeclaration> parameters = node.parameters();
        for (SingleVariableDeclaration parameter : parameters) {
            Type parameterType = parameter.getType();
            String parameterName = parameter.getName().toString();
            if (parameterName.equals(tmp) && (!typelist.contains(parameterType)))
                typelist.add(parameterType);
        }
        return true;
    }

    @Override
    public boolean visit(VariableDeclarationStatement node) {
        for (Object obj : node.fragments()) {
            VariableDeclarationFragment v = (VariableDeclarationFragment) obj;
            Type varType = node.getType();
            String varName = v.getName().toString();
            if (varName.equals(tmp) && (!typelist.contains(varType)))
                typelist.add(varType);
        }
        return true;
    }

    public List<Type> getTypelist() {
        return typelist;
    }
}