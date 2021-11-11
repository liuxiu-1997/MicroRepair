package us.msu.cse.repair.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import java.util.Map.Entry;

import javax.tools.JavaFileObject;

import jmetal.encodings.variable.ArrayInt;
import jmetal.encodings.variable.Binary;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.TextEdit;

import jmetal.core.Problem;
import jmetal.metaheuristics.moead.Utils;
import jmetal.util.Configuration;
import jmetal.util.JMException;
import us.msu.cse.repair.core.compiler.JavaJDKCompiler;
import us.msu.cse.repair.core.coverage.SeedLineGeneratorProcess;
import us.msu.cse.repair.core.coverage.TestFilterProcess;
import us.msu.cse.repair.core.faultlocalizer.*;
import us.msu.cse.repair.core.filterrules.IngredientFilterRule;
import us.msu.cse.repair.core.manipulation.AbstractManipulation;
import us.msu.cse.repair.core.manipulation.ManipulationFactory;
import us.msu.cse.repair.core.parser.FieldVarDetector;
import us.msu.cse.repair.core.parser.FileASTRequestorImpl;
import us.msu.cse.repair.core.parser.LCNode;
import us.msu.cse.repair.core.parser.LocalVarDetector;
import us.msu.cse.repair.core.parser.MethodDetector;
import us.msu.cse.repair.core.parser.ModificationPoint;
import us.msu.cse.repair.core.parser.SeedStatement;
import us.msu.cse.repair.core.parser.SeedStatementInfo;
import us.msu.cse.repair.core.parser.ingredient.AbstractIngredientScreener;
import us.msu.cse.repair.core.parser.ingredient.IngredientMode;
import us.msu.cse.repair.core.parser.ingredient.IngredientScreenerFactory;
import us.msu.cse.repair.core.testexecutors.ExternalTestExecutor;
import us.msu.cse.repair.core.testexecutors.ITestExecutor;
import us.msu.cse.repair.core.testexecutors.InternalTestExecutor;
import us.msu.cse.repair.core.util.ClassFinder;
import us.msu.cse.repair.core.util.CustomURLClassLoader;
import us.msu.cse.repair.core.util.Helper;
import us.msu.cse.repair.core.util.IO;
import us.msu.cse.repair.core.util.Patch;
import us.msu.cse.repair.filterExpression.DirectIngredientExpressionScreener;
import us.msu.cse.repair.informationExpression.ExpressionInfo;
import us.msu.cse.repair.repairExpression.RepairExpression;
import us.msu.cse.repair.toolsExpression.SimilarTarTemplateCheck;
import us.msu.cse.repair.toolsExpression.TemplateBoolean;

public abstract class AbstractRepairProblem extends Problem {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    protected String[] manipulationNames;

    protected Double percentage;
    protected Double thr;

    protected Integer maxNumberOfModificationPoints;

    protected List<ModificationPoint> modificationPoints;
    protected List<List<String>> availableManipulations;

    protected Map<String, CompilationUnit> sourceASTs;
    protected Map<String, String> sourceContents;

    protected Map<SeedStatement, SeedStatementInfo> seedStatements;
    protected Map<String, ITypeBinding> declaredClasses;

    protected Map<LCNode, Double> faultyLines;
    protected String faultyLinesInfoPath;

    protected Set<LCNode> seedLines;

    protected Set<String> positiveTests;
    protected Set<String> negativeTests;

    protected Boolean testFiltered;
    protected String orgPosTestsInfoPath;
    protected String finalTestsInfoPath;

    protected String srcJavaDir;

    protected String binJavaDir;
    protected String binTestDir;
    protected Set<String> dependences;

    protected String externalProjRoot;

    protected String binWorkingRoot;

    protected Set<String> binJavaClasses;
    protected Set<String> binExecuteTestClasses;
    protected String javaClassesInfoPath;
    protected String testClassesInfoPath;

    protected Integer waitTime;


    protected String patchOutputRoot;

    protected String testExecutorName;

    protected String ingredientScreenerName;
    protected IngredientMode ingredientMode;

    protected Boolean ingredientFilterRule;
    protected Boolean manipulationFilterRule;

    protected Boolean seedLineGenerated;

    protected Boolean diffFormat;

    protected String jvmPath;
    protected List<String> compilerOptions;

    protected URL[] progURLs;

