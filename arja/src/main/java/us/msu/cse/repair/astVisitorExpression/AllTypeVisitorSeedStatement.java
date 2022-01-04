package us.msu.cse.repair.astVisitorExpression;

import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.Expression;
import us.msu.cse.repair.core.parser.ModificationPoint;
import us.msu.cse.repair.informationExpression.ExpressionInfo;
import us.msu.cse.repair.informationExpression.LineAndNodeType;
import us.msu.cse.repair.informationExpression.MethClaPacOfExpName;
import us.msu.cse.repair.toolsExpression.OperatorFilterPreAndIn;
import us.msu.cse.repair.toolsExpression.TypeInformation;

import java.util.ArrayList;
import java.util.List;

/**
 * 对种子语句进行扫描，提取成分
 */

public class AllTypeVisitorSeedStatement extends ASTVisitor {
    private IfStatement ifStatement;
    private volatile List<ExpressionInfo> list = new ArrayList<>();
    private volatile List<ExpressionInfo> listfinal = new ArrayList<>();
    private MethClaPacOfExpName methClaPacOfExpName = new MethClaPacOfExpName();
    private LineAndNodeType lineAndNodeType = new LineAndNodeType();


    public AllTypeVisitorSeedStatement(MethClaPacOfExpName methClaPacOfExpName, LineAndNodeType lineAndNodeType) {
        this.methClaPacOfExpName = methClaPacOfExpName;
        this.lineAndNodeType = lineAndNodeType;
    }
    public AllTypeVisitorSeedStatement(ModificationPoint mp){
        this.methClaPacOfExpName = mp.getMethClaPacOfExpName();
        this.lineAndNodeType = mp.getLineAndNodeType();
    }

    @Override
    public void preVisit(ASTNode node) {
        /**
         *这里的目的是提取种子语句所在方法的参数信息
         */
        ASTNode curNode = node;
        while ((curNode != null) && (!(curNode instanceof MethodDeclaration))) {
            curNode = curNode.getParent();
        }
        if ((curNode != null)) {
            MethodDeclaration methodDeclaration = (MethodDeclaration) curNode;
            if (!(methodDeclaration.getName().toString().equals(methClaPacOfExpName.expressionMethodName))) {
                MethClaPacOfExpName methClaPacOfExpNameMid = new MethClaPacOfExpName();
                methClaPacOfExpNameMid.setExpressionClassName(methClaPacOfExpNameMid.expressionClassName);
                methClaPacOfExpNameMid.setExpressionMethodName(methodDeclaration.getName().toString());
                methClaPacOfExpNameMid.setExpressionPackageName(methClaPacOfExpName.expressionPackageName);
                for (Object obj : methodDeclaration.parameters()) {
                    SingleVariableDeclaration vd = (SingleVariableDeclaration) obj;
                    if (vd != null) {
                        list.add(new ExpressionInfo(vd.getName(), methClaPacOfExpName, lineAndNodeType, vd.getType(), vd.getName().toString()));
                    }
                }

            } else {
                for (Object obj : methodDeclaration.parameters()) {
                    SingleVariableDeclaration vd = (SingleVariableDeclaration) obj;
                    if (vd != null) {
                        list.add(new ExpressionInfo(vd.getName(), methClaPacOfExpName, lineAndNodeType, vd.getType(), vd.getName().toString()));
                    }
                }
            }
        }

    }

    @Override
    public boolean visit(ArrayAccess node) {
        ExpressionInfo expressionInfo = TypeInformation.getArrayAccessTypeInfo(node);
        if (expressionInfo!=null){
            expressionInfo.setMethClaPacOfExpName(methClaPacOfExpName);
            expressionInfo.setLineAndNodeType(lineAndNodeType);
            list.add(expressionInfo);
        }
        return true;
    }

    @Override
    public boolean visit(ArrayCreation node) {
        list.add(new ExpressionInfo(node,methClaPacOfExpName,lineAndNodeType,node.getType(),node.dimensions().toString()));
        return super.visit(node);
    }

