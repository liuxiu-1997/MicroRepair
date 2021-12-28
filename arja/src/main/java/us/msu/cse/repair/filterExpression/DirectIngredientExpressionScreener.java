package us.msu.cse.repair.filterExpression;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Statement;
import us.msu.cse.repair.astVisitorExpression.AllTypeVisitorModificationPoint;
import us.msu.cse.repair.astVisitorExpression.AllTypeVisitorSeedStatement;
import us.msu.cse.repair.astVisitorExpression.ModificationPointVisitor;
import us.msu.cse.repair.core.parser.ModificationPoint;
import us.msu.cse.repair.core.parser.SeedStatement;
import us.msu.cse.repair.core.parser.SeedStatementInfo;
import us.msu.cse.repair.informationExpression.ExpressionInfo;
import us.msu.cse.repair.informationExpression.MethClaPacOfExpName;
import us.msu.cse.repair.toolsExpression.GetCompilationUnit;

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
            CompilationUnit compilationUnit = GetCompilationUnit.getCompilationUnitOfPath(modi.getSourceFilePath());
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
                listFinal.add(e);
            }
        }
        return listFinal;
    }
    public void  allocatonExpressionForModificationPoints()  {
        screenModifications();
        screenIngredientExpression();
//        List<ExpressionInfo> expressionInfoList = expressionFilter();
        List<ExpressionInfo> expressionInfoList = list;//在这里我先不去过滤，以后去过滤
        List<ExpressionInfo> expressionInfoFinalList = null;
        for (ModificationPoint mp:modificationPoints) {
            List<ExpressionInfo> listIn = new ArrayList<>();
            expressionInfoFinalList = new ArrayList<>();
            for (ExpressionInfo e:expressionInfoList) {
                boolean boolean1 = TypeFilter(mp,e);
                boolean boolean2 = LineFilter(mp,e);
                boolean boolean3 = LocalFilter(mp,e);
                if ( boolean1 && boolean2 && boolean3 ){
                    try {
                        listIn.add((ExpressionInfo) e.clone());
                    } catch (CloneNotSupportedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
            for (ExpressionInfo e:listIn){
                if (!expressionInfoFinalList.contains(e)){
                    expressionInfoFinalList.add(e);
                }
            }
            mp.setIngredientsExpressionInfo(expressionInfoFinalList);
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
