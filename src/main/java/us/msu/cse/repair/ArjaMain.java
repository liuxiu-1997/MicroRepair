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
import us.msu.cse.repair.toolsExpression.Time;

public class ArjaMain {
    public static void main(String args[]) throws Exception {

        String[] args1 = new String[9];
//        for (int i = 1; i <= 1; i += 2) {
//            Time.setTime1(System.currentTimeMillis());
//            args1[0] = "Arja";
//            args1[1] = "-DsrcJavaDir";
//            args1[2] = "/home/liuguizhuang/arjaLiu/chart/chart_" + i + "_buggy/source";
//            args1[3] = "-DbinJavaDir";
//            args1[4] = "/home/liuguizhuang/arjaLiu/chart/chart_" + i + "_buggy/build";
//            args1[5] = "-DbinTestDir";
//            args1[6] = "/home/liuguizhuang/arjaLiu/chart/chart_" + i + "_buggy/build-tests";
//            args1[7] = "-Ddependences";
//            args1[8] = "/home/wangbo/workspace/defects4j/framework/projects/lib/junit-4.11.jar";
//            Test(args1, i);
//            Time.setTime9(System.currentTimeMillis());
//            Time.WriteTime("/home/liuguizhuang/arjaLiu/SPEA2/" + System.currentTimeMillis() + "普通", args1[2]);
//        }
//        for (int i = 22; i <= 106; i += 1) {
//        int i=22;
//            args1[0] = "Arja";
//            args1[1] = "-DsrcJavaDir";
//            args1[2] = "/home/liuguizhuang/arjaLiu/math/math_" + i + "_buggy/src/main/java";
//            args1[3] = "-DbinJavaDir";
//            args1[4] = "/home/liuguizhuang/arjaLiu/math/math_" + i + "_buggy/target/classes";
//            args1[5] = "-DbinTestDir";
//            args1[6] = "/home/liuguizhuang/arjaLiu/math/math_" + i + "_buggy/target/test-classes";
//            args1[7] = "-Ddependences";
//            args1[8] = "/home/wangbo/workspace/defects4j/framework/projects/lib/junit-4.11.jar";
//****************************0
//        for (int i=7;i<=85;i++){
//        int i=75;
//            args1[0] = "Arja";
//            args1[1] = "-DsrcJavaDir";
//            args1[2] = "/home/bjtucs/workspace/apr/benchmarks/defects4j/math/math_" + i + "_buggy/src/main/java";
//            args1[3] = "-DbinJavaDir";
//            args1[4] = "/home/bjtucs/workspace/apr/benchmarks/defects4j/math/math_" + i + "_buggy/target/classes";
//            args1[5] = "-DbinTestDir";
//            args1[6] = "/home/bjtucs/workspace/apr/benchmarks/defects4j/math/math_" + i + "_buggy/target/test-classes";
//            args1[7] = "-Ddependences";
//            args1[8] = "/home/bjtucs/program_files/defects4j/framework/projects/lib/junit-4.11.jar";
//            Test(args1,i);
//        }
//        for (int i=86;i<=106;i++){

//        int i=98;
//            args1[0] = "Arja";
//            args1[1] = "-DsrcJavaDir";
//            args1[2] = "/home/bjtucs/workspace/apr/benchmarks/defects4j/math/math_" + i + "_buggy/src/main/java";
//            args1[3] = "-DbinJavaDir";
//            args1[4] = "/home/bjtucs/workspace/apr/benchmarks/defects4j/math/math_" + i + "_buggy/target/classes";
//            args1[5] = "-DbinTestDir";
//            args1[6] = "/home/bjtucs/workspace/apr/benchmarks/defects4j/math/math_" + i + "_buggy/target/test-classes";
//            args1[7] = "-Ddependences";
//            args1[8] = "/home/bjtucs/program_files/defects4j/framework/projects/lib/junit-4.11.jar";
//            Test(args1,i);
//        }
//******************

//        for (int i=96;i<=106;i++){
//        int i=98;
//            args1[0] = "Arja";
//            args1[1] = "-DsrcJavaDir";
//            args1[2] = "/home/liuguizhuang/arjaLiu/math/math_" + i + "_buggy/src/java";
//            args1[3] = "-DbinJavaDir";
//            args1[4] = "/home/liuguizhuang/arjaLiu/math/math_" + i + "_buggy/target/classes";
//            args1[5] = "-DbinTestDir";
//            args1[6] = "/home/liuguizhuang/arjaLiu/math/math_" + i + "_buggy/target/test-classes";
//            args1[7] = "-Ddependences";
//            args1[8] = "/home/wangbo/workspace/defects4j/framework/projects/lib/junit-4.11.jar";
//            Test(args1,i);
////        }
//        for (int i=63;i<=85;i++){
//        int i=75;
//            args1[0] = "Arja";
//            args1[1] = "-DsrcJavaDir";
//            args1[2] = "/home/liuguizhuang/arjaLiu/math/math_" + i + "_buggy/src/main/java";
//            args1[3] = "-DbinJavaDir";
//            args1[4] = "/home/liuguizhuang/arjaLiu/math/math_" + i + "_buggy/target/classes";
//            args1[5] = "-DbinTestDir";
//            args1[6] = "/home/liuguizhuang/arjaLiu/math/math_" + i + "_buggy/target/test-classes";
//            args1[7] = "-Ddependences";
//            args1[8] = "/home/wangbo/workspace/defects4j/framework/projects/lib/junit-4.11.jar";
//            Test(args1,i);
////        }
//        for (int i = 87; i <= 106; i += 5) {
//            Time.setTime1(System.currentTimeMillis());
//            args1[0] = "Arja";
//            args1[1] = "-DsrcJavaDir";
//            args1[2] = "/home/liuguizhuang/arjaLiu/math/math_" + i + "_buggy/src/java";
//            args1[3] = "-DbinJavaDir";
//            args1[4] = "/home/liuguizhuang/arjaLiu/math/math_" + i + "_buggy/target/classes";
//            args1[5] = "-DbinTestDir";
//            args1[6] = "/home/liuguizhuang/arjaLiu/math/math_" + i + "_buggy/target/test-classes";
//            args1[7] = "-Ddependences";
//            args1[8] = "/home/wangbo/workspace/defects4j/framework/projects/lib/junit-4.11.jar";
//            Test(args1, i);
//            Time.setTime9(System.currentTimeMillis());
//            Time.WriteTime("/home/liuguizhuang/arjaLiu/" + System.currentTimeMillis() + "普通", args1[2]);
//        }
//
        for (int i = 79; i <= 79; i += 5) {
            Time.setTime1(System.currentTimeMillis());
            args1[0] = "Arja";
            args1[1] = "-DsrcJavaDir";
            args1[2] = "/home/liuguizhuang/arjaLiu/math/math_" + i + "_buggy/src/main/java";
            args1[3] = "-DbinJavaDir";
            args1[4] = "/home/liuguizhuang/arjaLiu/math/math_" + i + "_buggy/target/classes";
            args1[5] = "-DbinTestDir";
            args1[6] = "/home/liuguizhuang/arjaLiu/math/math_" + i + "_buggy/target/test-classes";
            args1[7] = "-Ddependences";
            args1[8] = "/home/wangbo/workspace/defects4j/framework/projects/lib/junit-4.11.jar";
            Test(args1, i);
            Time.setTime9(System.currentTimeMillis());
            Time.WriteTime("/home/liuguizhuang/arjaLiu/SPEA2/" + System.currentTimeMillis() + "普通", args1[2]);
        }


//        for (int i = 19; i <= 27; i+=5) {
//            Time.setTime1(System.currentTimeMillis());
//            args1[0] = "Arja";
//            args1[1] = "-DsrcJavaDir";
//            args1[2] = "/home/liuguizhuang/arjaLiu/time/time_" + i + "_buggy/src/main";
//            args1[3] = "-DbinJavaDir";
//            args1[4] = "/home/liuguizhuang/arjaLiu/time/time_" + i + "_buggy/build/classes";
//            args1[5] = "-DbinTestDir";
//            args1[6] = "/home/liuguizhuang/arjaLiu/time/time_" + i + "_buggy/build/tests";
//            args1[7] = "-Ddependences";
//            args1[8] = "/home/wangbo/workspace/defects4j/framework/projects/lib/junit-4.11.jar";
//            args1[0] = "Arja";
//            args1[1] = "-DsrcJavaDir";
//            args1[2] = "/home/bjtucs/workspace/apr/benchmarks/defects4j/time/time_" + i + "_buggy/src/main";
//            args1[3] = "-DbinJavaDir";
//            args1[4] = "/home/bjtucs/workspace/apr/benchmarks/defects4j/time/time_" + i + "_buggy/build/classes";
//            args1[5] = "-DbinTestDir";
//            args1[6] = "/home/bjtucs/workspace/apr/benchmarks/defects4j/time/time_" + i + "_buggy/build/tests";
//            args1[7] = "-Ddependences";
//            args1[8] = "/home/bjtucs/program_files/defects4j/framework/projects/lib/junit-4.11.jar";
//            Test(args1, i);
//            Time.setTime9(System.currentTimeMillis());
//            Time.WriteTime(""+System.currentTimeMillis(),args1[2]);
//            Time.WriteTime("/home/liuguizhuang/arjaLiu/" + System.currentTimeMillis()+"PESA2", args1[2]);
//        }
//        for (int i = 12; i <= 27; i+=5) {
//            Time.setTime1(System.currentTimeMillis());
//            args1[0] = "Arja";
//            args1[1] = "-DsrcJavaDir";
//            args1[2] = "/home/liuguizhuang/arjaLiu/time/time_" + i + "_buggy/src/main";
//            args1[3] = "-DbinJavaDir";
//            args1[4] = "/home/liuguizhuang/arjaLiu/time/time_" + i + "_buggy/build/classes";
//            args1[5] = "-DbinTestDir";
//            args1[6] = "/home/liuguizhuang/arjaLiu/time/time_" + i + "_buggy/build/tests";
//            args1[7] = "-Ddependences";
//            args1[8] = "/home/wangbo/workspace/defects4j/framework/projects/lib/junit-4.11.jar";
//            Test(args1, i);
//            Time.setTime9(System.currentTimeMillis());
//            Time.WriteTime("/home/liuguizhuang/arjaLiu/" + System.currentTimeMillis() + "PESA2", args1[2]);
//        }
//        }
//            args1[0] = "Arja";
//            args1[1] = "-DsrcJavaDir";
//            args1[2] = "/home/bjtucs/workspace/apr/benchmarks/defects4j/time/time_" + i + "_buggy/src/main";
//            args1[3] = "-DbinJavaDir";
//            args1[4] = "/home/bjtucs/workspace/apr/benchmarks/defects4j/time/time_" + i + "_buggy/build/classes";
//            args1[5] = "-DbinTestDir";
//            args1[6] = "/home/bjtucs/workspace/apr/benchmarks/defects4j/time/time_" + i + "_buggy/build/tests";
//            args1[7] = "-Ddependences";
//            args1[8] = "/home/bjtucs/program_files/defects4j/framework/projects/lib/junit-4.11.jar";
//            Test(args1, i);
//        }

//        for (int i = 4; i <= 27; i++) {
//            args1[0] = "Arja";
//            args1[1] = "-DsrcJavaDir";
//            args1[2] = "/home/bjtucs/workspace/apr/benchmarks/defects4j/closure/closure_" + i + "_buggy/src/com/google";
//            args1[3] = "-DbinJavaDir";
//            args1[4] = "/home/bjtucs/workspace/apr/benchmarks/defects4j/closure/closure_" + i + "_buggy/build/classes/com/google";
//            args1[5] = "-DbinTestDir";
//            args1[6] = "/home/bjtucs/workspace/apr/benchmarks/defects4j/closure/closure_" + i + "_buggy/build/test/com/google";
//            args1[7] = "-Ddependences";
//            args1[8] = "/home/bjtucs/program_files/defects4j/framework/projects/lib/junit-4.11.jar";
//            Test(args1, i);
//        }
//_______________________________________________________________________________________________

//        for (int i = 54; i <= 65; i += 3) {
//            Time.setTime1(System.currentTimeMillis());
//            args1[0] = "Arja";
//            args1[1] = "-DsrcJavaDir";
//            args1[2] = "/home/liuguizhuang/arjaLiu/lang/lang_" + i + "_buggy/src/java";
//            args1[3] = "-DbinJavaDir";
//            args1[4] = "/home/liuguizhuang/arjaLiu/lang/lang_" + i + "_buggy/target/classes";
//            args1[5] = "-DbinTestDir";
//            args1[6] = "/home/liuguizhuang/arjaLiu/lang/lang_" + i + "_buggy/target/tests";
//            args1[7] = "-Ddependences";
//            args1[8] = "/home/wangbo/workspace/defects4j/framework/projects/lib/junit-4.11.jar";
//            args1[0] = "Arja";
//            args1[1] = "-DsrcJavaDir";
//            args1[2] = "/home/bjtucs/workspace/apr/benchmarks/defects4j/lang/lang_" + i + "_buggy/src/main/java";
//            args1[3] = "-DbinJavaDir";
//            args1[4] = "/home/bjtucs/workspace/apr/benchmarks/defects4j/lang/lang_" + i + "_buggy/target/classes";
//            args1[5] = "-DbinTestDir";
//            args1[6] = "/home/bjtucs/workspace/apr/benchmarks/defects4j/lang/lang_" + i + "_buggy/target/tests";
//            args1[7] = "-Ddependences";
//            args1[8] = "/home/bjtucs/program_files/defects4j/framework/projects/lib/junit-4.11.jar";
//            Test(args1, i);
//            Time.setTime9(System.currentTimeMillis());
//            Time.WriteTime("/home/liuguizhuang/arjaLiu/" + System.currentTimeMillis()+"MOEAD", args1[2]);
//        }
////            args1[0] = "Arja";
//            args1[1] = "-DsrcJavaDir";
//            args1[2] = "/home/bjtucs/workspace/apr/benchmarks/defects4j/lang/lang_" + i + "_buggy/src/main";
//            args1[3] = "-DbinJavaDir";
//            args1[4] = "/home/bjtucs/workspace/apr/benchmarks/defects4j/lang/lang_" + i + "_buggy/target/classes";
//            args1[5] = "-DbinTestDir";
//            args1[6] = "/home/bjtucs/workspace/apr/benchmarks/defects4j/lang/lang_" + i + "_buggy/target/test-classes";
//            args1[7] = "-Ddependences";
//            args1[8] = "/home/bjtucs/program_files/defects4j/framework/projects/lib/junit-4.11.jar";
//            Test(args1,i);
//    }
//        for(int i=36;i<=41;i++){
//            args1[0] = "Arja";
//            args1[1] = "-DsrcJavaDir";
//            args1[2] = "/home/bjtucs/workspace/apr/benchmarks/defects4j/lang/lang_" + i + "_buggy/src/java";
//            args1[3] = "-DbinJavaDir";
//            args1[4] = "/home/bjtucs/workspace/apr/benchmarks/defects4j/lang/lang_" + i + "_buggy/target/classes";
//            args1[5] = "-DbinTestDir";
//            args1[6] = "/home/bjtucs/workspace/apr/benchmarks/defects4j/lang/lang_" + i + "_buggy/target/test-classes";
//            args1[7] = "-Ddependences";
//            args1[8] = "/home/bjtucs/program_files/defects4j/framework/projects/lib/junit-4.11.jar";
//            Test(args1,i);
//        }
//        for(int i=42;i<=53;i++){
//            args1[0] = "Arja";
//            args1[1] = "-DsrcJavaDir";
//            args1[2] = "/home/bjtucs/workspace/apr/benchmarks/defects4j/lang/lang_" + i + "_buggy/src/java";
//            args1[3] = "-DbinJavaDir";
//            args1[4] = "/home/bjtucs/workspace/apr/benchmarks/defects4j/lang/lang_" + i + "_buggy/target/classes";
//            args1[5] = "-DbinTestDir";
//            args1[6] = "/home/bjtucs/workspace/apr/benchmarks/defects4j/lang/lang_" + i + "_buggy/target/tests-classes";
//            args1[7] = "-Ddependences";
//            args1[8] = "/home/bjtucs/program_files/defects4j/framework/projects/lib/junit-4.11.jar";
//            Test(args1,i);
//        }
//        for (int i=57;i<=65;i++){
//            args1[0] = "Arja";
//            args1[1] = "-DsrcJavaDir";
//            args1[2] = "/home/bjtucs/workspace/apr/benchmarks/defects4j/lang/lang_" + i + "_buggy/src/java";
//            args1[3] = "-DbinJavaDir";
//            args1[4] = "/home/bjtucs/workspace/apr/benchmarks/defects4j/lang/lang_" + i + "_buggy/target/classes";
//            args1[5] = "-DbinTestDir";
//            args1[6] = "/home/bjtucs/workspace/apr/benchmarks/defects4j/lang/lang_" + i + "_buggy/target/tests";
//            args1[7] = "-Ddependences";
//            args1[8] = "/home/bjtucs/program_files/defects4j/framework/projects/lib/junit-4.11.jar";
//            Test(args1,i);
//        }


//        for (int i = 43; i <= 43; i += 3) {
//            Time.setTime1(System.currentTimeMillis());
//            args1[0] = "Arja";
//            args1[1] = "-DsrcJavaDir";
//            args1[2] = "/home/liuguizhuang/arjaLiu/lang/lang_" + i + "_buggy/src/java";
//            args1[3] = "-DbinJavaDir";
//            args1[4] = "/home/liuguizhuang/arjaLiu/lang/lang_" + i + "_buggy/target/classes";
//            args1[5] = "-DbinTestDir";
//            args1[6] = "/home/liuguizhuang/arjaLiu/lang/lang_" + i + "_buggy/target/tests";
//            args1[7] = "-Ddependences";
//            args1[8] = "/home/wangbo/workspace/defects4j/framework/projects/lib/junit-4.11.jar";
//            Test(args1, i);
//            Time.setTime9(System.currentTimeMillis());
//            Time.WriteTime("/home/liuguizhuang/arjaLiu/SPEA2/" + System.currentTimeMillis(), args1[2]);
//        }
////
//            args1[0] = "Arja";
//            args1[1] = "-DsrcJavaDir";
//            args1[2] = "/home/bjtucs/workspace/apr/benchmarks/defects4j/lang/lang_" + i + "_buggy/src/java";
//            args1[3] = "-DbinJavaDir";
//            args1[4] = "/home/bjtucs/workspace/apr/benchmarks/defects4j/lang/lang_" + i + "_buggy/target/classes";
//            args1[5] = "-DbinTestDir";
//            args1[6] = "/home/bjtucs/workspace/apr/benchmarks/defects4j/lang/lang_" + i + "_buggy/target/tests";
//            args1[7] = "-Ddependences";
//            args1[8] = "/home/bjtucs/program_files/defects4j/framework/projects/lib/junit-4.11.jar";
//            Test(args1,i);
//        }
    }