    protected String gzoltarDataDir;

    protected static int globalID;
    protected Set<Patch> patches;

    protected static long launchTime;
    protected static int evaluations;


    @SuppressWarnings("unchecked")
    public AbstractRepairProblem(Map<String, Object> parameters) throws Exception {
        binJavaDir = (String) parameters.get("binJavaDir");
        binTestDir = (String) parameters.get("binTestDir");
        srcJavaDir = (String) parameters.get("srcJavaDir");
        dependences = (Set<String>) parameters.get("dependences");

        binExecuteTestClasses = (Set<String>) parameters.get("tests");

        percentage = (Double) parameters.get("percentage");

        javaClassesInfoPath = (String) parameters.get("javaClassesInfoPath");
        testClassesInfoPath = (String) parameters.get("testClassesInfoPath");

        faultyLinesInfoPath = (String) parameters.get("faultyLinesInfoPath");

        gzoltarDataDir = (String) parameters.get("gzoltarDataDir");

        String id = Helper.getRandomID();

        thr = (Double) parameters.get("thr");
        if (thr == null)
            thr = 0.1;

        maxNumberOfModificationPoints = (Integer) parameters.get("maxNumberOfModificationPoints");
        if (maxNumberOfModificationPoints == null)
            maxNumberOfModificationPoints = 40;

        jvmPath = (String) parameters.get("jvmPath");
        if (jvmPath == null)
            jvmPath = System.getProperty("java.home") + "/bin/java";

        externalProjRoot = (String) parameters.get("externalProjRoot");
        if (externalProjRoot == null)
            externalProjRoot = new File("external").getCanonicalPath();

        binWorkingRoot = (String) parameters.get("binWorkingRoot");
        if (binWorkingRoot == null)
            binWorkingRoot = System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + "working_" + id;

        patchOutputRoot = (String) parameters.get("patchOutputRoot");
        if (patchOutputRoot == null)
            patchOutputRoot = "patches_" + id;

        orgPosTestsInfoPath = (String) parameters.get("orgPosTestsInfoPath");
        if (orgPosTestsInfoPath == null)
            orgPosTestsInfoPath = System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + "orgTests_" + id + ".txt";

        finalTestsInfoPath = (String) parameters.get("finalTestsInfoPath");
        if (finalTestsInfoPath == null)
            finalTestsInfoPath = System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + "finalTests_" + id + ".txt";

        manipulationNames = (String[]) parameters.get("manipulationNames");
        if (manipulationNames == null)
            manipulationNames = new String[]{"Delete", "Replace", "InsertBefore"};

        testExecutorName = (String) parameters.get("testExecutorName");
        if (testExecutorName == null)
            testExecutorName = "ExternalTestExecutor";

        ingredientScreenerName = (String) parameters.get("ingredientScreenerName");
        if (ingredientScreenerName == null)
            ingredientScreenerName = "Direct";

        String modeStr = (String) parameters.get("ingredientMode");
        if (modeStr == null)
            ingredientMode = IngredientMode.Package;
        else
            ingredientMode = IngredientMode.valueOf(modeStr);

        diffFormat = (Boolean) parameters.get("diffFormat");
        if (diffFormat == null)
            diffFormat = false;


        testFiltered = (Boolean) parameters.get("testFiltered");
        if (testFiltered == null)
            testFiltered = true;

        waitTime = (Integer) parameters.get("waitTime");
        if (waitTime == null)
            waitTime = 6000;

        seedLineGenerated = (Boolean) parameters.get("seedLineGenerated");
        if (seedLineGenerated == null)
            seedLineGenerated = true;

        manipulationFilterRule = (Boolean) parameters.get("manipulationFilterRule");
        if (manipulationFilterRule == null)
            manipulationFilterRule = true;

        ingredientFilterRule = (Boolean) parameters.get("ingredientFilterRule");
        if (ingredientFilterRule == null)
            ingredientFilterRule = true;

        checkParameters();
        invokeModules();

        globalID = 0;
        evaluations = 0;
        launchTime = System.currentTimeMillis();
        patches = new HashSet<Patch>();
    }

