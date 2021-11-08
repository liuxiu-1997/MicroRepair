package us.msu.cse.repair.algorithmsExpression;

import us.msu.cse.repair.informationExpression.ExpressionInfo;

import java.util.List;

public class ExpressionPriorityMaxSelect {
    public ExpressionPriorityMaxSelect(){
    }
    public ExpressionInfo getMaxPriority(List<ExpressionInfo> expressionInfoList){

        double maxpriority = 0;
        ExpressionInfo expInfo = null;

        for (ExpressionInfo exp:expressionInfoList) {
            if (exp.getPriority()>maxpriority){
                maxpriority = exp.getPriority();
                expInfo = exp;
            }
        }
        if (maxpriority>0){
            expInfo.setPriority(-1);
        }
        return expInfo;
    }
}
