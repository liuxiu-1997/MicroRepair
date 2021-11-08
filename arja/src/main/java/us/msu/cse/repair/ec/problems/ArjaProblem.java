package us.msu.cse.repair.ec.problems;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

import javax.tools.JavaFileObject;

import jmetal.util.PseudoRandom;
import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;

import jmetal.core.Solution;
import jmetal.encodings.variable.ArrayInt;
import jmetal.encodings.variable.Binary;
import jmetal.util.Configuration;
import jmetal.util.JMException;
import sun.awt.X11.XSystemTrayPeer;
import us.msu.cse.repair.core.AbstractRepairProblem;
import us.msu.cse.repair.core.filterrules.MIFilterRule;
import us.msu.cse.repair.core.parser.ModificationPoint;
import us.msu.cse.repair.core.testexecutors.ITestExecutor;
import us.msu.cse.repair.core.util.IO;
import us.msu.cse.repair.ec.representation.ArrayIntAndBinarySolutionType;
import us.msu.cse.repair.informationExpression.ExpressionInfo;
import us.msu.cse.repair.repairExpression.ExpressionIngredientSize;
import us.msu.cse.repair.repairExpression.RepairExpression;
import us.msu.cse.repair.toolsExpression.RandCheck;
import us.msu.cse.repair.toolsExpression.TemplateBoolean;

public class ArjaProblem extends AbstractRepairProblem {
	private static final long serialVersionUID = 1L;
	Double weight;

	Integer numberOfObjectives;
	Integer maxNumberOfEdits;
	Double mu;

	String initializationStrategy;

	Boolean miFilterRule;

	public ArjaProblem(Map<String, Object> parameters) throws Exception {
		super(parameters);

		weight = (Double) parameters.get("weight");
		if (weight == null)
			weight = 0.5;

		mu = (Double) parameters.get("mu");
		if (mu == null)
			mu = 0.06;

		numberOfObjectives = (Integer) parameters.get("numberOfObjectives");
		if (numberOfObjectives == null)
			numberOfObjectives = 2;

		initializationStrategy = (String) parameters.get("initializationStrategy");
		if (initializationStrategy == null)
			initializationStrategy = "Prior";

		miFilterRule = (Boolean) parameters.get("miFilterRule");
		if (miFilterRule == null)
			miFilterRule = true;

		maxNumberOfEdits = (Integer) parameters.get("maxNumberOfEdits");

		setProblemParams();
	}

	void setProblemParams() throws JMException {
		numberOfVariables_ = 2;
		numberOfObjectives_ = numberOfObjectives;
		numberOfConstraints_ = 0;
		problemName_ = "ArjaProblem";

		int size = modificationPoints.size();

		double[] prob = new double[size];
		if (initializationStrategy.equalsIgnoreCase("Prior")) {
			for (int i = 0; i < size; i++)
				prob[i] = modificationPoints.get(i).getSuspValue() * mu;
		} else if (initializationStrategy.equalsIgnoreCase("Random")) {
			for (int i = 0; i < size; i++)
				prob[i] = 0.5;
		} else {
			Configuration.logger_.severe("Initialization strategy " + initializationStrategy + " not found");
			throw new JMException("Exception in initialization strategy: " + initializationStrategy);
		}

		solutionType_ = new ArrayIntAndBinarySolutionType(this, size, prob);

		upperLimit_ = new double[2 * size];
		lowerLimit_ = new double[2 * size];
		for (int i = 0; i < size; i++) {
			lowerLimit_[i] = 0;
			upperLimit_[i] = availableManipulations.get(i).size() - 1;
		}

		for (int i = size; i < 2 * size; i++) {
			lowerLimit_[i] = 0;
			upperLimit_[i] = modificationPoints.get(i - size).getIngredients().size() - 1;
		}
	}

