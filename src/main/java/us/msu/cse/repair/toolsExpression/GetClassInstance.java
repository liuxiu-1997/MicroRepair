package us.msu.cse.repair.toolsExpression;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import us.msu.cse.repair.informationExpression.LineAndNodeType;

public class GetClassInstance {
    public static LineAndNodeType getLineAndNodeType(ASTNode node,int num){
        CompilationUnit cu = (CompilationUnit) node.getRoot();
        int lineNumber = cu.getLineNumber(node.getStartPosition());
        return new LineAndNodeType(lineNumber,num);
    }
}