    void checkParameters() throws Exception {
        if (binJavaDir == null)
            throw new Exception("The build directory of Java classes is not specified!");
        else if (binTestDir == null)
            throw new Exception("The build directory of test classes is not specified!");
        else if (srcJavaDir == null)
            throw new Exception("The directory of Java source code is not specified!");
        else if (dependences == null)
            throw new Exception("The dependences of the buggy program is not specified!");
        else if (!(new File(jvmPath).exists()))
            throw new Exception("The JVM path does not exist!");
        else if (!(new File(externalProjRoot).exists()))
            throw new Exception("The directory of external project does not exist!");
    }

    void invokeModules() throws Exception {
        invokeClassFinder();//类的加载就是把 'java的类文件' 与 'javaTest的类文件' 用classloader加载进来
        invokeFaultLocalizer();
        invokeSeedLineGenerator();
        invokeASTRequestor();
//		invokeLocalVarDetector();
//		invokeFieldVarDetector();
//		invokeMethodDetector();//Field与Method都是在选自修改点的所在类与继承类的（“public”、“protected”、“同包下的private”的变量和方法）
        invokeIngredientScreener();
        invokeManipulationInitializer();
//		invokeModificationPointsTrimmer();//modification修改点的整理——删除不必要的不符合理论逻辑的修改点
        invokeExpressionProduct();
        invokeTestFilter();
        invokeCompilerOptionsInitializer();
        invokeProgURLsInitializer();
    }

    void invokeClassFinder() throws ClassNotFoundException, IOException {
        ClassFinder finder = new ClassFinder(binJavaDir, binTestDir, dependences);
        binJavaClasses = finder.findBinJavaClasses();

        if (binExecuteTestClasses == null)
            binExecuteTestClasses = finder.findBinExecuteTestClasses();

        if (javaClassesInfoPath != null)
            FileUtils.writeLines(new File(javaClassesInfoPath), binJavaClasses);
        if (testClassesInfoPath != null)
            FileUtils.writeLines(new File(testClassesInfoPath), binExecuteTestClasses);
    }

    void invokeFaultLocalizer() throws FileNotFoundException, IOException {
        System.out.println("Fault localization starts...");
        IFaultLocalizer faultLocalizer;
        if (gzoltarDataDir == null)
            faultLocalizer = new GZoltarFaultLocalizer(binJavaClasses, binExecuteTestClasses, binJavaDir, binTestDir,
                    dependences);
        else
            faultLocalizer = new GZoltarFaultLocalizer2(gzoltarDataDir);

        faultyLines = faultLocalizer.searchSuspicious(thr);

        positiveTests = faultLocalizer.getPositiveTests();
        negativeTests = faultLocalizer.getNegativeTests();

        if (orgPosTestsInfoPath != null)
            FileUtils.writeLines(new File(orgPosTestsInfoPath), positiveTests);

        System.out.println("Number of positive tests: " + positiveTests.size());
        System.out.println("Number of negative tests: " + negativeTests.size());
        System.out.println("Fault localization is finished!");
    }

