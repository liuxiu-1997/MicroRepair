package us.msu.cse.repair.toolsExpression;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.SimpleName;

public class GetNumId {
    public static String getId(String str){

        String chart = "chart";
        if (str.contains(chart.subSequence(0,chart.length())))
            return str.substring(54,62);
        else
            return str.substring(53,60);
    }
    public static int getLineOfArrayAccess(ArrayAccess access){
        LineOfArrayAccess lineOfArrayAccess = new LineOfArrayAccess(access.getArray().toString());
        CompilationUnit compilationUnit = (CompilationUnit) access.getRoot();
        compilationUnit.accept(lineOfArrayAccess);
        return lineOfArrayAccess.getLineNumberAll();
    }
}
class LineOfArrayAccess extends ASTVisitor{
    private volatile  int lineNumberAll = 40000;
    private String str = "";
    public LineOfArrayAccess(String str){
        this.str = str;
    }
    @Override
    public boolean visit(SimpleName node) {
        if (node.toString().equals(str)) {
            CompilationUnit cu = (CompilationUnit) node.getRoot();
            int lineNumber = cu.getLineNumber(node.getStartPosition());
            if (lineNumberAll > lineNumber) {
                lineNumberAll = lineNumber;
            }
        }
        return true;
    }

    public int getLineNumberAll() {
        return lineNumberAll;
    }
}
