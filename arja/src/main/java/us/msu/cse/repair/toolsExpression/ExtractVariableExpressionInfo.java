package us.msu.cse.repair.toolsExpression;

import org.eclipse.jdt.core.dom.*;
import us.msu.cse.repair.core.parser.ModificationPoint;
import us.msu.cse.repair.informationExpression.ExpressionInfo;

import java.util.List;

public class ExtractVariableExpressionInfo {
    public static ExpressionInfo getExpressionInfo(ASTNode astNode, String name, Expression exp, ModificationPoint mp){
        Type type = null;
        ASTNode cur = astNode;

        while (cur!=null){
            if (cur instanceof MethodDeclaration){
                MethodDeclaration m = (MethodDeclaration)cur;
                List<SingleVariableDeclaration> list = m.parameters();
                for (SingleVariableDeclaration e:list){
                    if (e.getName().toString().equals(name)){
                        type = e.getType();
                        break;
                    }
                }

            }else if (cur instanceof TypeDeclaration){
                TypeDeclaration t = (TypeDeclaration)cur;
                FieldDeclaration[] fieldDeclaration = t.getFields();
                for (FieldDeclaration f:fieldDeclaration){
                    List list = f.fragments();
                    for (Object o:list){
                        String strings = o.toString();
                        int num = strings.indexOf("=");
                        if (num > 0)
                            strings = strings.substring(0, num);
                        if (strings.equals(name)){
                            type= f.getType();
                            break;
                        }
                    }
                }
            }
            cur = cur.getParent();
        }
        if (type!=null){
            return new ExpressionInfo(exp, mp.getMethClaPacOfExpName(), mp.getLineAndNodeType(),type,name);
        }
        return null;
    }
}
