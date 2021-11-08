package us.msu.cse.repair.core.parser;

import org.eclipse.jdt.core.dom.ASTMatcher;
import org.eclipse.jdt.core.dom.Statement;
import us.msu.cse.repair.informationExpression.LineAndNodeType;
import us.msu.cse.repair.informationExpression.MethClaPacOfExpName;

public class SeedStatement {
	Statement statement;

	MethClaPacOfExpName methClaPacOfExpName = new MethClaPacOfExpName();
	LineAndNodeType lineAndNodeType = new LineAndNodeType();

	public SeedStatement(Statement statement, MethClaPacOfExpName methClaPacOfExpName) {
		this.statement = statement;
		this.methClaPacOfExpName = methClaPacOfExpName;
	}

	public SeedStatement(Statement statement) {
		this.statement = statement;
	}

	public SeedStatement(Statement statement, MethClaPacOfExpName methClaPacOfExpName,LineAndNodeType lineAndNodeType) {
		this.statement = statement;
		this.methClaPacOfExpName = methClaPacOfExpName;
		this.lineAndNodeType = lineAndNodeType;
	}

	public Statement getStatement() {
		return statement;
	}

	public void setStatement(Statement statement) {
		this.statement = statement;
	}

	public MethClaPacOfExpName getMethClaPacOfExpName() {
		return methClaPacOfExpName;
	}

	public void setMethClaPacOfExpName(MethClaPacOfExpName methClaPacOfExpName) {
		this.methClaPacOfExpName = methClaPacOfExpName;
	}

	public LineAndNodeType getLineAndNodeType() {
		return lineAndNodeType;
	}

	public void setLineAndNodeType(LineAndNodeType lineAndNodeType) {
		this.lineAndNodeType = lineAndNodeType;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof SeedStatement))
			return false;
		SeedStatement ss = (SeedStatement) o;
		return statement.subtreeMatch(new ASTMatcher(true), ss.getStatement());

	}

	@Override
	public int hashCode() {
		return statement.toString().hashCode();
	}

}
