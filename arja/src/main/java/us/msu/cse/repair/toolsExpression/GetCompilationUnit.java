package us.msu.cse.repair.toolsExpression;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import us.msu.cse.repair.astVisitorExpression.GetVariableVisitor;
import us.msu.cse.repair.informationExpression.ExpressionInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

public class GetCompilationUnit {
    public static CompilationUnit getCompilationUnitOfPath(String sourcePath){
        File file=new File(sourcePath);
        try {
            FileInputStream in=new FileInputStream(file);
            // size  为字串的长度 ，这里一次性读完
            int size=in.available();
            byte[] buffer=new byte[size];
            in.read(buffer);
            in.close();
            String str=new String(buffer,"GB2312");

            ASTParser astParser = ASTParser.newParser(AST.JLS8);
            astParser.setKind(ASTParser.K_COMPILATION_UNIT);
            astParser.setResolveBindings(true);
            astParser.setStatementsRecovery(true);
            astParser.setSource(str.toCharArray());
            CompilationUnit compilationUnit = (CompilationUnit)astParser.createAST(null);
            if (compilationUnit == null){
                return null;
            }else
                return compilationUnit;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static CompilationUnit getCompilationUnitOfString(String strOfClass){
        ASTParser astParser = ASTParser.newParser(AST.JLS8);
        astParser.setKind(ASTParser.K_COMPILATION_UNIT);
        astParser.setResolveBindings(true);
        astParser.setStatementsRecovery(true);
        astParser.setSource(strOfClass.toCharArray());

        Map options = JavaCore.getOptions();
        JavaCore.setComplianceOptions(JavaCore.VERSION_1_7,options);
        astParser.setCompilerOptions(options);

        CompilationUnit compilationUnit = (CompilationUnit)astParser.createAST(null);
        return compilationUnit;
    }
}
