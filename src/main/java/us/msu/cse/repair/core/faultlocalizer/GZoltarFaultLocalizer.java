package us.msu.cse.repair.core.faultlocalizer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.HashMap;
import java.util.HashSet;

import java.util.Map;
import java.util.Set;

import com.gzoltar.core.GZoltar;
import com.gzoltar.core.components.Statement;
import com.gzoltar.core.instr.testing.TestResult;

import us.msu.cse.repair.core.parser.LCNode;

public class GZoltarFaultLocalizer implements IFaultLocalizer {
    Set<String> positiveTestMethods;
    Set<String> negativeTestMethods;

    Map<LCNode, Double> faultyLines;

    public GZoltarFaultLocalizer(Set<String> binJavaClasses, Set<String> binExecuteTestClasses, String binJavaDir,
                                 String binTestDir, Set<String> dependences) throws FileNotFoundException, IOException {
        String projLoc = new File("").getAbsolutePath();
        GZoltar gz = new GZoltar(projLoc);

        gz.getClasspaths().add(binJavaDir);
        gz.getClasspaths().add(binTestDir);

        if (dependences != null)
            gz.getClasspaths().addAll(dependences);
        //添加测试的class类
        for (String testClass : binExecuteTestClasses) {
            gz.addTestToExecute(testClass);
        }
        //添加java的class类
        for (String javaClass : binJavaClasses) {
            gz.addClassToInstrument(javaClass);
        }

        gz.run();

        positiveTestMethods = new HashSet<String>();
        negativeTestMethods = new HashSet<String>();

        for (TestResult tr : gz.getTestResults()) {
            String testName = tr.getName();
            if (tr.wasSuccessful())
                positiveTestMethods.add(testName);
            else {
                if (!tr.getName().startsWith("junit.framework"))
                    negativeTestMethods.add(testName);
            }
        }


        faultyLines = new HashMap<LCNode, Double>();
        for (Statement gzoltarStatement : gz.getSuspiciousStatements()) {
            String className = gzoltarStatement.getMethod().getParent().getLabel();
            int lineNumber = gzoltarStatement.getLineNumber();

            double suspValue = gzoltarStatement.getSuspiciousness();

            LCNode lcNode = new LCNode(className, lineNumber);
            faultyLines.put(lcNode, suspValue);

        }
    }

    @Override
    public Map<LCNode, Double> searchSuspicious(double thr) {
        // TODO Auto-generated method stub
        Map<LCNode, Double> partFaultyLines = new HashMap<LCNode, Double>();
        for (Map.Entry<LCNode, Double> entry : faultyLines.entrySet()) {
            //____________________完美定位代码______________________
//            String className = entry.getKey().getClassName();
//            String classPerfect1 = "Week";
//			String classPerfect2 = "UniformRealDistribution";
//			if((className.contains(classPerfect1.subSequence(0,classPerfect1.length())) && (entry.getKey().getLineNumber()==991))
//			||(className.contains(classPerfect2.subSequence(0,classPerfect2.length())) && (entry.getKey().getLineNumber()==779))){
//            if ((className.contains(classPerfect1.subSequence(0, classPerfect1.length())) && ((entry.getKey().getLineNumber() == 175)))) {
//                partFaultyLines.put(entry.getKey(), 1.0);
//            }else
            if (entry.getValue() >= thr)
                partFaultyLines.put(entry.getKey(), entry.getValue());
        }
        return partFaultyLines;
    }

    @Override
    public Set<String> getPositiveTests() {
        // TODO Auto-generated method stub
        return this.positiveTestMethods;
    }

    @Override
    public Set<String> getNegativeTests() {
        // TODO Auto-generated method stub
        return this.negativeTestMethods;
    }
}