    static void Test(String[] array, int i) throws Exception {
        System.out.println("输出这是第 " + i + " 个修改的地方:_" + "\n" + array[2] + "\n");
        HashMap<String, String> parameterStrs = Interpreter.getParameterStrings(array);
        HashMap<String, Object> parameters = Interpreter.getBasicParameterSetting(parameterStrs);

        String ingredientScreenerNameS = parameterStrs.get("ingredientScreenerName");
        if (ingredientScreenerNameS != null)
            parameters.put("ingredientScreenerName", ingredientScreenerNameS);


        int populationSize = 100;
        int maxGenerations = 200;

        String populationSizeS = parameterStrs.get("populationSize");
        if (populationSizeS != null)
            populationSize = Integer.parseInt(populationSizeS);

        String maxGenerationsS = parameterStrs.get("maxGenerations");
        if (maxGenerationsS != null)
            maxGenerations = Integer.parseInt(maxGenerationsS);


        ArjaProblem problem = new ArjaProblem(parameters);
        //___________Time_Computer____________
        Time.setTime7(System.currentTimeMillis());

        AbstractRepairAlgorithm repairAlg = new Arja(problem);

        repairAlg.setInputParameter("populationSize", populationSize);
        repairAlg.setInputParameter("maxEvaluations", populationSize * maxGenerations);

        //MOEAD算法的参数配置
//        repairAlg.setInputParameter("T",5);
//        repairAlg.setInputParameter("nr",1);
//        repairAlg.setInputParameter("delta",0.9);
//        repairAlg.setInputParameter("dataDirectory","/home/liuguizhuang/arjaLiu");
        //OMOPSO的参数配置
//        repairAlg.setInputParameter("swarmSize",2);
//        repairAlg.setInputParameter("archiveSize",2);
//        repairAlg.setInputParameter("maxIterations",10);

        //PAES
//        repairAlg.setInputParameter("biSections",2);
        repairAlg.setInputParameter("archiveSize",2);

        //PESA2
//        repairAlg.setInputParameter("bisections",2);
//        repairAlg.setInputParameter("archiveSize",2);

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
        //OMOPSO的参数配置
//        parameters = new HashMap<String, Object>();
//        parameters.put("probability", 1.0 / problem.getNumberOfModificationPoints());
//        Mutation mutation1 = ExtendedMutationFactory.getMutationOperator("BitFilpUniformMutation",parameters);
//        repairAlg.addOperator("uniformMutation",mutation1);
//        parameters = new HashMap<String, Object>();
//        parameters.put("probability", 1.0 / problem.getNumberOfModificationPoints());
//        Mutation mutation2 = ExtendedMutationFactory.getMutationOperator("BitFilpUniformMutation",parameters);
//        repairAlg.addOperator("nonUniformMutation",mutation2);

        repairAlg.execute();
        //___________Time_Computer____________
        Time.setTime8(System.currentTimeMillis());
    }
}
