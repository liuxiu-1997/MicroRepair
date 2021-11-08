package us.msu.cse.repair.core.util.visitors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.dom.*;

import us.msu.cse.repair.core.parser.LCNode;
import us.msu.cse.repair.core.parser.ModificationPoint;
import us.msu.cse.repair.core.parser.SeedStatement;
import us.msu.cse.repair.core.parser.SeedStatementInfo;
import us.msu.cse.repair.core.util.Helper;
import us.msu.cse.repair.informationExpression.LineAndNodeType;
import us.msu.cse.repair.informationExpression.MethClaPacOfExpName;

public class InitASTVisitor extends ASTVisitor {
	String sourceFilePath;

	Map<LCNode, Double> faultyLines;
	Set<LCNode> seedLines;

	List<ModificationPoint> modificationPoints;
	Map<SeedStatement, SeedStatementInfo> seedStatements;

	Map<String, ITypeBinding> declaredClasses;

	public InitASTVisitor(String sourceFilePath, Map<LCNode, Double> faultyLines, Set<LCNode> seedLines,
			List<ModificationPoint> modificationPoints, Map<SeedStatement, SeedStatementInfo> seedStatements,
			Map<String, ITypeBinding> declaredClasses) {
		this.sourceFilePath = sourceFilePath;
		this.faultyLines = faultyLines;
		this.seedLines = seedLines;

		this.modificationPoints = modificationPoints;
		this.seedStatements = seedStatements;

		this.declaredClasses = declaredClasses;
	}

	private void insertStatement(Statement statement) {
		AbstractTypeDeclaration td = Helper.getAbstractTypeDeclaration(statement);

		String className = td.resolveBinding().getBinaryName();

		CompilationUnit cu = (CompilationUnit) statement.getRoot();
		int lineNumber = cu.getLineNumber(statement.getStartPosition());

		LCNode lcNode = new LCNode(className, lineNumber);

		String packageName = cu.getPackage().getName().toString();
		ASTNode astNodeMid = (ASTNode) statement;
		String methodName = null;
		LineAndNodeType lineAndNodeType = new LineAndNodeType(lineNumber,statement.getNodeType());

		while(!(astNodeMid instanceof MethodDeclaration)&&(astNodeMid!=null)){
			astNodeMid = astNodeMid.getParent();
		}
		if (astNodeMid instanceof MethodDeclaration){
			methodName = ((MethodDeclaration) astNodeMid).getName().toString();
		}
		MethClaPacOfExpName methClaPacOfExpName = new MethClaPacOfExpName(methodName,className,packageName);
		if (faultyLines.containsKey(lcNode)) {
			ModificationPoint mp = new ModificationPoint();

			double suspValue = faultyLines.get(lcNode);
			boolean isInStaticMethod = Helper.isInStaticMethod(statement);



			mp.setSourceFilePath(sourceFilePath);
			mp.setLCNode(lcNode);
			mp.setSuspValue(suspValue);
			mp.setStatement(statement);

			mp.setMethClaPacOfExpName(methClaPacOfExpName);
			mp.setLineAndNodeType(lineAndNodeType);


			mp.setInStaticMethod(isInStaticMethod);

			modificationPoints.add(mp);
		}

		if (seedLines == null || seedLines.contains(lcNode)) {

			SeedStatement seedStatement = new SeedStatement(statement,methClaPacOfExpName,lineAndNodeType);

			if (seedStatements.containsKey(seedStatement)) {
				SeedStatementInfo ssi = seedStatements.get(seedStatement);
				ssi.getSourceFilePaths().add(sourceFilePath);
				ssi.getLCNodes().add(lcNode);
			} else {
				List<LCNode> lcNodes = new ArrayList<LCNode>();
				lcNodes.add(lcNode);
				List<String> sourceFilePaths = new ArrayList<String>();
				sourceFilePaths.add(sourceFilePath);

				SeedStatementInfo ssi = new SeedStatementInfo(lcNodes, sourceFilePaths);
				seedStatements.put(seedStatement, ssi);
			}

		}
	}

	@Override
	public boolean visit(TypeDeclaration node) {
		if (!node.isInterface()) {
			ITypeBinding tb = node.resolveBinding();
			if (tb != null) {
				String name = tb.getBinaryName();
				declaredClasses.put(name, tb);
			}
		}
		return true;
	}

	@Override
	public boolean visit(AnonymousClassDeclaration node) {
		ITypeBinding tb = node.resolveBinding();
		if (tb != null) {
			String name = tb.getBinaryName();
			declaredClasses.put(name, tb);
		}
		return true;
	}

	@Override
	public boolean visit(EnumDeclaration node) {
		ITypeBinding tb = node.resolveBinding();
		if (tb != null) {
			String name = tb.getBinaryName();
			declaredClasses.put(name, tb);
		}

		return true;
	}

	@Override
	public boolean visit(AssertStatement node) {
		insertStatement(node);
		return true;
	}

	@Override
	public boolean visit(BreakStatement node) {
		insertStatement(node);
		return true;
	}

	@Override
	public boolean visit(ContinueStatement node) {
		insertStatement(node);
		return true;
	}

	@Override
	public boolean visit(DoStatement node) {
		insertStatement(node);
		return true;
	}

	@Override
	public boolean visit(EmptyStatement node) {
		insertStatement(node);
		return true;
	}

	@Override
	public boolean visit(ExpressionStatement node) {
		insertStatement(node);
		return true;
	}

	@Override
	public boolean visit(ForStatement node) {
		insertStatement(node);
		return true;
	}

	@Override
	public boolean visit(EnhancedForStatement node) {
		insertStatement(node);
		return true;
	}

	@Override
	public boolean visit(IfStatement node) {
		insertStatement(node);
		return true;
	}

	@Override
	public boolean visit(LabeledStatement node) {
		insertStatement(node);
		return true;
	}

	@Override
	public boolean visit(ReturnStatement node) {
		insertStatement(node);
		return true;
	}

	@Override
	public boolean visit(SwitchCase node) {
		insertStatement(node);
		return true;
	}

	@Override
	public boolean visit(SwitchStatement node) {
		insertStatement(node);
		return true;
	}

	@Override
	public boolean visit(SynchronizedStatement node) {
		insertStatement(node);
		return true;
	}

	@Override
	public boolean visit(ThrowStatement node) {
		insertStatement(node);
		return true;
	}

	@Override
	public boolean visit(TryStatement node) {
		insertStatement(node);
		return true;
	}

	@Override
	public boolean visit(TypeDeclarationStatement node) {
		insertStatement(node);
		return true;
	}

	@Override
	public boolean visit(VariableDeclarationStatement node) {
		insertStatement(node);
		return true;
	}

	@Override
	public boolean visit(WhileStatement node) {
		insertStatement(node);
		return true;
	}
}
