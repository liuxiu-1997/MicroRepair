package us.msu.cse.repair.astVisitorExpression;

import org.eclipse.jdt.core.dom.*;
import us.msu.cse.repair.core.parser.ModificationPoint;
import us.msu.cse.repair.informationExpression.ExpressionInfo;
import us.msu.cse.repair.informationExpression.LineAndNodeType;
import us.msu.cse.repair.informationExpression.MethClaPacOfExpName;
import us.msu.cse.repair.toolsExpression.OperatorFilterPreAndIn;
import us.msu.cse.repair.toolsExpression.TypeInformation;

import java.util.ArrayList;
import java.util.List;

/**
 *对修改点进行扫描，目的是用于以后的Tbar模板修改
 */
public class AllTypeVisitorModificationPoint extends ASTVisitor {
    private volatile List<ExpressionInfo> list = new ArrayList<>();
    private volatile List<ExpressionInfo> listfinal = new ArrayList<>();
    private MethClaPacOfExpName methClaPacOfExpName = null;
    private LineAndNodeType lineAndNodeType = null;
    private CompilationUnit compilationUnit = null;
    private ModificationPoint mp = null;


    public AllTypeVisitorModificationPoint(ModificationPoint mp, CompilationUnit compilationUnit) {
        this.methClaPacOfExpName = mp.getMethClaPacOfExpName();
        this.lineAndNodeType = mp.getLineAndNodeType();
        this.compilationUnit = compilationUnit;
        this.mp = mp;
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

                        LineAndNodeType lineNode = new LineAndNodeType(vd.getStartPosition(), vd.getNodeType());
                        list.add(new ExpressionInfo(vd.getName(), methClaPacOfExpName, lineNode, vd.getType(), vd.getName().toString()));

                    }
                }

            } else {

                for (Object obj : methodDeclaration.parameters()) {
                    SingleVariableDeclaration vd = (SingleVariableDeclaration) obj;
                    if (vd != null) {
                        LineAndNodeType lineNode = new LineAndNodeType(vd.getStartPosition(), vd.getNodeType());
                        list.add(new ExpressionInfo(vd.getName(), methClaPacOfExpName, lineNode, vd.getType(), vd.getName().toString()));
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
        if (expressionInfo != null) {
            expressionInfo.setMethClaPacOfExpName(methClaPacOfExpName);
            LineAndNodeType lineNode = new LineAndNodeType(node.getStartPosition(), node.getNodeType());
            expressionInfo.setLineAndNodeType(lineNode);
            list.add(expressionInfo);
        }
        return true;
    }

    @Override
    public boolean visit(ArrayCreation node) {

        LineAndNodeType lineNode = new LineAndNodeType(node.getStartPosition(), node.getNodeType());
        list.add(new ExpressionInfo(node, methClaPacOfExpName, lineNode, node.getType(), node.dimensions().toString()));
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
        LineAndNodeType lineNode = new LineAndNodeType(node.getStartPosition(), node.getNodeType());
        if (expressionLeft != null) {
            list.add(new ExpressionInfo(expressionLeft, methClaPacOfExpName, lineNode));
            if (expressionLeft instanceof Name) {
                ExpressionInfo expressionInfo = TypeInformation.getTypeInformation((Name) expressionLeft, methClaPacOfExpName, lineNode);
                if (expressionInfo != null)
                    list.add(expressionInfo);
            }
        }
        if (expressionRight != null) {
            list.add(new ExpressionInfo(expressionRight, methClaPacOfExpName, lineNode));
            if (expressionRight instanceof Name) {
                ExpressionInfo expressionInfo = TypeInformation.getTypeInformation((Name) expressionRight, methClaPacOfExpName, lineNode);
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
        LineAndNodeType lineNode = new LineAndNodeType(node.getStartPosition(), node.getNodeType());
        list.add(new ExpressionInfo(node, methClaPacOfExpName, lineNode));
        return false;
    }

    @Override
    public boolean visit(ClassInstanceCreation node) {
        LineAndNodeType lineNode = new LineAndNodeType(node.getStartPosition(), node.getNodeType());
        list.add(new ExpressionInfo(node, methClaPacOfExpName, lineNode, node.getType(), node.toString()));
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
        LineAndNodeType lineNode = new LineAndNodeType(node.getStartPosition(), node.getNodeType());
        if (expression != null) {
            list.add(new ExpressionInfo(expression, methClaPacOfExpName, lineNode));
            if (expression instanceof Name) {
                ExpressionInfo expressionInfo = TypeInformation.getTypeInformation((Name) expression, methClaPacOfExpName, lineNode);
                if (expressionInfo != null)
                    list.add(expressionInfo);
            }
        }

        if (expressionElse != null) {
            list.add(new ExpressionInfo(expressionElse, methClaPacOfExpName, lineNode));
            if (expressionElse instanceof Name) {
                ExpressionInfo expressionInfo = TypeInformation.getTypeInformation((Name) expressionElse, methClaPacOfExpName, lineNode);
                if (expressionInfo != null)
                    list.add(expressionInfo);
            }
        }
        if (expressionThen != null) {
            list.add(new ExpressionInfo(expressionThen, methClaPacOfExpName, lineNode));
            if (expressionThen instanceof Name) {
                ExpressionInfo expressionInfo = TypeInformation.getTypeInformation((Name) expressionThen, methClaPacOfExpName, lineNode);
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
        LineAndNodeType lineNode = new LineAndNodeType(node.getStartPosition(), node.getNodeType());
        if (expression != null) {
            list.add(new ExpressionInfo(expression, methClaPacOfExpName, lineNode));
            if (expression instanceof Name) {
                ExpressionInfo expressionInfo = TypeInformation.getTypeInformation((Name) expression, methClaPacOfExpName, lineNode);
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
        LineAndNodeType lineNode = new LineAndNodeType(node.getStartPosition(), node.getNodeType());
        if (expression instanceof Name) {
            ExpressionInfo expressionInfo = TypeInformation.getTypeInformation((Name) expression, methClaPacOfExpName, lineNode);
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
        LineAndNodeType lineNode = new LineAndNodeType(node.getStartPosition(), node.getNodeType());
        if (expression != null) {
            list.add(new ExpressionInfo(expression, methClaPacOfExpName, lineNode));
            if (expression instanceof Name) {
                ExpressionInfo expressionInfo = TypeInformation.getTypeInformation((Name) expression, methClaPacOfExpName, lineNode);
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
        LineAndNodeType lineNode = new LineAndNodeType(node.getStartPosition(), node.getNodeType());
        list.add(new ExpressionInfo(node, methClaPacOfExpName, lineNode));

        ExpressionInfo expressionInfo = TypeInformation.getTypeInformation(node.getName(), methClaPacOfExpName, lineNode);
        if (expressionInfo != null)
            list.add(expressionInfo);

        return true;
    }

    @Override
    public boolean visit(FieldDeclaration node) {
        LineAndNodeType lineNode = new LineAndNodeType(node.getStartPosition(), node.getNodeType());
        for (Object obj : node.fragments()) {
            VariableDeclarationFragment v = (VariableDeclarationFragment) obj;
            String varName = v.getName().toString();
            list.add(new ExpressionInfo(v.getName(), methClaPacOfExpName, lineNode, node.getType(), varName));
        }
        return true;
    }

    @Override
    public boolean visit(ForStatement node) {

        Expression expression = node.getExpression();
        LineAndNodeType lineNode = new LineAndNodeType(node.getStartPosition(), node.getNodeType());
        if (expression != null)
            list.add(new ExpressionInfo(expression, methClaPacOfExpName, lineNode));

        return true;
    }



    @Override
    public boolean visit(IfStatement node) {
        Expression expression = node.getExpression();
        String s = "ifexpression" ;
        LineAndNodeType lineNode = new LineAndNodeType(node.getStartPosition(), node.getNodeType());
        list.add(new ExpressionInfo(expression,methClaPacOfExpName,lineNode,s));
        if (expression instanceof InfixExpression) {
            Expression expL = ((InfixExpression) expression).getLeftOperand();
            Expression expR = ((InfixExpression) expression).getRightOperand();
            if (expL instanceof Name) {
                ExpressionInfo expressionInfo = TypeInformation.getTypeInformation((Name) expL, methClaPacOfExpName, lineNode);
                if (expressionInfo != null)
                    list.add(expressionInfo);
            }
            if (expR instanceof Name) {
                ExpressionInfo expressionInfo = TypeInformation.getTypeInformation((Name) expR, methClaPacOfExpName, lineNode);
                if (expressionInfo != null)
                    list.add(expressionInfo);
            }
        } else if (expression instanceof PrefixExpression) {
            Expression expR = ((PrefixExpression) expression).getOperand();
            if (expR instanceof Name) {
                ExpressionInfo expressionInfo = TypeInformation.getTypeInformation((Name) expR, methClaPacOfExpName, lineNode);
                if (expressionInfo != null)
                    list.add(expressionInfo);
            }

        } else if (expression instanceof PostfixExpression) {
            Expression expL = ((PostfixExpression) expression).getOperand();
            if (expL instanceof Name) {
                ExpressionInfo expressionInfo = TypeInformation.getTypeInformation((Name) expL, methClaPacOfExpName, lineNode);
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
        LineAndNodeType lineNode = new LineAndNodeType(node.getStartPosition(), node.getNodeType());
        Expression expL = node.getLeftOperand();
        Expression expR = node.getRightOperand();
        if (expL instanceof Name) {
            list.add(new ExpressionInfo(expL, methClaPacOfExpName, lineNode));
        }
        if (expR instanceof Name) {
            list.add(new ExpressionInfo(expR, methClaPacOfExpName, lineNode));
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

        if(!mp.getMethodName().contains(node.getName().toString())){
            mp.getMethodName().add(node.getName().toString()) ;
        }
        return super.visit(node);
    }

    @Override
    public boolean visit(MemberValuePair node) {
        return super.visit(node);
    }

    @Override
    public boolean visit(MethodRef node) {
        if(!mp.getMethodName().contains(node.getName().toString())){
            mp.getMethodName().add(node.getName().toString()) ;
        }
        return super.visit(node);
    }

    @Override
    public boolean visit(MethodRefParameter node) {

        if(!mp.getMethodName().contains(node.getName().toString())){
            mp.getMethodName().add(node.getName().toString()) ;
        }
        return super.visit(node);
    }

    @Override
    public boolean visit(MethodDeclaration node) {
        LineAndNodeType lineNode = new LineAndNodeType(node.getStartPosition(), node.getNodeType());
        String methodName = node.getName().getFullyQualifiedName();
        List<SingleVariableDeclaration> parameters = node.parameters();
        for (SingleVariableDeclaration parameter : parameters) {
            Type parameterType = parameter.getType();
            String parameterName = parameter.getName().toString();
            list.add(new ExpressionInfo(parameter.getName(), methClaPacOfExpName, lineNode,
                    parameterType, parameterName));
        }
        return true;
    }

    @Override
    public boolean visit(MethodInvocation node) {
        if (!mp.getMethodAndTypeNameToFilter().contains(node.getName().toString())){
            mp.getMethodAndTypeNameToFilter().add(node.getName().toString());
        }

        Expression expression = node.getExpression();
        LineAndNodeType lineNode = new LineAndNodeType(node.getStartPosition(), node.getNodeType());
        list.add(new ExpressionInfo(node,methClaPacOfExpName,lineNode,"methodinvocation"));
        if (expression != null && OperatorFilterPreAndIn.ExpressionFilterReturn(expression))
            list.add(new ExpressionInfo(expression, methClaPacOfExpName, lineNode));
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
        LineAndNodeType lineNode = new LineAndNodeType(node.getStartPosition(), node.getNodeType());
        list.add(new ExpressionInfo(node, methClaPacOfExpName, lineNode));
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
        LineAndNodeType lineNode = new LineAndNodeType(node.getStartPosition(), node.getNodeType());

        if (node != null && OperatorFilterPreAndIn.ExpressionFilterReturn(node.getExpression()))
            list.add(new ExpressionInfo(node.getExpression(), methClaPacOfExpName, lineNode));
        return true;
    }

    @Override
    public boolean visit(PostfixExpression node) {
        LineAndNodeType lineNode = new LineAndNodeType(node.getStartPosition(), node.getNodeType());
        Expression expL = node.getOperand();
        if (expL instanceof Name) {
            list.add(new ExpressionInfo(expL, methClaPacOfExpName, lineNode));
        }
        list.add(new ExpressionInfo(node, methClaPacOfExpName, lineNode));
        return true;
    }

    @Override
    public boolean visit(PrefixExpression node) {
        LineAndNodeType lineNode = new LineAndNodeType(node.getStartPosition(), node.getNodeType());
        list.add(new ExpressionInfo(node, methClaPacOfExpName, lineNode));
        return true;
    }

    //用于变量的提取
    @Override
    public boolean visit(PrimitiveType node) {
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
        LineAndNodeType lineNode = new LineAndNodeType(node.getStartPosition(), node.getNodeType());
        Expression expression = node.getExpression();
        if (expression != null)
            list.add(new ExpressionInfo(expression, methClaPacOfExpName, lineNode));
        return true;
    }

    @Override
    public boolean visit(SimpleName node) {
        LineAndNodeType lineNode = new LineAndNodeType(node.getStartPosition(), node.getNodeType());
        ExpressionInfo expressionInfo = TypeInformation.getTypeInformation(node, methClaPacOfExpName, lineNode);
        if (expressionInfo != null)
            list.add(expressionInfo);
        list.add(new ExpressionInfo(node,methClaPacOfExpName,lineAndNodeType));
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
        LineAndNodeType lineNode = new LineAndNodeType(node.getStartPosition(), node.getNodeType());
        Expression expression = node.getInitializer();
        if (expression != null)
            list.add(new ExpressionInfo(expression, methClaPacOfExpName, lineNode));
        return true;
    }

    @Override
    public boolean visit(StringLiteral node) {
        LineAndNodeType lineNode = new LineAndNodeType(node.getStartPosition(), node.getNodeType());
        list.add(new ExpressionInfo(node, methClaPacOfExpName, lineNode));
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
        if (!mp.getMethodAndTypeNameToFilter().contains(node.getType().toString())){
            mp.getMethodAndTypeNameToFilter().add(node.getType().toString());
        }

        LineAndNodeType lineNode = new LineAndNodeType(node.getStartPosition(), node.getNodeType());
        for (Object obj : node.fragments()) {
            VariableDeclarationFragment v = (VariableDeclarationFragment) obj;
            Type varType = node.getType();
            String varName = v.getName().toString();
            list.add(new ExpressionInfo(v.getName(), methClaPacOfExpName, lineNode,
                    varType, varName));
        }
        return true;
    }

    @Override
    public boolean visit(VariableDeclarationFragment node) {
        LineAndNodeType lineNode = new LineAndNodeType(node.getStartPosition(), node.getNodeType());
        Expression expression = node.getInitializer();
        if (expression != null)
            list.add(new ExpressionInfo(expression, methClaPacOfExpName, lineNode));

       if(!mp.getVariableName().contains(node.getName().toString())){
          mp.getVariableName().add(node.getName().toString()) ;
       }

        return true;
    }

    @Override
    public boolean visit(WhileStatement node) {
        LineAndNodeType lineNode = new LineAndNodeType(node.getStartPosition(), node.getNodeType());
        Expression expression = node.getExpression();
        if (expression != null)
            list.add(new ExpressionInfo(expression, methClaPacOfExpName, lineNode));
        return true;
    }

    @Override
    public boolean visit(WildcardType node) {
        return super.visit(node);
    }
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
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
