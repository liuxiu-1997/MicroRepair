package us.msu.cse.repair.toolsExpression;

import org.eclipse.jdt.core.dom.*;
import us.msu.cse.repair.core.parser.ModificationPoint;
import us.msu.cse.repair.informationExpression.ExpressionInfo;

public class RuleCheck {
    //SimpleName不能替换Type
    public static boolean rule1OfSimpleName(ModificationPoint mp , SimpleName simpleName){
        if (mp.getTypeName()!=null){
            for (Type type:mp.getTypeName()){
                if (type.toString().equals(simpleName.toString())){
                    return false;
                }
            }
        }
        return true;
    }
    //SimpleName不能为定义的中介类Test1
    public static boolean rule2OfClassName(ModificationPoint mp , SimpleName simpleName){
        if (simpleName.toString().equals("Test1")){
            return false;
        }
        return true;
    }

    public static boolean rule2OfIfRepair(ModificationPoint mp,Expression expressionOfIf, ExpressionInfo expressionIngre){
        boolean flag1 = false;
        Expression expressionOfIngre = expressionIngre.getExpression();
        if (((expressionOfIf instanceof SimpleName)&&(mp.getVariableName().contains(expressionOfIf.toString())))
                ||(expressionOfIf instanceof QualifiedName)){
            if ((expressionOfIngre instanceof SimpleName)&&(mp.getVariableName().contains(expressionOfIngre.toString()))){
                flag1 = true;
            }
            if (expressionOfIngre instanceof MethodInvocation){
                flag1 = true;
            }
            if (expressionOfIngre instanceof FieldAccess){
                flag1 = true;
            }
            if (expressionOfIngre instanceof QualifiedName){
                flag1 = true;
            }
        }else if (expressionOfIf instanceof MethodInvocation){
            if (expressionOfIngre instanceof MethodInvocation){
                flag1 = true;
            }
        }
        return flag1;
    }
    //主要用于simpleName的范围检查，是否在规定的范围内
    public static boolean rule2OfSimpleName(ModificationPoint mp, SimpleName simpleName){
        if (mp.getGlobalVariableName().contains(simpleName.toString())||mp.getMethodName().contains(simpleName.toString()))
            return true;
        else{
            ASTNode cur = mp.getStatement();
            ASTNode checkASTNode = null;
            //第二种方法，检测是否时MethodDeclaration
            while ((cur !=null)&&(!(cur instanceof MethodDeclaration))){
                cur = cur.getParent();
            }
            if (cur!=null){
                checkASTNode = cur;
            }else {
                //第三种方法，也是出一个ASTNode
                cur = mp.getStatement();
                CompilationUnit compilationUnit = (CompilationUnit) simpleName.getRoot();
                int  lineNode = compilationUnit.getLineNumber(simpleName.getStartPosition());
                while(cur!=null){
                    CompilationUnit compilationUnitIn = (CompilationUnit) cur.getRoot();
                    int  lineCur = compilationUnitIn.getLineNumber(cur.getStartPosition());
                    if (cur instanceof Block){
                        checkASTNode = cur;
                    }
                    if ((lineNode - lineCur) > 8 ){
                        checkASTNode = cur;
                        break;
                    }
                    cur=cur.getParent();
                }
            }
            if (checkASTNode != null){

                SimpleNameCheckASTVisitor simplenameCheckASTVisitor = new SimpleNameCheckASTVisitor(simpleName.toString());
                checkASTNode.accept(simplenameCheckASTVisitor);
                return simplenameCheckASTVisitor.getIsIncluded();
            }
        }
        return false;
    }
}
class SimpleNameCheckASTVisitor extends ASTVisitor{
    private volatile boolean isIncluded = false;
    private String temp = "";
    public SimpleNameCheckASTVisitor(String temp){
        this.temp = temp;
    }

    @Override
    public boolean visit(SimpleName node) {
        if (node.toString().equals(temp))
            isIncluded = true;
        return super.visit(node);
    }

    public boolean getIsIncluded(){
        return isIncluded;
    }
}
