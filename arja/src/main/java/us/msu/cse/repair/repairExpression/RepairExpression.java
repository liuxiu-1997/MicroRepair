package us.msu.cse.repair.repairExpression;

import org.eclipse.jdt.core.dom.*;
import us.msu.cse.repair.algorithmsExpression.ExpressionPriorityMaxSelect;
import us.msu.cse.repair.core.parser.ModificationPoint;
import us.msu.cse.repair.informationExpression.ExpressionInfo;
import us.msu.cse.repair.toolsExpression.GlobalVariableCheck;
import us.msu.cse.repair.toolsExpression.RuleCheck;
import us.msu.cse.repair.toolsExpression.TemplateBoolean;
import us.msu.cse.repair.toolsExpression.TypeInformation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 第一种修复方式，模板修复：
 * 1.对if、while、do-while语句修改：  换运算符——换左边的变量——换右边的变量
 * 2.对return修改，换与函数返回类型相同的变量
 * 3.三种模板修复{空指针、数组越界、强制类型转换检查}
 */

public class RepairExpression {
    private ModificationPoint modificationPoint = null;
    private final ExpressionPriorityMaxSelect expressionPriorityMaxSelect = new ExpressionPriorityMaxSelect();
    private ExpressionInfo expressionInfo;
    private List<Statement> ingredients;
    private final AST ast = AST.newAST(AST.JLS8);

    public RepairExpression(ModificationPoint mp) {
        this.modificationPoint = mp;
        this.expressionInfo = expressionPriorityMaxSelect.getMaxPriority(modificationPoint.getExpressionInfosIngredients());
        if (mp.getIngredients() == null)
            this.ingredients = new ArrayList<>();
        else
            this.ingredients = new ArrayList<>(mp.getIngredients());
    }

    public boolean ifRepair() {
        IfStatement ifSta = (IfStatement) modificationPoint.getStatement();
        Expression ifExp = ifSta.getExpression();
        IfStatement ifStatement = ast.newIfStatement();
        if (ifSta.getElseStatement() != null) {
            Statement statement = (Statement) ASTNode.copySubtree(ifStatement.getAST(), ifSta.getElseStatement());
            if (statement != null) {
                ifStatement.setElseStatement(statement);
            }
        }
        if (ifSta.getThenStatement() != null) {
            Statement statement = (Statement) ASTNode.copySubtree(ifStatement.getAST(), ifSta.getThenStatement());
            if (statement != null) {
                ifStatement.setThenStatement(statement);
            }
        }
        if (ifSta.getExpression() != null) {
            Expression expression = (Expression) ASTNode.copySubtree(ifStatement.getAST(), ifSta.getExpression());
            if (expression != null) {
                ifStatement.setExpression(expression);
            }
        }

        //__________________________________________________________________________________________________________________
        Expression expression = null;
        //坚决不能用原先的表达式，因为会修改掉之前的
        if (ifExp instanceof InfixExpression) {
            /**
             * 依次进行运算符、表达式左运算符、表达式右运算符的修改;（每次修改的内容都不一样）
             * 当前面的无法修改或已经修改完时，执行后面的修改
             *
             */
            expression = InfixOperatorRepair((InfixExpression) ifStatement.getExpression(), ifStatement);
            if (expression == null) {
                InfixExpression eInfixLeft = (InfixExpression) ASTNode.copySubtree(ifStatement.getAST(), ifStatement.getExpression());
                expression = InfixFieldRepairL(eInfixLeft, ifStatement);
            }
            if (expression == null) {
                InfixExpression eInfixRight = (InfixExpression) ASTNode.copySubtree(ifStatement.getAST(), ifStatement.getExpression());
                expression = InfixFieldRepairL(eInfixRight, ifStatement);
            }
            if (expression == null) {
                for (ExpressionInfo expressionInfo : modificationPoint.getExpressionInfosIngredients()) {
                    Expression exp = expressionInfo.getExpression();
                    if ((exp instanceof InfixExpression) && (!TemplateBoolean.templateBooleanCheck(modificationPoint, exp.toString() + "infixOfIf"))) {
                        expression = (Expression) ASTNode.copySubtree(ast, exp);
                        modificationPoint.getTemplateBoolean().put(exp.toString() + "infixOfIf", true);
                    }
                }
            }
        }
        if (expression != null) {
            ifStatement.setExpression(expression);
            clearAndSetIngredient(ifStatement);
            return true;
        } else
            return false;

    }

