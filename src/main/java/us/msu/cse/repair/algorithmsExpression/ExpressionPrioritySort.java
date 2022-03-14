package us.msu.cse.repair.algorithmsExpression;

import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.Statement;
import us.msu.cse.repair.core.parser.ModificationPoint;
import us.msu.cse.repair.informationExpression.ExpressionInfo;
import us.msu.cse.repair.informationExpression.LineAndNodeType;

import javax.swing.*;
import java.util.List;
import java.util.Locale;

public class ExpressionPrioritySort {

    public ExpressionPrioritySort(){

    }

    /**
     * 对补丁表达式的成分进行优先级排序
     * 公式：优先级 = 行数差的倒数 + 类型是否相同*0.3 + 在函数体内是否出过*0.1 ;
     * @param mp
     * @param expressionInfos
     */
    public void priorityAllocation(ModificationPoint mp,List<ExpressionInfo> expressionInfos){
        if (expressionInfos!=null){
            LineAndNodeType mpLine = mp.getLineAndNodeType();
            for (ExpressionInfo exp:expressionInfos) {
                LineAndNodeType expLine = exp.getLineAndNodeType();
                int typeSame = mpLine.getNodeType()==expLine.lineOfStaOrExp?1:0;
                int bodySame = bodySameTest(mp.getStatement(),exp.getExpression());
                double priority = computer(mpLine.lineOfStaOrExp-expLine.lineOfStaOrExp,typeSame,bodySame);
                exp.setPriority(priority);
            }


        }
    }
    public double computer(double a,double b,double c){
        if (a!=0){
            return 1/a + b*0.2 + c*0.8;
        }else
            return 1 + b*0.2 + c*0.8;
    }

    public int bodySameTest(Statement statement, Expression expression){
        //expression中有一般的字符在是他特们他中出想则认为他俩有关联
        char[] staStr = statement.toString().toCharArray();
        int max=0;
        char[] expStr = expression.toString().toCharArray();

        for (int j=0;j<expStr.length;j++){
            for (int k=0;k<staStr.length;k++){
                int jj=j,kk=k,i=0;
                while((jj<expStr.length)&&(kk<staStr.length)&&(expStr[jj]==staStr[kk])){
                    i++;
                    jj++;
                    kk++;
                    if (i>max){
                        max = i;
                    }
                }
            }
        }
            for (int i=0;i<expStr.length;i++){
                if (expStr[i]=='='||expStr[i]=='！'||expStr[i]=='&'||expStr[i]==')'||expStr[i]=='('||
                        expStr[i]=='['||expStr[i]==']'){
                    for (int j=i;j<expStr.length-1;j++){
                        expStr[j]=expStr[j+1];
                    }

                }
            }



       if (max>=(expStr.length/2))
           return 1;
       return 0;
    }

}
