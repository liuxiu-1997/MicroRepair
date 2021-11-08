package us.msu.cse.repair.core.parser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.Statement;
import us.msu.cse.repair.informationExpression.ExpressionInfo;
import us.msu.cse.repair.informationExpression.LineAndNodeType;
import us.msu.cse.repair.informationExpression.MethClaPacOfExpName;

public class ModificationPoint {
	Statement statement;

	String sourceFilePath;

	//以下几种是我重新定义的，定义“表达式列表”的目的是为了进行表达式级别的修复
	MethClaPacOfExpName methClaPacOfExpName = new MethClaPacOfExpName();
	LineAndNodeType lineAndNodeType = new LineAndNodeType();
	List<ExpressionInfo> modificationPointExpressionInfosList;


	double suspValue;
	LCNode lcNode;

	//对应于每个修改点，其成分为普通语句还有表达式成分
	List<Statement> ingredients;
	List<ExpressionInfo> expressionInfosIngredients;

	Map<String, VarInfo> declaredFields;
	Map<String, VarInfo> inheritedFields;
	Map<String, VarInfo> outerFields;

	Map<String, VarInfo> localVars;

	Map<String, MethodInfo> delcaredMethods;
	Map<String, MethodInfo> inheritedMethods;
	Map<String, MethodInfo> outerMethods;

	boolean isInStaticMethod;

	Map<String, Boolean> templateBoolean = new HashMap<>();//对应的木板仅仅作为成分的补充，使用一次后（已有补丁成分）则舍弃

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

	public void setSuspValue(double suspValue) {
		this.suspValue = suspValue;
	}

	public double getSuspValue() {
		return this.suspValue;
	}

	public void setSourceFilePath(String sourceFilePath) {
		this.sourceFilePath = sourceFilePath;
	}

	public String getSourceFilePath() {
		return this.sourceFilePath;
	}

	public void setStatement(Statement statement) {
		this.statement = statement;
	}

	public Statement getStatement() {
		return this.statement;
	}

	public void setLCNode(LCNode lcNode) {
		this.lcNode = lcNode;
	}

	public LCNode getLCNode() {
		return this.lcNode;
	}

	public void setIngredients(List<Statement> ingredients) {
		this.ingredients = ingredients;
	}

	public List<Statement> getIngredients() {
		return this.ingredients;
	}

	public void setDeclaredFields(Map<String, VarInfo> declaredFields) {
		this.declaredFields = declaredFields;
	}

	public Map<String, VarInfo> getDeclaredFields() {
		return this.declaredFields;
	}

	public void setInheritedFields(Map<String, VarInfo> inheritedFields) {
		this.inheritedFields = inheritedFields;
	}

	public Map<String, VarInfo> getInheritedFields() {
		return this.inheritedFields;
	}

	public void setOuterFields(Map<String, VarInfo> outerFields) {
		this.outerFields = outerFields;
	}

	public Map<String, VarInfo> getOuterFields() {
		return this.outerFields;
	}

	public void setDeclaredMethods(Map<String, MethodInfo> declaredMethods) {
		this.delcaredMethods = declaredMethods;
	}

	public Map<String, MethodInfo> getDeclaredMethods() {
		return this.delcaredMethods;
	}

	public void setInheritedMethods(Map<String, MethodInfo> inheritedMethods) {
		this.inheritedMethods = inheritedMethods;
	}

	public Map<String, MethodInfo> getInheritedMethods() {
		return this.inheritedMethods;
	}

	public void setOuterMethods(Map<String, MethodInfo> outerMethods) {
		this.outerMethods = outerMethods;
	}

	public Map<String, MethodInfo> getOuterMethods() {
		return this.outerMethods;
	}

	public void setLocalVars(Map<String, VarInfo> localVars) {
		this.localVars = localVars;
	}

	public Map<String, VarInfo> getLocalVars() {
		return this.localVars;
	}

	public void setInStaticMethod(boolean isInStaticMethod) {
		this.isInStaticMethod = isInStaticMethod;
	}

	public boolean isInStaticMethod() {
		return this.isInStaticMethod;
	}


	public void setIngredientsExpressionInfo(List<ExpressionInfo> expressionInfos) {
		this.expressionInfosIngredients = expressionInfos;
	}

	public List<ExpressionInfo> getModificationPointExpressionInfosList() {
		return modificationPointExpressionInfosList;
	}

	public void setModificationPointExpressionInfosList(List<ExpressionInfo> modificationPointExpressionInfosList) {
		this.modificationPointExpressionInfosList = modificationPointExpressionInfosList;
	}

	public List<ExpressionInfo> getExpressionInfosIngredients() {
		return expressionInfosIngredients;
	}

	public Map<String, Boolean> getTemplateBoolean() {
		return templateBoolean;
	}

	public void setTemplateBoolean(Map<String, Boolean> templateBoolean) {
		this.templateBoolean = templateBoolean;
	}

	public void setExpressionInfosIngredients(List<ExpressionInfo> expressionInfosIngredients) {
		this.expressionInfosIngredients = expressionInfosIngredients;
	}

}