    public boolean whileRepair() {

        if (((WhileStatement) modificationPoint.getStatement()).getExpression() != null) {
            WhileStatement whileSta = (WhileStatement) modificationPoint.getStatement();
            WhileStatement whileStatement = ast.newWhileStatement();
            Expression whileExp = whileSta.getExpression();
            if (whileSta.getBody() != null) {
                Statement statement = (Statement) ASTNode.copySubtree(whileStatement.getAST(), whileSta.getBody());
                whileStatement.setBody(statement);
            }
            if (whileSta.getExpression() != null) {
                Expression expression = (Expression) ASTNode.copySubtree(whileStatement.getAST(), whileSta.getExpression());
                whileStatement.setExpression(expression);
            }
            //------------------------------上面是复制，下面是修复----------------------------------------------------
            Expression expression = null;
            if (whileExp instanceof InfixExpression) {
                expression = InfixOperatorRepair((InfixExpression) whileStatement.getExpression(), whileStatement);
                if (expression == null) {
                    InfixExpression eInfixLeft = (InfixExpression) ASTNode.copySubtree(ast, whileStatement.getExpression());
                    expression = InfixFieldRepairL(eInfixLeft, whileStatement);
                }
                if (expression == null) {
                    InfixExpression eInfixRight = (InfixExpression) ASTNode.copySubtree(ast, whileStatement.getExpression());
                    expression = InfixFieldRepairR(eInfixRight, whileStatement);
                }
                if (expression == null) {
                    for (ExpressionInfo expressionInfo : modificationPoint.getExpressionInfosIngredients()) {
                        Expression exp = expressionInfo.getExpression();
                        if ((exp instanceof InfixExpression) && (!TemplateBoolean.templateBooleanCheck(modificationPoint, exp.toString() + "infixOfWhile"))) {
                            expression = (Expression) ASTNode.copySubtree(ast, exp);
                            modificationPoint.getTemplateBoolean().put(exp.toString() + "infixOfWhile", true);
                        }
                    }
                }
            }
            if (expression != null) {
                whileStatement.setExpression(expression);
                clearAndSetIngredient(whileStatement);
                return true;
            }
        }
        return false;
    }

