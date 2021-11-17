package us.msu.cse.repair.filterExpression;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.Statement;
import us.msu.cse.repair.algorithmsExpression.ExpressionPrioritySort;
import us.msu.cse.repair.astVisitorExpression.AllTypeVisitorModificationPoint;
import us.msu.cse.repair.astVisitorExpression.AllTypeVisitorSeedStatement;
import us.msu.cse.repair.astVisitorExpression.ModificationPointVisitor;
import us.msu.cse.repair.core.parser.ModificationPoint;
import us.msu.cse.repair.core.parser.SeedStatement;
import us.msu.cse.repair.core.parser.SeedStatementInfo;
import us.msu.cse.repair.informationExpression.ExpressionInfo;
import us.msu.cse.repair.informationExpression.MethClaPacOfExpName;
import us.msu.cse.repair.toolsExpression.GetCompilcationUnit;
import us.msu.cse.repair.toolsExpression.TypeInformation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DirectIngredientExpressionScreener {

   private List<ModificationPoint> modificationPoints;
   private Map<SeedStatement, SeedStatementInfo> seedStatements;
   private List<ExpressionInfo> list = new ArrayList<>();

    public DirectIngredientExpressionScreener(List<ModificationPoint> modificationPoints, Map<SeedStatement, SeedStatementInfo> seedStatements) {
        this.modificationPoints = modificationPoints;
        this.seedStatements = seedStatements;
    }
    public void screenModifications(){

        for (ModificationPoint mp:modificationPoints){
            ModificationPointVisitor modificationPointVisitor = new ModificationPointVisitor(mp.getMethClaPacOfExpName(),mp.getLineAndNodeType());
            mp.getStatement().accept(modificationPointVisitor);
            mp.setModificationPointExpressionInfosList(modificationPointVisitor.getExpressionInfos());
        }
    }

    public void screenIngredientExpression() {

        /**
         * 此函数将seedstatement中的所有表达好似提取出来;
         */
        for (Map.Entry<SeedStatement, SeedStatementInfo> entry : seedStatements.entrySet()) {
            SeedStatement seedStatement = entry.getKey();
            Statement statement = seedStatement.getStatement();
            List<ExpressionInfo> infoList = null;
            AllTypeVisitorSeedStatement allTypeVisitorSeedStatement = new AllTypeVisitorSeedStatement(seedStatement.getMethClaPacOfExpName(),seedStatement.getLineAndNodeType());
            statement.accept(allTypeVisitorSeedStatement);
            infoList = allTypeVisitorSeedStatement.getList();
            list.addAll(infoList);
        }
        /**
         *  此函数，是编译整个修改点单元
         */
        for (ModificationPoint modi:modificationPoints){
            CompilationUnit compilationUnit = GetCompilcationUnit.getCompilationUnit(modi.getSourceFilePath());
            if (compilationUnit != null){
                AllTypeVisitorModificationPoint visitorModificationPoint = new AllTypeVisitorModificationPoint(modi,compilationUnit);
                compilationUnit.accept(visitorModificationPoint);
                List<ExpressionInfo> infoList = null;
                infoList = visitorModificationPoint.getList();
                list.addAll(infoList);
            }
        }
    }

    public List<ExpressionInfo> expressionFilter(){
        /**
         * 返回所有语句SeedStatement中的过滤掉后的内容
         */
        List<ExpressionInfo> listFinal = new ArrayList<>();
        for (ExpressionInfo e:list) {
            if (!listFinal.contains(e)){
                //是变量但是没有进行赋值的情况下，我需要进行重新赋值。
//                if ((e.getExpression() instanceof Name)&&(e.getVarType()==null))
//                    TypeInformation.getTypeInformation(e);
                listFinal.add(e);
            }
        }
        return listFinal;
    }
    public void  allocatonExpressionForModificationPoints()  {

        screenModifications();
        screenIngredientExpression();
        List<ExpressionInfo> expressionInfoList = expressionFilter();

        for (ModificationPoint mp:modificationPoints) {
            List<ExpressionInfo> list = new ArrayList<>();
//            ExpressionPrioritySort expressionPrioritySort = new ExpressionPrioritySort();
            for (ExpressionInfo e:expressionInfoList) {
                boolean boolean1 = TypeFilter(mp,e);
                boolean boolean2 = LineFilter(mp,e);
                boolean boolean3 = LocalFilter(mp,e);
                if ( boolean1 && boolean2 && boolean3){
                    try {
                        list.add((ExpressionInfo) e.clone());
                    } catch (CloneNotSupportedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
            mp.setIngredientsExpressionInfo(list);
//            expressionPrioritySort.priorityAllocation(mp,list);
        }
    }
    public boolean TypeFilter(ModificationPoint mp,ExpressionInfo e){
            return true;

    }

    public boolean LineFilter(ModificationPoint mp,ExpressionInfo e){
        int mpLine = mp.getLineAndNodeType().lineOfStaOrExp;
        int expLine = e.getLineAndNodeType().lineOfStaOrExp;
        return mpLine >= expLine;
    }
    public boolean LocalFilter(ModificationPoint mp,ExpressionInfo e){
        MethClaPacOfExpName mpName = mp.getMethClaPacOfExpName();
        MethClaPacOfExpName expName = e.getMethClaPacOfExpName();
        String mpMN = mpName.expressionMethodName;
        String expMN = expName.expressionMethodName;

        String mpCN = mpName.expressionClassName;
        String expCN = expName.expressionClassName;

        if ((Objects.equals(mpMN, expMN))||(Objects.equals(mpCN, expCN))){
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
