package us.msu.cse.repair.repairExpression;

import jmetal.util.PseudoRandom;
import org.eclipse.jdt.core.dom.*;
import us.msu.cse.repair.algorithmsExpression.ExpressionPriorityMaxSelect;
import us.msu.cse.repair.algorithmsExpression.ExpressionPrioritySort;
import us.msu.cse.repair.core.parser.ModificationPoint;
import us.msu.cse.repair.informationExpression.ExpressionInfo;
import us.msu.cse.repair.toolsExpression.TemplateBoolean;
import us.msu.cse.repair.toolsExpression.TypeInformation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class RepairExpression {
    private ModificationPoint modificationPoint = null;
    private final ExpressionPriorityMaxSelect expressionPriorityMaxSelect = new ExpressionPriorityMaxSelect();
    private ExpressionInfo expressionInfo;
    private List<Statement> ingredients ;
    private final AST ast = AST.newAST(AST.JLS8);

    public RepairExpression(ModificationPoint mp) {
        this.modificationPoint = mp;
        this.expressionInfo = expressionPriorityMaxSelect.getMaxPriority(modificationPoint.getExpressionInfosIngredients());
        this.ingredients = new ArrayList<>(mp.getIngredients());
    }

    public boolean ifRepair(){
        IfStatement ifSta = (IfStatement) modificationPoint.getStatement();
        Expression ifExp = ifSta.getExpression();
        IfStatement ifStatement = ast.newIfStatement();
        if (ifSta.getElseStatement()!=null){
            Statement statement = (Statement) ASTNode.copySubtree(ifStatement.getAST(),ifSta.getElseStatement());
            if (statement!=null){
                ifStatement.setElseStatement(statement);
            }
        }
        if (ifSta.getThenStatement()!=null){
            Statement statement = (Statement) ASTNode.copySubtree(ifStatement.getAST(),ifSta.getThenStatement());
            if (statement!=null){
                ifStatement.setThenStatement(statement);
            }
        }
        if (ifSta.getExpression()!=null){
            Expression expression =(Expression) ASTNode.copySubtree(ifStatement.getAST(),ifSta.getExpression());
            if (expression!=null){
                ifStatement.setExpression(expression);
            }
        }
        Expression expression = null;
//        infixOperaFilter();
        //坚决不能用原先的表达式，因为会修改掉之前的
        if (ifExp instanceof InfixExpression){

            expression = InfixOperatorRepair((InfixExpression)ifStatement.getExpression(),ifStatement);
            if (expression==null){
                expression = InfixFieldRepairL((InfixExpression)ifStatement.getExpression(),ifStatement);
            }
            if (expression==null){
                ExpressionPrioritySort expressionPrioritySort = new ExpressionPrioritySort();
                expressionPrioritySort.priorityAllocation(modificationPoint,modificationPoint.getExpressionInfosIngredients());
                expression = InfixFieldRepairR((InfixExpression) ifStatement.getExpression(),ifStatement);
            }
        }

        if ((expression==null)&&(expressionInfo.getExpression()!=null)){
            expression = (Expression) ASTNode.copySubtree(ifStatement.getAST(), expressionInfo.getExpression());
        }
        Expression exp = (Expression) ASTNode.copySubtree(ifStatement.getAST(),expression);
        //留有接口，为以后的其他类型的if语句修改使用
        if (exp!=null) {
            ifStatement.setExpression(exp);
            clearAndSetIngredient(ifStatement);
            return true;
        }
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
                ExpressionPrioritySort expressionPrioritySort = new ExpressionPrioritySort();
                expressionPrioritySort.priorityAllocation(modificationPoint, modificationPoint.getExpressionInfosIngredients());
                expression = InfixFieldRepairR((InfixExpression) whileStatement.getExpression(), whileStatement);
            }
        }
        if ((expression == null) && (expressionInfo.getExpression() != null)) {
            expression = (Expression) ASTNode.copySubtree(whileStatement.getAST(), expressionInfo.getExpression());
        }
        Expression exp = (Expression) ASTNode.copySubtree(whileStatement.getAST(), expression);
        //留有接口，为以后的其他类型的if语句修改使用
        if (exp != null) {
            whileStatement.setExpression(exp);
            clearAndSetIngredient(whileStatement);
            return true;
        }
        return false;
    }
    public boolean returnRepair(){
        Expression stReturn =  ((ReturnStatement)modificationPoint.getStatement()).getExpression();
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
        if ((stReturn instanceof BooleanLiteral)&& (!TemplateBoolean.templateBooleanCheck(modificationPoint,"return"))){
            returnStatement = ast.newReturnStatement();
            BooleanLiteral booleanLiteral = null;
            if (((BooleanLiteral)stReturn).booleanValue()){//这里是真，我则修改为假
                booleanLiteral = ast.newBooleanLiteral(false);

            }else {
                booleanLiteral = ast.newBooleanLiteral(true);
            }
            returnStatement.setExpression(booleanLiteral);
            modificationPoint.getTemplateBoolean().put("return",true);
        }else {
            String returnTypeStr = checkReturnType((ReturnStatement) modificationPoint.getStatement());
            List<ExpressionInfo> modiIngreExpList = modificationPoint.getExpressionInfosIngredients();
            double maxSort = -1;
            int mid=-1;
            for (int i=0;i<modiIngreExpList.size();i++){
                if (modiIngreExpList.get(i).getVarTypeStr()==returnTypeStr){
                    if (modiIngreExpList.get(i).getPriority()>maxSort){
                        maxSort = modiIngreExpList.get(i).getPriority();
                        mid = i;
                    }
                }
            }
            if (mid>=0){
                modiIngreExpList.get(mid).setPriority(-1);
                returnStatement = ast.newReturnStatement();
                Expression expression = (Expression)ASTNode.copySubtree(returnStatement.getAST(),modiIngreExpList.get(mid).getExpression());
                returnStatement.setExpression(expression);
            }
        }

//        else if (stReturn instanceof Name){
//            List<ExpressionInfo> modiExpressionList = modificationPoint.getModificationPointExpressionInfosList();
//            List<ExpressionInfo> modiIngreExpList = modificationPoint.getExpressionInfosIngredients();
//            for (int ii=0;ii<modiExpressionList.size();ii++)
//                for (int j=0;j<modiIngreExpList.size();j++)
//                    if (modiExpressionList.get(ii).getExpressionType()==modiIngreExpList.get(j).getExpressionType()){
//                        returnStatement = ast.newReturnStatement();
//                        Expression expression = (Expression)ASTNode.copySubtree(returnStatement.getAST(),modiIngreExpList.get(j).getExpression());
//                        returnStatement.setExpression(expression);
//                    }
//        }else if (stReturn instanceof NumberLiteral){
//            for (ExpressionInfo e:modificationPoint.getExpressionInfosIngredients()) {
//                if (e.getExpression() instanceof NumberLiteral){
//                    returnStatement = ast.newReturnStatement();
//                    Expression expression = (Expression)ASTNode.copySubtree(returnStatement.getAST(),e.getExpression());
//                    returnStatement.setExpression(expression);
//                }
//            }
//        }else if (stReturn instanceof MethodInvocation) {
//            for (ExpressionInfo e : modificationPoint.getExpressionInfosIngredients()) {
//                if (e.getExpression() instanceof MethodInvocation) {
//                    returnStatement = ast.newReturnStatement();
//                    Expression expression = (Expression) ASTNode.copySubtree(returnStatement.getAST(), e.getExpression());
//                    returnStatement.setExpression(expression);
//                }
//            }
//        }else if (stReturn instanceof ArrayAccess) {
//            for (ExpressionInfo e : modificationPoint.getExpressionInfosIngredients()) {
//                if (e.getExpression() instanceof ArrayAccess) {
//                    returnStatement = ast.newReturnStatement();
//                    Expression expression = (Expression) ASTNode.copySubtree(returnStatement.getAST(), e.getExpression());
//                    returnStatement.setExpression(expression);
//                }
//            }
//        }else if (stReturn instanceof FieldAccess) {
//            for (ExpressionInfo e : modificationPoint.getExpressionInfosIngredients()) {
//                if (e.getExpression() instanceof FieldAccess) {
//                    returnStatement = ast.newReturnStatement();
//                    Expression expression = (Expression) ASTNode.copySubtree(returnStatement.getAST(), e.getExpression());
//                    returnStatement.setExpression(expression);
//                }
//            }
//        }else if (stReturn instanceof StringLiteral) {
//            for (ExpressionInfo e : modificationPoint.getExpressionInfosIngredients()) {
//                if (e.getExpression() instanceof StringLiteral) {
//                    returnStatement = ast.newReturnStatement();
//                    Expression expression = (Expression) ASTNode.copySubtree(returnStatement.getAST(), e.getExpression());
//                    returnStatement.setExpression(expression);
//                }
//            }
//        }else if (stReturn instanceof ConditionalExpression) {
//            for (ExpressionInfo e : modificationPoint.getExpressionInfosIngredients()) {
//                if (e.getExpression() instanceof ConditionalExpression) {
//                    returnStatement = ast.newReturnStatement();
//                    Expression expression = (Expression) ASTNode.copySubtree(returnStatement.getAST(), e.getExpression());
//                    returnStatement.setExpression(expression);
//                }
//            }
//        }
        if(returnStatement==null){
            returnStatement = ast.newReturnStatement();
            Expression expression = (Expression)ASTNode.copySubtree(returnStatement.getAST(), expressionInfo.getExpression());
            returnStatement.setExpression(expression);
        }
        if (returnStatement!=null) {
            clearAndSetIngredient(returnStatement);
            return true;
        }
        return false;
    }
    public boolean doWhileRepair(){
        DoStatement doSta = (DoStatement)modificationPoint.getStatement();
        Expression doExp = doSta.getExpression();
        DoStatement doStatement = ast.newDoStatement();
        if (doSta.getBody()!=null){
            Statement statement = (Statement)ASTNode.copySubtree(doStatement.getAST(),doSta.getBody());
            if (statement!=null)
                doStatement.setBody(statement);
        }
        if (doSta.getExpression() != null){
            Expression expression = (Expression) ASTNode.copySubtree(doStatement.getAST(),doSta.getExpression());
            if (expression!=null)
                doStatement.setExpression(expression);
        }

//        infixOperaFilter();

        Expression expression = null;
        if (doExp instanceof InfixExpression){
            expression = InfixOperatorRepair((InfixExpression)doStatement.getExpression(),doStatement);
            if (expression==null){
                expression = InfixFieldRepairL((InfixExpression)doStatement.getExpression(),doStatement);
            }
            if (expression==null){
                ExpressionPrioritySort expressionPrioritySort = new ExpressionPrioritySort();
                expressionPrioritySort.priorityAllocation(modificationPoint,modificationPoint.getExpressionInfosIngredients());
                expression = InfixFieldRepairR((InfixExpression) doStatement.getExpression(),doStatement);
            }
        }
        if ((expression==null)&&(expressionInfo.getExpression()!=null)){
            expression = (Expression) ASTNode.copySubtree(doStatement.getAST(), expressionInfo.getExpression());
        }
        Expression exp = (Expression) ASTNode.copySubtree(doStatement.getAST(),expression);
        //留有接口，为以后的其他类型的if语句修改使用
        if (exp!=null) {
            doStatement.setExpression(exp);
            clearAndSetIngredient(doStatement);
            return true;
        }
        return false;
//        Expression expression = (Expression) ASTNode.copySubtree(doStatement.getAST(),expressionInfo.getExpression());
//        doStatement.setExpression(expression);
//        clearAndSetIngredient(doStatement);
    }
    public void castTypeRepair(CastExpression castExpression){
        InstanceofExpression instanceofExpression = ast.newInstanceofExpression();
        Expression expressionCast =(Expression)ASTNode.copySubtree(instanceofExpression.getAST(),castExpression.getExpression());
        Type type = (Type)ASTNode.copySubtree(instanceofExpression.getAST(),castExpression.getType());
        instanceofExpression.setLeftOperand(expressionCast);
        instanceofExpression.setRightOperand(type);
        IfStatement ifStatement = ast.newIfStatement();
        ifStatement.setExpression(instanceofExpression);
        Statement statementThen = (Statement)ASTNode.copySubtree(ifStatement.getAST(),modificationPoint.getStatement());
        ifStatement.setThenStatement(statementThen);
        clearAndSetIngredient(ifStatement);
    }

    public void arrayRepair(ArrayAccess access){
        String s = access.getArray().toString()+".length";
        Name name = ast.newName(s);
        IfStatement ifStatement = ast.newIfStatement();
        InfixExpression infixExpression = ast.newInfixExpression();
        Expression access1 = (Expression)ASTNode.copySubtree(infixExpression.getAST(),access.getIndex());
        infixExpression.setLeftOperand(access1);
        infixExpression.setRightOperand(name);
        infixExpression.setOperator(InfixExpression.Operator.LESS);
        ifStatement.setExpression(infixExpression);
        Statement statemenThen = (Statement) ASTNode.copySubtree(ifStatement.getAST(),modificationPoint.getStatement());
        ifStatement.setThenStatement(statemenThen);
        clearAndSetIngredient(ifStatement);
    }
    public void fieldRepair(FieldAccess expression){
        NullLiteral nullLiteral = ast.newNullLiteral();
        IfStatement ifStatement = ast.newIfStatement();
        FieldAccess fieldAccess = (FieldAccess)ASTNode.copySubtree(ifStatement.getAST(),expression);
        Statement statementThen = (Statement)ASTNode.copySubtree(ifStatement.getAST(),modificationPoint.getStatement());
        InfixExpression infixExpression1 = ast.newInfixExpression();
        infixExpression1.setOperator(InfixExpression.Operator.NOT_EQUALS);
        infixExpression1.setLeftOperand(fieldAccess);
        infixExpression1.setRightOperand(nullLiteral);
        ifStatement.setExpression(infixExpression1);
        ifStatement.setThenStatement(statementThen);
        clearAndSetIngredient(ifStatement);
    }

    public void infixOperaFilter(){
        while((expressionInfo.getExpression()!=null)&&expressionInfo.getExpression() instanceof InfixExpression){
            /**
             * only save
             *
             */
            InfixExpression infixExpression = (InfixExpression) expressionInfo.getExpression();
            if ((infixExpression.getOperator().equals(InfixExpression.Operator.LESS))||
                    (infixExpression.getOperator().equals(InfixExpression.Operator.GREATER))||
                    (infixExpression.getOperator().equals(InfixExpression.Operator.GREATER_EQUALS))||
                    (infixExpression.getOperator().equals(InfixExpression.Operator.LESS_EQUALS))||
                    (infixExpression.getOperator().equals(InfixExpression.Operator.EQUALS))||
                    (infixExpression.getOperator().equals(InfixExpression.Operator.NOT_EQUALS))||
                    (infixExpression.getOperator().equals(InfixExpression.Operator.AND))||
                    (infixExpression.getOperator().equals(InfixExpression.Operator.OR))||
                    (infixExpression.getOperator().equals(InfixExpression.Operator.CONDITIONAL_OR))||
                    (infixExpression.getOperator().equals(InfixExpression.Operator.CONDITIONAL_AND))){

               break;
            }else{
                if (expressionPriorityMaxSelect.getMaxPriority(modificationPoint.getModificationPointExpressionInfosList())!=null)
                    expressionInfo = expressionPriorityMaxSelect.getMaxPriority(modificationPoint.getModificationPointExpressionInfosList());
                else
                    break;
            }
            expressionInfo.setPriority(-1);
        }
    }
    public void clearAndSetIngredient(Statement s){
        if (s!=null) {
            ingredients.add(s);
            modificationPoint.setIngredients(ingredients);
        }
    }
    public String checkReturnType(ReturnStatement r){
        ASTNode astNode = r;
        String typeStr = null;
        while ((astNode!=null)&&(!(astNode instanceof MethodDeclaration))){
            astNode=astNode.getParent();
        }
        if (astNode!=null){
            MethodDeclaration methodDeclaration = (MethodDeclaration) astNode;
            typeStr = methodDeclaration.getReturnType2().toString();
        }
        return typeStr;
    }

    public Expression InfixOperatorRepair(InfixExpression e,Statement statement) {
        InfixExpression.Operator operator = e.getOperator();
        List<InfixExpression.Operator> opList = new ArrayList<>();//<,<=,>,>=
        opList.add(InfixExpression.Operator.LESS);
        opList.add(InfixExpression.Operator.LESS_EQUALS);
        opList.add(InfixExpression.Operator.GREATER);
        opList.add(InfixExpression.Operator.LESS_EQUALS);
        for (int i=0;i<opList.size();i++){
           if ((!opList.get(i).equals(operator))&&(!TemplateBoolean.templateBooleanCheck(modificationPoint,opList.get(i).toString()))){
               e.setOperator(opList.get(i));
               modificationPoint.getTemplateBoolean().put(opList.get(i).toString(), true);
               break;
           }
        }
        AST ast = AST.newAST(AST.JLS8);
        Expression expression= (Expression) ASTNode.copySubtree(ast,e);
        return expression;
    }
    public Expression InfixFieldRepairL(InfixExpression e,Statement statement){

        /**
         * 两个待改进的问题
         * 1.左表达式使用后，右表达式的分数存在问题;
         * expressionPrioritySort.priorityAllocation(mp,list);修复一下，然后，在拆分为左右进行运算;
         * 2.有点罗嗦zasz
         */
        Expression expLeft = e.getLeftOperand();

        AST ast1 = AST.newAST(AST.JLS8);
//        AST ast2 = AST.newAST(AST.JLS8);
        Expression expression1 = null;
        Expression expression2 = null;
        if (expLeft instanceof Name){
            ExpressionInfo expressionInfo = TypeInformation.getTypeInformation((Name)expLeft,modificationPoint);
            if (expressionInfo!=null){
                List<ExpressionInfo> modiIngreExpList = modificationPoint.getExpressionInfosIngredients();
                double maxSort = -1;
                int mid=-1;
                for (int i=0;i<modiIngreExpList.size();i++){
                    if (Objects.equals(modiIngreExpList.get(i).getVarTypeStr(), expressionInfo.getVarTypeStr())){
                        if (modiIngreExpList.get(i).getPriority()>maxSort){
                            maxSort = modiIngreExpList.get(i).getPriority();
                            mid = i;
                        }
                    }
                }
                if (mid>=0){
                    modiIngreExpList.get(mid).setPriority(-1);
                    expression1 = (Expression)ASTNode.copySubtree(ast1,modiIngreExpList.get(mid).getExpression());
                    e.setLeftOperand(expression1);
                    expression2 = (Expression)ASTNode.copySubtree(ast1,e);
                    return expression2;
                }
            }
        }
        return null;
    }
    public Expression InfixFieldRepairR(InfixExpression e,Statement statement){

        /**
         * 两个待改进的问题
         * 1.左表达式使用后，右表达式的分数存在问题;
         * expressionPrioritySort.priorityAllocation(mp,list);修复一下，然后，在拆分为左右进行运算;
         * 2.有点罗嗦zasz
         */

        Expression expRight = e.getRightOperand();
        AST ast1 = AST.newAST(AST.JLS8);
//        AST ast2 = AST.newAST(AST.JLS8);
        Expression expression1 = null;
        Expression expression2 = null;

        if (expRight instanceof Name){
            ExpressionInfo expressionInfo = TypeInformation.getTypeInformation((Name)expRight,modificationPoint);
            if (expressionInfo!=null){
                List<ExpressionInfo> modiIngreExpList = modificationPoint.getModificationPointExpressionInfosList();
                double maxSort = -1;
                int mid=-1;
                for (int i=0;i<modiIngreExpList.size();i++){
                    if (Objects.equals(modiIngreExpList.get(i).getVarTypeStr(), expressionInfo.getVarTypeStr())){
                        if (modiIngreExpList.get(i).getPriority()>maxSort){
                            maxSort = modiIngreExpList.get(i).getPriority();
                            mid = i;
                        }
                    }
                }
                if (mid>=0){
                    modiIngreExpList.get(mid).setPriority(-1);
                    expression1 = (Expression)ASTNode.copySubtree(ast1,modiIngreExpList.get(mid).getExpression());
                    e.setLeftOperand(expression1);
                    expression2 = (Expression) ASTNode.copySubtree(ast1,e);
                    return expression2;
                }
            }
        }
        return null;
    }

}
