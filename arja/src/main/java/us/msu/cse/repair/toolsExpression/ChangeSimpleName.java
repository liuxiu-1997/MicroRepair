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
            if (mp.getMethodName().contains(simpleName) && mp.getMethodName().contains(eInfo.getExpression().toString())) {
                if ( (!TemplateBoolean.templateBooleanCheck(mp, eInfo.getExpressionStr() + modiSta + simpleName + "s1"))) {
                    if (i < 49) {
//                    char[] modiIngreExp = eInfo.getExpression().toString().toCharArray();
//                    String str="";
//                    int iIn=0,jIn=0,kIn=0;
//                    char[] modiPointExp = modiPointChar.clone();
//                    char[] aaChar;
//                    for (iIn=0;iIn<modiPointExp.length;iIn++){
//                        if (modiPointExp[iIn] == simpleNameChar[0]){
//                            boolean Flags = true;
//                            for (jIn=0,kIn = iIn;jIn < simpleNameChar.length ; jIn++,kIn++){
//                                if (((kIn>=modiPointExp.length)||(modiPointExp[kIn]!=simpleNameChar[jIn]))){
//                                    Flags = false;
//                                    break;
//                                }
//                            }
//                            if (jIn == simpleNameChar.length){
//                                if ((iIn-1>=0)&&((('a'<modiPointExp[iIn-1])&&(modiPointExp[iIn-1]<'z'))||(('A'<modiPointExp[iIn-1])&&(modiPointExp[iIn-1]<'Z')))) {
//                                    Flags = false;
//                                }
//                                if ((kIn<modiPointExp.length)&&((('a'<modiPointExp[kIn])&&(modiPointExp[kIn]<'z'))||(('A'<modiPointExp[kIn])&&(modiPointExp[kIn]<'Z')))){
//                                    Flags = false;
//                                }
//                                if (Flags){
//                                    int len = modiIngreExp.length - simpleNameChar.length;
//                                    aaChar = new char[modiPointExp.length + len];
//                                    str = "";
//                                    if (len>0) {
//                                        for (int m = modiPointExp.length + len-1,n=modiPointExp.length-1; n>= iIn + simpleNameChar.length; m--,n--) {
//                                            aaChar[m] = modiPointExp[n];
//                                        }
//                                        System.arraycopy(modiPointExp, 0, aaChar, 0, iIn + 1);
//                                        for (int m = iIn, n = 0; m < iIn + modiIngreExp.length; m++, n++) {
//                                            aaChar[m] = modiIngreExp[n];
//                                        }
//                                        if (simpleName.equals("minMiddleIndex")) {
//                                            System.out.println("len>0:" );
//                                            for (char c : aaChar)
//                                                System.out.print(c);
//                                            System.out.println();
//                                        }
//                                    }else if (len < 0) {
//                                        for (int m = iIn+modiIngreExp.length, n = iIn + simpleName.length(); n < modiPointExp.length - 2; m++, n++)
//                                            aaChar[m] = modiPointExp[n];
//                                        System.arraycopy(modiPointExp, 0, aaChar, 0, iIn);
//                                        for (int m = iIn, n = 0; m < iIn + modiIngreExp.length; m++, n++) {
//                                            aaChar[m] = modiIngreExp[n];
//                                        }
//                                        if (simpleName.equals("minMiddleIndex")) {
//                                            System.out.println("len<0:");
//                                            for (char c : aaChar)
//                                                System.out.print(c);
//                                            System.out.println();
//                                        }
//                                    }else {
//                                        for (int m=iIn,n=0;n<modiIngreExp.length;m++,n++)
//                                            aaChar[m] = modiIngreExp[n];
//                                        if (simpleName.equals("minMiddleIndex")) {
//                                            System.out.println("len==0:" );
//                                            for (char c : aaChar)
//                                                System.out.print(c);
//                                            System.out.println();
//                                        }
//                                    }
//                                    for (char c : aaChar)
//                                        str += c;
//
//                                    if (simpleName.equals("minMiddleIndex")&&(str!=null))
//                                        System.out.println("162-----str:"+str+"\ni: "+(i+1)+"\n"+mp.getLCNode().getLineNumber());
//                                    strings[i++]=str;
//                                    modiPointExp = aaChar;
//                                }
//
//                            }
//                        }
//                    }
//                    CharSequence c1 = simpleName.subSequence(0, simpleName.length());
//                    CharSequence c2 = modiExp.subSequence(0, modiExp.length());
//                    strings[i++] = modiSta.replace(c1,c2);
//                    strings[i++] = str;
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
            }else if (mp.getVariableName().contains(simpleName) && mp.getVariableName().contains(eInfo.getExpression().toString())) {
                if ( (!TemplateBoolean.templateBooleanCheck(mp, eInfo.getExpressionStr() + modiSta + simpleName + "s1"))) {
                    if (i < 49) {
//                    char[] modiIngreExp = eInfo.getExpression().toString().toCharArray();
//                    String str="";
//                    int iIn=0,jIn=0,kIn=0;
//                    char[] modiPointExp = modiPointChar.clone();
//                    char[] aaChar;
//                    for (iIn=0;iIn<modiPointExp.length;iIn++){
//                        if (modiPointExp[iIn] == simpleNameChar[0]){
//                            boolean Flags = true;
//                            for (jIn=0,kIn = iIn;jIn < simpleNameChar.length ; jIn++,kIn++){
//                                if (((kIn>=modiPointExp.length)||(modiPointExp[kIn]!=simpleNameChar[jIn]))){
//                                    Flags = false;
//                                    break;
//                                }
//                            }
//                            if (jIn == simpleNameChar.length){
//                                if ((iIn-1>=0)&&((('a'<modiPointExp[iIn-1])&&(modiPointExp[iIn-1]<'z'))||(('A'<modiPointExp[iIn-1])&&(modiPointExp[iIn-1]<'Z')))) {
//                                    Flags = false;
//                                }
//                                if ((kIn<modiPointExp.length)&&((('a'<modiPointExp[kIn])&&(modiPointExp[kIn]<'z'))||(('A'<modiPointExp[kIn])&&(modiPointExp[kIn]<'Z')))){
//                                    Flags = false;
//                                }
//                                if (Flags){
//                                    int len = modiIngreExp.length - simpleNameChar.length;
//                                    aaChar = new char[modiPointExp.length + len];
//                                    str = "";
//                                    if (len>0) {
//                                        for (int m = modiPointExp.length + len-1,n=modiPointExp.length-1; n>= iIn + simpleNameChar.length; m--,n--) {
//                                            aaChar[m] = modiPointExp[n];
//                                        }
//                                        System.arraycopy(modiPointExp, 0, aaChar, 0, iIn + 1);
//                                        for (int m = iIn, n = 0; m < iIn + modiIngreExp.length; m++, n++) {
//                                            aaChar[m] = modiIngreExp[n];
//                                        }
//                                        if (simpleName.equals("minMiddleIndex")) {
//                                            System.out.println("len>0:" );
//                                            for (char c : aaChar)
//                                                System.out.print(c);
//                                            System.out.println();
//                                        }
//                                    }else if (len < 0) {
//                                        for (int m = iIn+modiIngreExp.length, n = iIn + simpleName.length(); n < modiPointExp.length - 2; m++, n++)
//                                            aaChar[m] = modiPointExp[n];
//                                        System.arraycopy(modiPointExp, 0, aaChar, 0, iIn);
//                                        for (int m = iIn, n = 0; m < iIn + modiIngreExp.length; m++, n++) {
//                                            aaChar[m] = modiIngreExp[n];
//                                        }
//                                        if (simpleName.equals("minMiddleIndex")) {
//                                            System.out.println("len<0:");
//                                            for (char c : aaChar)
//                                                System.out.print(c);
//                                            System.out.println();
//                                        }
//                                    }else {
//                                        for (int m=iIn,n=0;n<modiIngreExp.length;m++,n++)
//                                            aaChar[m] = modiIngreExp[n];
//                                        if (simpleName.equals("minMiddleIndex")) {
//                                            System.out.println("len==0:" );
//                                            for (char c : aaChar)
//                                                System.out.print(c);
//                                            System.out.println();
//                                        }
//                                    }
//                                    for (char c : aaChar)
//                                        str += c;
//
//                                    if (simpleName.equals("minMiddleIndex")&&(str!=null))
//                                        System.out.println("162-----str:"+str+"\ni: "+(i+1)+"\n"+mp.getLCNode().getLineNumber());
//                                    strings[i++]=str;
//                                    modiPointExp = aaChar;
//                                }
//
//                            }
//                        }
//                    }
//                    CharSequence c1 = simpleName.subSequence(0, simpleName.length());
//                    CharSequence c2 = modiExp.subSequence(0, modiExp.length());
//                    strings[i++] = modiSta.replace(c1,c2);
//                    strings[i++] = str;
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
