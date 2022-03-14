package us.msu.cse.repair.algorithms.arja;

import jmetal.metaheuristics.ibea.IBEA;
import jmetal.metaheuristics.moead.MOEAD;
import jmetal.metaheuristics.omopso.OMOPSO;
import jmetal.metaheuristics.paes.PAES;
import jmetal.metaheuristics.pesa2.PESA2;
import jmetal.metaheuristics.spea2.SPEA2;
import us.msu.cse.repair.core.AbstractRepairAlgorithm;
import us.msu.cse.repair.ec.problems.ArjaProblem;
import jmetal.metaheuristics.nsgaII.NSGAII;

public class Arja extends AbstractRepairAlgorithm {
	public Arja(ArjaProblem problem) throws Exception {
//		algorithm = new NSGAII(problem);
//		algorithm = new MOEAD(problem);
//		algorithm = new OMOPSO(problem);
		algorithm = new SPEA2(problem);
//		algorithm =new IBEA(problem);
//		algorithm = new PAES(problem);
//		algorithm = new PESA2(problem);

	}
}
