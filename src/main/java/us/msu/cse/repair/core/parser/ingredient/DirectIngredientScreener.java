package us.msu.cse.repair.core.parser.ingredient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.Statement;

import us.msu.cse.repair.core.parser.ModificationPoint;
import us.msu.cse.repair.core.parser.SeedStatement;
import us.msu.cse.repair.core.parser.SeedStatementInfo;
import us.msu.cse.repair.core.parser.StatementInfoExtractor;

public class DirectIngredientScreener extends AbstractIngredientScreener {
	public DirectIngredientScreener(List<ModificationPoint> modificationPoints,
			Map<SeedStatement, SeedStatementInfo> seedStatements, IngredientMode ingredientMode) {
		super(modificationPoints, seedStatements, ingredientMode);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void screenIngredients(ModificationPoint mp, IngredientMode ingredientMode, boolean includeSelf) {
		// TODO Auto-generated method stub
		List<Statement> ingredients = new ArrayList<Statement>();
		for (Map.Entry<SeedStatement, SeedStatementInfo> entry : seedStatements.entrySet()) {
			if (canPreFiltered(mp, entry, ingredientMode, includeSelf))
				continue;  //当种子成分与修改点不在一个Mode里面是可以提前被过滤掉;当种子成分与修改点的return与throw类型不兼容时可以被过滤掉;当种子成分为自己本身时可以被提前过滤掉

			Statement seed = entry.getKey().getStatement();
			StatementInfoExtractor sie = new StatementInfoExtractor(seed);//语句信息提据包括可见变量、类变量、继承变量;方法同理
			sie.extract();

			if (IngredientUtil.isInMethodScope(seed, mp, sie) && IngredientUtil.isInVarScope(seed, mp, sie))
				ingredients.add(seed);

		}
		mp.setIngredients(ingredients);
	}
	
}
