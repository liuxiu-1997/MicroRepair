package us.msu.cse.repair.astVisitorExpression;

import org.eclipse.jdt.core.dom.*;
import us.msu.cse.repair.AbstractClass.ASTVisitorPlus;
import us.msu.cse.repair.core.parser.ModificationPoint;
import us.msu.cse.repair.toolsExpression.ChangeSimpleName;
import us.msu.cse.repair.toolsExpression.ChangeTypeName;
import us.msu.cse.repair.toolsExpression.OperatorInformation;
import us.msu.cse.repair.toolsExpression.TemplateBoolean;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BooleanAndTypeRepairVisitor extends ASTVisitorPlus {
    private ModificationPoint mp = null;
    private volatile boolean isRepaired = false;
    private volatile boolean statementFlag = false;
    private Statement s = null;

    public BooleanAndTypeRepairVisitor(ModificationPoint mp) {
        this.mp = mp;
    }

    @Override
    public boolean visit(BooleanLiteral node) {
        if (node.booleanValue()) {
            node.setBooleanValue(false);
        } else
            node.setBooleanValue(true);
        return super.visit(node);
    }


    @Override
    public boolean visit(VariableDeclarationStatement node) {

        List<Statement> statementList = ChangeTypeName.getChangedType(mp,node,node.getType().toString());
        if (mp.getIngredients() == null) {
            mp.setIngredients(statementList);
        } else {
            mp.getIngredients().addAll(statementList);
        }
        return true;
    }


    @Override
    public void endVisit(Block node) {
        if (!statementFlag) {
            ASTNode cur = node;
            while (!(cur.getParent() instanceof TypeDeclaration)) {
                cur = cur.getParent();
            }
            Initializer initializer = (Initializer) cur;
            s = (Statement) initializer.getBody().statements().get(0);
            statementFlag = true;
        }
    }

    public Statement getStatement() {
        return s;
    }

    public boolean isRepaired() {
        return isRepaired;
    }

    public void setRepaired(boolean flag) {
        isRepaired = flag;
    }
}
