package us.msu.cse.repair.repairExpression;

import us.msu.cse.repair.core.parser.ModificationPoint;
import us.msu.cse.repair.informationExpression.ExpressionInfo;

import java.util.List;

public class ExpressionIngredientSize{
    public static boolean isNullOfExpIngre(ModificationPoint mp){
        List<ExpressionInfo> list = mp.getExpressionInfosIngredients();
        for (ExpressionInfo e:list) {
            if (e.getPriority()>=0)
                return false;
        }
        return true;
    }
}
