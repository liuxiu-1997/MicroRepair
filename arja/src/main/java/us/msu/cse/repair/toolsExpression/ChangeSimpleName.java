package us.msu.cse.repair.toolsExpression;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.*;
import us.msu.cse.repair.astVisitorExpression.GetStatementFromText;
import us.msu.cse.repair.core.parser.ModificationPoint;
import us.msu.cse.repair.informationExpression.ExpressionInfo;

import java.util.ArrayList;
import java.util.List;

import java.util.Stack;

public class ChangeSimpleName {
    public static List<Statement> getChangedNameStaOfSuperConstructor(ModificationPoint mp, SuperConstructorInvocation node, List list) {
        String modiSta = mp.getStatement().toString();
        List<Statement> statementList = new ArrayList<>();
        int i = 0;
        List<ExpressionInfo> expressionInfoList = mp.getExpressionInfosIngredients();
        String[] strings = new String[50];
        //模式一，只替换一个变量
        for (ExpressionInfo eInfo : expressionInfoList) {
            if ((eInfo.getExpression() instanceof SimpleName) && (!TemplateBoolean.templateBooleanCheck(mp, eInfo.getExpressionStr() + modiSta + "c1"))) {

                String modiExp = eInfo.getExpression().toString();
                for (int j = 0; (j < list.size()) && (i < 49); j++) {
                    String listJ = list.get(j).toString();
                    strings[i++] = modiSta.replace(listJ.subSequence(0, listJ.length()), modiExp.subSequence(0, modiExp.length()));
                }
                mp.getTemplateBoolean().put(eInfo.getExpressionStr() + modiSta + "c1", true);
            }
        }
        //模式二，替换两个变量
        for (ExpressionInfo eInfo : expressionInfoList) {
            if ((eInfo.getExpression() instanceof SimpleName) && (!TemplateBoolean.templateBooleanCheck(mp, eInfo.getExpressionStr() + modiSta + "c2"))) {

                String modiExp = eInfo.getExpression().toString();
                for (int j = 0; (j < list.size()) && (i < 49); j++) {
                    String listJ = list.get(j).toString();
                    strings[i++] = modiSta.replace(listJ.subSequence(0, listJ.length()), modiExp.subSequence(0, modiExp.length()));
                }
                mp.getTemplateBoolean().put(eInfo.getExpressionStr() + modiSta + "c2", true);
            }
        }
        String staClass = "public class Test{\n{\n";
        for (int k = 0; k < 50; k++) {
            if (strings[k] != null) {
                staClass += strings[k];
                staClass += "}\n}";
                Statement statement = ChangeSimpleName.getStatement(staClass);
                if ((statement != null) && (strings[k].equals(statement.toString())))
                    statementList.add(statement);
            }
        }
        return statementList;
    }
    public static List<Statement> getChangedConstructor(ModificationPoint mp,String nodeStr, List list) {
        String modiSta = mp.getStatement().toString();
        List<Statement> statementList = new ArrayList<>();
        int i = 0;
        List<ExpressionInfo> expressionInfoList = mp.getExpressionInfosIngredients();
        String[] strings = new String[49];
        //模式一，只替换一个变量
        for (ExpressionInfo eInfo : expressionInfoList) {
            if ((eInfo.getExpression() instanceof SimpleName) && (!TemplateBoolean.templateBooleanCheck(mp, eInfo.getExpressionStr() + modiSta + nodeStr + "c1"))) {

                String modiExp = eInfo.getExpression().toString();
                for (int j = 0; (j < list.size()) && (i < 49); j++) {
                    String listJ = list.get(j).toString();
                    strings[i++] = modiSta.replace(listJ.subSequence(0, listJ.length()), modiExp.subSequence(0, modiExp.length()));
                }
                mp.getTemplateBoolean().put(eInfo.getExpressionStr() + modiSta + nodeStr + "c1", true);
            }
        }
        //模式二，替换两个变量
        for (ExpressionInfo eInfo : expressionInfoList) {
            if ((eInfo.getExpression() instanceof SimpleName) && (!TemplateBoolean.templateBooleanCheck(mp, eInfo.getExpressionStr() + modiSta + "c2"))) {

                String modiExp = eInfo.getExpression().toString();
                for (int j = 0; (j < list.size()) && (i < 49); j++) {
                    String listJ = list.get(j).toString();
                    strings[i++] = modiSta.replace(listJ.subSequence(0, listJ.length()), modiExp.subSequence(0, modiExp.length()));
                }
                mp.getTemplateBoolean().put(eInfo.getExpressionStr() + modiSta + "c2", true);
            }
        }
        String staClass = "public class Test{\n{\n";
        for (int k = 0; k < 50; k++) {
            if (strings[k] != null) {
                staClass += strings[k];
                staClass += "}\n}";
                Statement statement = ChangeSimpleName.getStatement(staClass);
                if ((statement != null) && (strings[k].equals(statement.toString())))
                    statementList.add(statement);
            }
        }
        return statementList;
    }

    public static List<Statement> getChangedSimpleName(ModificationPoint mp, String simpleName) {
        String modiSta = mp.getStatement().toString();
        List<Statement> statementList = new ArrayList<>();
        int i = 0;
        List<ExpressionInfo> expressionInfoList = mp.getExpressionInfosIngredients();
        String[] strings = new String[50];
        //模式一，只替换一个变量
        for (ExpressionInfo eInfo : expressionInfoList) {
            String midStr = "";
            String expStr = eInfo.getExpression().toString();
            if (mp.getVariableName().contains(expStr) || mp.getMethodName().contains(expStr)) {
                if ( (!TemplateBoolean.templateBooleanCheck(mp, eInfo.getExpressionStr() + modiSta + simpleName + "s1"))) {
                    if (i < 49) {
                        int num = 0;
                        if (modiSta.split(simpleName).length >= 3)
                            num = 3;
                        else if (modiSta.split(simpleName).length == 2)
                            num = 2;

                        if (num == 2) {
                            String[] array = modiSta.split(simpleName, num);
                            midStr += array[0] + eInfo.getExpression().toString();
                            midStr += array[1];
                        } else if (num == 3) {
                            String[] array = modiSta.split(simpleName, num);
                            midStr += array[0] + eInfo.getExpression().toString();
                            midStr += array[1] + eInfo.getExpression().toString();
                            midStr += array[2];
                        }
                        strings[i++] = midStr;
                    }
                    mp.getTemplateBoolean().put(eInfo.getExpressionStr() + modiSta + simpleName + "s1", true);
                }
            }
        }
        for (int k = 0; k < 50; k++) {
            if (strings[k] != null) {
                String staClass = "public class Test{\n{\n";
                staClass += strings[k];
                staClass += "}\n}";
                Statement statement = ChangeSimpleName.getStatement(staClass);
                if (statement != null) {
                    statementList.add(statement);
                }
            }
        }
        return statementList;
    }


    public static Statement getStatement(String staClass) {
        ASTParser astParser = ASTParser.newParser(AST.JLS8);
        astParser.setSource(staClass.toCharArray());
        astParser.setKind(ASTParser.K_COMPILATION_UNIT);
        CompilationUnit compilationUnit = (CompilationUnit) astParser.createAST(null);
        GetStatementFromText visitor = new GetStatementFromText();
        compilationUnit.accept(visitor);
        return visitor.getStatement();
    }
}
