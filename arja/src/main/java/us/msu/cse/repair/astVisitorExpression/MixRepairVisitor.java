package us.msu.cse.repair.astVisitorExpression;

import org.eclipse.jdt.core.dom.*;
import us.msu.cse.repair.AbstractClass.ASTVisitorPlus;
import us.msu.cse.repair.core.parser.ModificationPoint;
import us.msu.cse.repair.formWorkExpression.makeFormWorkStatement;
import us.msu.cse.repair.informationExpression.ExpressionInfo;
import us.msu.cse.repair.toolsExpression.ChangeSimpleName;
import us.msu.cse.repair.toolsExpression.OperatorInformation;
import us.msu.cse.repair.toolsExpression.TemplateBoolean;
import us.msu.cse.repair.toolsExpression.TypeInformation;

import java.util.ArrayList;
import java.util.List;

public class MixRepairVisitor extends ASTVisitorPlus {

    private ModificationPoint mp = null;
    private volatile boolean isRepaired = false;
    private volatile boolean statementFlag = false;
    private List<ExpressionInfo> expressionInfoList = null;
    private Statement s = null;
    public MixRepairVisitor(ModificationPoint mp) {
        this.mp = mp;
        this.expressionInfoList = mp.getExpressionInfosIngredients();
    }


    @Override
    public boolean visit(ArrayAccess node) {
        ExpressionInfo expressionInfo = TypeInformation.getArrayAccessTypeInfo(node);
        if (expressionInfo == null)
            expressionInfo = TypeInformation.getSourceVariable(mp.getSourceFilePath(), node.getArray().toString());
        if (expressionInfo != null) {
            for (ExpressionInfo e : expressionInfoList) {
                if ((e.getVarType() instanceof ArrayType)&&(expressionInfo.getVarType() instanceof ArrayType )) {
                    ArrayType t1 = (ArrayType) e.getVarType();
                    ArrayType t2 = (ArrayType) expressionInfo.getVarType();
                    String t1S = t1.getElementType().toString();
                    String t2S = t2.getElementType().toString();
                    if ((t1.toString().equals(t2.toString())) && (t1S.equals(t2S)) && (!(TemplateBoolean.templateBooleanCheck(mp, e.getExpressionStr() + "aa")))) {
                        Expression expression = (Expression) ASTNode.copySubtree(node.getAST(), e.getExpression());
                        node.setArray(expression);
                        mp.getTemplateBoolean().put(e.getExpression().toString() + "aa", true);
                        isRepaired = true;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean visit(ArrayCreation node) {
        for (ExpressionInfo e : expressionInfoList) {
            if ((e.getVarType() instanceof ArrayType) && (e.getExpression() instanceof ArrayAccess)) {
                ArrayType t1 = (ArrayType) e.getVarType();
                ArrayType t2 = node.getType();
                String t1S = t1.getElementType().toString();
                String t2S = t2.getElementType().toString();
                if ((t1.toString().equals(t2.toString())) && (t1S.equals(t2S)) && (!TemplateBoolean.templateBooleanCheck(mp, e.getExpressionStr()  +"ac"))) {
                    ArrayCreation access = (ArrayCreation) e.getExpression();
                    if ((access.getInitializer() != null) && (access.dimensions().toString().equals(node.dimensions().toString()))) {
                        ArrayInitializer expression = (ArrayInitializer) ASTNode.copySubtree(node.getAST(), access.getInitializer());
                        node.setInitializer(expression);
                        mp.getTemplateBoolean().put(e.getExpressionStr()  +"ac",true);
                        isRepaired = true;
                        return true;
                    }
                }
            }

        }
        return false;
    }

    @Override
    public boolean visit(Assignment node) {
        Expression eLeft = node.getLeftHandSide();
        Expression eRight = node.getRightHandSide();
        if (eLeft != null) {
            if ((ASTNode.nodeClassForType(eLeft.getNodeType()).getSimpleName().toString()).equals("FieldAccess")) {
                FieldAccess access = (FieldAccess) eLeft;
                ExpressionInfo expressionInfo = TypeInformation.getTypeInformation(access.getName(), mp);
                if (expressionInfo == null)
                    expressionInfo = TypeInformation.getSourceVariable(mp.getSourceFilePath(), access.getExpression().toString());
                if (expressionInfo != null) {
                    if (expressionInfo.getVarType() != null) {
                        Type typeAssign = expressionInfo.getVarType();
                        for (ExpressionInfo e : expressionInfoList) {
                            if ((e.getVarType() != null) && (e.getExpression() instanceof Name) && (typeAssign.toString().equals(e.getVarType().toString())) && (!(TemplateBoolean.templateBooleanCheck(mp, e.getExpressionStr() + "asleftfield")))) {
                                SimpleName simpleName = (SimpleName) ASTNode.copySubtree(eLeft.getAST(), e.getExpression());
                                ((FieldAccess) eLeft).setName(simpleName);
                                mp.getTemplateBoolean().put(e.getExpressionStr() + "asleftfield", true);
                                isRepaired = true;
                                return true;
                            }
                        }
                    }
                }
            } else if ((ASTNode.nodeClassForType(eLeft.getNodeType()).getSimpleName().toString()).equals("SimpleName")) {
                SimpleName name = (SimpleName) eLeft;
                ExpressionInfo expressionInfo = TypeInformation.getTypeInformation(name, mp);
                if (expressionInfo == null)
                    expressionInfo = TypeInformation.getSourceVariable(mp.getSourceFilePath(), name.toString());
                if (expressionInfo != null) {
                    if (expressionInfo.getVarType() != null) {
                        Type typeAssign = expressionInfo.getVarType();
                        for (ExpressionInfo e : expressionInfoList) {
                            if ((e.getVarType() != null) && (e.getVarType() != null) && (typeAssign.toString().equals(e.getVarType().toString())) && (!(TemplateBoolean.templateBooleanCheck(mp, e.getExpressionStr() + "asleftsimple")))) {
                                Expression expression = (Expression) ASTNode.copySubtree(node.getAST(), e.getExpression());
                                node.setLeftHandSide(expression);
                                mp.getTemplateBoolean().put(e.getExpressionStr() + "asleftsimple", true);
                                isRepaired = true;
                                return true;
                            }
                        }
                    }
                }
            } else if ((ASTNode.nodeClassForType(eLeft.getNodeType()).getSimpleName().toString()).equals("NumberLiteral")) {
                for (ExpressionInfo e : expressionInfoList) {
                    if ((e.getExpression() instanceof NumberLiteral) && (!(TemplateBoolean.templateBooleanCheck(mp, e.getExpressionStr() + "asleftnumber")))) {
                        Expression expression = (Expression) ASTNode.copySubtree(node.getAST(), e.getExpression());
                        node.setLeftHandSide(expression);
                        mp.getTemplateBoolean().put(e.getExpressionStr() + "asleftnumber", true);
                        isRepaired = true;
                        return true;
                    }
                }
            }
        }

        if (eRight != null) {
            if ((ASTNode.nodeClassForType(eRight.getNodeType()).getSimpleName().toString()).equals("FieldAccess")) {
                FieldAccess access = (FieldAccess) eRight;
                ExpressionInfo expressionInfo = TypeInformation.getTypeInformation(access.getName(), mp);
                if (expressionInfo == null)
                    expressionInfo = TypeInformation.getSourceVariable(mp.getSourceFilePath(), access.getExpression().toString());
                if (expressionInfo != null) {
                    if (expressionInfo.getVarType() != null) {
                        Type typeAssign = expressionInfo.getVarType();
                        for (ExpressionInfo e : expressionInfoList) {
                            if ((e.getVarType() != null) && (e.getExpression() instanceof Name) && (typeAssign.toString().equals(e.getVarType().toString())) && (!(TemplateBoolean.templateBooleanCheck(mp, e.getExpressionStr() + "asrightfield")))) {
                                SimpleName simpleName = (SimpleName) ASTNode.copySubtree(access.getAST(), e.getExpression());
                                access.setName(simpleName);
                                mp.getTemplateBoolean().put(e.getExpressionStr() + "asrightfield", true);
                                isRepaired = true;
                                return true;
                            }
                        }
                    }
                }
            } else if ((ASTNode.nodeClassForType(eRight.getNodeType()).getSimpleName().toString()).equals("SimpleName")) {
                SimpleName name = (SimpleName) eRight;
                ExpressionInfo expressionInfo = TypeInformation.getTypeInformation(name, mp);
                if (expressionInfo == null)
                    expressionInfo = TypeInformation.getSourceVariable(mp.getSourceFilePath(), name.toString());
                if (expressionInfo != null) {
                    Type typeAssign = expressionInfo.getVarType();
                    if (typeAssign != null) {
                        for (ExpressionInfo e : expressionInfoList) {
                            if ((e.getVarType() != null) && (typeAssign.toString().equals(e.getVarType().toString()))
                                    && (!(TemplateBoolean.templateBooleanCheck(mp, e.getExpressionStr() +  "asrightsimple")))) {
                                Expression expression = (Expression) ASTNode.copySubtree(node.getAST(), e.getExpression());
                                node.setRightHandSide(expression);
                                mp.getTemplateBoolean().put(e.getExpressionStr() +  "asrightsimple", true);
                                isRepaired = true;
                                return true;
                            }
                        }
                    }
                }
            } else if ((ASTNode.nodeClassForType(eRight.getNodeType()).getSimpleName().toString()).equals("NumberLiteral")) {
                for (ExpressionInfo e : expressionInfoList) {
                    if ((e.getExpression() instanceof NumberLiteral) && (!(TemplateBoolean.templateBooleanCheck(mp, e.getExpressionStr()  + "asrightnumber")))) {
                        Expression expression = (Expression) ASTNode.copySubtree(node.getAST(), e.getExpression());
                        node.setLeftHandSide(expression);
                        mp.getTemplateBoolean().put(e.getExpressionStr()  + "asrightnumber", true);
                        isRepaired = true;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean visit(BooleanLiteral node) {
        if (node.booleanValue()) {
            node.setBooleanValue(false);
        } else
            node.setBooleanValue(true);
        return super.visit(node);
    }


    @Override
    public boolean visit(CastExpression node) {
        return super.visit(node);
    }

    @Override
    public boolean visit(CharacterLiteral node) {
        for (ExpressionInfo e : expressionInfoList) {
            if ((e.getExpression() instanceof CharacterLiteral) && (!(TemplateBoolean.templateBooleanCheck(mp, e.getExpressionStr() + "char")))) {
                CharacterLiteral literal = (CharacterLiteral) e.getExpression();
                node.setCharValue(literal.charValue());
                mp.getTemplateBoolean().put(e.getExpressionStr() + "char", true);
                isRepaired = true;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean visit(ConditionalExpression node) {

        return super.visit(node);
    }

    @Override
    public boolean visit(ConstructorInvocation node) {
        if (node.arguments() != null) {
            if ((!TemplateBoolean.templateBooleanCheck(mp, node.toString() + "cons"))) {
                List<Statement> statementList = ChangeSimpleName.getChangedConstructor(mp, node.toString(), node.arguments());
                if (mp.getIngredients() == null) {
                    mp.setIngredients(statementList);
                } else {
                    mp.getIngredients().addAll(statementList);
                }
                mp.getTemplateBoolean().put(node.toString() +  "cons", true);
            }
        }
        return super.visit(node);
    }

    @Override
    public boolean visit(EnumConstantDeclaration node) {
        return super.visit(node);
    }

    @Override
    public boolean visit(ExpressionStatement node) {
        return super.visit(node);
    }

    @Override
    public boolean visit(FieldAccess node) {
        ExpressionInfo expressionInfo = TypeInformation.getTypeInformation(node.getName(), mp);
        if (expressionInfo == null)
            expressionInfo = TypeInformation.getSourceVariable(mp.getSourceFilePath(), node.getName().toString());
        if (expressionInfo != null) {
            if (expressionInfo.getVarType() != null) {
                Type typeAssign = expressionInfo.getVarType();
                for (ExpressionInfo e : expressionInfoList) {
                    if ((e.getVarType() != null) && (e.getExpression() instanceof Name) && (typeAssign.toString().equals(e.getVarType().toString()))
                            && (!(TemplateBoolean.templateBooleanCheck(mp, e.getExpressionStr() + "field")))) {
                        SimpleName s = (SimpleName) ASTNode.copySubtree(node.getAST(), e.getExpression());
                        node.setName(s);
                        mp.getTemplateBoolean().put(e.getExpressionStr() + "field", true);
                        isRepaired = true;
                        return true;
                    }
                }
            }
        }
        return false;
    }

//    @Override
//    public boolean visit(InfixExpression node) {
//        if ((!(mp.getStatement() instanceof IfStatement))) {
//            InfixExpression.Operator operator = node.getOperator();
//            List<InfixExpression.Operator> listTDRPM = OperatorInformation.getTDRPM();// * / % + -
//            List<InfixExpression.Operator> listLRR = OperatorInformation.getLRR();// << >> >>>
//            List<InfixExpression.Operator> listLGLGEN = OperatorInformation.getLGLGEN(); //< > <= >= == !=
//            List<InfixExpression.Operator> listXAOCC = OperatorInformation.getXAOCC(); //^ & | && ||
//            if (listTDRPM.contains(operator)) {
//                for (InfixExpression.Operator value : listTDRPM) {
//                    if ((!value.equals(operator)) && (!TemplateBoolean.templateBooleanCheck(mp, value.toString() + "infix"))) {
//                        node.setOperator(value);
//                        mp.getTemplateBoolean().put(value.toString() + "infix", true);
//                        isRepaired = true;
//                        return true;
//                    }
//                }
//            } else if (listLRR.contains(operator)) {
//                for (InfixExpression.Operator value : listLRR) {
//                    if ((!value.equals(operator)) && (!TemplateBoolean.templateBooleanCheck(mp, value.toString() + "infix"))) {
//                        node.setOperator(value);
//                        mp.getTemplateBoolean().put(value.toString() + "infix", true);
//                        isRepaired = true;
//                        return true;
//                    }
//                }
//            } else if (listLGLGEN.contains(operator)) {
//                for (InfixExpression.Operator value : listLGLGEN) {
//                    if ((!value.equals(operator)) && (!TemplateBoolean.templateBooleanCheck(mp, value.toString() + "infix"))) {
//                        node.setOperator(value);
//                        mp.getTemplateBoolean().put(value.toString() + "infix", true);
//                        isRepaired = true;
//                        return true;
//                    }
//                }
//            } else if (listXAOCC.contains(operator)) {
//                for (InfixExpression.Operator value : listXAOCC) {
//                    if ((!value.equals(operator)) && (!TemplateBoolean.templateBooleanCheck(mp, value.toString() + "infix"))) {
//                        node.setOperator(value);
//                        mp.getTemplateBoolean().put(value.toString() + "infix", true);
//                        isRepaired = true;
//                        return true;
//                    }
//                }
//            }
//        }
//        return false;
//    }

    @Override
    public boolean visit(MethodInvocation node) {


        for (ExpressionInfo e : expressionInfoList) {
            if (!TemplateBoolean.templateBooleanCheck(mp,  e + "MethodInvocation") && (mp.getMethodName().contains(e.getExpression().toString()))) {
                Expression eMid = (Expression) ASTNode.copySubtree(node.getAST(), e.getExpression());
                node.setExpression(eMid);
                mp.getTemplateBoolean().put( e + "MethodInvocation", true);
                isRepaired = true;
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean visit(NullLiteral node) {
        return super.visit(node);
    }

    @Override
    public boolean visit(NumberLiteral node) {
        return false;
    }


//    @Override
//    public boolean visit(PostfixExpression node) {
//
//        List<PostfixExpression.Operator> listID = new ArrayList<>();
//        PostfixExpression.Operator operator = node.getOperator();
//        listID.add(PostfixExpression.Operator.DECREMENT);
//        listID.add(PostfixExpression.Operator.INCREMENT);
//        if (listID.contains(operator)) {
//            for (PostfixExpression.Operator value : listID) {
//                if ((!value.equals(operator)) && (!TemplateBoolean.templateBooleanCheck(mp, value.toString() + "post"))) {
//                    node.setOperator(value);
//                    mp.getTemplateBoolean().put(value.toString() + "post", true);
//                    isRepaired = true;
//                    return true;
//                }
//            }
//        }
//        return false;
//    }

//    @Override
//    public boolean visit(PrefixExpression node) {
//        // ++  INCREMENT
//        // --  DECREMENT
//        // +  PLUS
//        // -  MINUS
//        // ~  COMPLEMENT
//        // !  NOT
//        List<PrefixExpression.Operator> listIDPMCN = new ArrayList<>();
//        PrefixExpression.Operator operator = node.getOperator();
//        listIDPMCN.add(PrefixExpression.Operator.INCREMENT);
//        listIDPMCN.add(PrefixExpression.Operator.DECREMENT);
//        listIDPMCN.add(PrefixExpression.Operator.PLUS);
//        listIDPMCN.add(PrefixExpression.Operator.MINUS);
//        listIDPMCN.add(PrefixExpression.Operator.COMPLEMENT);
//        listIDPMCN.add(PrefixExpression.Operator.NOT);
//        if (listIDPMCN.contains(operator)) {
//            for (PrefixExpression.Operator value : listIDPMCN) {
//                if ((!value.equals(operator)) && (!TemplateBoolean.templateBooleanCheck(mp, value.toString() + "pre"))) {
//                    node.setOperator(value);
//                    mp.getTemplateBoolean().put(value.toString() + "pre", true);
//                    isRepaired = true;
//                    return true;
//                }
//            }
//        }
//        return false;
//    }

    @Override
    public boolean visit(ReturnStatement node) {
        return super.visit(node);
    }

    @Override
    public boolean visit(SimpleName node) {

        if ((!TemplateBoolean.templateBooleanCheck(mp, node.toString() + "all")) && (!(node.toString().equals("Test1")))) {
            mp.getTemplateBoolean().put(node.toString() + "all", true);
            int num = 0;
            if (mp.getIngredients() != null) {
                num = mp.getIngredients().size();
            }
            if ((!TemplateBoolean.templateBooleanCheck(mp, node.toString() + "simpleRepair"))) {
                List<Statement> statementList = ChangeSimpleName.getChangedSimpleName(mp, node.toString());
                if (mp.getIngredients() == null) {
                    mp.setIngredients(statementList);
                } else {
                    mp.getIngredients().addAll(statementList);
                }
                mp.getTemplateBoolean().put(node.toString() + "simpleRepair", true);
            }
            if ((!TemplateBoolean.templateBooleanCheck(mp, node.toString() + "formwork"))) {
                List<Statement> statementList = makeFormWorkStatement.getStatement(mp, node);

                if (mp.getIngredients() == null) {
                    mp.setIngredients(statementList);
                } else {
                    mp.getIngredients().addAll(statementList);
                }
                mp.getTemplateBoolean().put(node.toString() + "formwork", true);
            }
            if (mp.getIngredients() != null) {
                if (num != mp.getIngredients().size())
                    isRepaired = true;
            }
        }
        return false;
    }

    @Override
    public boolean visit(StringLiteral node) {

        for (ExpressionInfo e : expressionInfoList) {
            if ((e.getExpression() instanceof StringLiteral) && (!(TemplateBoolean.templateBooleanCheck(mp, e.getExpressionStr() + "string")))) {
                node.setLiteralValue(node.toString());
                mp.getTemplateBoolean().put(e.getExpressionStr() + "string", true);
                isRepaired = true;
                return true;
            }
        }

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
        return super.visit(node);
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
        return super.visit(node);
    }

    @Override
    public boolean visit(SuperConstructorInvocation node) {
        return true;
    }

    @Override
    public boolean visit(VariableDeclarationFragment node) {
        if ( (node.getInitializer() != null)) {
            int nodeType = node.getInitializer().getNodeType();
            for (ExpressionInfo e : expressionInfoList) {
                if (nodeType == e.getExpression().getNodeType()) {
                    boolean flag = TemplateBoolean.templateBooleanCheck(mp, e.getExpression().toString() + "varia");
                    if (!flag) {
                        Expression expression = (Expression) ASTNode.copySubtree(node.getAST(), e.getExpression());
                        node.setInitializer(expression);
                        mp.getTemplateBoolean().put(e.getExpression().toString() + "varia", true);
                        isRepaired = true;
                        return true;
                    }
                }
            }
        }
        return super.visit(node);
    }

    @Override
    public void endVisit(Block node) {
        if (!statementFlag) {
            ASTNode cur = node;
            while (!(cur.getParent() instanceof TypeDeclaration)) {
                cur = cur.getParent();
            }
            Initializer initializer = (Initializer) cur;
            s = (Statement) initializer.getBody().statements().get(0);
            statementFlag = true;
        }
    }

    public Statement getStatement() {
        return s;
    }

    public boolean isRepaired() {
        return isRepaired;
    }

    public void setRepaired(boolean flag) {
        isRepaired = flag;
    }
}
