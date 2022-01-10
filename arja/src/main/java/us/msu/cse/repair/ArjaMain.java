package us.msu.cse.repair;

import java.util.HashMap;

import jmetal.operators.crossover.Crossover;
import jmetal.operators.mutation.Mutation;
import jmetal.operators.selection.Selection;
import jmetal.operators.selection.SelectionFactory;
import jmetal.util.JMException;
import org.junit.Test;
import us.msu.cse.repair.algorithms.arja.Arja;
import us.msu.cse.repair.core.AbstractRepairAlgorithm;
import us.msu.cse.repair.ec.operators.crossover.ExtendedCrossoverFactory;
import us.msu.cse.repair.ec.operators.mutation.ExtendedMutationFactory;
import us.msu.cse.repair.ec.problems.ArjaProblem;

public class ArjaMain {
    public static void main(String args[]) throws Exception {

        String[] args1 = new String[9];
//        for (int i = 1; i <= 26; i += 1) {
////            args1[0] = "Arja";
////            args1[1] = "-DsrcJavaDir";
////            args1[2] = "/home/liuguizhuang/arjaLiu/chart/chart_" + i + "_buggy/source";
////            args1[3] = "-DbinJavaDir";
////            args1[4] = "/home/liuguizhuang/arjaLiu/chart/chart_" + i + "_buggy/build";
////            args1[5] = "-DbinTestDir";
////            args1[6] = "/home/liuguizhuang/arjaLiu/chart/chart_" + i + "_buggy/build-tests";
////            args1[7] = "-Ddependences";
////            args1[8] = "/home/wangbo/workspace/defects4j/framework/projects/lib/junit-4.11.jar";
////            args1[9] = "-DexternalProjRoot";
////            args1[10] = "/home/liuguizhuang/arjaLiu";
//            args1[0] = "Arja";
//            args1[1] = "-DsrcJavaDir";
//            args1[2] = "/home/bjtucs/workspace/apr/benchmarks/defects4j/chart/chart_" + i + "_buggy/source";
//            args1[3] = "-DbinJavaDir";
//            args1[4] = "/home/bjtucs/workspace/apr/benchmarks/defects4j/chart/chart_" + i + "_buggy/build";
//            args1[5] = "-DbinTestDir";
//            args1[6] = "/home/bjtucs/workspace/apr/benchmarks/defects4j/chart/chart_" + i + "_buggy/build-tests";
//            args1[7] = "-Ddependences";
//            args1[8] = "/home/bjtucs/program_files/defects4j/framework/projects/lib/junit-4.11.jar";
//            Test(args1,i);
//        }
        for (int i = 22; i <= 106; i += 1) {
//            args1[0] = "Arja";
//            args1[1] = "-DsrcJavaDir";
//            args1[2] = "/home/liuguizhuang/arjaLiu/math/math_" + i + "_buggy/src/main/java";
//            args1[3] = "-DbinJavaDir";
//            args1[4] = "/home/liuguizhuang/arjaLiu/math/math_" + i + "_buggy/target/classes";
//            args1[5] = "-DbinTestDir";
//            args1[6] = "/home/liuguizhuang/arjaLiu/math/math_" + i + "_buggy/target/test-classes";
//            args1[7] = "-Ddependences";
//            args1[8] = "/home/wangbo/workspace/defects4j/framework/projects/lib/junit-4.11.jar";
//            args1[9] = "-DexternalProjRoot";
//            args1[10] = "/home/liuguizhuang/arjaLiu";
            args1[0] = "Arja";
            args1[1] = "-DsrcJavaDir";
            args1[2] = "/home/bjtucs/workspace/apr/benchmarks/defects4j/math/math_" + i + "_buggy/src/main/java";
            args1[3] = "-DbinJavaDir";
            args1[4] = "/home/bjtucs/workspace/apr/benchmarks/defects4j/math/math_" + i + "_buggy/target/classes";
            args1[5] = "-DbinTestDir";
            args1[6] = "/home/bjtucs/workspace/apr/benchmarks/defects4j/math/math_" + i + "_buggy/target/test-classes";
            args1[7] = "-Ddependences";
            args1[8] = "/home/bjtucs/program_files/defects4j/framework/projects/lib/junit-4.11.jar";
            Test(args1,i);
        }
//
//        for (int i=1;i<=27;i++) {
//            args1[0] = "Arja";
//            args1[1] = "-DsrcJavaDir";
//            args1[2] = "/home/liuguizhuang/arjaLiu/time/time_" + i + "_buggy/src/main";
//            args1[3] = "-DbinJavaDir";
//            args1[4] = "/home/liuguizhuang/arjaLiu/time/time_" + i + "_buggy/target/classes";
//            args1[5] = "-DbinTestDir";
//            args1[6] = "/home/liuguizhuang/arjaLiu/time/time_" + i + "_buggy/target/test-classes";
//            args1[7] = "-Ddependences";
//            args1[8] = "/home/wangbo/workspace/defects4j/framework/projects/lib/junit-4.11.jar";
//            args1[9] = "-DexternalProjRoot";
//            args1[10] = "/home/liuguizhuang/arjaLiu";
//            Test(args1,i);
//        }
//        for (int i = 1; i <= 65; i += 1) {
//            args1[0] = "Arja";
//            args1[1] = "-DsrcJavaDir";
//            args1[2] = "/home/liuguizhuang/arjaLiu/lang/lang_" + i + "_buggy/src/main";
//            args1[3] = "-DbinJavaDir";
//            args1[4] = "/home/liuguizhuang/arjaLiu/lang/lang_" + i + "_buggy/target/classes";
//            args1[5] = "-DbinTestDir";
//            args1[6] = "/home/liuguizhuang/arjaLiu/lang/lang_" + i + "_buggy/target/tests";
//            args1[7] = "-Ddependences";
//            args1[8] = "/home/wangbo/workspace/defects4j/framework/projects/lib/junit-4.11.jar";
//            args1[9] = "-DexternalProjRoot";
//            args1[10] = "/home/liuguizhuang/arjaLiu";
//            Test(args1,i);
//        }
    }

