package us.msu.cse.repair.toolsExpression;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import us.msu.cse.repair.core.parser.ModificationPoint;
import us.msu.cse.repair.informationExpression.ExpressionInfo;
import us.msu.cse.repair.informationExpression.MethClaPacOfExpName;

import java.util.List;

public class GlobalVariableCheck {
    public static boolean globalVariable(ModificationPoint mp,ExpressionInfo e){

        if (mp.getMethClaPacOfExpName().expressionMethodName == e.getMethClaPacOfExpName().expressionMethodName)
            return true;
        ASTNode curNode = e.getExpression();
        while(curNode!=null){
            if (curNode instanceof TypeDeclaration){
                TypeDeclaration cur = (TypeDeclaration) curNode;
                FieldDeclaration[] fieldDeclaration =cur.getFields();
                for (FieldDeclaration f:fieldDeclaration){
                    List list = f.fragments();
                    for (Object o:list){
                        String strings = o.toString();
                        int num = strings.indexOf("=");
                        if (num > 0)
                            strings = strings.substring(0, num);
                        if (strings.equals(e.getVarNameStr()))
                            return true;
                    }
                }
            }
            curNode = curNode.getParent();
        }
        return false;


    }
}
