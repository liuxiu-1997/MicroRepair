package us.msu.cse.repair.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import java.util.Map.Entry;

import javax.tools.JavaFileObject;

import jmetal.metaheuristics.moead.Utils;
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
import jmetal.util.Configuration;
import jmetal.util.JMException;
import us.msu.cse.repair.AbstractClass.ASTVisitorPlus;
import us.msu.cse.repair.IngredientFilterRule;
import us.msu.cse.repair.astVisitorExpression.*;
import us.msu.cse.repair.core.compiler.JavaJDKCompiler;
import us.msu.cse.repair.core.coverage.SeedLineGeneratorProcess;
import us.msu.cse.repair.core.coverage.TestFilterProcess;
import us.msu.cse.repair.core.faultlocalizer.*;
import us.msu.cse.repair.core.manipulation.AbstractManipulation;
import us.msu.cse.repair.core.manipulation.ManipulationFactory;
import us.msu.cse.repair.core.parser.FileASTRequestorImpl;
import us.msu.cse.repair.core.parser.LCNode;
import us.msu.cse.repair.core.parser.ModificationPoint;
import us.msu.cse.repair.core.parser.SeedStatement;
import us.msu.cse.repair.core.parser.SeedStatementInfo;
import us.msu.cse.repair.core.parser.ingredient.IngredientMode;
import us.msu.cse.repair.core.testexecutors.ExternalTestExecutor;
import us.msu.cse.repair.core.testexecutors.ITestExecutor;
import us.msu.cse.repair.core.testexecutors.InternalTestExecutor;
import us.msu.cse.repair.core.util.ClassFinder;
import us.msu.cse.repair.core.util.CustomURLClassLoader;
import us.msu.cse.repair.core.util.Helper;
import us.msu.cse.repair.core.util.IO;
import us.msu.cse.repair.core.util.Patch;
import us.msu.cse.repair.filterExpression.DirectIngredientExpressionScreener;
import us.msu.cse.repair.repairExpression.RepairExpression;
import us.msu.cse.repair.toolsExpression.*;