    @Override
    public boolean visit(Assignment node) {
        Expression expressionLeft = node.getLeftHandSide();
        Expression expressionRight = node.getRightHandSide();
        if (expressionLeft != null) {
            list.add(new ExpressionInfo(expressionLeft, methClaPacOfExpName, lineAndNodeType));
            if (expressionLeft instanceof Name) {
                ExpressionInfo expressionInfo = TypeInformation.getTypeInformation((Name) expressionLeft, methClaPacOfExpName, lineAndNodeType);
                if (expressionInfo != null)
                    list.add(expressionInfo);
            }
        }
        if (expressionRight != null) {
            list.add(new ExpressionInfo(expressionRight, methClaPacOfExpName, lineAndNodeType));
            if (expressionRight instanceof Name) {
                ExpressionInfo expressionInfo = TypeInformation.getTypeInformation((Name) expressionRight, methClaPacOfExpName, lineAndNodeType);
                if (expressionInfo != null)
                    list.add(expressionInfo);
            }
        }

        return true;
    }

    @Override
    public boolean visit(CharacterLiteral node) {
        list.add(new ExpressionInfo(node,methClaPacOfExpName,lineAndNodeType));
        return false;
    }

    @Override
    public boolean visit(ClassInstanceCreation node) {
        list.add(new ExpressionInfo(node,methClaPacOfExpName,lineAndNodeType,node.getType(),node.toString()));
        return true;
    }

    @Override
    public boolean visit(ConditionalExpression node) {
        Expression expressionElse = node.getElseExpression();
        Expression expressionThen = node.getThenExpression();
        Expression expression = node.getExpression();
        if (expression != null) {
            list.add(new ExpressionInfo(expression, methClaPacOfExpName, lineAndNodeType));
            if (expression instanceof Name) {
                ExpressionInfo expressionInfo = TypeInformation.getTypeInformation((Name) expression, methClaPacOfExpName, lineAndNodeType);
                if (expressionInfo != null)
                    list.add(expressionInfo);
            }
        }
        if (expressionElse != null) {
            list.add(new ExpressionInfo(expressionElse, methClaPacOfExpName, lineAndNodeType));
            if (expressionElse instanceof Name) {
                ExpressionInfo expressionInfo = TypeInformation.getTypeInformation((Name) expressionElse, methClaPacOfExpName, lineAndNodeType);
                if (expressionInfo != null)
                    list.add(expressionInfo);
            }
        }
        if (expressionThen != null) {
            list.add(new ExpressionInfo(expressionThen, methClaPacOfExpName, lineAndNodeType));
            if (expressionThen instanceof Name) {
                ExpressionInfo expressionInfo = TypeInformation.getTypeInformation((Name) expressionThen, methClaPacOfExpName, lineAndNodeType);
                if (expressionInfo != null)
                    list.add(expressionInfo);
            }
        }
        return true;
    }

    @Override
    public boolean visit(DoStatement node) {
        Expression expression = node.getExpression();
        if (expression != null) {
            list.add(new ExpressionInfo(expression, methClaPacOfExpName, lineAndNodeType));
            if (expression instanceof Name) {
                ExpressionInfo expressionInfo = TypeInformation.getTypeInformation((Name) expression, methClaPacOfExpName, lineAndNodeType);
                if (expressionInfo != null)
                    list.add(expressionInfo);
            }
        }
        return true;
    }

    @Override
    public boolean visit(EmptyStatement node) {
        return super.visit(node);
    }

    @Override
    public boolean visit(EnhancedForStatement node) {
        Expression expression = node.getExpression();
        if (expression instanceof Name) {
            ExpressionInfo expressionInfo = TypeInformation.getTypeInformation((Name) expression, methClaPacOfExpName, lineAndNodeType);
            if (expressionInfo != null)
                list.add(expressionInfo);
        }
        return super.visit(node);
    }

    @Override
    public boolean visit(ExpressionMethodReference node) {
        Expression expression = node.getExpression();
        if (expression != null) {
            list.add(new ExpressionInfo(expression, methClaPacOfExpName, lineAndNodeType));
            if (expression instanceof Name) {
                ExpressionInfo expressionInfo = TypeInformation.getTypeInformation((Name) expression, methClaPacOfExpName, lineAndNodeType);
                if (expressionInfo != null)
                    list.add(expressionInfo);
            }
        }
        return true;
    }

    @Override
    public boolean visit(FieldAccess node) {
        list.add(new ExpressionInfo(node, methClaPacOfExpName, lineAndNodeType));
        ExpressionInfo expressionInfo = TypeInformation.getTypeInformation(node.getName(), methClaPacOfExpName, lineAndNodeType);
        if (expressionInfo != null)
            list.add(expressionInfo);

        return true;
    }

