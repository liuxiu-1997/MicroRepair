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
import java.util.Objects;

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
    public boolean preVisit2(ASTNode node) {
        return super.preVisit2(node);
    }

    @Override
    public void postVisit(ASTNode node) {
        super.postVisit(node);
    }

    @Override
    public boolean visit(AnnotationTypeDeclaration node) {
        return super.visit(node);
    }

    @Override
    public boolean visit(AnnotationTypeMemberDeclaration node) {
        return super.visit(node);
    }

    @Override
    public boolean visit(AnonymousClassDeclaration node) {
        return super.visit(node);
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
    public boolean visit(ArrayInitializer node) {
        return super.visit(node);
    }

    @Override
    public boolean visit(ArrayType node) {
        return super.visit(node);
    }

    @Override
    public boolean visit(AssertStatement node) {
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
    public boolean visit(Block node) {
        return super.visit(node);
    }

    @Override
    public boolean visit(BlockComment node) {
        return super.visit(node);
    }

    @Override
    public boolean visit(BooleanLiteral node) {
        return super.visit(node);
    }

    @Override
    public boolean visit(BreakStatement node) {
        return super.visit(node);
    }

    @Override
    public boolean visit(CastExpression node) {
        return super.visit(node);
    }

    @Override
    public boolean visit(CatchClause node) {
        return super.visit(node);
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
    public boolean visit(CompilationUnit node) {
        return super.visit(node);
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
    public boolean visit(ConstructorInvocation node) {
        return super.visit(node);
    }

    @Override
    public boolean visit(ContinueStatement node) {
        return super.visit(node);
    }

    @Override
    public boolean visit(CreationReference node) {
        return super.visit(node);
    }

    @Override
    public boolean visit(Dimension node) {
        return super.visit(node);
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
    public boolean visit(EnumConstantDeclaration node) {
        return super.visit(node);
    }

    @Override
    public boolean visit(EnumDeclaration node) {
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
    public boolean visit(ExpressionStatement node) {
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
            list.add(new ExpressionInfo(v.getName(), methClaPacOfExpName, lineAndNodeType, node.getType(), varName));
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
    public boolean visit(ImportDeclaration node) {
        return super.visit(node);
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
    public boolean visit(Initializer node) {

        return super.visit(node);
    }

    @Override
    public boolean visit(InstanceofExpression node) {
        return super.visit(node);
    }

    @Override
    public boolean visit(IntersectionType node) {
        return super.visit(node);
    }

    @Override
    public boolean visit(Javadoc node) {
        return super.visit(node);
    }

    @Override
    public boolean visit(LabeledStatement node) {
        return super.visit(node);
    }

    @Override
    public boolean visit(LambdaExpression node) {

        return super.visit(node);
    }

    @Override
    public boolean visit(LineComment node) {
        return super.visit(node);
    }

    @Override
    public boolean visit(MarkerAnnotation node) {
        return super.visit(node);
    }

    @Override
    public boolean visit(MemberRef node) {
        return super.visit(node);
    }

    @Override
    public boolean visit(MemberValuePair node) {
        return super.visit(node);
    }

    @Override
    public boolean visit(MethodRef node) {
        return super.visit(node);
    }

    @Override
    public boolean visit(MethodRefParameter node) {
        return super.visit(node);
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

    @Override
    public boolean visit(Modifier node) {
        return super.visit(node);
    }

    @Override
    public boolean visit(NameQualifiedType node) {
        return super.visit(node);
    }

    @Override
    public boolean visit(NormalAnnotation node) {
        return super.visit(node);
    }

    @Override
    public boolean visit(NullLiteral node) {
        return super.visit(node);
    }

    @Override
    public boolean visit(NumberLiteral node) {

        list.add(new ExpressionInfo(node, methClaPacOfExpName, lineAndNodeType));
        return super.visit(node);
    }

    @Override
    public boolean visit(PackageDeclaration node) {
        return super.visit(node);
    }

    @Override
    public boolean visit(ParameterizedType node) {
        return super.visit(node);
    }

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
    public boolean visit(QualifiedName node) {
        return super.visit(node);
    }

    @Override
    public boolean visit(QualifiedType node) {
        return super.visit(node);
    }


    @Override
    public boolean visit(ReturnStatement node) {
        Expression expression = node.getExpression();
        if (expression != null)
            list.add(new ExpressionInfo(expression, methClaPacOfExpName, lineAndNodeType));
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
    public boolean visit(SimpleType node) {
        return super.visit(node);
    }

    @Override
    public boolean visit(SingleMemberAnnotation node) {
        return super.visit(node);
    }

    @Override
    public boolean visit(SingleVariableDeclaration node) {

        Expression expression = node.getInitializer();
        if (expression != null)
            list.add(new ExpressionInfo(expression, methClaPacOfExpName, lineAndNodeType));
        return true;
    }

    @Override
    public boolean visit(StringLiteral node) {
       list.add(new ExpressionInfo(node,methClaPacOfExpName,lineAndNodeType));
        return false;
    }

    @Override
    public boolean visit(SuperConstructorInvocation node) {
        return super.visit(node);
    }

    @Override
    public boolean visit(SuperFieldAccess node) {
        return super.visit(node);
    }

    @Override
    public boolean visit(SuperMethodInvocation node) {
        return super.visit(node);
    }

    @Override
    public boolean visit(SuperMethodReference node) {
        return super.visit(node);
    }

    @Override
    public boolean visit(SwitchCase node) {
        return super.visit(node);
    }


    @Override
    public boolean visit(SwitchStatement node) {
        return super.visit(node);
    }

    @Override
    public boolean visit(SynchronizedStatement node) {
        return super.visit(node);
    }

    @Override
    public boolean visit(TagElement node) {
        return super.visit(node);
    }


    @Override
    public boolean visit(TextElement node) {
        return super.visit(node);
    }

    @Override
    public boolean visit(ThisExpression node) {
        return super.visit(node);
    }

    @Override
    public boolean visit(ThrowStatement node) {
        return super.visit(node);
    }

    @Override
    public boolean visit(TryStatement node) {
        return super.visit(node);
    }

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
    public boolean visit(TypeDeclarationStatement node) {
        return super.visit(node);
    }

    @Override
    public boolean visit(TypeLiteral node) {
        return super.visit(node);
    }

    @Override
    public boolean visit(TypeMethodReference node) {
        return super.visit(node);
    }

    @Override
    public boolean visit(TypeParameter node) {

        return super.visit(node);
    }

    @Override
    public boolean visit(UnionType node) {
        return super.visit(node);
    }

    @Override
    public boolean visit(VariableDeclarationExpression node) {

        return super.visit(node);
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

    @Override
    public boolean visit(WildcardType node) {
        return super.visit(node);
    }

    @Override
    public void endVisit(AnnotationTypeDeclaration node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(AnnotationTypeMemberDeclaration node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(AnonymousClassDeclaration node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(ArrayAccess node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(ArrayCreation node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(ArrayInitializer node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(ArrayType node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(AssertStatement node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(Assignment node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(Block node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(BlockComment node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(BooleanLiteral node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(BreakStatement node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(CastExpression node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(CatchClause node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(CharacterLiteral node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(ClassInstanceCreation node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(CompilationUnit node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(ConditionalExpression node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(ConstructorInvocation node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(ContinueStatement node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(CreationReference node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(DoStatement node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(EmptyStatement node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(EnhancedForStatement node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(EnumConstantDeclaration node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(EnumDeclaration node) {
        super.endVisit(node);
    }


    @Override
    public void endVisit(ExpressionMethodReference node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(ExpressionStatement node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(Dimension node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(FieldAccess node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(FieldDeclaration node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(ForStatement node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(IfStatement node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(ImportDeclaration node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(InfixExpression node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(InstanceofExpression node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(Initializer node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(Javadoc node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(LabeledStatement node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(LambdaExpression node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(LineComment node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(MarkerAnnotation node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(MemberRef node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(MemberValuePair node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(MethodRef node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(MethodRefParameter node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(MethodDeclaration node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(MethodInvocation node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(Modifier node) {
        super.endVisit(node);
    }


    @Override
    public void endVisit(NameQualifiedType node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(NormalAnnotation node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(NullLiteral node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(NumberLiteral node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(PackageDeclaration node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(ParameterizedType node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(ParenthesizedExpression node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(PostfixExpression node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(PrefixExpression node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(PrimitiveType node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(QualifiedName node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(QualifiedType node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(ReturnStatement node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(SimpleName node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(SimpleType node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(SingleMemberAnnotation node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(SingleVariableDeclaration node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(StringLiteral node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(SuperConstructorInvocation node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(SuperFieldAccess node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(SuperMethodInvocation node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(SuperMethodReference node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(SwitchCase node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(SwitchStatement node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(SynchronizedStatement node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(TagElement node) {
        super.endVisit(node);
    }


    @Override
    public void endVisit(TextElement node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(ThisExpression node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(ThrowStatement node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(TryStatement node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(TypeDeclaration node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(TypeDeclarationStatement node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(TypeLiteral node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(TypeMethodReference node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(TypeParameter node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(UnionType node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(IntersectionType node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(VariableDeclarationExpression node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(VariableDeclarationStatement node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(VariableDeclarationFragment node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(WhileStatement node) {
        super.endVisit(node);
    }

    @Override
    public void endVisit(WildcardType node) {
        super.endVisit(node);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    public List<ExpressionInfo> getList() {

        display();
        return listfinal;
    }

    //此函数目的是为了去重
    public void display() {
        for (ExpressionInfo expressionInfo : list) {
            if (!listfinal.contains(expressionInfo) && (expressionInfo.getExpression().getNodeType() != ASTNode.NULL_LITERAL)) {
                listfinal.add(expressionInfo);
            }
        }
    }

}

