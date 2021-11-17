package us.msu.cse.repair.informationExpression;

public class LineAndNodeType implements Cloneable{
    public int lineOfStaOrExp;
    public int NodeType;

    public LineAndNodeType(int lineOfStaOrExp, int nodeType) {
        this.lineOfStaOrExp = lineOfStaOrExp;
        NodeType = nodeType;
    }

    public LineAndNodeType() {

    }

    public int getLineOfStaOrExp() {
        return lineOfStaOrExp;
    }

    public void setLineOfStaOrExp(int lineOfStaOrExp) {
        this.lineOfStaOrExp = lineOfStaOrExp;
    }

    public int getNodeType() {
        return NodeType;
    }

    public void setNodeType(int nodeType) {
        NodeType = nodeType;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        LineAndNodeType lineAndNodeType = null;
        try {
            lineAndNodeType = (LineAndNodeType) super.clone();
        }catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return lineAndNodeType;
    }
}