    @Override
    public boolean visit(FieldDeclaration node) {
        for (Object obj : node.fragments()) {
            VariableDeclarationFragment v = (VariableDeclarationFragment) obj;
            String varName = v.getName().toString();
            ExpressionInfo expressionInfo = new ExpressionInfo(v.getName(), methClaPacOfExpName, lineAndNodeType, node.getType(), varName);
            list.add(expressionInfo);
        }
        return true;
    }

    @Override
    public boolean visit(ForStatement node) {
        Expression expression = node.getExpression();
        if (expression != null)
            list.add(new ExpressionInfo(expression, methClaPacOfExpName, lineAndNodeType));

        return true;
    }

    @Override
    public boolean visit(IfStatement node) {
        String s = "ifexpression";
        list.add(new ExpressionInfo(node.getExpression(),methClaPacOfExpName,lineAndNodeType,s));
        Expression expression = node.getExpression();
        if (expression instanceof InfixExpression){
            Expression expL = ((InfixExpression) expression).getLeftOperand();
            Expression expR = ((InfixExpression) expression).getRightOperand();
            if (expL instanceof Name) {
                ExpressionInfo expressionInfo = TypeInformation.getTypeInformation((Name) expL, methClaPacOfExpName, lineAndNodeType);
                if (expressionInfo != null)
                    list.add(expressionInfo);
            }
            if (expR instanceof Name) {
                ExpressionInfo expressionInfo = TypeInformation.getTypeInformation((Name) expR, methClaPacOfExpName, lineAndNodeType);
                if (expressionInfo != null)
                    list.add(expressionInfo);
            }
        }else if (expression instanceof PrefixExpression){
            Expression expR = ((PrefixExpression)expression).getOperand();
            if (expR instanceof Name) {
                ExpressionInfo expressionInfo = TypeInformation.getTypeInformation((Name) expR, methClaPacOfExpName, lineAndNodeType);
                if (expressionInfo != null)
                    list.add(expressionInfo);
            }

        }else if (expression instanceof PostfixExpression){
            Expression expL = ((PostfixExpression) expression).getOperand();
            if (expL instanceof Name) {
                ExpressionInfo expressionInfo = TypeInformation.getTypeInformation((Name) expL, methClaPacOfExpName, lineAndNodeType);
                if (expressionInfo != null)
                    list.add(expressionInfo);
            }
        }
        return true;
    }



    @Override
    public boolean visit(InfixExpression node) {
        Expression expL = node.getLeftOperand();
        Expression expR = node.getRightOperand();
        if (expL instanceof Name){
            list.add(new ExpressionInfo(expL, methClaPacOfExpName, lineAndNodeType));
        }
        if (expR instanceof Name){
            list.add(new ExpressionInfo(expR, methClaPacOfExpName, lineAndNodeType));
        }
        return true;
    }

    @Override
    public boolean visit(MethodDeclaration node) {
        String methodName = node.getName().getFullyQualifiedName();
        List<SingleVariableDeclaration> parameters = node.parameters();
        for (SingleVariableDeclaration parameter : parameters) {
            Type parameterType = parameter.getType();
            String parameterName = parameter.getName().toString();
            list.add(new ExpressionInfo(parameter.getName(), methClaPacOfExpName, lineAndNodeType,
                    parameterType, parameterName));
        }
        return true;
    }

    @Override
    public boolean visit(MethodInvocation node) {
        Expression expression = node.getExpression();
        list.add(new ExpressionInfo(node,methClaPacOfExpName,lineAndNodeType,"methodinvocation"));
        if (expression != null && OperatorFilterPreAndIn.ExpressionFilterReturn(expression))
            list.add(new ExpressionInfo(expression, methClaPacOfExpName, lineAndNodeType));
        return true;
    }

//    @Override
//    public boolean visit(NumberLiteral node) {
//        list.add(new ExpressionInfo(node, methClaPacOfExpName, lineAndNodeType));
//        return super.visit(node);
//    }

    @Override
    public boolean visit(ParenthesizedExpression node) {
        if (node != null && OperatorFilterPreAndIn.ExpressionFilterReturn(node.getExpression()))
            list.add(new ExpressionInfo(node.getExpression(), methClaPacOfExpName, lineAndNodeType));
        return true;
    }

    @Override
    public boolean visit(PostfixExpression node) {
        Expression expL = node.getOperand();
        if (expL instanceof Name){
            list.add(new ExpressionInfo(expL, methClaPacOfExpName, lineAndNodeType));
        }
        list.add(new ExpressionInfo(node, methClaPacOfExpName, lineAndNodeType));
        return true;
    }

