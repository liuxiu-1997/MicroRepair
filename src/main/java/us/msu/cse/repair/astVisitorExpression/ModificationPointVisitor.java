package us.msu.cse.repair.astVisitorExpression;

import org.eclipse.jdt.core.dom.*;
import us.msu.cse.repair.informationExpression.ExpressionInfo;
import us.msu.cse.repair.informationExpression.LineAndNodeType;
import us.msu.cse.repair.informationExpression.MethClaPacOfExpName;

import java.util.ArrayList;
import java.util.List;

public class ModificationPointVisitor extends ASTVisitor {
    private List<ExpressionInfo> expressionInfos = new ArrayList<>();
    private MethClaPacOfExpName methClaPacOfExpName = new MethClaPacOfExpName();
    private LineAndNodeType lineAndNodeType= new LineAndNodeType();

    public ModificationPointVisitor(MethClaPacOfExpName methClaPacOfExpName, LineAndNodeType lineAndNodeType){
        this.methClaPacOfExpName = methClaPacOfExpName;
        this.lineAndNodeType = lineAndNodeType;
    }
    @Override
    public boolean visit(CastExpression node) {
        if (node!=null)
            expressionInfos.add(new ExpressionInfo(node,methClaPacOfExpName,lineAndNodeType));
        return true;
    }

    //提取为了后续进行非空指针检查
    @Override
    public boolean visit(FieldAccess node) {
        if (node!=null)
            expressionInfos.add(new ExpressionInfo(node,methClaPacOfExpName,lineAndNodeType));
        return true;
    }

    @Override
    public boolean visit(ReturnStatement node) {
        if (node.getExpression() instanceof Name ) {
            Name name = (Name) node.getExpression();
            ASTNode curNode = node;
            boolean flag=false;
            while ((curNode != null)&&(!flag)) {
                //如果是方法声明，证明这个return 后的变量只能在方法中找；
                //否则，我则去typeDeclaration中找
                if (curNode instanceof MethodDeclaration) {

                    MethodDeclaration methodDeclaration = (MethodDeclaration) curNode;
                    Block block = methodDeclaration.getBody();
                    List stL = block.statements();
                    for (Object o : stL) {
                        Statement statement = (Statement) o;
                        if (statement instanceof VariableDeclarationStatement) {
                            VariableDeclarationStatement vb = (VariableDeclarationStatement) statement;
                            List list = vb.fragments();
                            String strings = list.get(0).toString();
                            int num = strings.indexOf("=");
                            if (num > 0)
                                strings = strings.substring(0, num);
                            if (strings.equals(name.toString())) {
                                expressionInfos.add(new ExpressionInfo(node.getExpression(), methClaPacOfExpName, lineAndNodeType, vb.getType(), strings));
                                flag = true;
                                break;

                            }
                        }
                    }
                } else if (curNode instanceof Block) {
                    Block td = (Block) curNode;
                    List stL = td.statements();
                    for (Object o : stL) {

                        Statement statement = (Statement) o;
                        if (statement instanceof VariableDeclarationStatement) {
                            VariableDeclarationStatement vb = (VariableDeclarationStatement) statement;
                            List list = vb.fragments();
                            String strings = list.get(0).toString();
                            int num = strings.indexOf("=");
                            if (num > 0)
                                strings = strings.substring(0, num);
                            if (strings.equals(name.toString())) {
                                expressionInfos.add(new ExpressionInfo(node.getExpression(), methClaPacOfExpName, lineAndNodeType, vb.getType(), strings));
                                flag=true;
                                break;
                            }
                        }
                    }
                }
                curNode = curNode.getParent();
            }
        }
//        else if ((node.getExpression() instanceof BooleanLiteral)||(node.getExpression() instanceof NumberLiteral)||
//                (node.getExpression() instanceof MethodInvocation)||(node.getExpression() instanceof ArrayAccess)||
//                (node.getExpression() instanceof FieldAccess)||(node.getExpression() instanceof StringLiteral)||
//                (node.getExpression() instanceof ConditionalExpression)) {
//
//            expressionInfos.add(new ExpressionInfo(node.getExpression(), methClaPacOfExpName, lineAndNodeType));
//        }
        return true;
    }

    public List<ExpressionInfo> getExpressionInfos() {
        //除了返回语句外，我还进行陈分的扫描，目的是过滤掉相同的部分
        List<ExpressionInfo> expressionInfoFinalList = new ArrayList<>();
        for (ExpressionInfo e:expressionInfos)
            if (!expressionInfoFinalList.contains(e)){
                expressionInfoFinalList.add(e);
            }
        return expressionInfoFinalList;
    }
}
