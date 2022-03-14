package us.msu.cse.repair.astVisitorExpression;

import org.eclipse.jdt.core.dom.*;
import us.msu.cse.repair.AbstractClass.ASTVisitorPlus;
import us.msu.cse.repair.core.parser.ModificationPoint;
import us.msu.cse.repair.formWorkExpression.makeFormWorkStatement;
import us.msu.cse.repair.informationExpression.ExpressionInfo;
import us.msu.cse.repair.toolsExpression.ChangeSimpleName;
import us.msu.cse.repair.toolsExpression.TemplateBoolean;
import us.msu.cse.repair.toolsExpression.TypeInformation;

import java.util.List;

public class AssignmentRepairVisitor extends ASTVisitorPlus {
    private ModificationPoint mp = null;
    private volatile boolean isRepaired = false;
    private volatile boolean statementFlag = false;
    private List<ExpressionInfo> expressionInfoList = null;
    private Statement s = null;

    public AssignmentRepairVisitor(ModificationPoint mp) {
        this.mp = mp;
        expressionInfoList = mp.getExpressionInfosIngredients();
    }

    @Override
    public boolean visit(Assignment node) {
        if (!mp.isRepair()) {
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
                                    mp.getTemplateBoolean().put(e.getExpressionStr() + "asleftfield", true);
                                    SimpleName simpleName = (SimpleName) ASTNode.copySubtree(eLeft.getAST(), e.getExpression());
                                    ((FieldAccess) eLeft).setName(simpleName);
                                    mp.setRepair(true);
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
                                    mp.getTemplateBoolean().put(e.getExpressionStr() + "asleftsimple", true);
                                    Expression expression = (Expression) ASTNode.copySubtree(node.getAST(), e.getExpression());
                                    node.setLeftHandSide(expression);
                                    mp.setRepair(true);
                                    return true;
                                }
                            }
                        }
                    }
                } else if ((ASTNode.nodeClassForType(eLeft.getNodeType()).getSimpleName().toString()).equals("NumberLiteral")) {
                    for (ExpressionInfo e : expressionInfoList) {
                        if ((e.getExpression() instanceof NumberLiteral) && (!(TemplateBoolean.templateBooleanCheck(mp, e.getExpressionStr() + "asleftnumber")))) {
                            mp.getTemplateBoolean().put(e.getExpressionStr() + "asleftnumber", true);
                            Expression expression = (Expression) ASTNode.copySubtree(node.getAST(), e.getExpression());
                            node.setLeftHandSide(expression);
                            mp.setRepair(true);
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
                                    mp.getTemplateBoolean().put(e.getExpressionStr() + "asrightfield", true);
                                    SimpleName simpleName = (SimpleName) ASTNode.copySubtree(access.getAST(), e.getExpression());
                                    access.setName(simpleName);
                                    mp.setRepair(true);
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
                                        && (!(TemplateBoolean.templateBooleanCheck(mp, e.getExpressionStr() + "asrightsimple")))) {
                                    mp.getTemplateBoolean().put(e.getExpressionStr() + "asrightsimple", true);
                                    Expression expression = (Expression) ASTNode.copySubtree(node.getAST(), e.getExpression());
                                    node.setRightHandSide(expression);
                                    mp.setRepair(true);
                                    return true;
                                }
                            }
                        }
                    }
                } else if ((ASTNode.nodeClassForType(eRight.getNodeType()).getSimpleName().toString()).equals("NumberLiteral")) {
                    for (ExpressionInfo e : expressionInfoList) {
                        if ((e.getExpression() instanceof NumberLiteral) && (!(TemplateBoolean.templateBooleanCheck(mp, e.getExpressionStr() + "asrightnumber")))) {
                            mp.getTemplateBoolean().put(e.getExpressionStr() + "asrightnumber", true);
                            Expression expression = (Expression) ASTNode.copySubtree(node.getAST(), e.getExpression());
                            node.setLeftHandSide(expression);

                            mp.setRepair(true);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
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
