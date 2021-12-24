package us.msu.cse.repair.toolsExpression;

public class GetPatchId {
    public static String getId(String str){

        String chart = "chart";
        if (str.contains(chart.subSequence(0,chart.length())))
            return str.substring(54,62);
        else
            return str.substring(53,60);
    }
}
