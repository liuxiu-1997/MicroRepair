package us.msu.cse.repair.ec.representation;

import jmetal.core.Problem;
import jmetal.core.SolutionType;
import jmetal.core.Variable;
import jmetal.encodings.variable.ArrayInt;
import jmetal.encodings.variable.Binary;

public class ArrayIntAndBinarySolutionType extends SolutionType {
	private final int size;
	private final double[] prob;

	public ArrayIntAndBinarySolutionType(Problem problem, int size, double[] prob) {
		super(problem);
		this.size = size;
		this.prob = prob;
		// TODO Auto-generated constructor stub
	}

	@Override
	public Variable[] createVariables() throws ClassNotFoundException {
		// TODO Auto-generated method stub
		Variable[] variables = new Variable[2];

		variables[0] = new ArrayInt(2 * size, problem_);//array[]数组，分成了修改操作+成分两部分。在之前初始化lowerLimit[]的基础上，在里面随机挑选。
		variables[1] = new Binary(size, prob);//并不是所有的位置都能修改,我根据随机生成数系统

		return variables;
	}

}
