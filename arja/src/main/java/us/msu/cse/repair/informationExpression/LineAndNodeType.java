package us.msu.cse.repair.informationExpression;

public class LineAndNodeType {
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
}