    public boolean returnRepair() {
        if (((ReturnStatement) modificationPoint.getStatement()).getExpression() != null) {
            Expression returnOfExpression = ((ReturnStatement) modificationPoint.getStatement()).getExpression();
            ReturnStatement returnStatement = null;
            for (ExpressionInfo e : modificationPoint.getExpressionInfosIngredients()) {
                Expression expression = e.getExpression();
                if (!TemplateBoolean.templateBooleanCheck(modificationPoint, expression.toString() + "returnRepair")) {
                    boolean flagCheck = RuleCheck.rule1OfReturnStatement(modificationPoint,returnOfExpression,expression);
                    if (flagCheck){
                        returnStatement = ast.newReturnStatement();
                        Expression expressionCopy = (Expression) ASTNode.copySubtree(returnStatement.getAST(), expression);
                        returnStatement.setExpression(expressionCopy);
                    };
                    modificationPoint.getTemplateBoolean().put(expression.toString() + "returnRepair", true);
                }
                if (returnStatement != null) {
                    clearAndSetIngredient(returnStatement);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean doWhileRepair() {
        DoStatement doSta = (DoStatement) modificationPoint.getStatement();
        Expression doExp = doSta.getExpression();
        DoStatement doStatement = ast.newDoStatement();
        if (doSta.getBody() != null) {
            Statement statement = (Statement) ASTNode.copySubtree(doStatement.getAST(), doSta.getBody());
            if (statement != null)
                doStatement.setBody(statement);
        }
        if (doSta.getExpression() != null) {
            Expression expression = (Expression) ASTNode.copySubtree(doStatement.getAST(), doSta.getExpression());
            if (expression != null)
                doStatement.setExpression(expression);
        }

        //______________________________________________________________________________________________________________
        Expression expression = null;
        if (doExp instanceof InfixExpression) {
            expression = InfixOperatorRepair((InfixExpression) doStatement.getExpression(), doStatement);
            if (expression == null) {
                InfixExpression infixExpressionLeft = (InfixExpression) ASTNode.copySubtree(ast, doStatement.getExpression());
                expression = InfixFieldRepairL(infixExpressionLeft, doStatement);
            }
            if (expression == null) {
                InfixExpression infixExpressionRight = (InfixExpression) ASTNode.copySubtree(ast, doStatement.getExpression());
                expression = InfixFieldRepairR(infixExpressionRight, doStatement);
            }
            if (expression == null) {
                for (ExpressionInfo expressionInfo : modificationPoint.getExpressionInfosIngredients()) {
                    Expression exp = expressionInfo.getExpression();
                    if ((exp instanceof InfixExpression) && (!TemplateBoolean.templateBooleanCheck(modificationPoint, exp.toString() + "infixOfDoWhile"))) {
                        expression = (Expression) ASTNode.copySubtree(ast, exp);
                        modificationPoint.getTemplateBoolean().put(exp.toString() + "infixOfDoWhile", true);
                    }
                }
            }
        }
        if (expression != null) {
            doStatement.setExpression(expression);
            clearAndSetIngredient(doStatement);
            return true;
        }
        return false;
    }

//    public void castTypeRepair(CastExpression castExpression) {
//        InstanceofExpression instanceofExpression = ast.newInstanceofExpression();
//        Expression expressionCast = (Expression) ASTNode.copySubtree(instanceofExpression.getAST(), castExpression.getExpression());
//        Type type = (Type) ASTNode.copySubtree(instanceofExpression.getAST(), castExpression.getType());
//        instanceofExpression.setLeftOperand(expressionCast);
//        instanceofExpression.setRightOperand(type);
//        IfStatement ifStatement = ast.newIfStatement();
//        ifStatement.setExpression(instanceofExpression);
//        Statement statementThen = (Statement) ASTNode.copySubtree(ifStatement.getAST(), modificationPoint.getStatement());
//        ifStatement.setThenStatement(statementThen);
//        clearAndSetIngredient(ifStatement);
//    }
//
//    public void arrayRepair(ArrayAccess access) {
//        String s = access.getArray().toString() + ".length";
//        Name name = ast.newName(s);
//        IfStatement ifStatement = ast.newIfStatement();
//        InfixExpression infixExpression = ast.newInfixExpression();
//        Expression access1 = (Expression) ASTNode.copySubtree(infixExpression.getAST(), access.getIndex());
//        infixExpression.setLeftOperand(access1);
//        infixExpression.setRightOperand(name);
//        infixExpression.setOperator(InfixExpression.Operator.LESS);
//        ifStatement.setExpression(infixExpression);
//        Statement statemenThen = (Statement) ASTNode.copySubtree(ifStatement.getAST(), modificationPoint.getStatement());
//        ifStatement.setThenStatement(statemenThen);
//        clearAndSetIngredient(ifStatement);
//    }
//
//    public void fieldRepair(FieldAccess expression) {
//        NullLiteral nullLiteral = ast.newNullLiteral();
//        IfStatement ifStatement = ast.newIfStatement();
//        FieldAccess fieldAccess = (FieldAccess) ASTNode.copySubtree(ifStatement.getAST(), expression);
//        Statement statementThen = (Statement) ASTNode.copySubtree(ifStatement.getAST(), modificationPoint.getStatement());
//        InfixExpression infixExpression1 = ast.newInfixExpression();
//        infixExpression1.setOperator(InfixExpression.Operator.NOT_EQUALS);
//        infixExpression1.setLeftOperand(fieldAccess);
//        infixExpression1.setRightOperand(nullLiteral);
//        ifStatement.setExpression(infixExpression1);
//        ifStatement.setThenStatement(statementThen);
//        clearAndSetIngredient(ifStatement);
//
//        ReturnStatement returnStatement = ast.newReturnStatement();
//        IfStatement ifelse = (IfStatement) ASTNode.copySubtree(ast, ifStatement);
//        ifelse.setElseStatement(returnStatement);
//        clearAndSetIngredient(ifelse);
//
//    }


    public void clearAndSetIngredient(Statement s) {
        if (s != null) {
            ingredients = new ArrayList<>();
            ingredients.add(s);
            if (modificationPoint.getIngredients() == null)
                modificationPoint.setIngredients(ingredients);
            else
                modificationPoint.getIngredients().addAll(ingredients);
        }
    }

//    public String checkReturnType(ReturnStatement r) {
//        ASTNode astNode = r;
//        String typeStr = null;
//        while ((astNode != null) && (!(astNode instanceof MethodDeclaration))) {
//            astNode = astNode.getParent();
//        }
//        if (astNode != null) {
//            MethodDeclaration methodDeclaration = (MethodDeclaration) astNode;
//            typeStr = methodDeclaration.getReturnType2().toString();
//        }
//        return typeStr;
//    }

    public Expression InfixOperatorRepair(InfixExpression e, Statement statement) {
        InfixExpression.Operator operator = e.getOperator();
        List<InfixExpression.Operator> opList = new ArrayList<>();//<,<=,>,>=
        List<InfixExpression.Operator> opListAndOr = new ArrayList<>();
        opList.add(InfixExpression.Operator.LESS);// <
        opList.add(InfixExpression.Operator.EQUALS);// ==
        opList.add(InfixExpression.Operator.NOT_EQUALS);// !=
        opList.add(InfixExpression.Operator.LESS_EQUALS);// <=
        opList.add(InfixExpression.Operator.GREATER);// >
        opList.add(InfixExpression.Operator.GREATER_EQUALS);// >=
        opListAndOr.add(InfixExpression.Operator.AND); // &&
        opListAndOr.add(InfixExpression.Operator.CONDITIONAL_AND); // &
        opListAndOr.add(InfixExpression.Operator.OR); //  ||
        opListAndOr.add(InfixExpression.Operator.CONDITIONAL_OR); // |
        boolean flag = true;
        if (opList.contains(operator)) {
            for (InfixExpression.Operator value : opList) {
                if ((!value.equals(operator)) && (!TemplateBoolean.templateBooleanCheck(modificationPoint, value.toString()))) {
                    e.setOperator(value);
                    modificationPoint.getTemplateBoolean().put(value.toString(), true);
                    flag = false;
                    break;
                }
            }
        } else if (opListAndOr.contains(operator)) {
            for (InfixExpression.Operator value : opListAndOr) {
                if ((!value.equals(operator)) && (!TemplateBoolean.templateBooleanCheck(modificationPoint, value.toString()))) {
                    e.setOperator(value);
                    modificationPoint.getTemplateBoolean().put(value.toString(), true);
                    flag = false;
                    break;
                }
            }
        }

        if (flag)
            return null;
        else
            return (Expression) ASTNode.copySubtree(statement.getAST(), e);

    }

    public Expression InfixFieldRepairL(InfixExpression e, Statement statement) {

        Expression expLeft = e.getLeftOperand();
        List<ExpressionInfo> modiExpressionList = modificationPoint.getExpressionInfosIngredients();
        for (ExpressionInfo expressionInfo : modiExpressionList) {
            if (!TemplateBoolean.templateBooleanCheck(modificationPoint, expressionInfo.getExpression().toString() + "infixLeftVar")) {
//               boolean rule1 = RuleCheck.rule1OfIfRepair(expLeft,expressionInfo);
               boolean rule2 = RuleCheck.rule2OfIfRepair(modificationPoint,expLeft,expressionInfo);
                if ( rule2) {
                    Expression expression = expressionInfo.getExpression();
                    Expression expressionCopy1 = (Expression) ASTNode.copySubtree(statement.getAST(), expression);
                    e.setLeftOperand(expressionCopy1);
                    modificationPoint.getTemplateBoolean().put(expressionInfo.getExpression().toString() + "infixLeftVar", true);
                    return (Expression) ASTNode.copySubtree(statement.getAST(), e);
                }
            }
        }
        return null;
    }

    public Expression InfixFieldRepairR(InfixExpression e, Statement statement) {

        Expression expRight = e.getRightOperand();
        List<ExpressionInfo> modiExpressionList = modificationPoint.getExpressionInfosIngredients();
        for (ExpressionInfo expressionInfo : modiExpressionList) {
            if (!TemplateBoolean.templateBooleanCheck(modificationPoint, expressionInfo.getExpression().toString() + "infixRightVar")) {
//                boolean rule1 = RuleCheck.rule1OfIfRepair(expRight,expressionInfo);
                boolean rule2 = RuleCheck.rule2OfIfRepair(modificationPoint,expRight,expressionInfo);
                if ( rule2 ) {
                    modificationPoint.getTemplateBoolean().put(expressionInfo.getExpression().toString() + "Right", true);
                    Expression expression = expressionInfo.getExpression();
                    Expression expressionCopy1 = (Expression) ASTNode.copySubtree(statement.getAST(), expression);
                    e.setRightOperand(expressionCopy1);
                    modificationPoint.getTemplateBoolean().put(expressionInfo.getExpression().toString() + "infixRightVar", true);
                    return (Expression) ASTNode.copySubtree(statement.getAST(), e);
                }
            }
        }
        return null;
    }

}