public abstract class AbstractRepairProblem extends Problem {
    /**
     * void invokeExpressionProduct(); 这个函数里面三个模板，这主要是我产生补丁的地方
     * invokeIngredientScreener(); 这主要是我产生 表达式 的地方
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

    int testMid = 0;
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

//        String id = Helper.getRandomID();
        String id = GetPatchId.getId(srcJavaDir);

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
        invokeIngredientScreener();
        invokeManipulationInitializer();
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

    void invokeIngredientScreener() throws JMException, CloneNotSupportedException {
        System.out.println("Ingredient screener starts...");
        DirectIngredientExpressionScreener dir = new DirectIngredientExpressionScreener(modificationPoints, seedStatements);
        dir.allocatonExpressionForModificationPoints();
        System.out.println("Ingredient screener is finished!");
    }

    void invokeManipulationInitializer() {
        System.out.println("Initialization of manipulations starts...");
        availableManipulations = new ArrayList<List<String>>(modificationPoints.size());

        for (int i = 0; i < modificationPoints.size(); i++) {      //操作的初始化就是对于每一个修改点初始化：‘删除’/‘替换’/‘增加’操作,同时,
            ModificationPoint mp = modificationPoints.get(i);       //我还制定了规则来决定这些操作是否保留
            List<String> list = new ArrayList<String>();
            list.addAll(Arrays.asList(manipulationNames));
            availableManipulations.add(list);
        }
        System.out.println("Initialization of manipulations is finished!");
    }

    void invokeExpressionProduct() throws IOException, InterruptedException {
        // TODO Auto-generated method stub
        //这里主要是产生初始化补丁成分，产生我的补丁，是我自己的补丁
        System.out.println("modification-initial of expressionIngredient starts...");


        int size = modificationPoints.size();
        List<ModificationPoint> tmp = new ArrayList<>();//过滤掉一些修改点，仅仅保留我需要的那几个
        for (int i = 0; i < size; i++) {
            ModificationPoint modificationPoint = modificationPoints.get(i);
            RepairExpression repairExpression = new RepairExpression(modificationPoint);
            Statement statement = modificationPoint.getStatement();
            //——————————————————————————————————————————— 修复— ——————————————————————————————————————————————————————————
            //——————————————————————Ifstatement—WhileStatement—DoWhileStatement—ReturnStatement—————————————————————————-
            if (statement instanceof IfStatement) {
                boolean mid = repairExpression.ifRepair();
                while (mid) {
//                    System.out.println("If:"+(++testMid));
                    mid = repairExpression.ifRepair();
                }
            }
            if ((statement instanceof WhileStatement)) {
                boolean mid = repairExpression.whileRepair();
                while (mid) {
//         /           System.out.println("while:"+(++testMid));
                    mid = repairExpression.whileRepair();
                }

            }
            if ((statement instanceof ReturnStatement)) {
                boolean mid = repairExpression.returnRepair();
                while (mid) {
//                    System.out.println("return:"+(++testMid));
                    mid = repairExpression.returnRepair();
                }
            }
            if ((statement instanceof DoStatement)) {
                boolean mid = repairExpression.doWhileRepair();
                while (mid) {
//                    System.out.println("dowhile:"+(++testMid));
                    mid = repairExpression.doWhileRepair();
                }
            }
            //——————————————————————————————————————————— 修复二 ——————————————————————————————————————————————————————————
            //———————————————————————————————————强制类型转换、数组调用、变量调用检查——————————————————————————————————————————————-
//            for (ExpressionInfo expression : modificationPoint.getModificationPointExpressionInfosList()) {
//                /**
//                 * 当修改点部位ifStatement、whileStatement、doWhile时，看是否满足：
//                 * 1.强制类型转换;
//                 * 2.数组调用;
//                 * 3.变量调用;
//                 * 如果满足以上条件，则进行补丁的生成。并且此条补丁生成之后，不再进行使用。
//                 *///这里还要进行进一步过滤，因为有时候人家已经写了这个函数了
//                Expression etem = expression.getExpression();
//                if ((etem instanceof CastExpression) &&
//                        (!TemplateBoolean.templateBooleanCheck(modificationPoints.get(i), expression.getExpressionStr())) &&
//                        (!SimilarTarTemplateCheck.templateCheck(etem, "CastExpression"))) {
//                    repairExpression.castTypeRepair((CastExpression) etem);
//                    modificationPoint.getTemplateBoolean().put(expression.getExpressionStr(), true);
//                } else if ((etem instanceof ArrayAccess) &&
//                        (!TemplateBoolean.templateBooleanCheck(modificationPoint, expression.getExpressionStr())) &&
//                        (!SimilarTarTemplateCheck.templateCheck(etem, "ArrayAccess"))) {
//                    repairExpression.arrayRepair((ArrayAccess) etem);
//                    modificationPoint.getTemplateBoolean().put(expression.getExpressionStr(), true);
//                } else if ((etem instanceof FieldAccess) &&
//                        (!TemplateBoolean.templateBooleanCheck(modificationPoint, expression.getExpressionStr())) &&
//                        (!SimilarTarTemplateCheck.templateCheck(etem, "FieldAccess"))) {
//                    repairExpression.fieldRepair((FieldAccess) etem);
//                    modificationPoint.getTemplateBoolean().put(expression.getExpressionStr(), true);
//                }
//            }
            //——————————————————————————————————————————— 修复三——————————————————————————————————————————————————————————
            //—————————————————————————————————————对修改点在ASTVisitor上进行修复————————————————————————————————————————————-
            {
                String staClass = "public class Test1{\n{\n";
                staClass += modificationPoint.getStatement();
                staClass += "\n}\n}";
                CompilationUnit compilationUnit = GetCompilationUnit.getCompilationUnitOfString(staClass);

                StringLiteralRepairVisitor stringLiteralRepairVisitor = new StringLiteralRepairVisitor(modificationPoint);
                push(modificationPoint,compilationUnit,stringLiteralRepairVisitor);

                SimpleNameRepairVisitor simpleNameRepairVisitor = new SimpleNameRepairVisitor(modificationPoint);
                push(modificationPoint,compilationUnit,simpleNameRepairVisitor);

                MethodInvocationRepairVisitor methodInvocationRepairVisitor = new MethodInvocationRepairVisitor(modificationPoint);
                push(modificationPoint,compilationUnit,methodInvocationRepairVisitor);

                FieldAccessRepairVisitor fieldAccessRepairVisitor = new FieldAccessRepairVisitor(modificationPoint);
                push(modificationPoint,compilationUnit,fieldAccessRepairVisitor);

                ConstructorInvocationRepairVisitor constructorInvocationRepairVisitor = new ConstructorInvocationRepairVisitor(modificationPoint);
                push(modificationPoint,compilationUnit,constructorInvocationRepairVisitor);

                CharacterLiteralRepairVisitor characterLiteralRepairVisitor = new CharacterLiteralRepairVisitor(modificationPoint);
                push(modificationPoint,compilationUnit,characterLiteralRepairVisitor);

                AssignmentRepairVisitor assignmentRepairVisitor = new AssignmentRepairVisitor(modificationPoint);
                push(modificationPoint,compilationUnit,assignmentRepairVisitor);

                ArrayCreationRepairVisitor arrayCreationRepairVisitor = new ArrayCreationRepairVisitor(modificationPoint);
                push(modificationPoint,compilationUnit,arrayCreationRepairVisitor);

                ArrayAccessRepairVisitor arrayAccessRepairVisitor = new ArrayAccessRepairVisitor(modificationPoint);
                push(modificationPoint,compilationUnit,arrayAccessRepairVisitor);

                BooleanAndTypeRepairVisitor booleanOperatorRepairVisitor = new BooleanAndTypeRepairVisitor(modificationPoint);
                push(modificationPoint,compilationUnit,booleanOperatorRepairVisitor);

                QualifiedNameRepairVisitor qualifiedNameRepairVisitor = new QualifiedNameRepairVisitor(modificationPoint);
                push(modificationPoint,compilationUnit,qualifiedNameRepairVisitor);

                MixRepairVisitor mixRepairVisitor = new MixRepairVisitor(modificationPoint);
                push(modificationPoint,compilationUnit,mixRepairVisitor);

            }
            if (modificationPoint.getIngredients() != null)
                if (modificationPoint.getIngredients().size() > 0)
                    tmp.add(modificationPoint);
        }
        for (ModificationPoint mp : tmp) {
            List<Statement> l = new ArrayList<>();
            if (mp.getIngredients() != null) {
                //用于Ingredients的去重
                for (Statement sOut : mp.getIngredients()) {
                    boolean containFlag = false;
                    boolean ruleFlag ;
                    if (l.size() > 0) {
                        for (Statement sIn : l) {
                            if (sIn != null && sOut != null) {
                                if (sIn.toString().equals(sOut.toString())) {
                                    containFlag = true;
                                    break;
                                }
                            }
                        }
                    }
                    ruleFlag = IngredientFilterRule.getIsMatchRule(sOut,mp);
                    if ((!containFlag)&&(!ruleFlag) && (sOut != null)&&(!sOut.toString().equals(mp.getStatement().toString()))) {
                        l.add(sOut);
                    }
                }
            }
            mp.setIngredients(l);
        }
        modificationPoints = tmp;
        System.out.println("modification-initial of expressionIngredient finish...");
    }

    void push(ModificationPoint modificationPoint, CompilationUnit compilationUnit, ASTVisitorPlus mpVisitor) {
        boolean visitorRepairFlag = true;
        AST ast = AST.newAST(AST.JLS8);
        List<Statement> ingredientList = new ArrayList<>();
        while (visitorRepairFlag) {
            int number = 0;
            if (modificationPoint.getIngredients() != null) {
                number = modificationPoint.getIngredients().size();
            }
            mpVisitor.setRepaired(false);
            CompilationUnit compilationUnitIn = (CompilationUnit) ASTNode.copySubtree(ast, compilationUnit);
            compilationUnitIn.accept(mpVisitor);
            visitorRepairFlag = mpVisitor.isRepaired();
            Statement sta = mpVisitor.getStatement();

            if ((modificationPoint.getIngredients() != null) && (sta != null)) {
                if (number == modificationPoint.getIngredients().size()) {
                    ingredientList.add(sta);
                }
            } else if (visitorRepairFlag && (sta != null)) {
                ingredientList.add(sta);
            }
        }
        if (modificationPoint.getIngredients() == null) {
            modificationPoint.setIngredients(ingredientList);
        } else {
            modificationPoint.getIngredients().addAll(ingredientList);
        }
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

