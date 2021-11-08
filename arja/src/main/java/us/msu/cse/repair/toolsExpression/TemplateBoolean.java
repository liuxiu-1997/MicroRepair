package us.msu.cse.repair.toolsExpression;

import us.msu.cse.repair.core.parser.ModificationPoint;

import java.util.Map;

public class TemplateBoolean {
    /**
     * 我定义的木板如果被使用后，就不再使用了;
     */
    public static boolean templateBooleanCheck(ModificationPoint modificationPoint,String key){
        Map<String,Boolean> stringBooleanMap = modificationPoint.getTemplateBoolean();
        if (stringBooleanMap.containsKey(key)){
            if (stringBooleanMap.get(key)){
                return true;
            }
        }
        return false;


    }
}
