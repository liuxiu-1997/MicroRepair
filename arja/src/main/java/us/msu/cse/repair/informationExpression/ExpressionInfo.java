package us.msu.cse.repair.informationExpression;

import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.Type;

import java.util.Objects;

public class ExpressionInfo implements Cloneable{
    private Expression expression;
    private Integer expressionNodeType =-1; //用于记录表达式的nodeType值
    private  String expressionStr=null;     //用于记录表达式的字符串形式

   private MethClaPacOfExpName methClaPacOfExpName = new MethClaPacOfExpName();


    private Type varType;//用于记录变量的类型
    private String varTypeStr;//用于记录变量类型的字符串值
    private String varNameStr;//用于记录变量名的字符串值


//    private int expressionLine;//用于记录表达式在java文件中的位置
//    private int statementNodeType; //用于记录表达式所在语句的 语句类型
    private LineAndNodeType lineAndNodeType = new LineAndNodeType();

    private double priority;     //用于补丁生成时，优先选择priority大的
                                 //当使用过或有问题，则赋值为-1,以后不再使用
    /**
     * 此变量用于确定我修改后变量的适应类型：
     * ——仅通过set与get去访问
     * 1.记 1 为返回boolean值
     * 2.记 2 为返回field(变量)值
     * 3.记 3 为返回值
     */
    private int ExpressionType;
    public ExpressionInfo(Expression expression,MethClaPacOfExpName methClaPacOfExpName,LineAndNodeType lineAndNodeType){
        this.expression = expression;
        if (expression != null) {
            this.expressionStr = expression.toString();
            this.expressionNodeType = expression.getNodeType();
        }
        this.methClaPacOfExpName = methClaPacOfExpName;
        this.lineAndNodeType = lineAndNodeType;
    }

    public ExpressionInfo(Expression expression, MethClaPacOfExpName methClaPacOfExpName,LineAndNodeType lineAndNodeType, Type varType, String varNameStr) {
        this.expression = expression;
        this.expressionNodeType = expression.getNodeType();
        this.expressionStr = expression.toString();
        this.methClaPacOfExpName = methClaPacOfExpName;

        this.lineAndNodeType = lineAndNodeType;
        this.varType = varType;
        varTypeStr = varType.toString();
        this.varNameStr = varNameStr;
    }
    //有时候对于一些语句，真的 是没有类型，但是我需要一位进行标记;
    //利用varNameStr进行标记
    public ExpressionInfo(Expression expression, MethClaPacOfExpName methClaPacOfExpName,LineAndNodeType lineAndNodeType,String varNameStr) {
        this.expression = expression;
        this.expressionNodeType = expression.getNodeType();
        this.expressionStr = expression.toString();
        this.methClaPacOfExpName = methClaPacOfExpName;
        this.lineAndNodeType = lineAndNodeType;
        this.varNameStr = varNameStr;
    }
    public ExpressionInfo(Expression expression, Type varType, String varNameStr) {
        this.expression = expression;
        this.expressionNodeType = expression.getNodeType();
        this.expressionStr = expression.toString();
        this.varType = varType;
        varTypeStr = varType.toString();
        this.varNameStr = varNameStr;
    }

    public ExpressionInfo() {

    }


    public int getExpressionType() {
        return ExpressionType;
    }

    public void setExpressionType(int expressionType) {
        ExpressionType = expressionType;
    }

    public Expression getExpression() {
        return expression;
    }

    public Integer getExpressionNodeType() {
        return expressionNodeType;
    }

    public String getExpressionStr() {
        return expressionStr;
    }


    public Type getVarType() {
        if (varType!=null)
            return varType;
        else
            return null;
    }

    public void setVarType(Type varType) {
        this.varType = varType;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }




    public String getVarTypeStr() {
        return varTypeStr;
    }

    public void setVarTypeStr(String varTypeStr) {
        this.varTypeStr = varTypeStr;
    }

    public String getVarNameStr() {
        return varNameStr;
    }

    public void setVarNameStr(String varNameStr) {
        this.varNameStr = varNameStr;
    }

    public MethClaPacOfExpName getMethClaPacOfExpName() {
        return methClaPacOfExpName;
    }

    public void setMethClaPacOfExpName(MethClaPacOfExpName methClaPacOfExpName) {
        this.methClaPacOfExpName = methClaPacOfExpName;
    }

    public LineAndNodeType getLineAndNodeType() {
        return lineAndNodeType;
    }

    public void setLineAndNodeType(LineAndNodeType lineAndNodeType) {
        this.lineAndNodeType = lineAndNodeType;
    }

    public double getPriority() {
        return priority;
    }

    public void setPriority(double priority) {
        this.priority = priority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExpressionInfo that = (ExpressionInfo) o;
        if ((that.expressionStr!=null)&&(expressionStr!=null)){
            return Objects.equals(that.expressionStr,expressionStr)&&Objects.equals(that.lineAndNodeType,lineAndNodeType);
        }else
            return false;

    }

    @Override
    public int hashCode() {
        return Objects.hash(expression.toString());
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        ExpressionInfo eI = null;
        try {
            eI = (ExpressionInfo) super.clone();
        }catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return eI;
    }
}

