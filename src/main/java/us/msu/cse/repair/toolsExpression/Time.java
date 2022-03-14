package us.msu.cse.repair.toolsExpression;

import java.io.File;
import java.io.FileOutputStream;
import java.io.WriteAbortedException;

public class Time {
    private static  long time1=0;
    private static  long time2=0;
    private static  long time3=0;
    private static  long time4=0;
    private static  long time5=0;
    private static  long time6=0;
    private static  long time7=0;
    private static  long time8=0;
    private static  long time9=0;

    private static String  arjaIngredientNum = "";
    private static String  expressionIngredientNum = "";



    /**
     * 1. 设置一个初始时间——补丁生成之前 time1
     * 2. 补丁生成开始时间 time2
     * 3.种子语句生成开始时间  time3
     * 4.种子语句生成完成结束时间  time4
     * 5.表达式生成开始时间  time5
     * 6.表达式生成结束时间=补丁生成结束时间  time6
     * 7. 搜索补丁开始时间  time7
     * 8. 搜索补丁结束时间  time8
     * 9. 到最后总的时间  time9
     */
    //语句补丁生成时间
    public static long getTime1(){
        return (time4 - time3);
    }
    //表达式补丁生成时间
    public static long getTime2(){
        return (time6 - time5);
    }
    //总的补丁生成时间
    public static long getTime3(){
        return (time6 - time2);
    }
    //补丁搜索时间
    public static long getTime4(){
        return (time8 - time7);
    }
    //总的时间花费
    public static long getTime5(){
        return (time9 - time1);
    }


    public static void setTime1(long time1) {
        Time.time1 = time1;
    }

    public static void setTime2(long time2) {
        Time.time2 = time2;
    }

    public static void setTime3(long time3) {
        Time.time3 = time3;
    }

    public static void setTime4(long time4) {
        Time.time4 = time4;
    }

    public static void setTime5(long time5) {
        Time.time5 = time5;
    }

    public static void setTime6(long time6) {
        Time.time6 = time6;
    }

    public static void setTime7(long time7) {
        Time.time7 = time7;
    }

    public static void setTime8(long time8) {
        Time.time8 = time8;
    }

    public static void setTime9(long time9) {
        Time.time9 = time9;
    }


    public static void setArjaIngredientNum(String arjaIngredientNum) {
        Time.arjaIngredientNum = arjaIngredientNum;
    }

    public static void setExpressionIngredientNum(String expressionIngredientNum) {
        Time.expressionIngredientNum = expressionIngredientNum;
    }

    public static void WriteTime(String str, String filePath) {
        File file = new File(str);

        String str1 = "\n语句补丁生成时间:"+getTime1();
        String str2 = "\n表达式补丁生成时间:"+getTime2();
        String str3 = "\n总的补丁生成时间:"+getTime3();
        String str4 = "\n补丁搜索时间:"+getTime4();
        String str5 = "\n总的时间花费:"+getTime5();
        String str6 = writeIngredientNum();
        String strFirst = "这是修复bug时间数据\nbug路径:\n" + str+"\n"+filePath+str1+str2+str3+str4+str5+str6;
        byte[] b = strFirst.getBytes();
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            out.write(b);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static String writeIngredientNum(){
        String s1 = "";
        s1+="\n原Arja产生的成分数量：\n"+arjaIngredientNum;
        s1+="\n表达式级别产生的成分数量:\n"+expressionIngredientNum;
        return s1;
    }
}

