package us.msu.cse.repair.filterExpression;

import com.sun.xml.internal.ws.wsdl.writer.document.Import;
import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.util.IEnclosingMethodAttribute;
import us.msu.cse.repair.astVisitorExpression.AllTypeVisitorModificationPoint;
import us.msu.cse.repair.astVisitorExpression.AllTypeVisitorSeedStatement;
import us.msu.cse.repair.astVisitorExpression.MethodInvocationRepairVisitor;
import us.msu.cse.repair.astVisitorExpression.ModificationPointVisitor;
import us.msu.cse.repair.core.parser.ModificationPoint;
import us.msu.cse.repair.core.parser.SeedStatement;
import us.msu.cse.repair.core.parser.SeedStatementInfo;
import us.msu.cse.repair.informationExpression.ExpressionInfo;
import us.msu.cse.repair.informationExpression.MethClaPacOfExpName;
import us.msu.cse.repair.toolsExpression.GetCompilationUnit;
import us.msu.cse.repair.toolsExpression.RuleCheck;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * 1.对修改点做ASTVisitor扫描，目的是为了获得对应的 表达式 为类似Tbar的模板修复
 * 2.在对种子语句ASTVisitor扫描之后，根据方法名-类名是否相同,行号是否满足.分配给每个修改点
 */
public class DirectIngredientExpressionScreener {

    private List<ModificationPoint> modificationPoints;
    private Map<SeedStatement, SeedStatementInfo> seedStatements;
    private List<ExpressionInfo> list = new ArrayList<>();

    public DirectIngredientExpressionScreener(List<ModificationPoint> modificationPoints, Map<SeedStatement, SeedStatementInfo> seedStatements) {
        this.modificationPoints = modificationPoints;
        this.seedStatements = seedStatements;
    }

    public void screenIngredientExpression() {

        /**
         * 此函数将seedstatement中的所有表达好似提取出来;
         */
        for (Map.Entry<SeedStatement, SeedStatementInfo> entry : seedStatements.entrySet()) {
            SeedStatement seedStatement = entry.getKey();
            Statement statement = seedStatement.getStatement();
            List<ExpressionInfo> infoList = null;
            AllTypeVisitorSeedStatement allTypeVisitorSeedStatement = new AllTypeVisitorSeedStatement(seedStatement.getMethClaPacOfExpName(), seedStatement.getLineAndNodeType());
            statement.accept(allTypeVisitorSeedStatement);
            infoList = allTypeVisitorSeedStatement.getList();
            list.addAll(infoList);
        }
        /**
         *  此函数，是编译整个修改点单元
         */
        for (ModificationPoint modi : modificationPoints) {
            CompilationUnit compilationUnit = GetCompilationUnit.getCompilationUnitOfPath(modi.getSourceFilePath());
            if (compilationUnit != null) {
                AllTypeVisitorModificationPoint visitorModificationPoint = new AllTypeVisitorModificationPoint(modi, compilationUnit);
                compilationUnit.accept(visitorModificationPoint);
                List<ExpressionInfo> infoList = null;
                infoList = visitorModificationPoint.getList();
                list.addAll(infoList);
            }
        }
    }

    public void allocatonExpressionForModificationPoints() throws CloneNotSupportedException {
        screenIngredientExpression();
        List<ExpressionInfo> expressionInfoList = list;
        int MPNUM = 1;
        for (ModificationPoint mp : modificationPoints) {
            System.out.println("为第" + (MPNUM++) + "个修改点分配表达式成分 ...");
            List<ExpressionInfo> listIn = new ArrayList<>();
            for (ExpressionInfo e : expressionInfoList) {
                boolean boolean1 = TypeFilter(mp, e);
                boolean boolean2 = LineFilter(mp, e);
                boolean boolean3 = LocalFilter(mp, e);
                boolean boolean4 = ImportFilter(mp, e);
                boolean boolean5 = NullFilter(mp, e);
                boolean boolean6 = simpleNameFilter(mp, e);

                if (boolean1 && boolean2 && boolean3 && boolean4 && boolean5 && boolean6) {
                    listIn.add(e);
                }
            }
            //过滤掉相同的ExpressionInfo
            //过滤掉相同的Type
            List<Type> finalTypeName = new ArrayList<>();
            for (Type type : mp.getTypeName()) {
                boolean flag = false;
                for (Type typeFinal : finalTypeName) {
                    if (typeFinal.toString().equals(type.toString())) {
                        flag = true;
                        break;
                    }
                }
                if (!flag)
                    finalTypeName.add(type);
            }
            List<ExpressionInfo> finalListOfMp = new ArrayList<>();
            //______________________________去重，更新最小行号
            for (ExpressionInfo expressionInfo : listIn) {
                if (finalListOfMp.contains(expressionInfo)) {
                    int i = finalListOfMp.indexOf(expressionInfo);
                    ExpressionInfo eIn1 = finalListOfMp.get(i);
                    if (eIn1.getLineAndNodeType().lineOfStaOrExp >= expressionInfo.getLineAndNodeType().lineOfStaOrExp) {
                        finalListOfMp.remove(i);
                        finalListOfMp.add(expressionInfo);
                    }
                } else
                    finalListOfMp.add(expressionInfo);
            }
            //------------------------------
            mp.setTypeName(finalTypeName);
            mp.setIngredientsExpressionInfo(finalListOfMp);
            System.out.println("分配完成 ! 共 " + mp.getExpressionInfosIngredients().size() + " 个");
        }
    }

