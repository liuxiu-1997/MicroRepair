package us.msu.cse.repair.toolsExpression;

import jmetal.util.Permutation;
import jmetal.util.PseudoRandom;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.*;
import org.eclipse.osgi.internal.resolver.SystemState;
import us.msu.cse.repair.astVisitorExpression.GetStatementFromText;
import us.msu.cse.repair.core.parser.ModificationPoint;
import us.msu.cse.repair.informationExpression.ExpressionInfo;

import java.security.Permission;
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

    public static List<Statement> getChangedConstructor(ModificationPoint mp, String simpleName) {
        List<Statement> statementList = null;
        if ((!TemplateBoolean.templateBooleanCheck(mp, "onlyone" + simpleName))) {
            mp.getTemplateBoolean().put("onlyone" + simpleName, true);
            String[] strings = new String[700];
            int iArray = 0;
            String[] sMid = mp.getStatement().toString().split(simpleName);

            for (ExpressionInfo eInfo : mp.getExpressionInfosIngredients()) {
                String midStr = "";
                String expStr = eInfo.getExpression().toString();
                StringBuilder finalOne = new StringBuilder();
                StringBuilder finalTwo = new StringBuilder();
                for (int i = 0; i < sMid.length; i++) {
                    finalOne.append(sMid[i]);
                    if (i != sMid.length - 1) {
                        boolean flag = false;
                        if ((i >= 0) && (sMid[i].length() >= 1)) {
                            char c = sMid[i].charAt(sMid[i].length() - 1);
                            if (((c >= 'a') && (c <= 'z')) || ((c >= 'A') && (c <= 'Z'))) {
                                flag = true;
                            }
                        }
                        if (((i + 1) < sMid.length)&&(sMid[i+1].length()>=1)) {
                            char c = sMid[i + 1].charAt(0);
                            if (((c >= 'a') && (c <= 'z')) || ((c >= 'A') && (c <= 'Z'))) {
                                flag = true;
                            }
                        }
                        if (!flag) {
                            finalTwo = new StringBuilder(finalOne + expStr);
                        } else
                            finalTwo = new StringBuilder(finalOne + simpleName);

                    }
                    for (int j = i + 1; j < sMid.length; j++) {
                        finalTwo.append(sMid[j]);
                        if (j != sMid.length - 1)
                            finalTwo.append(simpleName);
                    }
                    if (iArray < 698)
                        strings[iArray++] = finalTwo.toString();
                    else
                        break;
                    finalTwo = new StringBuilder();
                    finalOne.append(simpleName);
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
        }

        return statementList;

    }

    public static List<Statement> getChangedSimpleName(ModificationPoint mp, String simpleName) {
        String modiSta = mp.getStatement().toString();
        List<Statement> statementList = new ArrayList<>();
        int iArray = 0;
        long recordNum = System.currentTimeMillis();
        List<ExpressionInfo> expressionInfoList = mp.getExpressionInfosIngredients();
        String[] strings = new String[700];

        //我替换的是整体，而不是替换里面的一个字符。只要是防止一个字母的情况出现
        boolean checkFlag = false;
        String[] check = modiSta.split(simpleName);
        for (int kk = 1; (kk < check.length) && (check[kk].length() > 0); kk++) {
            if (((check[kk].charAt(0) >= 'a') && (check[kk].charAt(0) <= 'z')) ||
                    ((check[kk].charAt(0) >= 'A') && (check[kk].charAt(0) <= 'Z'))) {
                checkFlag = true;
                break;
            }
        }

        if (!checkFlag) {
            for (ExpressionInfo eInfo : expressionInfoList) {
                String midStr = "";
                String expStr = eInfo.getExpression().toString();

                boolean flagAll = RuleCheck.changeNameRuleAll(mp, simpleName, eInfo.getExpression());

                //模式一，只替换一个变量,相同的變量依次都更換
                if (flagAll) {
//                    if ((!TemplateBoolean.templateBooleanCheck(mp, recordNum +expStr + "all"))) {
//                        if (iArray < 698) {
//                            int num = 0;
//                            if (modiSta.split(simpleName).length >= 3)
//                                num = 3;
//                            else if (modiSta.split(simpleName).length == 2)
//                                num = 2;
//
//                            if (num == 2) {
//                                String[] array = modiSta.split(simpleName, num);
//                                midStr += array[0] + expStr;
//                                midStr += array[1];
//                            } else if (num == 3) {
//                                String[] array = modiSta.split(simpleName, num);
//                                midStr += array[0] + expStr;
//                                midStr += array[1] + expStr;
//                                midStr += array[2];
//                            }
//                            strings[iArray++] = midStr;
//                            if ((mp.getLCNode().getLineNumber()==300)||(mp.getLCNode().getLineNumber()==302)){
//                                System.out.println("____________________");
//                            }
//                        }
//                        mp.getTemplateBoolean().put(recordNum +expStr+ "all", true);
//                    }
                    //模式二，若有多个变量每次仅仅更新一个
                    if ((!TemplateBoolean.templateBooleanCheck(mp, recordNum + expStr + "onlyone"))) {

                        String[] sMid = modiSta.split(simpleName);
                        StringBuilder finalOne = new StringBuilder();
                        StringBuilder finalTwo = new StringBuilder();
                        for (int i = 0; i < sMid.length; i++) {
                            finalOne.append(sMid[i]);
                            if (i != sMid.length - 1) {
                                boolean flag = false;
                                if ((i >= 0) && (sMid[i].length() >= 1)) {
                                    char c = sMid[i].charAt(sMid[i].length() - 1);
                                    if (((c >= 'a') && (c <= 'z')) || ((c >= 'A') && (c <= 'Z'))) {
                                        flag = true;
                                    }
                                }
                                if (((i + 1) < sMid.length)&&(sMid[i+1].length()>=1)) {
                                    char c = sMid[i + 1].charAt(0);
                                    if (((c >= 'a') && (c <= 'z')) || ((c >= 'A') && (c <= 'Z'))) {
                                        flag = true;
                                    }
                                }
                                if (!flag) {
                                    finalTwo = new StringBuilder(finalOne + expStr);
                                } else
                                    finalTwo = new StringBuilder(finalOne + simpleName);

                            }
                            for (int j = i + 1; j < sMid.length; j++) {
                                finalTwo.append(sMid[j]);
                                if (j != sMid.length - 1)
                                    finalTwo.append(simpleName);
                            }
                            if (iArray < 698)
                                strings[iArray++] = finalTwo.toString();
                            else
                                break;

                            if ((mp.getLCNode().getLineNumber() == 300) || (mp.getLCNode().getLineNumber() == 302)) {
                                System.out.println("____________________");
                            }

                            finalTwo = new StringBuilder();
                            finalOne.append(simpleName);
                        }
                    }
                    mp.getTemplateBoolean().put(recordNum + expStr + "onlyone", true);
                }
            }
        } else {
            //其实这个已经可以代替上面的if了，还没有大规模验证，等待等待再替换
            char[] modiStaChar = modiSta.toCharArray();
            char[] simpleNameChar = simpleName.toCharArray();
            String sfinalStr = "";
            for (ExpressionInfo expressionInfo : expressionInfoList) {
                sfinalStr = "";
                String expressionStr = expressionInfo.getExpression().toString();

                boolean flagAll = RuleCheck.changeNameRuleAll(mp, simpleName, expressionInfo.getExpression());
                if (flagAll) {
                    if (!TemplateBoolean.templateBooleanCheck(mp, recordNum + expressionStr + "OnlyOneElseC")) {
                        mp.getTemplateBoolean().put(recordNum + expressionStr + "OnlyOneElseC", true);
                        for (int i = 0; i < modiStaChar.length; i++) {
                            boolean flag = false;
                            if (modiStaChar[i] == simpleNameChar[0]) {
                                for (int j = 0, k = i; (j < simpleNameChar.length) && (k < modiStaChar.length); j++, k++) {
                                    if (modiStaChar[k] != simpleNameChar[j]) {
                                        flag = true;
                                        break;
                                    }
                                }
                                if (!flag) {
                                    if ((i > 0) && (((modiStaChar[i - 1] >= 'a') && (modiStaChar[i - 1] <= 'z')) || ((modiStaChar[i - 1] >= 'A') && (modiStaChar[i - 1] <= 'Z')))) {
                                        flag = true;
                                    } else if ((i + simpleNameChar.length < modiStaChar.length)
                                            && (((modiStaChar[i + simpleNameChar.length] >= 'a') && (modiStaChar[i + simpleNameChar.length] <= 'z'))
                                            || ((modiStaChar[i + simpleNameChar.length] >= 'A') && (modiStaChar[i + simpleNameChar.length] <= 'Z')))) {
                                        flag = true;
                                    }
                                }
                                if (!flag) {
                                    String sMid = sfinalStr;
                                    sMid += expressionStr;
                                    for (int k = i + simpleNameChar.length; k < modiStaChar.length; k++) {
                                        sMid += modiStaChar[k];
                                    }
                                }
                            }
                            sfinalStr += modiStaChar[i];
                        }
                    }
                }
            }

        }
        for (int k = 0; k < 700; k++) {
            if ((strings[k] != null) && (strings[k].length() > 2)) {
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


    public static List<Statement> getChangedQualifiedName(ModificationPoint mp, String simpleName) {
        String modiSta = mp.getStatement().toString();
        List<Statement> statementList = new ArrayList<>();
        int iArray = 0;
        long recordNum = System.currentTimeMillis();
        List<ExpressionInfo> expressionInfoList = mp.getExpressionInfosIngredients();
        String[] strings = new String[700];

        //我替换的是整体，而不是替换里面的一个字符。只要是防止一个字母的情况出现
        boolean checkFlag = false;
        String[] check = modiSta.split(simpleName);
        for (int kk = 1; (kk < check.length) && (check[kk].length() > 0); kk++) {
            if (((check[kk].charAt(0) >= 'a') && (check[kk].charAt(0) <= 'z')) ||
                    ((check[kk].charAt(0) >= 'A') && (check[kk].charAt(0) <= 'Z'))) {
                checkFlag = true;
                break;
            }
        }

        if (!checkFlag) {
            for (ExpressionInfo eInfo : expressionInfoList) {
                String midStr = "";
                String expStr = eInfo.getExpression().toString();

                boolean flagAll = RuleCheck.changeNameRuleAll(mp, simpleName, eInfo.getExpression());

                //模式一，只替换一个变量,相同的變量依次都更換
                if (flagAll) {
                    if ((!TemplateBoolean.templateBooleanCheck(mp, recordNum + expStr + "all"))) {
                        mp.getTemplateBoolean().put(recordNum + expStr + "all", true);

                        if (iArray < 698) {
                            int num = 0;
                            if (modiSta.split(simpleName).length >= 3)
                                num = 3;
                            else if (modiSta.split(simpleName).length == 2)
                                num = 2;

                            if (num == 2) {
                                String[] array = modiSta.split(simpleName, num);
                                midStr += array[0] + expStr;
                                midStr += array[1];
                            } else if (num == 3) {
                                String[] array = modiSta.split(simpleName, num);
                                midStr += array[0] + expStr;
                                midStr += array[1] + expStr;
                                midStr += array[2];
                            }
                            strings[iArray++] = midStr;
                        }

                    }
                    //模式二，若有多个变量每次仅仅更新一个
                    if ((!TemplateBoolean.templateBooleanCheck(mp, recordNum + expStr + "onlyone"))) {
                        mp.getTemplateBoolean().put(recordNum + expStr + "onlyone", true);
                        String[] sMid = modiSta.split(simpleName);
                        StringBuilder finalOne = new StringBuilder();
                        StringBuilder finalTwo = new StringBuilder();
                        for (int i = 0; i < sMid.length; i++) {
                            finalOne.append(sMid[i]);
                            if (i != sMid.length - 1)
                                finalTwo = new StringBuilder(finalOne + expStr);
                            for (int j = i + 1; j < sMid.length; j++) {
                                finalTwo.append(sMid[j]);
                                if (j != sMid.length - 1)
                                    finalTwo.append(simpleName);
                            }
                            if (iArray < 698)
                                strings[iArray++] = finalTwo.toString();
                            else
                                break;
                            finalTwo = new StringBuilder();
                            finalOne.append(simpleName);
                        }
                    }

                }
            }
        } else {
            //其实这个已经可以代替上面的if了，还没有大规模验证，等待等待再替换
            char[] modiStaChar = modiSta.toCharArray();
            char[] simpleNameChar = simpleName.toCharArray();
            String sfinalStr = "";
            for (ExpressionInfo expressionInfo : expressionInfoList) {
                sfinalStr = "";
                String expressionStr = expressionInfo.getExpression().toString();

                boolean flagAll = RuleCheck.changeNameRuleAll(mp, simpleName, expressionInfo.getExpression());

                if (flagAll) {
                    if (!TemplateBoolean.templateBooleanCheck(mp, recordNum + expressionStr + "OnlyOneElseC")) {
                        mp.getTemplateBoolean().put(recordNum + expressionStr + "OnlyOneElseC", true);
                        for (int i = 0; i < modiStaChar.length; i++) {
                            boolean flag = false;
                            if (modiStaChar[i] == simpleNameChar[0]) {
                                for (int j = 0, k = i; (j < simpleNameChar.length) && (k < modiStaChar.length); j++, k++) {
                                    if (modiStaChar[k] != simpleNameChar[j]) {
                                        flag = true;
                                        break;
                                    }
                                }
                                if (!flag) {
                                    if ((i > 0) && (((modiStaChar[i - 1] >= 'a') && (modiStaChar[i - 1] <= 'z')) || ((modiStaChar[i - 1] >= 'A') && (modiStaChar[i - 1] <= 'Z')))) {
                                        flag = true;
                                    } else if ((i + simpleNameChar.length < modiStaChar.length)
                                            && (((modiStaChar[i + simpleNameChar.length] >= 'a') && (modiStaChar[i + simpleNameChar.length] <= 'z'))
                                            || ((modiStaChar[i + simpleNameChar.length] >= 'A') && (modiStaChar[i + simpleNameChar.length] <= 'Z')))) {
                                        flag = true;
                                    }
                                }
                                if (!flag) {
                                    String sMid = sfinalStr;
                                    sMid += expressionStr;
                                    for (int k = i + simpleNameChar.length; k < modiStaChar.length; k++) {
                                        sMid += modiStaChar[k];
                                    }
                                }
                            }
                            sfinalStr += modiStaChar[i];
                        }
                    }
                }
            }

        }
        for (int k = 0; k < 700; k++) {
            if ((strings[k] != null) && (strings[k].length() > 2)) {
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
