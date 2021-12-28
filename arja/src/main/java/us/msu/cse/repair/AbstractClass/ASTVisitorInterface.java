package us.msu.cse.repair.AbstractClass;

import org.eclipse.jdt.core.dom.Statement;

public interface ASTVisitorInterface {
    public abstract Statement getStatement();
    public abstract boolean isRepaired();
    public abstract void setRepaired(boolean flag);
}