    private boolean simpleNameFilter(ModificationPoint mp, ExpressionInfo e) {
        if (e.getExpression() instanceof SimpleName) {
            return RuleCheck.rule2OfSimpleName(mp, (SimpleName) e.getExpression());
        } else if (e.getExpression() instanceof ArrayAccess) {
            return RuleCheck.rule1OfArrayAccess(mp, (ArrayAccess) e.getExpression());
        } else if (e.getExpression() instanceof NumberLiteral) {
            return RuleCheck.rule1OfNumberliteral(mp, (NumberLiteral) e.getExpression());
        } else if (e.getExpression() instanceof StringLiteral) {
            return RuleCheck.rule1OfStringliteral(mp, (StringLiteral) e.getExpression());
        } else if (e.getExpression() instanceof MethodInvocation) {
            return RuleCheck.rule1OfMethodInvocation(mp, (MethodInvocation) e.getExpression());
        } else if (e.getExpression() instanceof FieldAccess) {
            return RuleCheck.rule2OfFieldAccess(mp, (FieldAccess) e.getExpression());
        } else if(e.getExpression() instanceof QualifiedName){
            return RuleCheck.rule1OfQualifiedName(mp, (QualifiedName) e.getExpression());
        } else
            return true;
    }

    private boolean NullFilter(ModificationPoint mp, ExpressionInfo e) {
        if ((e.getExpression() instanceof NullLiteral) || (e.getExpression() == null))
            return false;
        return true;
    }

    private boolean ImportFilter(ModificationPoint mp, ExpressionInfo e) {
        List<String> importAndOther = mp.getImportAndOther();
        Expression expression = e.getExpression();
        if (((expression instanceof SimpleName) || (expression instanceof QualifiedName)) && (expression.toString().length() >= 2)) {
            for (String s : importAndOther) {
                if ((s.indexOf(e.getExpression().toString()) >= 1) || (s.matches(expression.toString())))
                    return false;
            }
        }
        return true;
    }

    public boolean TypeFilter(ModificationPoint mp, ExpressionInfo e) {
        return true;

    }

    public boolean LineFilter(ModificationPoint mp, ExpressionInfo e) {
        int mpLine = mp.getLCNode().getLineNumber();
        int expLine = e.getLineAndNodeType().lineOfStaOrExp;
        if ((e.getExpression() instanceof SimpleName) || (e.getExpression() instanceof StringLiteral) ||
                (e.getExpression() instanceof NumberLiteral))
            return mpLine >= expLine;
        else
            return true;
    }

    public boolean LocalFilter(ModificationPoint mp, ExpressionInfo e) {
        MethClaPacOfExpName mpName = mp.getMethClaPacOfExpName();
        MethClaPacOfExpName expName = e.getMethClaPacOfExpName();
        String mpMN = mpName.expressionMethodName;
        String expMN = expName.expressionMethodName;

        String mpCN = mpName.expressionClassName;
        String expCN = expName.expressionClassName;

        if (mpMN.equals(expMN) || mpCN.equals(expCN)) {
            return true;
        }
        return false;

    }

    public List<ExpressionInfo> getList() {
        return list;
    }

    public void setList(List<ExpressionInfo> list) {
        this.list = list;
    }
}