    void invokeSeedLineGenerator() throws IOException, InterruptedException {
        if (seedLineGenerated) {
            SeedLineGeneratorProcess slgp = new SeedLineGeneratorProcess(binJavaClasses, javaClassesInfoPath,
                    binExecuteTestClasses, testClassesInfoPath, binJavaDir, binTestDir, dependences, externalProjRoot,
                    jvmPath);
            seedLines = slgp.getSeedLines();
        } else
            seedLines = null;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    void invokeASTRequestor() throws IOException {
        System.out.println("AST parsing starts...");

        modificationPoints = new ArrayList<ModificationPoint>();
        seedStatements = new HashMap<SeedStatement, SeedStatementInfo>();
        sourceASTs = new HashMap<String, CompilationUnit>();
        sourceContents = new HashMap<String, String>();
        declaredClasses = new HashMap<String, ITypeBinding>();

        FileASTRequestorImpl requestor = new FileASTRequestorImpl(faultyLines, seedLines, modificationPoints,
                seedStatements, sourceASTs, sourceContents, declaredClasses);

        ASTParser parser = ASTParser.newParser(AST.JLS8);
        String[] classpathEntries = null;
        if (dependences != null)
            classpathEntries = dependences.toArray(new String[dependences.size()]);

        parser.setEnvironment(classpathEntries, new String[]{srcJavaDir}, null, true);
        parser.setResolveBindings(true);//在每个叶子节点的基础上绑定更多的信息。
        parser.setBindingsRecovery(true);

        Map options = JavaCore.getOptions();
        JavaCore.setComplianceOptions(JavaCore.VERSION_1_7, options);
        parser.setCompilerOptions(options);

        File srcFile = new File(srcJavaDir);
        Collection<File> javaFiles = FileUtils.listFiles(srcFile, new SuffixFileFilter(".java"),
                TrueFileFilter.INSTANCE);
        String[] sourceFilePaths = new String[javaFiles.size()];

        int i = 0;
        for (File file : javaFiles)
            sourceFilePaths[i++] = file.getCanonicalPath();
        //输入.java文件的绝对路径，对相应的java文件进行AST抽象语法树分析。
        parser.createASTs(sourceFilePaths, null, new String[]{"UTF-8"}, requestor, null);

        if (maxNumberOfModificationPoints != null && modificationPoints.size() > maxNumberOfModificationPoints) {
            Collections.sort(modificationPoints, new Comparator<ModificationPoint>() {
                @Override
                public int compare(ModificationPoint o1, ModificationPoint o2) {
                    Double d1 = new Double(o1.getSuspValue());
                    Double d2 = new Double(o2.getSuspValue());
                    return d2.compareTo(d1);
                }
            });

            List<ModificationPoint> temp = new ArrayList<ModificationPoint>();
            int[] permutation = new int[maxNumberOfModificationPoints];
            Utils.randomPermutation(permutation, maxNumberOfModificationPoints);

            for (i = 0; i < maxNumberOfModificationPoints; i++) {
                ModificationPoint mp = modificationPoints.get(permutation[i]);
                temp.add(mp);
            }


            modificationPoints = temp;
        }
        System.out.println("AST parsing is finished!");
    }

    void invokeLocalVarDetector() {
        System.out.println("Detection of local variables starts...");
        LocalVarDetector lvd = new LocalVarDetector(modificationPoints);
        lvd.detect();
        System.out.println("Detection of local variables is finished!");
    }

    void invokeMethodDetector() throws ClassNotFoundException, IOException {
        System.out.println("Detection of methods starts...");
        MethodDetector md = new MethodDetector(modificationPoints, declaredClasses, dependences);
        md.detect();
        System.out.println("Detection of methods is finished!");
    }

    void invokeFieldVarDetector() throws ClassNotFoundException, IOException {
        System.out.println("Detection of fields starts...");
        FieldVarDetector fvd = new FieldVarDetector(modificationPoints, declaredClasses, dependences);
        fvd.detect();
        System.out.println("Detection of fields is finished!");
    }

    void invokeIngredientScreener() throws JMException {
        System.out.println("Ingredient screener starts...");
//		AbstractIngredientScreener ingredientScreener = IngredientScreenerFactory
//				.getIngredientScreener(ingredientScreenerName, modificationPoints, seedStatements, ingredientMode);
//		ingredientScreener.screen();
//
//		if (ingredientFilterRule) {
//			for (ModificationPoint mp : modificationPoints) {
//				Iterator<Statement> iterator = mp.getIngredients().iterator();
//				while (iterator.hasNext()) {
//					Statement seed = iterator.next();
//					if (IngredientFilterRule.canFiltered(seed, mp))
//						iterator.remove();
//				}
//			}
//		}
        DirectIngredientExpressionScreener dir = new DirectIngredientExpressionScreener(modificationPoints, seedStatements);
        dir.allocatonExpressionForModificationPoints();
//		dir.TypeFilter();
        System.out.println("Ingredient screener is finished!");
    }

    void invokeManipulationInitializer() {
        System.out.println("Initialization of manipulations starts...");
        availableManipulations = new ArrayList<List<String>>(modificationPoints.size());

        for (int i = 0; i < modificationPoints.size(); i++) {      //操作的初始化就是对于每一个修改点初始化：‘删除’/‘替换’/‘增加’操作,同时,
            ModificationPoint mp = modificationPoints.get(i);       //我还制定了规则来决定这些操作是否保留
            List<String> list = new ArrayList<String>();
            //				if (manipulationFilterRule) {
            //			/		if (!ManipulationFilterRule.canFiltered(manipulationName, mp))
            //				} else
            //					list.add(manipulationName);
            list.addAll(Arrays.asList(manipulationNames));
            availableManipulations.add(list);
        }
        System.out.println("Initialization of manipulations is finished!");
    }

    void invokeExpressionProduct() {
        // TODO Auto-generated method stub
        //这里主要是产生初始化补丁成分，产生我的补丁，是我自己的补丁
        System.out.println("modification-initial of expressionIngredient starts...");

        int size = modificationPoints.size();
        List<ModificationPoint> tmp = new ArrayList<>();//过滤掉一些修改点，仅仅保留我需要的那几个
        for (int i = 0; i < size; i++) {
            boolean flag = true;

            ModificationPoint modificationPoint = modificationPoints.get(i);
            RepairExpression repairExpression = new RepairExpression(modificationPoint);
            Statement statement = modificationPoint.getStatement();

            /**
             * 当为特定的语句类型 并且 表达式成分不为空时执行
             */
            if (statement instanceof IfStatement) {
                boolean mid = repairExpression.ifRepair();
                while(mid){
                    mid = repairExpression.ifRepair();
                    flag=false;
                }
            }
            if ((statement instanceof WhileStatement)) {
                boolean mid = repairExpression.whileRepair();
                while(mid){
                    mid = repairExpression.whileRepair();
                    flag = false;
                }

            }
            if ((statement instanceof ReturnStatement)) {
                boolean mid = repairExpression.returnRepair();
                while(mid){
                    mid = repairExpression.returnRepair();
                    flag=false;
                }
            }
            if ((statement instanceof DoStatement)) {
                boolean mid = repairExpression.doWhileRepair();
                while(mid){
                    mid = repairExpression.doWhileRepair();
                    flag=false;
                }
            }
            for (ExpressionInfo expression : modificationPoint.getModificationPointExpressionInfosList()) {
                /**
                 * 当修改点部位ifStatement、whileStatement、doWhile时，看是否满足：
                 * 1.强制类型转换;
                 * 2.数组调用;
                 * 3.变量调用;
                 * 如果满足以上条件，则进行补丁的生成。并且此条补丁生成之后，不再进行使用。
                 *///这里还要进行进一步过滤，因为有时候人家已经写了这个函数了
                Expression etem = expression.getExpression();
                if ((etem instanceof CastExpression) &&
                        (!TemplateBoolean.templateBooleanCheck(modificationPoints.get(i), expression.getExpressionStr()))&&
                        (!SimilarTarTemplateCheck.templateCheck(etem,"CastExpression"))) {
                    repairExpression.castTypeRepair((CastExpression) etem);
                    modificationPoint.getTemplateBoolean().put(expression.getExpressionStr(), true);
                    flag = false;
                } else if ((etem instanceof ArrayAccess) &&
                        (!TemplateBoolean.templateBooleanCheck(modificationPoint, expression.getExpressionStr()))&&
                        (!SimilarTarTemplateCheck.templateCheck(etem,"ArrayAccess"))) {
                    repairExpression.arrayRepair((ArrayAccess) etem);
                    modificationPoint.getTemplateBoolean().put(expression.getExpressionStr(), true);
                    flag = false;
                } else if ((etem instanceof FieldAccess) &&
                        (!TemplateBoolean.templateBooleanCheck(modificationPoint, expression.getExpressionStr()))&&
                        (!SimilarTarTemplateCheck.templateCheck(etem,"FieldAccess"))) {
                    repairExpression.fieldRepair((FieldAccess) etem);
                    modificationPoint.getTemplateBoolean().put(expression.getExpressionStr(), true);
                    flag = false;
                }

            }
            if (!flag)
                tmp.add(modificationPoint);
        }

        modificationPoints = tmp;
        System.out.println("modification-initial of expressionIngredient finish...");
    }

    void invokeTestFilter() throws IOException, InterruptedException {
        System.out.println("Filtering of the tests starts...");
        if (testFiltered) {
            Set<LCNode> fLines;
            if (maxNumberOfModificationPoints != null) {
                fLines = new HashSet<LCNode>();
                for (ModificationPoint mp : modificationPoints)
                    fLines.add(mp.getLCNode());
            } else
                fLines = faultyLines.keySet();

            if (faultyLinesInfoPath != null) {
                List<String> lines = new ArrayList<String>();
                for (LCNode node : fLines)
                    lines.add(node.toString());
                FileUtils.writeLines(new File(faultyLinesInfoPath), lines);
            }

            TestFilterProcess tfp = new TestFilterProcess(fLines, faultyLinesInfoPath, positiveTests,
                    orgPosTestsInfoPath, binJavaDir, binTestDir, dependences, externalProjRoot, jvmPath);
            positiveTests = tfp.getFilteredPositiveTests();
        }

        if (finalTestsInfoPath != null) {
            List<String> finalTests = new ArrayList<String>();
            finalTests.addAll(positiveTests);
            finalTests.addAll(negativeTests);
            FileUtils.writeLines(new File(finalTestsInfoPath), finalTests);
        }

        System.out.println("Number of positive tests considered: " + positiveTests.size());
        System.out.println("Filtering of the tests is finished!");
    }

    void invokeCompilerOptionsInitializer() {
        compilerOptions = new ArrayList<String>();
        compilerOptions.add("-nowarn");
        compilerOptions.add("-source");
        compilerOptions.add("1.7");
        compilerOptions.add("-cp");
        String cpStr = binJavaDir;

        if (dependences != null) {
            for (String str : dependences)
                cpStr += (File.pathSeparator + str);
        }

        compilerOptions.add(cpStr);
    }

    void invokeProgURLsInitializer() throws MalformedURLException {
        List<String> tempList = new ArrayList<String>();
        tempList.add(binJavaDir);
        tempList.add(binTestDir);
        if (dependences != null)
            tempList.addAll(dependences);
        progURLs = Helper.getURLs(tempList);
    }

    void invokeModificationPointsTrimmer() {
        int i = 0;
        while (i < modificationPoints.size()) {
            ModificationPoint mp = modificationPoints.get(i);
            List<String> manips = availableManipulations.get(i);

            if (mp.getIngredients().isEmpty()) {
                Iterator<String> iter = manips.iterator();
                while (iter.hasNext()) {
                    String manipName = iter.next();
                    if (!manipName.equalsIgnoreCase("Delete"))
                        iter.remove();
                }
            }

            if (manips.isEmpty()) {
                modificationPoints.remove(i);
                availableManipulations.remove(i);
            } else
                i++;
        }
    }

    protected Map<String, String> getModifiedJavaSources(Map<String, ASTRewrite> astRewriters) {
        Map<String, String> javaSources = new HashMap<String, String>();
        //ASTRewrite.entrySet由 “java文件的绝对路径” + “其抽象语法树” 组成
        for (Entry<String, ASTRewrite> entry : astRewriters.entrySet()) {
            String sourceFilePath = entry.getKey();
            String content = sourceContents.get(sourceFilePath);

            Document doc = new Document(content);
            TextEdit edits = entry.getValue().rewriteAST(doc, null);

            try {
                edits.apply(doc);
            } catch (BadLocationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            javaSources.put(sourceFilePath, doc.get());
        }
        return javaSources;
    }

    //涉及的修改点——修改操作———种子成分————源文件的抽象语法树
    protected boolean manipulateOneModificationPoint(ModificationPoint mp, String manipName, Statement ingredStatement,
                                                     Map<String, ASTRewrite> astRewriters) throws JMException {
        String sourceFilePath = mp.getSourceFilePath();
        ASTRewrite rewriter;
        if (astRewriters.containsKey(sourceFilePath))
            rewriter = astRewriters.get(sourceFilePath);
        else {
            CompilationUnit unit = sourceASTs.get(sourceFilePath);
            rewriter = ASTRewrite.create(unit.getAST());
            astRewriters.put(sourceFilePath, rewriter);
        }

        AbstractManipulation manipulation = ManipulationFactory.getManipulation(manipName, mp, ingredStatement,
                rewriter);
        return manipulation.manipulate();
    }

    protected Map<String, JavaFileObject> getCompiledClassesForTestExecution(Map<String, String> javaSources) {
        JavaJDKCompiler compiler = new JavaJDKCompiler(ClassLoader.getSystemClassLoader(), compilerOptions);
        try {
            boolean isCompiled = compiler.compile(javaSources);
            if (isCompiled)
                return compiler.getClassLoader().getCompiledClasses();
            else
                return null;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    protected ITestExecutor getTestExecutor(Map<String, JavaFileObject> compiledClasses, Set<String> executePosTests)
            throws JMException, IOException {
        if (testExecutorName.equalsIgnoreCase("ExternalTestExecutor")) {
            File binWorkingDirFile = new File(binWorkingRoot, "bin_" + (globalID++));
            IO.saveCompiledClasses(compiledClasses, binWorkingDirFile);//将我要编译的类写入指定的工作目录中去
            String binWorkingDir = binWorkingDirFile.getCanonicalPath();
            String tempPath = (executePosTests == positiveTests) ? finalTestsInfoPath : null;
            return new ExternalTestExecutor(executePosTests, negativeTests, tempPath, binJavaDir, binTestDir,
                    dependences, binWorkingDir, externalProjRoot, jvmPath, waitTime);

        } else if (testExecutorName.equalsIgnoreCase("InternalTestExecutor")) {
            CustomURLClassLoader urlClassLoader = new CustomURLClassLoader(progURLs, compiledClasses);
            return new InternalTestExecutor(executePosTests, negativeTests, urlClassLoader, waitTime);
        } else {
            Configuration.logger_.severe("test executor name '" + testExecutorName + "' not found ");
            throw new JMException("Exception in getTestExecutor()");
        }
    }

    protected Set<String> getSamplePositiveTests() {
        if (percentage == null || percentage == 1)
            return positiveTests;
        else {
            int num = (int) (positiveTests.size() * percentage);
            List<String> tempList = new ArrayList<String>(positiveTests);
            Collections.shuffle(tempList);
            Set<String> samplePositiveTests = new HashSet<String>();
            for (int i = 0; i < num; i++)
                samplePositiveTests.add(tempList.get(i));
            return samplePositiveTests;
        }
    }

    public List<List<String>> getAvailableManipulations() {
        return this.availableManipulations;
    }

    public List<ModificationPoint> getModificationPoints() {
        return this.modificationPoints;
    }

    public String[] getManipulationNames() {
        return this.manipulationNames;
    }

    public Map<String, CompilationUnit> getSourceASTs() {
        return this.sourceASTs;
    }

    public Map<String, String> getSourceContents() {
        return this.sourceContents;
    }

    public Set<String> getNegativeTests() {
        return this.negativeTests;
    }

    public Set<String> getPositiveTests() {
        return this.positiveTests;
    }

    public Double getPercentage() {
        return this.percentage;
    }

    public void saveTestAdequatePatch(List<Integer> opList, List<Integer> locList, List<Integer> ingredList)
            throws IOException {
        long estimatedTime = System.currentTimeMillis() - launchTime;
        if (patchOutputRoot != null)
            IO.savePatch(opList, locList, ingredList, modificationPoints, availableManipulations, patchOutputRoot,
                    globalID, evaluations, estimatedTime);
    }

    public boolean addTestAdequatePatch(List<Integer> opList, List<Integer> locList, List<Integer> ingredList) {
        Patch patch = new Patch(opList, locList, ingredList, modificationPoints, availableManipulations);
        return patches.add(patch);
    }

    public String getSrcJavaDir() {
        return this.srcJavaDir;
    }

    public String getBinJavaDir() {
        return this.binJavaDir;
    }

    public String getBinTestDir() {
        return this.binTestDir;
    }

    public Set<String> getDependences() {
        return this.dependences;
    }

    public int getNumberOfModificationPoints() {
        return modificationPoints.size();
    }

    public Set<Patch> getPatches() {
        return this.patches;
    }

    public void clearPatches() {
        patches.clear();
    }

    public void resetPatchOutputRoot(String patchOutputRoot) {
        this.patchOutputRoot = patchOutputRoot;
    }


    public void resetBinWorkingRoot(String binWorkingRoot) {
        this.binWorkingRoot = binWorkingRoot;
    }

    public static void resetGlobalID(int id) {
        globalID = id;
    }

    public static void increaseGlobalID() {
        globalID++;
    }

    public static void resetLaunchTime(long time) {
        launchTime = time;
    }

    public static long getLaunchTime() {
        return launchTime;
    }

    public static void resetEvaluations(int evals) {
        evaluations = evals;
    }

    public static int getEvaluations() {
        return evaluations;
    }

    public String getBinWorkingRoot() {
        return binWorkingRoot;
    }

    public String getOrgPosTestsInfoPath() {
        return orgPosTestsInfoPath;
    }

    public String getFinalTestsInfoPath() {
        return finalTestsInfoPath;
    }

}

