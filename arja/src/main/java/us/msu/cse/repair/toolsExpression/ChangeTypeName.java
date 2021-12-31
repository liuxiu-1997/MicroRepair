package us.msu.cse.repair.toolsExpression;

import org.eclipse.jdt.core.dom.*;
import us.msu.cse.repair.astVisitorExpression.GetStatementFromText;
import us.msu.cse.repair.core.parser.ModificationPoint;

import java.util.ArrayList;
import java.util.List;

public class ChangeTypeName {
    public static List<Statement> getChangedType(ModificationPoint mp, VariableDeclarationStatement node, String typeName) {
        List<Type> list = mp.getTypeName();
        String statementString = mp.getStatement().toString();
        List<Statement> listStatement = new ArrayList<>();
        String[] strings = new String[50];
        int i = 0;
        for (Type type : list) {
            if (!TemplateBoolean.templateBooleanCheck(mp, type.toString() + "type")&&(!type.toString().equals(typeName))) {
                if ((type instanceof SimpleType)&&(node.getType() instanceof SimpleType)) {
                    String s = statementString.replaceAll(typeName, type.toString());
                    if (i < 49)
                        strings[i++] = s;
                    mp.getTemplateBoolean().put(type.toString() + "type", true);
                }else if ((type instanceof NameQualifiedType)&&((node.getType() instanceof NameQualifiedType))) {
                    String s = statementString.replaceAll(typeName, type.toString());
                    if (i < 49)
                        strings[i++] = s;
                    mp.getTemplateBoolean().put(type.toString() + "type", true);
                } else if ((type instanceof QualifiedType)&&((node.getType() instanceof QualifiedType))) {
                    String s = statementString.replaceAll(typeName, type.toString());
                    if (i < 49)
                        strings[i++] = s;
                    mp.getTemplateBoolean().put(type.toString() + "type", true);
                }else if ((type instanceof WildcardType)&&((node.getType() instanceof WildcardType))) {
                    String s = statementString.replaceAll(typeName, type.toString());
                    if (i < 49)
                        strings[i++] = s;
                    mp.getTemplateBoolean().put(type.toString() + "type", true);
                }else if ((type instanceof ParameterizedType)&&((node.getType() instanceof ParameterizedType))) {
                    String s = statementString.replaceAll(typeName, type.toString());
                    if (i < 49)
                        strings[i++] = s;
                    mp.getTemplateBoolean().put(type.toString() + "type", true);
                }else if ((type instanceof PrimitiveType) && (node.getType() instanceof PrimitiveType)) {
                    String s = statementString.replaceAll(typeName, type.toString());
                    if (i < 49)
                        strings[i++] = s;
                    mp.getTemplateBoolean().put(type.toString() + "type", true);
                } else if ((type instanceof ArrayType) && (node.getType() instanceof ArrayType)) {
                    String s = statementString.replaceAll(typeName, type.toString());
                    if (i < 49)
                        strings[i++] = s;
                    mp.getTemplateBoolean().put(type.toString() + "type", true);
                }
            }
        }
        for (int k = 0; k < 50; k++) {
            if (strings[k] != null) {
                String staClass = "public class Test{\n{\n";
                staClass += strings[k];
                staClass += "}\n}";
                Statement statement = ChangeTypeName.getStatement(staClass);
                if (statement != null) {
                    listStatement.add(statement);
                }
            }
        }
        return listStatement;
    }
    public static Statement getStatement(String staClass) {
        ASTParser astParser = ASTParser.newParser(AST.JLS8);
        astParser.setSource(staClass.toCharArray());
        astParser.setKind(ASTParser.K_COMPILATION_UNIT);
        CompilationUnit compilationUnit = (CompilationUnit) astParser.createAST(null);
        GetStatementFromText visitor = new GetStatementFromText();
        compilationUnit.accept(visitor);
        return visitor.getStatement();
    }
}
