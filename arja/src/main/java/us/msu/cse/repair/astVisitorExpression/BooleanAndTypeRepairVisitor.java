package us.msu.cse.repair.astVisitorExpression;

import org.eclipse.jdt.core.dom.*;
import us.msu.cse.repair.AbstractClass.ASTVisitorPlus;
import us.msu.cse.repair.core.parser.ModificationPoint;
import us.msu.cse.repair.toolsExpression.OperatorInformation;
import us.msu.cse.repair.toolsExpression.TemplateBoolean;

import java.util.ArrayList;
import java.util.List;

public class BooleanAndTypeRepairVisitor extends ASTVisitorPlus {
    private ModificationPoint mp = null;
    private volatile boolean isRepaired = false;
    private volatile boolean statementFlag = false;
    private Statement s = null;

    public BooleanAndTypeRepairVisitor(ModificationPoint mp) {
        this.mp = mp;
    }

    @Override
    public boolean visit(BooleanLiteral node) {
        if (node.booleanValue()) {
            node.setBooleanValue(false);
        } else
            node.setBooleanValue(true);
        return super.visit(node);
    }

//    @Override
//    public boolean visit(InfixExpression node) {
//        if (!(mp.getStatement() instanceof IfStatement))){
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
//
//    @Override
//    public boolean visit(PostfixExpression node) {
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
//
//    @Override
//    public boolean visit(PrefixExpression node) {
//            // ++  INCREMENT
//            // --  DECREMENT
//            // +  PLUS
//            // -  MINUS
//            // ~  COMPLEMENT
//            // !  NOT
//            List<PrefixExpression.Operator> listIDPMCN = new ArrayList<>();
//            PrefixExpression.Operator operator = node.getOperator();
//            listIDPMCN.add(PrefixExpression.Operator.INCREMENT);
//            listIDPMCN.add(PrefixExpression.Operator.DECREMENT);
//            listIDPMCN.add(PrefixExpression.Operator.PLUS);
//            listIDPMCN.add(PrefixExpression.Operator.MINUS);
//            listIDPMCN.add(PrefixExpression.Operator.COMPLEMENT);
//            listIDPMCN.add(PrefixExpression.Operator.NOT);
//            if (listIDPMCN.contains(operator)) {
//                for (PrefixExpression.Operator value : listIDPMCN) {
//                    if ((!value.equals(operator)) && (!TemplateBoolean.templateBooleanCheck(mp, value.toString() + "pre"))) {
//                        node.setOperator(value);
//                        mp.getTemplateBoolean().put(value.toString() + "pre", true);
//                        isRepaired = true;
//                        return true;
//                    }
//                }
//            }
//        return false;
//    }


    @Override
    public void endVisit(VariableDeclarationStatement node) {
        if (!isRepaired) {
            List<Type> list = mp.getTypeName();
            for (Type type : list) {
                if (!TemplateBoolean.templateBooleanCheck(mp,type.toString()+"type")) {
                    Type typeCopy = (Type) ASTNode.copySubtree(node.getAST(),type);
                    if ((type instanceof PrimitiveType)||(type instanceof ArrayType)||(type instanceof SimpleType)
                    ||(type instanceof NameQualifiedType)||(type instanceof QualifiedType)||(type instanceof WildcardType)
                    ||(type instanceof ParameterizedType)) {
                        node.setType(typeCopy);
                        isRepaired = true;
                        mp.getTemplateBoolean().put(type.toString()+"type",true);
                        break;
                    }
                }
            }
        }
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
