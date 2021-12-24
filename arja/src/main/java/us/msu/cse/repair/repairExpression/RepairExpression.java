package us.msu.cse.repair.repairExpression;

import org.eclipse.jdt.core.dom.*;
import us.msu.cse.repair.algorithmsExpression.ExpressionPriorityMaxSelect;
import us.msu.cse.repair.core.parser.ModificationPoint;
import us.msu.cse.repair.informationExpression.ExpressionInfo;
import us.msu.cse.repair.toolsExpression.GlobalVariableCheck;
import us.msu.cse.repair.toolsExpression.TemplateBoolean;
import us.msu.cse.repair.toolsExpression.TypeInformation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 第一种修复方式，模板修复：
 *   1.对if、while、do-while语句修改：  换运算符——换左边的变量——换右边的变量
 *   2.对return修改，换与函数返回类型相同的变量
 *   3.三种模板修复{空指针、数组越界、强制类型转换检查}
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

        Expression expression = null;

        //坚决不能用原先的表达式，因为会修改掉之前的
        if (ifExp instanceof InfixExpression) {
            /**
             * 依次进行运算符、表达式左运算符、表达式右运算符的修改;（每次修改的内容都不一样）
             * 当前面的无法修改或已经修改完时，执行后面的修改
             *
             */
            expression = InfixOperatorRepair((InfixExpression) ifStatement.getExpression(), ifStatement);
            if ((expression == null) && (!TemplateBoolean.templateBooleanCheck(modificationPoint, "LEFT" + modificationPoint.getStatement()))) {
                expression = InfixFieldRepairL((InfixExpression) ifStatement.getExpression(), ifStatement);
            }
            if (expression == null) {
                expression = InfixFieldRepairR((InfixExpression) ifStatement.getExpression(), ifStatement);
            }
        }
        if (expression == null){
            for (int i=0;i<modificationPoint.getExpressionInfosIngredients().size();i++){
                ExpressionInfo expInfo = modificationPoint.getExpressionInfosIngredients().get(i);
                if ((Objects.equals(expInfo.getVarNameStr(), "ifexpression"))&&(!TemplateBoolean.templateBooleanCheck(modificationPoint,expInfo.getExpressionStr()+"ie"))){
                    expression = expInfo.getExpression();
                    modificationPoint.getTemplateBoolean().put(expInfo.getExpressionStr()+"ie",true);
                    break;
                }
            }
        }
        if (expression != null) {
            Expression exp = (Expression) ASTNode.copySubtree(ifStatement.getAST(), expression);
            //留有接口，为以后的其他类型的if语句修改使用
            ifStatement.setExpression(exp);
            clearAndSetIngredient(ifStatement);
            return true;
        }else
            return false;

    }

    public boolean whileRepair() {

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

        Expression expression = null;
        if (whileExp instanceof InfixExpression) {
            expression = InfixOperatorRepair((InfixExpression) whileStatement.getExpression(), whileStatement);
            if (expression == null) {
                expression = InfixFieldRepairL((InfixExpression) whileStatement.getExpression(), whileStatement);
            }
            if (expression == null) {
                expression = InfixFieldRepairR((InfixExpression) whileStatement.getExpression(), whileStatement);
            }
        }
        Expression exp = null;
        if (expression != null) {
            exp = (Expression) ASTNode.copySubtree(whileStatement.getAST(), expression);
            //留有接口，为以后的其他类型的if语句修改使用
            whileStatement.setExpression(exp);
            clearAndSetIngredient(whileStatement);
            return true;
        }
        return false;
    }

    public boolean returnRepair() {
        Expression stReturn = ((ReturnStatement) modificationPoint.getStatement()).getExpression();
        /**
         * 1.普通的return则普通对待
         * 2.如果return为boolean，则制造相应相反的boolean值
         * 3.如果NumberLiteral、MethodInvocation、ArrayAccess、FieldAccess、StringLiteral、ConditionalExpression
         *   则返回相应的类型
         * 4.如果有是Name我则用相同的类型进行替换
         *
         * ！！！！！！！！！这里的方法调用，需要进一步修改，保持返回值相等
         *
         */
        //没有使用过，我则需要
        ReturnStatement returnStatement = null;
        if ((stReturn instanceof BooleanLiteral) && (!TemplateBoolean.templateBooleanCheck(modificationPoint, "return"))) {
            returnStatement = ast.newReturnStatement();
            BooleanLiteral booleanLiteral = null;
            if (((BooleanLiteral) stReturn).booleanValue()) {//这里是真，我则修改为假
                booleanLiteral = ast.newBooleanLiteral(false);

            } else {
                booleanLiteral = ast.newBooleanLiteral(true);
            }
            returnStatement.setExpression(booleanLiteral);
            modificationPoint.getTemplateBoolean().put("return", true);
        } else {
            String returnTypeStr = checkReturnType((ReturnStatement) modificationPoint.getStatement());
            List<ExpressionInfo> modiIngreExpList = modificationPoint.getExpressionInfosIngredients();
            double maxSort = -1;
            int mid = -1;
            for (int i = 0; i < modiIngreExpList.size(); i++) {
                ExpressionInfo ex = modiIngreExpList.get(i);
                if (Objects.equals(ex.getVarTypeStr(), returnTypeStr)) {
                    if ((ex.getPriority() > maxSort) &&
                            (!TemplateBoolean.templateBooleanCheck(modificationPoint, ex.getExpression().toString() + "reelse"))) {
                        maxSort = ex.getPriority();
                        mid = i;
                    }
                }
            }
            if (mid >= 0) {
                Expression expression = modiIngreExpList.get(mid).getExpression();
                modificationPoint.getTemplateBoolean().put(expression.toString() + "reelse", true);
                returnStatement = ast.newReturnStatement();
                Expression exp = (Expression) ASTNode.copySubtree(returnStatement.getAST(), expression);
                returnStatement.setExpression(exp);
            } else {
                for (int i = 0; i < modiIngreExpList.size(); i++) {
                    Expression expression = modiIngreExpList.get(i).getExpression();
                    boolean flag2 = TemplateBoolean.templateBooleanCheck(modificationPoint,expression.toString() + "reelsenode");
                    if(!flag2){

                        modificationPoint.getTemplateBoolean().put(expression.toString() + "reelsenode", true);
                        returnStatement = ast.newReturnStatement();
                        Expression exp = (Expression) ASTNode.copySubtree(returnStatement.getAST(), expression);
                        returnStatement.setExpression(exp);
                        break;
                    }
                }
            }
        }

        if (returnStatement != null) {
            clearAndSetIngredient(returnStatement);
            return true;
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


        Expression expression = null;
        if (doExp instanceof InfixExpression) {
            expression = InfixOperatorRepair((InfixExpression) doStatement.getExpression(), doStatement);
            if (expression == null) {
                expression = InfixFieldRepairL((InfixExpression) doStatement.getExpression(), doStatement);
            }
            if (expression == null) {
                expression = InfixFieldRepairR((InfixExpression) doStatement.getExpression(), doStatement);
            }
        }

        Expression exp = null;
        if (expression != null) {
            exp = (Expression) ASTNode.copySubtree(doStatement.getAST(), expression);
            doStatement.setExpression(exp);
            clearAndSetIngredient(doStatement);
            return true;
        }
        return false;
    }

    public void castTypeRepair(CastExpression castExpression) {
        InstanceofExpression instanceofExpression = ast.newInstanceofExpression();
        Expression expressionCast = (Expression) ASTNode.copySubtree(instanceofExpression.getAST(), castExpression.getExpression());
        Type type = (Type) ASTNode.copySubtree(instanceofExpression.getAST(), castExpression.getType());
        instanceofExpression.setLeftOperand(expressionCast);
        instanceofExpression.setRightOperand(type);
        IfStatement ifStatement = ast.newIfStatement();
        ifStatement.setExpression(instanceofExpression);
        Statement statementThen = (Statement) ASTNode.copySubtree(ifStatement.getAST(), modificationPoint.getStatement());
        ifStatement.setThenStatement(statementThen);
        clearAndSetIngredient(ifStatement);
    }

    public void arrayRepair(ArrayAccess access) {
        String s = access.getArray().toString() + ".length";
        Name name = ast.newName(s);
        IfStatement ifStatement = ast.newIfStatement();
        InfixExpression infixExpression = ast.newInfixExpression();
        Expression access1 = (Expression) ASTNode.copySubtree(infixExpression.getAST(), access.getIndex());
        infixExpression.setLeftOperand(access1);
        infixExpression.setRightOperand(name);
        infixExpression.setOperator(InfixExpression.Operator.LESS);
        ifStatement.setExpression(infixExpression);
        Statement statemenThen = (Statement) ASTNode.copySubtree(ifStatement.getAST(), modificationPoint.getStatement());
        ifStatement.setThenStatement(statemenThen);
        clearAndSetIngredient(ifStatement);
    }

    public void fieldRepair(FieldAccess expression) {
        NullLiteral nullLiteral = ast.newNullLiteral();
        IfStatement ifStatement = ast.newIfStatement();
        FieldAccess fieldAccess = (FieldAccess) ASTNode.copySubtree(ifStatement.getAST(), expression);
        Statement statementThen = (Statement) ASTNode.copySubtree(ifStatement.getAST(), modificationPoint.getStatement());
        InfixExpression infixExpression1 = ast.newInfixExpression();
        infixExpression1.setOperator(InfixExpression.Operator.NOT_EQUALS);
        infixExpression1.setLeftOperand(fieldAccess);
        infixExpression1.setRightOperand(nullLiteral);
        ifStatement.setExpression(infixExpression1);
        ifStatement.setThenStatement(statementThen);
        clearAndSetIngredient(ifStatement);

        ReturnStatement returnStatement = ast.newReturnStatement();
        IfStatement ifelse = (IfStatement) ASTNode.copySubtree(ast,ifStatement);
        ifelse.setElseStatement(returnStatement);
        clearAndSetIngredient(ifelse);

    }


    public void clearAndSetIngredient(Statement s) {
        if (s != null) {
            ingredients.add(s);
            modificationPoint.setIngredients(ingredients);
        }
    }

    public String checkReturnType(ReturnStatement r) {
        ASTNode astNode = r;
        String typeStr = null;
        while ((astNode != null) && (!(astNode instanceof MethodDeclaration))) {
            astNode = astNode.getParent();
        }
        if (astNode != null) {
            MethodDeclaration methodDeclaration = (MethodDeclaration) astNode;
            typeStr = methodDeclaration.getReturnType2().toString();
        }
        return typeStr;
    }

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


        Expression expression1 = null;
        Expression expression2 = null;
        if (expLeft instanceof Name) {
            ExpressionInfo expressionInfo = TypeInformation.getTypeInformation((Name) expLeft, modificationPoint);
            if (expressionInfo == null) {
                expressionInfo = TypeInformation.getSourceVariable(modificationPoint.getSourceFilePath(), expLeft.toString());
            }
            if (expressionInfo != null) {
                List<ExpressionInfo> modiIngreExpList = modificationPoint.getExpressionInfosIngredients();
                /**
                 * 目的是选取满足条件中，分数最大的表达式;
                 */
                double maxSort = -1;
                int mid = -1;
                for (int i = 0; i < modiIngreExpList.size(); i++) {
                    ExpressionInfo ex = modiIngreExpList.get(i);
                    if ((expressionInfo.getVarTypeStr() != null) && (ex.getVarTypeStr() != null) &&
                            Objects.equals(ex.getVarTypeStr(), expressionInfo.getVarTypeStr()) &&
                            (GlobalVariableCheck.globalVariable(modificationPoint, expressionInfo))) {
                        if ((ex.getPriority() > maxSort) &&
                                (!TemplateBoolean.templateBooleanCheck(modificationPoint, ex.getExpression().toString() + modificationPoint.getStatement().toString() + "infixl"))) {
                            maxSort = ex.getPriority();
                            mid = i;
                        }
                    }
                }
                if (mid >= 0) {
                    Expression expression = modiIngreExpList.get(mid).getExpression();
                    modificationPoint.getTemplateBoolean().put(expression.toString() + modificationPoint.getStatement().toString() + "infixl", true);
                    expression1 = (Expression) ASTNode.copySubtree(statement.getAST(), expression);
                    e.setLeftOperand(expression1);
                    expression2 = (Expression) ASTNode.copySubtree(statement.getAST(), e);
                    return expression2;
                }
            }
        }
        if ((expLeft instanceof CharacterLiteral) || (expLeft instanceof NumberLiteral) || (expLeft instanceof StringLiteral)) {

            List<ExpressionInfo> modiIngreExpList = modificationPoint.getExpressionInfosIngredients();
            /**
             * 目的是选取满足条件中，分数最大的表达式;
             */
            double maxSort = -1;
            int mid = -1;
            for (int i = 0; i < modiIngreExpList.size(); i++) {
                if (expLeft.getNodeType() == modiIngreExpList.get(i).getExpressionNodeType()) {
                    boolean flag1 = (modiIngreExpList.get(i).getPriority() > maxSort);
                    boolean flag2 = TemplateBoolean.templateBooleanCheck(modificationPoint,
                            modiIngreExpList.get(i).getExpression().toString() + modificationPoint.getStatement().toString() + "cnsl");
                    if (flag1 && (!flag2)) {
                        maxSort = modiIngreExpList.get(i).getPriority();
                        mid = i;
                    }
                }
            }
            if (mid >= 0) {
                modificationPoint.getTemplateBoolean().put(modiIngreExpList.get(mid).getExpression().toString() + modificationPoint.getStatement().toString() + "cnsl", true);
                expression1 = (Expression) ASTNode.copySubtree(statement.getAST(), modiIngreExpList.get(mid).getExpression());
                e.setLeftOperand(expression1);
                expression2 = (Expression) ASTNode.copySubtree(statement.getAST(), e);
                return expression2;
            }
        }
        return null;
    }

    public Expression InfixFieldRepairR(InfixExpression e, Statement statement) {

        Expression expRight = e.getRightOperand();

        Expression expression1 = null;
        Expression expression2 = null;

        if (expRight instanceof Name) {
            ExpressionInfo expressionInfo = TypeInformation.getTypeInformation((Name) expRight, modificationPoint);
            if (expressionInfo == null) {
                expressionInfo = TypeInformation.getSourceVariable(modificationPoint.getSourceFilePath(), expRight.toString());
            }
            if (expressionInfo != null) {
                List<ExpressionInfo> modiIngreExpList = modificationPoint.getModificationPointExpressionInfosList();
                double maxSort = -1;
                int mid = -1;
                for (int i = 0; i < modiIngreExpList.size(); i++) {
                    ExpressionInfo ex = modiIngreExpList.get(i);
                    if ((expressionInfo.getVarTypeStr() != null) && (ex.getVarTypeStr() != null) &&
                            Objects.equals(ex.getVarTypeStr(), expressionInfo.getVarTypeStr()) &&
                            (GlobalVariableCheck.globalVariable(modificationPoint, expressionInfo))) {
                        if ((ex.getPriority() > maxSort) &&
                                (!TemplateBoolean.templateBooleanCheck(modificationPoint, ex.getExpression().toString() + modificationPoint.getStatement().toString() + "infixr"))) {
                            maxSort = modiIngreExpList.get(i).getPriority();
                            mid = i;
                        }
                    }
                }
                if (mid >= 0) {
                    Expression expression = modiIngreExpList.get(mid).getExpression();
                    modificationPoint.getTemplateBoolean().put(expression.toString() + modificationPoint.getStatement().toString() + "infixr", true);
                    expression1 = (Expression) ASTNode.copySubtree(statement.getAST(), modiIngreExpList.get(mid).getExpression());
                    e.setLeftOperand(expression1);
                    expression2 = (Expression) ASTNode.copySubtree(statement.getAST(), e);
                    return expression2;
                }
            }
        }
        if ((expRight instanceof CharacterLiteral) || (expRight instanceof NumberLiteral) || (expRight instanceof StringLiteral)) {

            List<ExpressionInfo> modiIngreExpList = modificationPoint.getExpressionInfosIngredients();
            /**
             * 目的是选取满足条件中，分数最大的表达式;
             */
            double maxSort = -1;
            int mid = -1;
            for (int i = 0; i < modiIngreExpList.size(); i++) {
                ExpressionInfo ex = modiIngreExpList.get(i);
                if (expRight.getNodeType() == ex.getExpressionNodeType()) {
                    if ((ex.getPriority() > maxSort) &&
                            (!TemplateBoolean.templateBooleanCheck(modificationPoint, ex.getExpression().toString() + modificationPoint.getStatement().toString() + "cnsr"))) {
                        maxSort = ex.getPriority();
                        mid = i;
                    }
                }
            }
            if (mid >= 0) {
                Expression expression = modiIngreExpList.get(mid).getExpression();
                modificationPoint.getTemplateBoolean().put(expression.toString() + modificationPoint.getStatement().toString() + "cnsr", true);
                expression1 = (Expression) ASTNode.copySubtree(statement.getAST(), modiIngreExpList.get(mid).getExpression());
                e.setLeftOperand(expression1);
                expression2 = (Expression) ASTNode.copySubtree(statement.getAST(), e);
                return expression2;
            }
        }
        return null;
    }

}
