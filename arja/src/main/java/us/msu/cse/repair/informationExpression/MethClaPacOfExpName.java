package us.msu.cse.repair.informationExpression;

public class MethClaPacOfExpName {
    public String expressionMethodName;
    public String expressionClassName;
    public String expressionPackageName;

    public MethClaPacOfExpName(String expressionMethodName, String expressionClassName, String expressionPackageName) {
        this.expressionMethodName = expressionMethodName;
        this.expressionClassName = expressionClassName;
        this.expressionPackageName = expressionPackageName;
    }

    public MethClaPacOfExpName() {

    }

    public String getExpressionMethodName() {
        return expressionMethodName;
    }

    public void setExpressionMethodName(String expressionMethodName) {
        this.expressionMethodName = expressionMethodName;
    }

    public String getExpressionClassName() {
        return expressionClassName;
    }

    public void setExpressionClassName(String expressionClassName) {
        this.expressionClassName = expressionClassName;
    }

    public String getExpressionPackageName() {
        return expressionPackageName;
    }

    public void setExpressionPackageName(String expressionPackageName) {
        this.expressionPackageName = expressionPackageName;
    }
}