    static void Test(String[] array,int i) throws Exception {
        System.out.println("输出这是第 "+i+" 个修改的地方:_" + "\n"+ array[2]+"\n");
        HashMap<String, String> parameterStrs = Interpreter.getParameterStrings(array);
        HashMap<String, Object> parameters = Interpreter.getBasicParameterSetting(parameterStrs);

        String ingredientScreenerNameS = parameterStrs.get("ingredientScreenerName");
        if (ingredientScreenerNameS != null)
            parameters.put("ingredientScreenerName", ingredientScreenerNameS);


        int populationSize = 500;
        int maxGenerations = 500;

        String populationSizeS = parameterStrs.get("populationSize");
        if (populationSizeS != null)
            populationSize = Integer.parseInt(populationSizeS);

        String maxGenerationsS = parameterStrs.get("maxGenerations");
        if (maxGenerationsS != null)
            maxGenerations = Integer.parseInt(maxGenerationsS);


        ArjaProblem problem = new ArjaProblem(parameters);
        AbstractRepairAlgorithm repairAlg = new Arja(problem);

        repairAlg.setInputParameter("populationSize", populationSize);
        repairAlg.setInputParameter("maxEvaluations", populationSize * maxGenerations);

        Crossover crossover;
        Mutation mutation;
        Selection selection;

        parameters = new HashMap<String, Object>();
        parameters.put("probability", 1.0);
        crossover = ExtendedCrossoverFactory.getCrossoverOperator("HUXSinglePointCrossover", parameters);

        parameters = new HashMap<String, Object>();
        parameters.put("probability", 1.0 / problem.getNumberOfModificationPoints());
        mutation = ExtendedMutationFactory.getMutationOperator("BitFilpUniformMutation", parameters);

        // Selection Operator
        parameters = null;
        selection = SelectionFactory.getSelectionOperator("BinaryTournament2", parameters);

        // Add the operators to the algorithm
        repairAlg.addOperator("crossover", crossover);
        repairAlg.addOperator("mutation", mutation);
        repairAlg.addOperator("selection", selection);

        repairAlg.execute();
    }
}