	@Override
	public void evaluate(Solution solution) throws JMException {    //计算这个新的解的目标值是什么
		// TODO Auto-generated method stub
		System.out.println("One fitness evaluation starts...");
		//array数组,0-38即前一半是修改操作，后一半是补丁陈分操作(即选第几个语句)
		int[] array = ((ArrayInt) solution.getDecisionVariables()[0]).array_;
		BitSet bits = ((Binary) solution.getDecisionVariables()[1]).bits_;
		//array[]数组，分成了修改操作+成分两部分。在之前初始化lowerLimit[]的基础上，在里面随机挑选;并不是所有的位置都能修改,我根据随机生成数系统——即BitSet
		int size = modificationPoints.size();
		Map<String, ASTRewrite> astRewriters = new HashMap<String, ASTRewrite>();//Map<String,ASTRewrite>这里的String代表的是修改的java文件的绝对路径;value是据其创建的ASTRewrite

		Map<Integer, Double> selectedMP = new HashMap<Integer, Double>();

		//注意：我并不是所有的点都可以修改,我是随机的产生修改的位置;

//		RandCheck.randCheck(bits,size);
	 	for (int i = 0; i < size; i++) {
			/**
			 * 定义三种修改依次进行，确定优先级
			 * 1.特定类型模板修改
			 * 2.表达式修改
			 * 3.普通修改
			 * 优先级: 1 > 2 > 3
			 */
			boolean flag = true;
			ModificationPoint modificationPoint = modificationPoints.get(i);
			RepairExpression repairExpression  = null;
			boolean isnull = ExpressionIngredientSize.isNullOfExpIngre(modificationPoint);
			if (!isnull)
			  repairExpression  = new RepairExpression(modificationPoint);


//			if (bits.get(i)) {
				double suspValue = modificationPoint.getSuspValue();
				Statement statement = modificationPoint.getStatement();

				//进行IfStatement的判断
				if ((statement instanceof IfStatement)&&(!isnull)) {
					boolean mid = repairExpression.ifRepair();
					if(mid) {
						array[i] = 1;
						array[i + size] = modificationPoint.getIngredients().size() - 1;
						flag = false;
						System.out.println("buding chengfen 1:" + modificationPoint.getIngredients().get(array[i + size]));
					}
				} else if ((statement instanceof WhileStatement)&&(!isnull)) {
					boolean mid= repairExpression.whileRepair();
					if(mid) {
						array[i] = 1;
						array[i + size] = modificationPoint.getIngredients().size() - 1;
						flag = false;
						System.out.println("buding chengfen 2:" + modificationPoint.getIngredients().get(array[i + size]));
					}
				} else if ((statement instanceof ReturnStatement)&&(!isnull)) {
					boolean mid=repairExpression.returnRepair();
					if (mid) {
						array[i] = 1;
						array[i + size] = modificationPoint.getIngredients().size() - 1;
						flag = false;
					}
				} else if ((statement instanceof DoStatement)&&(!isnull)) {
					boolean mid = repairExpression.doWhileRepair();
					if (mid) {
						array[i] = 1;
						array[i + size] = modificationPoint.getIngredients().size() - 1;
						flag = false;
					}
					System.out.println("buding chengfen 4:"+modificationPoint.getIngredients().get(array[i+size]));
				} else if (!isnull){
					for (ExpressionInfo expression : modificationPoint.getModificationPointExpressionInfosList()) {
						if ((expression.getExpression() instanceof CastExpression) &&
								(!TemplateBoolean.templateBooleanCheck(modificationPoints.get(i), "cast"))) {
							repairExpression.castTypeRepair((CastExpression) expression.getExpression());
							array[i] = 2;
							array[i + size] = modificationPoint.getIngredients().size()-1;
							modificationPoint.getTemplateBoolean().put("cast", true);
							flag = false;
						} else if ((expression.getExpression() instanceof ArrayAccess) &&
								(!TemplateBoolean.templateBooleanCheck(modificationPoint, "array"))) {
							repairExpression.arrayRepair((ArrayAccess) expression.getExpression());
							array[i] = 2;
							array[i + size] = modificationPoint.getIngredients().size()-1;
							modificationPoint.getTemplateBoolean().put("array", true);
							flag = false;
						} else if ((expression.getExpression() instanceof FieldAccess) &&
								(!TemplateBoolean.templateBooleanCheck(modificationPoint, "field"))) {
							repairExpression.fieldRepair((FieldAccess) expression.getExpression());
							array[i] = 2;
							array[i + size] = modificationPoint.getIngredients().size()-1;
							modificationPoint.getTemplateBoolean().put("field", true);
							flag = false;
						}
					}
				}

				if (flag){
					bits.set(i,false);
				}else {
					selectedMP.put(i, suspValue);
					bits.set(i,true);
				}
//				if (miFilterRule && flag) {
//					String manipName = availableManipulations.get(i).get(array[i]);
//					Statement seed = null;
//					if (!modificationPoint.getIngredients().isEmpty())
//						seed = modificationPoint.getIngredients().get(array[i + size]);
//
//					int index = MIFilterRule.canFiltered(manipName, seed, modificationPoints.get(i));
//					if (index == -1)
//						selectedMP.put(i, suspValue);
//					else if (index < modificationPoint.getIngredients().size()) {
//						array[i + size] = index;       //猜测是给他一个最大值，以后不再想去用他
//						selectedMP.put(i, suspValue);
//					} else
//						bits.set(i, false);
//				} else {

//				}
		}
		if (selectedMP.isEmpty()) {
			assignMaxObjectiveValues(solution);
			return;
		}
		int numberOfEdits = selectedMP.size();
		List<Map.Entry<Integer, Double>> list = new ArrayList<Map.Entry<Integer, Double>>(selectedMP.entrySet());

		if (maxNumberOfEdits != null && selectedMP.size() > maxNumberOfEdits) {
			Collections.sort(list, new Comparator<Map.Entry<Integer, Double>>() {
				@Override
				public int compare(Entry<Integer, Double> o1, Entry<Integer, Double> o2) {
					return o2.getValue().compareTo(o1.getValue());
				}
			});

			numberOfEdits = maxNumberOfEdits;
		}

		for (int i = 0; i < numberOfEdits; i++)
			manipulateOneModificationPoint(list.get(i).getKey(), size, array, astRewriters);


		for (int i = numberOfEdits; i < selectedMP.size(); i++)
			bits.set(list.get(i).getKey(), false);
		//获得修改后的java源文件：————由java文件的绝对路径+Document文件组成
		Map<String, String> modifiedJavaSources = getModifiedJavaSources(astRewriters);
		Map<String, JavaFileObject> compiledClasses = getCompiledClassesForTestExecution(modifiedJavaSources);
		//对类进行编译
		boolean status = false;
		if (compiledClasses != null) {
			if (numberOfObjectives == 2 || numberOfObjectives == 3)
				solution.setObjective(0, numberOfEdits);//恍然大悟：两个目标值，第一个目标值是修改点的大小;第二个目标值是适应读函数
			try {//看我这条补丁执行成功了吗？————————用status来探测————————
				status = invokeTestExecutor(compiledClasses, solution);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			assignMaxObjectiveValues(solution);//当类编译失败证明我的一步就错误了,无法进行下一步的测试了，所以对修改点设置最大的目标值
			System.out.println("Compilation fails!");
		}
		//如果执行成功了就保存这条补丁
		if (status) {
			save(solution, modifiedJavaSources, compiledClasses, list, numberOfEdits);
		}

		evaluations++;
		System.out.println("One fitness evaluation is finished...");
	}

	void save(Solution solution, Map<String, String> modifiedJavaSources, Map<String, JavaFileObject> compiledClasses,
			List<Map.Entry<Integer, Double>> list, int numberOfEdits) {
		List<Integer> opList = new ArrayList<Integer>();
		List<Integer> locList = new ArrayList<Integer>();
		List<Integer> ingredList = new ArrayList<Integer>();

		int[] var0 = ((ArrayInt) solution.getDecisionVariables()[0]).array_;
		int size = var0.length / 2;

		for (int i = 0; i < numberOfEdits; i++) {
			int loc = list.get(i).getKey();
			int op = var0[loc];
			int ingred = var0[loc + size];
			opList.add(op);
			locList.add(loc);
			ingredList.add(ingred);
		}

		try {
			if (addTestAdequatePatch(opList, locList, ingredList)) {
				if (diffFormat) {
					try {
						IO.savePatch(modifiedJavaSources, srcJavaDir, this.patchOutputRoot, globalID);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				saveTestAdequatePatch(opList, locList, ingredList);
				globalID++;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	boolean manipulateOneModificationPoint(int i, int size, int array[], Map<String, ASTRewrite> astRewriters)
			throws JMException {
		ModificationPoint mp = modificationPoints.get(i);
		String manipName = availableManipulations.get(i).get(array[i]);

		Statement ingredStatement = null;
		ingredStatement = mp.getIngredients().get(array[i + size]);
		return manipulateOneModificationPoint(mp, manipName, ingredStatement, astRewriters);
	}

	boolean invokeTestExecutor(Map<String, JavaFileObject> compiledClasses, Solution solution) throws Exception {
		Set<String> samplePosTests = getSamplePositiveTests();
		ITestExecutor testExecutor = getTestExecutor(compiledClasses, samplePosTests);

		boolean status = testExecutor.runTests();//对修改的后的文件执行测试，看测试是否通过
		//先执行失败的测试用例，如果失败的测试用例执行成功后,那么紧接着执行之前成功的测试用例
		if (status && percentage != null && percentage < 1) {
			testExecutor = getTestExecutor(compiledClasses, positiveTests);
			status = testExecutor.runTests();
		}
		//实锤啦，这是适应度函数！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！
		if (!testExecutor.isExceptional()) {
			double ratioOfFailuresInPositive = testExecutor.getRatioOfFailuresInPositive();
			double ratioOfFailuresInNegative = testExecutor.getRatioOfFailuresInNegative();
			double fitness = weight * testExecutor.getRatioOfFailuresInPositive()
					+ testExecutor.getRatioOfFailuresInNegative();
		//这是是适应度函数！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！
			System.out.println("Number of failed tests: "
					+ (testExecutor.getFailureCountInNegative() + testExecutor.getFailureCountInPositive()));
			System.out.println("Weighted failure rate: " + fitness);
			
			if (numberOfObjectives == 1 || numberOfObjectives == 2) 
				solution.setObjective(numberOfObjectives - 1, fitness);
			else {
				solution.setObjective(1, ratioOfFailuresInPositive);
				solution.setObjective(2, ratioOfFailuresInNegative);
			}
		} else {
			assignMaxObjectiveValues(solution);
			System.out.println("Timeout occurs!");
		}
		return status;
	}

	void assignMaxObjectiveValues(Solution solution) {
		for (int i = 0; i < solution.getNumberOfObjectives(); i++)
			solution.setObjective(i, Double.MAX_VALUE);
	}

}
