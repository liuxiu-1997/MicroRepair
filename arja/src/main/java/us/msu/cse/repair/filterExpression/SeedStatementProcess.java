package us.msu.cse.repair.filterExpression;

import org.eclipse.jdt.core.dom.*;
import us.msu.cse.repair.core.parser.ModificationPoint;
import us.msu.cse.repair.core.parser.SeedStatement;
import us.msu.cse.repair.core.parser.SeedStatementInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SeedStatementProcess {
    /**
     * 此类主要进行对所有的种子语句进行初步处理
     * 用途：
     * 1.提取之前AllTypeVisitor中的各类表达式。同时为表达式指定：方法名:类名：包名
     */
//    private Map<SeedStatement, SeedStatementInfo> seedStatements;
//    private List<ExpressionInfo> expressionInfos = new ArrayList<>();
//
//    public void process(){
//        while (!(curNode.getParent() instanceof MethodDeclaration || curNode.getParent() instanceof Initializer)) {
//            if (curNode.getParent() instanceof Block || curNode.getParent() instanceof SwitchStatement) {//curNode为语句块或Switch语句时执行
//                StructuralPropertyDescriptor property = curNode.getLocationInParent();;
//                List<?> statements = (List<?>) (curNode.getParent().getStructuralProperty(property));
//
//                int index = statements.indexOf(curNode);//提取的语句为方法内修改点之前的语句
//                // i<index or i<=index
//                for (int i = 0; i < index; i++) {
//                    Statement st = (Statement) statements.get(i);
//                    if (st instanceof VariableDeclarationStatement)//当语句为声明语句时,我将它提取到localVars中
//                        extractVarDecl((VariableDeclarationStatement) st, localVars);
//                }
//            } else if (curNode.getParent() instanceof ForStatement)
//                extractVarDecl((ForStatement) curNode.getParent(), localVars);
//            else if (curNode.getParent() instanceof EnhancedForStatement)
//                extractVarDecl((EnhancedForStatement) curNode.getParent(), localVars);
//            else if (curNode.getParent() instanceof CatchClause)
//                extractVarDecl((CatchClause) curNode.getParent(), localVars);
//
//            curNode = curNode.getParent();
//
//
//            /*
//             * System.out.println(curNode== null);
//             * System.out.println(curNode.toString());
//             */
//        }
//        //当当前节点的父类非方法声明时,我提取其中的变量声明成分;当当前节点的父类为方法声明时,我将提取其中的形参变量。
//        if (curNode.getParent() instanceof MethodDeclaration) {
//            MethodDeclaration md = (MethodDeclaration) curNode.getParent();
//            extractVarDecl(md, localVars);
//        }
//
//        for (Map.Entry<SeedStatement,SeedStatementInfo> entry:seedStatements.entrySet()){
//            Statement statement = entry.getKey().getStatement();
//            statement.getParent()
//        }
//    }

}