    @Override
    public boolean visit(PrefixExpression node) {
        list.add(new ExpressionInfo(node, methClaPacOfExpName, lineAndNodeType));
        return true;
    }

    //用于变量的提取
    @Override
    public boolean visit(PrimitiveType node) {
        String methString = null; //用于存放方法名，确定是否在一个方法最好
        ASTNode curNode = node;
        while (!(curNode instanceof MethodDeclaration) && (curNode != null)) {
            curNode = curNode.getParent();
        }
        if ((curNode != null)) {
            MethodDeclaration me = (MethodDeclaration) curNode;
            methString = me.getName().toString();
        }

        if (node.getParent() instanceof VariableDeclarationStatement) {
            VariableDeclarationStatement vb = (VariableDeclarationStatement) node.getParent();

            List list1 = vb.fragments();
            String strings = list1.get(0).toString();
            String variableStr = null;
            int num = strings.indexOf("=");
            AST ast = AST.newAST(AST.JLS8);
            StringLiteral stringLiteral = ast.newStringLiteral();
            if (num > 0) {

                variableStr = strings.substring(0, num);
                stringLiteral.setLiteralValue(variableStr);
                list.add(new ExpressionInfo(stringLiteral, methClaPacOfExpName, lineAndNodeType,
                        vb.getType(), variableStr));
            } else {

                variableStr = strings;
                stringLiteral.setLiteralValue(variableStr);
                list.add(new ExpressionInfo(stringLiteral, methClaPacOfExpName, lineAndNodeType,
                        vb.getType(), strings));
            }

        }

        return super.visit(node);
    }

    @Override
    public boolean visit(ReturnStatement node) {
        Expression expression = node.getExpression();
        if (expression != null) {
            list.add(new ExpressionInfo(expression, methClaPacOfExpName, lineAndNodeType));
        }
        return true;
    }

    @Override
    public boolean visit(SimpleName node) {
        ExpressionInfo expressionInfo = TypeInformation.getTypeInformation(node,methClaPacOfExpName,lineAndNodeType);
        if (expressionInfo!=null)
           list.add(expressionInfo);
        return super.visit(node);
    }

    @Override
    public boolean visit(SingleVariableDeclaration node) {

        Expression expression = node.getInitializer();
        if (expression != null)
            list.add(new ExpressionInfo(expression, methClaPacOfExpName, lineAndNodeType));
        return true;
    }

//    @Override
//    public boolean visit(StringLiteral node) {
//        list.add(new ExpressionInfo(node,methClaPacOfExpName,lineAndNodeType));
//        return false;
//    }

    @Override
    public boolean visit(TypeDeclaration node) {

//        FieldDeclaration[] fieldDeclarations = node.getFields();
//        for (FieldDeclaration fieldDeclaration : fieldDeclarations){
//            Type type = fieldDeclaration.getType();
//            String s = fieldDeclaration.toString();
//            AST ast = node.getAST();
//            String s1 = s.substring(1,s.length()-1);
//            if (s1.indexOf("=")>0){
//                s1 = s.substring(1,s.indexOf("=")+1);
//            }
//            SimpleName simpleName = ast.newSimpleName(s1);
//            list.add(new ExpressionInfo(simpleName,simpleName.getNodeType(),statementMethodName,statementClassName,statementPackageName,type));
//        }
        return true;
    }

    @Override
    public boolean visit(VariableDeclarationStatement node) {
        for (Object obj : node.fragments()) {
            VariableDeclarationFragment v = (VariableDeclarationFragment) obj;
            Type varType = node.getType();
            String varName = v.getName().toString();
            list.add(new ExpressionInfo(v.getName(), methClaPacOfExpName, lineAndNodeType,
                    varType, varName));
        }
        return true;
    }

    @Override
    public boolean visit(VariableDeclarationFragment node) {

        Expression expression = node.getInitializer();
        if (expression != null)
            list.add(new ExpressionInfo(expression, methClaPacOfExpName, lineAndNodeType));
        return true;
    }

    @Override
    public boolean visit(WhileStatement node) {

        Expression expression = node.getExpression();
        if (expression != null)
            list.add(new ExpressionInfo(expression, methClaPacOfExpName, lineAndNodeType));
        return true;
    }

    public List<ExpressionInfo> getList() {
        return list;
    }
}

